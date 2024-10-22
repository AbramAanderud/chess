package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import requests.LoginRequest;
import requests.LogoutRequest;
import requests.RegisterRequest;
import result.LoginResult;
import result.LogoutResult;
import result.RegisterResult;

public class UserService {
    private final UserDAO userDAO = MemoryUserDAO.getInstance();
    private final AuthDAO authDAO = MemoryAuthDAO.getInstance();

    public RegisterResult register(RegisterRequest r) throws DataAccessException {

        if(r.username() == null || r.email() == null || r.password() == null) {
            System.out.println("bad request");
            return new RegisterResult(null, null, "\"message\": \"Error: bad request\"");
        }

        if (userDAO.getUser(r.username()) != null) {
            System.out.println("alreadyTaken");
            return new RegisterResult(null,null, "\"message\": \"Error: already taken\"");
        }

        UserData u = new UserData(r.username(), r.password(), r.email());
        userDAO.createUser(u);


        String authToken = authDAO.createAuth(u.username());

        return new RegisterResult(u.username(), authToken, null);
    }

    public LoginResult login(LoginRequest l) throws DataAccessException {
        UserData user = userDAO.getUser(l.username());

        if(user == null) {
            return new LoginResult(null, null, "\"message\": \"Error: unauthorized\"");
        }

        if(!user.password().equals(l.password())) {
            System.out.println("passwords don't match");
            return new LoginResult(null, null, "\"message\": \"Error: unauthorized\"");
        }

        String newAuthToken = authDAO.createAuth(l.username());

        return new LoginResult(l.username(), newAuthToken, null);
    }

    public LogoutResult logout(LogoutRequest req) throws DataAccessException {

        if(authDAO.isValidAuth(req.authToken())) {
            authDAO.deleteAuthByAuth(req.authToken());
            return new LogoutResult(null);
        } else {
            return new LogoutResult("error: unauthorized");
        }

    }
}
