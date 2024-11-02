package handlers;

import dataaccess.DataAccessException;
import requests.LoginRequest;
import result.LoginResult;
import service.UserService;
import spark.Request;
import spark.Response;

public class LoginHandler {
    private final JsonHandler jsonHandler = new JsonHandler();
    private final UserService userService = new UserService();

    public LoginHandler() throws DataAccessException {
    }

    public String handleRequest(Request req, Response res) {
        try {
            LoginRequest request = jsonHandler.fromJson(req, LoginRequest.class);
            LoginResult result = userService.login(request);

            if (result.message() != null &&
                    result.message().contains("unauthorized")) {
                res.status(401);
            } else if (result.authToken() != null) {
                res.status(200);
            }
            return jsonHandler.toJson(result);

        } catch (Exception e) {
            res.status(500);
            throw new RuntimeException(e);
        }
    }

}
