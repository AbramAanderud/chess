package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class MemoryGameDAO implements GameDAO {
    private static MemoryGameDAO instance;
    private final Collection<GameData> gameData = new ArrayList<>();

    private MemoryGameDAO() { }

    public static MemoryGameDAO getInstance() {
        if (instance == null) {
            instance = new MemoryGameDAO();
        }
        return instance;
    }

    public void createGame(GameData g) {
        for(GameData currData : gameData) {
            if(currData.gameID() == g.gameID()) {
                System.out.println("gameID is already taken");
                return;
            }
        }
        gameData.add(g);
    }

    public GameData getGame(int gameID) {
        if(isDataEmpty(gameData)) {
            System.out.println("no game data");
            return null;
        }
        for(GameData currData : gameData) {
            if(currData.gameID() == gameID) {
                return currData;
            }
        }

        System.out.println("couldnt find game by gameID");
        return null;
    }

    public void updateGame(GameData g) {
        if(isDataEmpty(gameData)) {
            System.out.println("no game data");
            return;
        }
        for(GameData currData : gameData) {
            if(currData.gameID() == g.gameID()) {
                gameData.remove(currData);
                gameData.add(g);
                System.out.println("updated");
                return;
            }
        }
        System.out.println("couldnt find game by gameID when trying to update");
    }

    public Collection<GameData> listGames() {
        return gameData;
    }

    public void deleteGame(int gameID) {
        if(isDataEmpty(gameData)) {
            System.out.println("no game data");
            return;
        }
        gameData.removeIf(currData -> currData.gameID() == gameID);
    }

    public boolean isDataEmpty(Collection<GameData> gameData) {
        return gameData.isEmpty();
    }

    public void clearAllGameData() {
        gameData.clear();
    }

    public void joinGameRequest(Integer gameID, String username, String color) {
        if(isDataEmpty(gameData)) {
            System.out.println("no game data");
            return;
        }
        for(GameData currData : gameData) {
            if(currData.gameID() == gameID) {
                GameData updatedData;

                if(Objects.equals(color, "WHITE")) {
                    updatedData = new GameData(currData.gameID(), username, currData.blackUsername(), currData.gameName(), currData.game());
                } else if(Objects.equals(color, "BLACK")) {
                    updatedData = new GameData(currData.gameID(), currData.whiteUsername(), username, currData.gameName(), currData.game());
                } else {
                    System.out.println("color is bad");
                    return;
                }
                gameData.remove(currData);
                gameData.add(updatedData);
                System.out.println("updated");
                return;
            }
        }
    }

}
