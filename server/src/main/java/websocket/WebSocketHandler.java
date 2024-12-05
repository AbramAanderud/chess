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
import websocket.messages.ErrorMessage;
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
    public void onMessage(Session session, String message) throws IOException {
        try {
            UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
            if (userGameCommand == null || userGameCommand.getAuthToken() == null) {
                throw new IllegalArgumentException("Bad request: Missing required fields");
            }

            try (Connection connection = DatabaseManager.daoConnectors()) {
                SQLAuthDAO sqlAuthDAO = new SQLAuthDAO(connection);
                String username = sqlAuthDAO.getUsernameByAuth(userGameCommand.getAuthToken());
                if (username == null) {
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
                    case LEAVE -> leaveGame(username, userGameCommand);
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


    private void loadGame(String username, int gameId, boolean broadcast) {
        try (Connection connection = DatabaseManager.daoConnectors()) {
            GameDAO gameDAO = new SQLGameDAO(connection);
            GameData gameData = gameDAO.getGame(gameId);

            if (gameData == null) {
                throw new DataAccessException("Game not found for ID: " + gameId);
            }

            LoadGameMessage loadGameMessage = new LoadGameMessage(gameData);

            if (broadcast) {
                connections.broadcast(username, loadGameMessage);
            } else {
                connections.sendTo(username, loadGameMessage);
            }

        } catch (DataAccessException | SQLException | IOException e) {
            throw new RuntimeException("Error loading game: " + e.getMessage(), e);
        }
    }


    private void connect(Session session, String username, ConnectCommand connectCommand) throws IOException {
        connections.add(username, session);

        String message;
        loadGame(username, connectCommand.getGameID(), false);

        if (!connectCommand.isObserver()) {
            message = username + " has been connected as the " + connectCommand.getPlayerColor() + " player";
        } else {
            message = username + " has been connected as an observer";
        }

        NotificationMessage notificationMessage = new NotificationMessage(username, message);
        connections.broadcast(username, notificationMessage);
    }

    private void leaveGame(String username, UserGameCommand userGameCommand) throws IOException {
        String message = username + " has left the game";


        try (Connection connection = DatabaseManager.daoConnectors()) {
            GameDAO gameDAO = new SQLGameDAO(connection);
            GameData gameData = gameDAO.getGame(userGameCommand.getGameID());

            if(gameData.whiteUsername() == username) {
                gameData.whiteUsername() = null;
            } else if(gameData.blackUsername() == username) {
                gameData.blackUsername() = null;
            }

            gameDAO.

        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Error loading game: " + e.getMessage(), e);
        }




        NotificationMessage notificationMessage = new NotificationMessage(username, message);
        connections.broadcast(username, notificationMessage);
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
            NotificationMessage notificationMessage = new NotificationMessage(username, message);
            connections.broadcast(null, notificationMessage);
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Error loading game: " + e.getMessage(), e);
        }
    }


    private void makeMove(String username, MakeMoveCommand makeMoveCommand) throws IOException {
        try (Connection connection = DatabaseManager.daoConnectors()) {
            // Access the game data from the database
            SQLGameDAO gameDAO = new SQLGameDAO(connection);
            GameData gameData = gameDAO.getGame(makeMoveCommand.getGameID());

            if (gameData == null) {
                throw new DataAccessException("Game not found for ID: " + makeMoveCommand.getGameID());
            }

            ChessGame currentGame = gameData.game();

            // Step 1: Verify the validity of the move
            if (currentGame.isGameOver()) {
                // Send a notification to the root client indicating the game is over
                NotificationMessage gameOverNotification = new NotificationMessage(
                        username,
                        "Cannot make move because the game is over."
                );
                connections.sendTo(username, gameOverNotification);
                return;
            }

            try {
                // Attempt to make the move, throw an exception if invalid
                currentGame.makeMove(makeMoveCommand.getMove());
            } catch (InvalidMoveException e) {
                // Send an error message to the root client if the move is invalid
                ErrorMessage errorMessage = new ErrorMessage("Error Invalid move: " + e.getMessage());
                connections.sendTo(username, errorMessage);
                return;
            }

            // Step 2: Update the game in the database
            gameDAO.updateGame(makeMoveCommand.getGameID(), currentGame);

            // Step 3: Send a LOAD_GAME message to all clients in the game (including the root client)
            loadGame(null, makeMoveCommand.getGameID(), true);

            // Step 4: Send a Notification message to all other clients (excluding the root client)
            String moveDescription = username + " made move " + makeMoveCommand.getMove().toString();
            NotificationMessage moveNotification = new NotificationMessage(username, moveDescription);
            connections.broadcast(username, moveNotification); // Exclude root client from this notification

            // Step 5: Check if the move results in check, checkmate, or stalemate and notify all clients accordingly
            if (currentGame.isInCheck(currentGame.getTeamTurn())) {
                String checkMessage = currentGame.getTeamTurn() + " is in check!";
                NotificationMessage checkNotification = new NotificationMessage(null, checkMessage);
                connections.broadcast(null, checkNotification);
            } else if (currentGame.isInCheckmate(currentGame.getTeamTurn())) {
                String checkmateMessage = currentGame.getTeamTurn() + " is in checkmate, game over!";
                NotificationMessage checkmateNotification = new NotificationMessage(null, checkmateMessage);
                connections.broadcast(null, checkmateNotification);
            }

            if (currentGame.isInStalemate(currentGame.getTeamTurn())) {
                NotificationMessage stalemateNotification = new NotificationMessage(null, "The game has ended in stalemate.");
                connections.broadcast(null, stalemateNotification);
            }

        } catch (DataAccessException | SQLException e) {
            // Handle database access exceptions
            throw new RuntimeException("Error loading game: " + e.getMessage(), e);
        } catch (Exception e) {
            // Handle any other unexpected exceptions by sending an error message to the root client
            ErrorMessage errorMessage = new ErrorMessage("An unexpected error occurred: " + e.getMessage());
            connections.sendTo(username, errorMessage);
        }
    }




}
