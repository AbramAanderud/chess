package websocket.commands;

public class LeaveCommand extends UserGameCommand {
    private final boolean isObserver;

    public LeaveCommand(CommandType commandType, String authToken, Integer gameID, boolean isObserver) {
        super(commandType, authToken, gameID);
        this.isObserver = isObserver;
    }

    public boolean isObserver() {
        return isObserver;
    }


}
