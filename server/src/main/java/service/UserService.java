package service;

import client.requests.LoginRequest;
import client.requests.LogoutRequest;
import client.requests.RegisterRequest;
import client.result.LoginResult;
import client.result.LogoutResult;
import client.result.RegisterResult;
import dataaccess.*;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService() throws DataAccessException {
        Connection connection = DatabaseManager.daoConnectors(); // Obtain a connection
        this.userDAO = new SQLUserDAO(connection);
        this.authDAO = new SQLAuthDAO(connection);
    }

    public RegisterResult register(RegisterRequest r) throws DataAccessException {
        if (r.username()==null || r.email()==null || r.password()==null) {
            return new RegisterResult(null, null, "error: bad request");
        }

        if (userDAO.getUser(r.username())!=null) {
            return new RegisterResult(null, null, "error: already taken");
        }

        UserData u = new UserData(r.username(), r.password(), r.email());
        userDAO.createUser(u);


        String authToken = authDAO.createAuth(u.username());

        return new RegisterResult(u.username(), authToken, null);
    }

    public LoginResult login(LoginRequest l) throws DataAccessException {
        UserData user = userDAO.getUser(l.username());

        if (user==null) {
            return new LoginResult(null, null, "error: unauthorized");
        }

        if (!BCrypt.checkpw(l.password(), user.password())) {
            return new LoginResult(null, null, "error: unauthorized");
        }

        String newAuthToken = authDAO.createAuth(l.username());

        return new LoginResult(l.username(), newAuthToken, null);
    }

    public LogoutResult logout(LogoutRequest req) throws DataAccessException {

        if (authDAO.isValidAuth(req.authToken())) {
            authDAO.deleteAuthByAuth(req.authToken());
            return new LogoutResult(null);
        } else {
            return new LogoutResult("error: unauthorized");
        }

    }
}
