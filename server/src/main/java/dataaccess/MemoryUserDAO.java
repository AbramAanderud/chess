package dataaccess;

import model.UserData;

import java.util.ArrayList;
import java.util.Collection;

public class MemoryUserDAO implements UserDAO {
    private static MemoryUserDAO instance;
    private final Collection<UserData> userData = new ArrayList<>();

    private MemoryUserDAO() {
    }

    /*public static MemoryUserDAO getInstance() {
        if (instance==null) {
            instance = new MemoryUserDAO();
        }
        return instance;
    }*/

    public void createUser(UserData u) {
        if (!userData.contains(u)) {
            userData.add(u);
        }
    }

    public UserData getUser(String username) {
        for (UserData currData : userData) {
            if (currData.username().equals(username)) {
                return currData;
            }
        }
        return null;
    }

    public void clearAllUserData() {
        userData.clear();
    }

}
