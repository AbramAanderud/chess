package service;

import dataaccess.*;
import result.ClearResult;

public class ClearService {
    private final UserDAO userDAO = MemoryUserDAO.getInstance();
    private final AuthDAO authDAO = MemoryAuthDAO.getInstance();
    private final GameDAO gameDAO = MemoryGameDAO.getInstance();

    public ClearResult clearAll() {
        try {
            gameDAO.clearAllGameData();
            userDAO.clearAllUserData();
            authDAO.clearAllAuthData();

            return new ClearResult(null);
        } catch (Exception e) {
            return new ClearResult("Error: " + e.getMessage());
        }
    }

}
