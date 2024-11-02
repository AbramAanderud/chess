package service;

import dataaccess.*;
import model.UserData;
import requests.LoginRequest;
import requests.LogoutRequest;
import requests.RegisterRequest;
import result.LoginResult;
import result.LogoutResult;
import result.RegisterResult;

public class UserService {
    private final UserDAO SQLUserDAO;
    private final AuthDAO MemoryAuthDAO;

    public RegisterResult register(RegisterRequest r) {

        if (r.username() == null || r.email() == null || r.password() == null) {
            return new RegisterResult(null, null, "error: bad request");
        }

        if (userDAO.getUser(r.username()) != null) {
            return new RegisterResult(null, null, "error: already taken");
        }

        UserData u = new UserData(r.username(), r.password(), r.email());
        userDAO.createUser(u);


        String authToken = authDAO.createAuth(u.username());

        return new RegisterResult(u.username(), authToken, null);
    }

    public LoginResult login(LoginRequest l) {
        UserData user = userDAO.getUser(l.username());

        if (user == null) {
            return new LoginResult(null, null, "error: unauthorized");
        }

        if (!user.password().equals(l.password())) {
            return new LoginResult(null, null, "error: unauthorized");
        }

        String newAuthToken = authDAO.createAuth(l.username());

        return new LoginResult(l.username(), newAuthToken, null);
    }

    public LogoutResult logout(LogoutRequest req) {

        if (authDAO.isValidAuth(req.authToken())) {
            authDAO.deleteAuthByAuth(req.authToken());
            return new LogoutResult(null);
        } else {
            return new LogoutResult("error: unauthorized");
        }

    }
}
