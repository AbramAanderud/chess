package websocket.commands;

public class ConnectCommand extends UserGameCommand {
    private final boolean isObserver;
    private final String playerColor;

    public ConnectCommand(CommandType commandType, String authToken, Integer gameID, boolean isObserver, String playerColor) {
        super(commandType, authToken, gameID);
        this.isObserver = isObserver;
        this.playerColor = playerColor;
    }

    public boolean isObserver() {
        return isObserver;
    }

    public String getPlayerColor() {
        return playerColor;
    }
}
