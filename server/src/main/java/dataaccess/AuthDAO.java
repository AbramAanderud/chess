package dataaccess;

public interface AuthDAO {
    String createAuth(String username);

    void clearAllAuthData();

    boolean isValidAuth(String authToken);

    String getUsernameByAuth(String authToken);

    void deleteAuthByAuth(String authToken);
}
