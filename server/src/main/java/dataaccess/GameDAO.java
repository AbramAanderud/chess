package dataaccess;

import model.GameData;
import model.UserData;

import java.util.Collection;

public class GameDAO {
    DataAccess dataAccess;
    //Crud class

    public void createGame(GameData g) {
        dataAccess.createGame(g);
    }

    public GameData getGame(int gameID) {
        return dataAccess.getGame(gameID);
    }

    public void updateGame(GameData g) {
        dataAccess.updateGame(g);
    }

    public Collection<GameData> listGames() {
        return dataAccess.listGames();
    }

    public void deleteGame(int gameID) {
        dataAccess.deleteGame(gameID);
    }
}
