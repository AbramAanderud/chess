package websocket;

import com.google.gson.Gson;
import dataaccess.*;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import websocket.commands.UserGameCommand;

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
                    case CONNECT -> connect(session, username, (ConnectCommand) userGameCommand);
                    case MAKE_MOVE -> makeMove(session, username, (MakeMoveCommand) userGameCommand);
                    case LEAVE -> leaveGame(session, username, (LeaveGameCommand) userGameCommand);
                    case RESIGN -> resign(session, username, (ResignCommand) userGameCommand);
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

    private void connect(Session session, ConnectCommand userGameCommand) {
        connections.add(visitorName, session);
        var message = String.format("%s is in the shop", visitorName);
        var notification = new Notification(Notification.Type.ARRIVAL, message);
        connections.broadcast(visitorName, notification);
    }





}
