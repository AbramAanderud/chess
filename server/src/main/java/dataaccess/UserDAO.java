package dataaccess;

import model.UserData;

public interface UserDAO {
    void createUser(UserData u);

    UserData getUser(String username);

    void updateUser(UserData u);

    void deleteUser(String username);

    void clearAllUserData();

    boolean correctPassword(UserData u);

}
