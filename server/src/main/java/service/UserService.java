package service;

import dataaccess.UserDAO;
import model.UserData;
import requests.LoginRequest;
import requests.RegisterRequest;
import result.RegisterResult;

public class UserService {

    public RegisterResult register(RegisterRequest r) {
        UserDAO userDAO = new UserDAO();

        if (userDAO.getUser(r.username()) != null) {
            return new RegisterResult(null,null, "\"message\": \"Error: already taken\"");
        }

        UserData u = new UserData(r.username(), r.password(), r.email());
        userDAO.createUser(u);


        return null;
    }
}
