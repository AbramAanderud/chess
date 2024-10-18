package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;

public class DataAccess {
    private final MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
    private final MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
    private final MemoryGameDAO memoryGameDAO = new MemoryGameDAO();
    //User Functions
    public void createUser(UserData u) {
        memoryUserDAO.createUser(u);
    }

    public UserData getUser(String username) {
        return memoryUserDAO.getUser(username);
    }

    public void updateUser(UserData u) {
        memoryUserDAO.updateUser(u);
    }

    public void deleteUser(String username) {
        memoryUserDAO.deleteUser(username);
    }

    //Auth Functions
    public void createAuth(String username) {
        memoryAuthDAO.createAuth(username);
    }

    public AuthData getAuth(String username) {
        return memoryAuthDAO.getAuth(username);
    }

    public void deleteAuth(String username) {
        memoryAuthDAO.deleteAuth(username);
    }


    //Game Functions
    public void createGame(GameData g) {
        memoryGameDAO.createGame(g);
    }

    public GameData getGame(int gameID) {
        return memoryGameDAO.getGame(gameID);
    }

    public void updateGame(GameData g) {
        memoryGameDAO.updateGame(g);
    }

    public Collection<GameData> listGames() {
        return memoryGameDAO.listGames();
    }

    public void deleteGame(int gameID) {
        memoryGameDAO.deleteGame(gameID);
    }
}
