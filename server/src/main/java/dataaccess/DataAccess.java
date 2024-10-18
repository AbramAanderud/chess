package dataaccess;

import model.AuthData;
import model.UserData;

public class DataAccess {
    private final MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
    private final MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();

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

}
