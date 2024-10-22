package dataaccess;

import model.UserData;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collection;

public class MemoryUserDAO implements UserDAO {
    private static MemoryUserDAO instance;
    private final Collection<UserData> userData = new ArrayList<>();

    private MemoryUserDAO() {
    }

    public static MemoryUserDAO getInstance() {
        if (instance == null) {
            instance = new MemoryUserDAO();
        }
        return instance;
    }

    public void createUser(UserData u) {
        if(!userData.contains(u)) {
            userData.add(u);
        }
    }

    public UserData getUser(String username) {
        for(UserData currData : userData) {
            if(currData.username().equals(username)) {
                return currData;
            }
        }
        return null;
    }

    public void updateUser(UserData u) {
        if(userData.isEmpty()) {
            return;
        }
        for(UserData currData : userData) {
            if(currData.username().equals(u.username())) {
                userData.remove(currData);
                userData.add(u);
            }
        }
    }

    public boolean correctPassword(UserData u) {
        if(userData.isEmpty()) {
            return false;
        }
        for(UserData currData : userData) {
            if(currData.username().equals(u.username())) {
                if(currData.password().equals(u.password())) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public void deleteUser(String username) {
        if(userData.isEmpty()) {
            return;
        }
        userData.removeIf(currData -> currData.username().equals(username));
    }

    public void clearAllUserData() {
        userData.clear();
    }

}
