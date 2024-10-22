package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    void createGame(GameData g);

    GameData getGame(int gameID);

    Collection<GameData> listGames();

    void clearAllGameData();

    void joinGameRequest(Integer gameID, String username, String color);
}
