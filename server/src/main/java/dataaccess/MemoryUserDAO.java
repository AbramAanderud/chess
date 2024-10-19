package dataaccess;

import model.UserData;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collection;

public class MemoryUserDAO implements UserDAO {
    Collection<UserData> userData = new ArrayList<>();

    public void createUser(UserData u) {
        if(!userData.contains(u)) {
            userData.add(u);
        } else {
            System.out.println("The user already exists");
        }
    }

    public UserData getUser(String username) {
        if(userData.isEmpty()) {
            System.out.println("userData is empty");
            return null;
        }
        for(UserData currData : userData) {
            if(currData.username().equals(username)) {
                return currData;
            }
        }
        System.out.println("didn't find username to get user from");
        return null;
    }

    public void updateUser(UserData u) {
        if(userData.isEmpty()) {
            System.out.println("data is empty");
            return;
        }
        for(UserData currData : userData) {
            if(currData.username().equals(u.username())) {
                userData.remove(currData);
                userData.add(u);
            }
        }
        System.out.println("User with that username isnt found");
    }

    public void deleteUser(String username) {
        if(userData.isEmpty()) {
            System.out.println("data is empty when trying to delete");
            return;
        }
        userData.removeIf(currData -> currData.username().equals(username));
    }

    public void clearAllUserData() {
        userData.clear();
    }

}
