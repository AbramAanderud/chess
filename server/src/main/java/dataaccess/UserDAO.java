package dataaccess;

import model.UserData;

import java.util.Collection;

public interface UserDAO {
    void createUser(UserData u);

    UserData getUser(String username);

    void updateUser(UserData u);

    void deleteUser(String username);

    void clearAllUserData();
}
