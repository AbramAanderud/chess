package service;

import dataaccess.*;
import result.ClearResult;

public class ClearService {
    private final UserDAO userDAO = new SQLUserDAO();
    private final AuthDAO authDAO = new SQLAuthDAO();
    private final GameDAO gameDAO = new SQLGameDAO();

    public ClearService() throws DataAccessException {
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
