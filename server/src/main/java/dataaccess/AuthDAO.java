package dataaccess;

import model.AuthData;
import model.UserData;

public class AuthDAO {
    DataAccess dataAccess;
    //CRUD class

    public void createAuth(String username) {
        dataAccess.createAuth(username);
    }

    public AuthData getAuth(String username) {
        return dataAccess.getAuth(username);
    }

    public void deleteAuth(String username) {
        dataAccess.deleteAuth(username);
    }
}
