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
                        String messageJson;

                        if (message instanceof NotificationMessage notification) {
                            messageJson = gson.toJson(notification.getMessage());
                        } else if (message instanceof LoadGameMessage loadGame) {
                            String role = getRole(c.username, loadGame.getGameData());
                            String customLoadGameMessage = String.format(
                                    "Game loaded for %s: %s",
                                    role,
                                    gson.toJson(loadGame.getGameData())
                            );
                            messageJson = gson.toJson(customLoadGameMessage);
                        } else {
                            messageJson = gson.toJson(message);
                        }

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


    private String getRole(String username, GameData gameData) {
        if (username.equals(gameData.whiteUsername())) {
            return "White Player";
        } else if (username.equals(gameData.blackUsername())) {
            return "Black Player";
        } else {
            return "Observer";
        }
    }



}
