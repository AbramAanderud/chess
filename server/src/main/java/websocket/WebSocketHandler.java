package websocket;

import com.google.gson.Gson;
import dataaccess.*;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import websocket.commands.ConnectCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.sql.Connection;

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
            e.printStackTrace();
            session.getRemote().sendString("Database error occurred");
        } catch (Exception e) {
            e.printStackTrace();
            session.getRemote().sendString("An error occurred: " + e.getMessage());
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

    private void makeMove(String username, MakeMoveCommand makeMoveCommand) throws IOException {
        String message = username + " has made move " + makeMoveCommand.getMove();

        NotificationMessage notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, username, message);
        connections.broadcast(username, notificationMessage);
    }





}
