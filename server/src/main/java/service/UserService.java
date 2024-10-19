package service;

import dataaccess.*;
import model.UserData;
import requests.LoginRequest;
import requests.RegisterRequest;
import result.RegisterResult;

public class UserService {

    public RegisterResult register(RegisterRequest r) throws DataAccessException {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();

        if (userDAO.getUser(r.username()) != null) {
            return new RegisterResult(null,null, "\"message\": \"Error: already taken\"");
        }

        UserData u = new UserData(r.username(), r.password(), r.email());
        userDAO.createUser(u);
        String authToken = authDAO.createAuth(u.username());

        return new RegisterResult(u.username(), authToken, null);
    }
}
