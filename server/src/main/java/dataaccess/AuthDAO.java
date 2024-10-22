package dataaccess;

import model.AuthData;

public interface AuthDAO {
    String createAuth(String username);

    AuthData getAuth(String username);

    void deleteAuth(String username);

    void clearAllAuthData();

    boolean isValidAuth(String authToken);

    String getUsernameByAuth(String authToken);

    void deleteAuthByAuth(String authToken);
}
