package dataaccess;

import model.AuthData;
import model.UserData;

public interface AuthDAO {
    String createAuth(String username);

    AuthData getAuth(String username);

    void deleteAuth(String username);

    void clearAllAuthData();
}
