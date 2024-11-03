package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;

public class SQLUserDAOTests {
    private SQLUserDAO userDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        Connection connection = DatabaseManager.getConnection();
        userDAO = new SQLUserDAO(connection);
        userDAO.clearAllUserData();
    }

    @Test
    public void successCreateUser() throws DataAccessException {
        UserData user = new UserData("TestUser", "password", "email");
        userDAO.createUser(user);

        UserData retrievedUser = userDAO.getUser("TestUser");
        Assertions.assertNotNull(retrievedUser, "User should be created successfully and retrievable.");
        Assertions.assertEquals("TestUser", retrievedUser.username(), "Username should match");
        Assertions.assertEquals("email", retrievedUser.email(), "Email should match");
        Assertions.assertTrue(BCrypt.checkpw("password", retrievedUser.password()), "Password should be hashed and match");
    }

    @Test
    public void failCreateUserWithDuplicateUsername() throws DataAccessException {
        UserData user = new UserData("TestUser", "password", "email");
        userDAO.createUser(user);

        Assertions.assertThrows(DataAccessException.class, () -> {
            userDAO.createUser(new UserData("TestUser", "AnotherPassword", "email"));
        }, "Creating a user with a duplicate username should get an error");
    }

    @Test
    public void successGetUser() throws DataAccessException {
        UserData user = new UserData("TestUser", "password", "email");
        userDAO.createUser(user);

        UserData retrievedUser = userDAO.getUser("TestUser");
        Assertions.assertNotNull(retrievedUser, "User should be retrieved");
        Assertions.assertEquals("TestUser", retrievedUser.username(), "Retrieved username should match");
        Assertions.assertEquals("email", retrievedUser.email(), "Retrieved email should match");
        Assertions.assertTrue(BCrypt.checkpw("password", retrievedUser.password()), "Retrieved password should match");
    }

    @Test
    public void failGetUserWithInvalidUsername() throws DataAccessException {
        UserData retrievedUser = userDAO.getUser("NonExistentUser");
        Assertions.assertNull(retrievedUser, "Trying to get a User that doesn't exist should get null");
    }

    @Test
    public void successClearAllUserData() throws DataAccessException {
        userDAO.createUser(new UserData("User1", "Password1", "user1@example.com"));
        userDAO.createUser(new UserData("User2", "Password2", "user2@example.com"));
        userDAO.clearAllUserData();

        Assertions.assertNull(userDAO.getUser("User1"), "All user data should be cleared.");
        Assertions.assertNull(userDAO.getUser("User2"), "All user data should be cleared.");
    }

    @Test
    public void failCreateUserWithInvalidConnection() throws DataAccessException {
        SQLUserDAO invalidUserDAO = new SQLUserDAO(null);
        Assertions.assertThrows(NullPointerException.class, () -> {
            invalidUserDAO.createUser(new UserData("TestUser", "password", "email"));
        }, "Creating a user with a null connection should throw a NullPointerException");
    }
}
