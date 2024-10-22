package dataaccess;

import model.GameData;
import model.UserData;

import java.util.Collection;

public interface GameDAO {
    void createGame(GameData g);

    GameData getGame(int gameID);

    void updateGame(GameData g);

    Collection<GameData> listGames();

    void deleteGame(int gameID);

    void clearAllGameData();

    void joinGameRequest(Integer gameID, String username, String color);
}
