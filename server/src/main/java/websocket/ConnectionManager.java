package websocket;

import com.google.gson.Gson;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import server.Server;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String username, Session session) {
        var connection = new Connection(username, session);
        connections.put(username, connection);
    }

    public void remove(String username) {
        connections.remove(username);
    }

    public void broadcast(String excludeUsername, ServerMessage message) throws IOException {
        var removeList = new ArrayList<Connection>();
        Gson gson = new Gson();

        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (!c.username.equals(excludeUsername)) {
                    try {
                        String messageJson = gson.toJson(message);
                        c.send(messageJson);
                    } catch (IOException e) {
                        removeList.add(c);
                    }
                }
            }
        }

        for (var c : removeList) {
            connections.remove(c.username);
        }
    }

    public void sendTo(String username, ServerMessage message) throws IOException {
        var connection = connections.get(username);
        if (connection != null && connection.session.isOpen()) {
            String messageJson = new Gson().toJson(message);
            connection.send(messageJson);
        }
    }


}
