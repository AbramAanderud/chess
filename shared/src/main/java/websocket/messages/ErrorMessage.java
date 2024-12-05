package websocket.messages;

public class ErrorMessage extends ServerMessage {
    private final String message;
    private final String username;

    public ErrorMessage(ServerMessageType type, String username, String message) {
        super(type);
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
