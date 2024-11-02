package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    int createGame(GameData g) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

    void clearAllGameData() throws DataAccessException;

    void joinGameRequest(Integer gameID, String username, String color) throws DataAccessException;
}
