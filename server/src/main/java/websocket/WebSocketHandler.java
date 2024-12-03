package websocket;

import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;

public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        Action action = new Gson().fromJson(message, Action.class);
        switch (action.type()) {
            case ENTER -> enter(action.visitorName(), session);
            case EXIT -> exit(action.visitorName());
        }
    }

}
