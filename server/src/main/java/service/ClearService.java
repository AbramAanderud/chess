package service;

import dataaccess.*;
import result.ClearResult;

import java.sql.Connection;

public class ClearService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public ClearService() throws DataAccessException {
        Connection connection = DatabaseManager.DAOconnectors(); // Obtain a connection
        this.userDAO = new SQLUserDAO(connection);
        this.authDAO = new SQLAuthDAO(connection);
        this.gameDAO = new SQLGameDAO(connection);
    }

    public ClearResult clearAll() {
        try {
            gameDAO.clearAllGameData();
            userDAO.clearAllUserData();
            authDAO.clearAllAuthData();
            System.out.println("CLEARED");
            return new ClearResult(null);
        } catch (Exception e) {
            return new ClearResult("Error: " + e.getMessage());
        }
    }

}
