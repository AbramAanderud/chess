package dataaccess;

public interface AuthDAO {
    String createAuth(String username) throws DataAccessException;

    void clearAllAuthData() throws DataAccessException;

    boolean isValidAuth(String authToken) throws DataAccessException;

    String getUsernameByAuth(String authToken) throws DataAccessException;

    void deleteAuthByAuth(String authToken) throws DataAccessException;
}
