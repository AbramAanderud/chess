package websocket.messages;

public class NotificationMessage extends ServerMessage {
    private final String message;
    private final String username;

    public NotificationMessage(String username, String message) {
        super(ServerMessageType.NOTIFICATION);
        this.username = username;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getUsername() {
        return username;
    }
}
