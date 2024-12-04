package websocket;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.*;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import websocket.commands.ConnectCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        try {
            if (session==null || !session.isOpen()) {
                throw new IOException("Session not opened");
            }

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
                        makeMove(makeMoveCommand);
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
            e.printStackTrace();
            session.getRemote().sendString("Database error occurred");
        } catch (Exception e) {
            e.printStackTrace();
            session.getRemote().sendString("An error occurred: " + e.getMessage());
        }
    }

    private void loadGame(int gameID) {
        try(Connection connection = DatabaseManager.daoConnectors()) {
            GameDAO gameDAO = new SQLGameDAO(connection);
            GameData gameData = gameDAO.getGame(gameID);

            if (gameData == null) {
                throw new DataAccessException("Game not found for ID: " + gameID);
            }

            LoadGameMessage loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameData);
            connections.broadcast(null, loadGameMessage);

        } catch (DataAccessException | SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void connect(Session session, String username, ConnectCommand userGameCommand) throws IOException {
        connections.add(username, session);

        String message;
        if (!userGameCommand.isObserver()) {
            message = username + " has been connected as the " + userGameCommand.getPlayerColor() + " player";
        } else {
            message = username + " has been connected as an observer";
        }

        NotificationMessage notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, username, message);
        connections.broadcast(username, notificationMessage);
        loadGame(userGameCommand.getGameID());
    }

    private void leaveGame(String username, UserGameCommand userGameCommand) throws IOException {
        String message = username + " has left the game";

        NotificationMessage notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, username, message);
        connections.broadcast(username, notificationMessage);
        connections.remove(username);
    }

    private void resign(String username, UserGameCommand userGameCommand) throws IOException {
        String message = username + " has resigned from the game";

        NotificationMessage notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, username, message);
        connections.broadcast(username, notificationMessage);
        connections.remove(username);
    }

    private void makeMove(MakeMoveCommand makeMoveCommand) throws IOException {
        try (Connection connection = DatabaseManager.daoConnectors()) {
            SQLGameDAO gameDAO = new SQLGameDAO(connection);
            GameData gameData = gameDAO.getGame(makeMoveCommand.getGameID());
            ChessGame currentGame = gameData.game();

            currentGame.makeMove(makeMoveCommand.getMove());

            gameDAO.updateGame(makeMoveCommand.getGameID(), currentGame);
        } catch (DataAccessException | InvalidMoveException | SQLException e) {
            throw new RuntimeException(e);
        }

        loadGame(makeMoveCommand.getGameID());
    }



}
