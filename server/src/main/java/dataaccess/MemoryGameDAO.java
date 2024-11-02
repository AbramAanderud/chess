package dataaccess;

import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class MemoryGameDAO implements GameDAO {
    private static MemoryGameDAO instance;
    private final Collection<GameData> gameData = new ArrayList<>();

    private MemoryGameDAO() {
    }

    public static MemoryGameDAO getInstance() {
        if (instance==null) {
            instance = new MemoryGameDAO();
        }
        return instance;
    }

    public int createGame(GameData g) {
        for (GameData currData : gameData) {
            if (currData.gameID()==g.gameID()) {
                return currData.gameID();
            }
        }
        gameData.add(g);
        return g.gameID();
    }

    public GameData getGame(int gameID) {
        if (isDataEmpty(gameData)) {
            return null;
        }
        for (GameData currData : gameData) {
            if (currData.gameID()==gameID) {
                return currData;
            }
        }
        return null;
    }

    public Collection<GameData> listGames() {
        return gameData;
    }

    public boolean isDataEmpty(Collection<GameData> gameData) {
        return gameData.isEmpty();
    }

    public void clearAllGameData() {
        gameData.clear();
    }

    public void joinGameRequest(Integer gameID, String username, String color) {
        if (isDataEmpty(gameData)) {
            return;
        }
        for (GameData currData : gameData) {
            if (currData.gameID()==gameID) {
                GameData updatedData;

                if (Objects.equals(color, "WHITE")) {
                    updatedData = new GameData(currData.gameID(), username,
                            currData.blackUsername(), currData.gameName(),
                            currData.game());
                } else if (Objects.equals(color, "BLACK")) {
                    updatedData = new GameData(currData.gameID(),
                            currData.whiteUsername(), username,
                            currData.gameName(), currData.game());
                } else {
                    return;
                }
                gameData.remove(currData);
                gameData.add(updatedData);
            }
        }
    }

}
