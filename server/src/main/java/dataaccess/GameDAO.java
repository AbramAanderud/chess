package dataaccess;

import model.UserData;

public class GameDAO {
    DataAccess dataAccess;
    //Crud class

    public void createUser(UserData u) {
        dataAccess.createUser(u);
    }

    public UserData getUser(String username) {
        return dataAccess.getUser(username);
    }

    public void updateUser(UserData u) {
        dataAccess.updateUser(u);
    }

    public void deleteUser(String username) {
        dataAccess.deleteUser(username);
    }
}
