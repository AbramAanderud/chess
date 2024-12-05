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
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        System.out.println("\n Received message: " + message);
        try {
            UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
            if (userGameCommand==null || userGameCommand.getAuthToken()==null) {
                throw new IllegalArgumentException("Bad request");
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
                        leaveGame(username, userGameCommand);
                    }
                    case RESIGN -> {
                        resign(username, userGameCommand);
                    }
                    default -> throw new IllegalArgumentException("Unsupported command type: " + userGameCommand.getCommandType());
                }
            }
        } catch (DataAccessException e) {
            session.getRemote().sendString("Database error occurred");
        } catch (Exception e) {
            session.getRemote().sendString("An error occurred: " + e.getMessage());
        }
    }

    private void loadGame(String username, int gameID) {
        try(Connection connection = DatabaseManager.daoConnectors()) {
            GameDAO gameDAO = new SQLGameDAO(connection);
            GameData gameData = gameDAO.getGame(gameID);

            if (gameData == null) {
                throw new DataAccessException("Game not found for ID: " + gameID);
            }

            LoadGameMessage loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameData);
            connections.broadcast(username, loadGameMessage);

        } catch (DataAccessException | SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void connect(Session session, String username, ConnectCommand connectCommand) throws IOException {
        connections.add(username, session);

        String message;
        loadGame(username, connectCommand.getGameID());

        if (!connectCommand.isObserver()) {
            message = username + " has been connected as the " + connectCommand.getPlayerColor() + " player";
        } else {
            message = username + " has been connected as an observer";
        }

        NotificationMessage notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, username, message);
        connections.broadcast(null, notificationMessage);
    }

    private void leaveGame(String username, UserGameCommand userGameCommand) throws IOException {
        String message = username + " has left the game";

        NotificationMessage notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, username, message);
        connections.broadcast(null, notificationMessage);
        connections.remove(username);
    }

    private void resign(String username, UserGameCommand userGameCommand) throws IOException {
        try (Connection connection = DatabaseManager.daoConnectors()) {
            GameDAO gameDAO = new SQLGameDAO(connection);
            GameData gameData = gameDAO.getGame(userGameCommand.getGameID());
            ChessGame currentGame = gameData.game();

            currentGame.setGameOver(true);
            gameDAO.updateGame(userGameCommand.getGameID(), currentGame);

            String message = username + " has resigned from the game";
            NotificationMessage notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, username, message);
            connections.broadcast(null, notificationMessage);
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }


    private void makeMove(String username, MakeMoveCommand makeMoveCommand) throws IOException {
        try (Connection connection = DatabaseManager.daoConnectors()) {
            SQLGameDAO gameDAO = new SQLGameDAO(connection);
            GameData gameData = gameDAO.getGame(makeMoveCommand.getGameID());
            ChessGame currentGame = gameData.game();

            if (currentGame.isGameOver()) {
                NotificationMessage gameOverNotification = new NotificationMessage(
                        ServerMessage.ServerMessageType.NOTIFICATION,
                        username,
                        "Cannot make move because the game is over"
                );
                connections.broadcast(username, gameOverNotification);
                return;
            }

            currentGame.makeMove(makeMoveCommand.getMove());
            gameDAO.updateGame(makeMoveCommand.getGameID(), currentGame);

            loadGame(null, makeMoveCommand.getGameID());

            String moveDescription = username + " made a move: " + makeMoveCommand.getMove();
            NotificationMessage notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, username, moveDescription);
            connections.broadcast(null, notificationMessage);

            if (currentGame.isInCheck(currentGame.getTeamTurn())) {
                String checkMessage = currentGame.getTeamTurn() + " is in check!";
                NotificationMessage checkNotification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, null, checkMessage);
                connections.broadcast(null, checkNotification);
            } else if (currentGame.isInCheckmate(currentGame.getTeamTurn())) {
                String checkmateMessage = currentGame.getTeamTurn() + " is in checkmate, game over!";
                NotificationMessage checkmateNotification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, null, checkmateMessage);
                connections.broadcast(null, checkmateNotification);
            }

            if (currentGame.isInStalemate(currentGame.getTeamTurn())) {
                NotificationMessage stalemateNotification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, username, "The game has ended in stalemate");
                connections.broadcast(null, stalemateNotification);
            }


        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException(e);
        } catch (InvalidMoveException e) {
            System.out.println("invalid move");
        }

        loadGame(null, makeMoveCommand.getGameID());
    }


}
