package service;

import dataaccess.UserDAO;
import requests.RegisterRequest;
import result.RegisterResult;

public class UserService {

    public RegisterResult register(RegisterRequest r) {
        UserDAO userData = new UserDAO();

        if (userData.getUser(r.username()) != null) {
            return new RegisterResult(null,null, "\"message\": \"Error: already taken\"");
        }


        return null;
    }
}
