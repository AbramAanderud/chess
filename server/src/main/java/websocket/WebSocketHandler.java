package websocket;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.*;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        try {
            UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
            if (userGameCommand==null || userGameCommand.getAuthToken()==null) {
                throw new IllegalArgumentException("Bad request: Missing required fields");
            }

            try (Connection connection = DatabaseManager.daoConnectors()) {
                SQLAuthDAO sqlAuthDAO = new SQLAuthDAO(connection);
                String username = sqlAuthDAO.getUsernameByAuth(userGameCommand.getAuthToken());
                if (username==null) {
                    throw new IllegalArgumentException("Invalid authentication token");
                }

                switch (userGameCommand.getCommandType()) {
                    case CONNECT -> {
                        ConnectCommand connectCommand = new Gson().fromJson(message, ConnectCommand.class);
                        connect(session, username, connectCommand);
                    }
                    case MAKE_MOVE -> {
                        MakeMoveCommand makeMoveCommand = new Gson().fromJson(message, MakeMoveCommand.class);
                        makeMove(username, makeMoveCommand);
                    }
                    case LEAVE -> {
                        LeaveCommand leaveCommand = new Gson().fromJson(message, LeaveCommand.class);
                        leaveGame(username, leaveCommand);
                    }
                    case RESIGN -> resign(username, userGameCommand);
                    default -> throw new IllegalArgumentException("Unsupported command type: " + userGameCommand.getCommandType());
                }
            }
        } catch (DataAccessException e) {
            sendErrorMessage(session, "Database error occurred: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            sendErrorMessage(session, e.getMessage());
        } catch (Exception e) {
            sendErrorMessage(session, "An unexpected error occurred: " + e.getMessage());
        }
    }

    private void sendErrorMessage(Session session, String message) {
        try {
            ErrorMessage errorMessage = new ErrorMessage(message);
            session.getRemote().sendString(new Gson().toJson(errorMessage));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void loadGame(String username, int gameID, boolean broadcast) {
        try (Connection connection = DatabaseManager.daoConnectors()) {
            SQLGameDAO gameDAO = new SQLGameDAO(connection);
            GameData gameData = gameDAO.getGame(gameID);

            if (gameData==null) {
                throw new DataAccessException("Game not found for ID: " + gameID);
            }

            LoadGameMessage loadGameMessage = new LoadGameMessage(gameData);

            if (broadcast) {
                connections.broadcast(username, loadGameMessage, gameID);
            } else {
                connections.sendTo(username, loadGameMessage);
            }

        } catch (DataAccessException | SQLException | IOException e) {
            throw new RuntimeException("Error loading game: " + e.getMessage(), e);
        }
    }


    private void connect(Session session, String username, ConnectCommand connectCommand) throws IOException {
        connections.add(username, session, connectCommand.getGameID());

        String message;
        loadGame(username, connectCommand.getGameID(), false);

        if (!connectCommand.isObserver()) {
            message = username + " has been connected as the " + connectCommand.getPlayerColor() + " player";
        } else {
            message = username + " has been connected as an observer";
        }

        NotificationMessage notificationMessage = new NotificationMessage(username, message);
        connections.broadcast(username, notificationMessage, connectCommand.getGameID());
    }

    private void leaveGame(String username, LeaveCommand leaveCommand) throws IOException {
        String message;

        if (leaveCommand.isObserver()) {
            message = username + " (observer) has left the game";
        } else {
            message = username + " has left the game";
        }

        try (Connection connection = DatabaseManager.daoConnectors()) {
            SQLGameDAO gameDAO = new SQLGameDAO(connection);
            GameData gameData = gameDAO.getGame(leaveCommand.getGameID());

            if (Objects.equals(username, gameData.blackUsername()) || Objects.equals(username, gameData.whiteUsername())) {
                gameDAO.playerLeft(leaveCommand.getGameID(), username);
            }

            NotificationMessage notificationMessage = new NotificationMessage(username, message);

            connections.broadcast(username, notificationMessage, leaveCommand.getGameID());
            connections.remove(username);

        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private void resign(String username, UserGameCommand userGameCommand) throws IOException {
        try (Connection connection = DatabaseManager.daoConnectors()) {
            GameDAO gameDAO = new SQLGameDAO(connection);
            GameData gameData = gameDAO.getGame(userGameCommand.getGameID());
            ChessGame currentGame = gameData.game();

            if (!Objects.equals(username, gameData.whiteUsername()) && !Objects.equals(username, gameData.blackUsername())) {
                ErrorMessage errorMessage = new ErrorMessage("Cannot make move as observer");
                connections.sendTo(username, errorMessage);
                return;
            }

            if (currentGame.isGameOver()) {
                ErrorMessage errorMessage = new ErrorMessage("Cannot resign as game is already over");
                connections.sendTo(username, errorMessage);
                return;
            }

            currentGame.setGameOver(true);
            gameDAO.updateGame(userGameCommand.getGameID(), currentGame);

            String message = username + " has resigned from the game";
            NotificationMessage notificationMessage = new NotificationMessage(username, message);
            connections.broadcast(null, notificationMessage, userGameCommand.getGameID());
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Error loading game: " + e.getMessage(), e);
        }
    }


    private void makeMove(String username, MakeMoveCommand makeMoveCommand) throws IOException {
        try (Connection connection = DatabaseManager.daoConnectors()) {
            SQLGameDAO gameDAO = new SQLGameDAO(connection);
            GameData gameData = gameDAO.getGame(makeMoveCommand.getGameID());

            if (gameData==null) {
                throw new DataAccessException("Game not found for ID: " + makeMoveCommand.getGameID());
            }

            ChessGame currentGame = gameData.game();

            if (currentGame.isGameOver()) {
                ErrorMessage errorMessage = new ErrorMessage("Cannot make move because the game is over");
                connections.sendTo(username, errorMessage);
                return;
            }

            ChessGame.TeamColor currentTurnColor = currentGame.getTeamTurn();
            String playerColor;

            if (Objects.equals(gameData.whiteUsername(), username)) {
                playerColor = "WHITE";
            } else if (Objects.equals(gameData.blackUsername(), username)) {
                playerColor = "BLACK";
            } else {
                ErrorMessage errorMessage = new ErrorMessage("You are not a player in this game");
                connections.sendTo(username, errorMessage);
                return;
            }
            if (!currentTurnColor.name().equalsIgnoreCase(playerColor)) {
                ErrorMessage errorMessage = new ErrorMessage("It's not your turn to move.");
                connections.sendTo(username, errorMessage);
                return;
            }

            try {
                currentGame.makeMove(makeMoveCommand.getMove());
            } catch (InvalidMoveException e) {
                ErrorMessage errorMessage = new ErrorMessage("Error Invalid move: " + e.getMessage());
                connections.sendTo(username, errorMessage);
                return;
            }
            gameDAO.updateGame(makeMoveCommand.getGameID(), currentGame);

            loadGame(null, makeMoveCommand.getGameID(), true);

            String moveDescription = username + " made move " + makeMoveCommand.getMove().toString();
            NotificationMessage moveNotification = new NotificationMessage(username, moveDescription);
            connections.broadcast(username, moveNotification, gameData.gameID());

            if (currentGame.isInCheck(currentGame.getTeamTurn())) {
                String checkMessage = currentGame.getTeamTurn() + " is in check!";
                NotificationMessage checkNotification = new NotificationMessage(null, checkMessage);
                connections.broadcast(null, checkNotification, gameData.gameID());
            } else if (currentGame.isInCheckmate(currentGame.getTeamTurn())) {
                String checkmateMessage = currentGame.getTeamTurn() + " is in checkmate, game over!";
                NotificationMessage checkmateNotification = new NotificationMessage(null, checkmateMessage);
                connections.broadcast(null, checkmateNotification, gameData.gameID());
            }

            if (currentGame.isInStalemate(currentGame.getTeamTurn())) {
                NotificationMessage stalemateNotification = new NotificationMessage(null, "The game has ended in stalemate");
                connections.broadcast(null, stalemateNotification, gameData.gameID());
            }

        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException("Error loading game: " + e.getMessage(), e);
        } catch (Exception e) {
            ErrorMessage errorMessage = new ErrorMessage("An unexpected error occurred: " + e.getMessage());
            connections.sendTo(username, errorMessage);
        }
    }


}
