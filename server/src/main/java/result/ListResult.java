package result;

import java.util.List;

public record ListResult(List<GameInfo> games, String message) {
    public record GameInfo(int gameID, String whiteUsername, String blackUsername, String gameName) {
    }
}
