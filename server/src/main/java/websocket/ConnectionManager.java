package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String username, Session session, Integer gameID) {
        var connection = new Connection(username, session, gameID);
        connections.put(username, connection);
    }

    public void remove(String username) {
        connections.remove(username);
    }

    public void broadcast(String excludeUsername, ServerMessage message, Integer gameID) throws IOException {
        var removeList = new ArrayList<Connection>();
        Gson gson = new Gson();

        for (var c : connections.values()) {
            if (c.session.isOpen() && c.gameID.equals(gameID)) {
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
        if (connection!=null && connection.session.isOpen()) {
            String messageJson = new Gson().toJson(message);
            connection.send(messageJson);
        }
    }


}
