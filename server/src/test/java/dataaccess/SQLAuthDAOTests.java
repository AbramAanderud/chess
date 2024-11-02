package dataaccess;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

public class SQLAuthDAOTests {
    private SQLAuthDAO authDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        Connection connection = DatabaseManager.getConnection();
        authDAO = new SQLAuthDAO(connection);
        authDAO.clearAllAuthData();
    }

    @Test
    public void successCreateAuth() throws DataAccessException {
        String username = "TestUser";
        String authToken = authDAO.createAuth(username);

        Assertions.assertNotNull(authToken, "Auth token should not be null");
        Assertions.assertTrue(authDAO.isValidAuth(authToken), "Auth token should be valid after creation");
    }

    @Test
    public void successIsValidAuth() throws DataAccessException {
        String username = "TestUser";
        String authToken = authDAO.createAuth(username);

        Assertions.assertTrue(authDAO.isValidAuth(authToken), "Auth token should be valid");
    }

    @Test
    public void failInvalidConnection() throws DataAccessException {
        SQLAuthDAO invalidDAO = new SQLAuthDAO(null);
        Assertions.assertThrows(NullPointerException.class, () -> {
            invalidDAO.createAuth("TestUser");
        });
    }

    @Test
    public void isValidAuthWithInvalidToken() throws DataAccessException {
        Assertions.assertFalse(authDAO.isValidAuth("invalidToken"), "Invalid auth token should return false");
    }

    @Test
    public void successGetUsernameByAuth() throws DataAccessException {
        String username = "TestUser";
        String authToken = authDAO.createAuth(username);

        Assertions.assertEquals(username, authDAO.getUsernameByAuth(authToken), "Username should match the one used to create the auth token");
    }

    @Test
    public void getUsernameByInvalidAuth() throws DataAccessException {
        Assertions.assertNull(authDAO.getUsernameByAuth("invalidToken"), "Should return null for invalid auth token");
    }

    @Test
    public void successDeleteAuthByAuth() throws DataAccessException {
        String username = "TestUser";
        String authToken = authDAO.createAuth(username);
        authDAO.deleteAuthByAuth(authToken);

        Assertions.assertFalse(authDAO.isValidAuth(authToken), "Auth token should no longer be valid after deletion");
    }

    @Test
    public void deleteAuthByInvalidToken() throws DataAccessException {
        Assertions.assertDoesNotThrow(() -> {
            authDAO.deleteAuthByAuth("invalidToken");
        }, "Should not throw an exception when deleting an invalid token");
    }

    @Test
    public void successClearAllAuthData() throws DataAccessException {
        authDAO.createAuth("User1");
        authDAO.createAuth("User2");
        authDAO.clearAllAuthData();

        Assertions.assertFalse(authDAO.isValidAuth("User1"), "All auth data should be cleared");
        Assertions.assertFalse(authDAO.isValidAuth("User2"), "All auth data should be cleared");
    }
}
