package handlers;

import client.requests.RegisterRequest;
import client.result.RegisterResult;
import dataaccess.DataAccessException;
import service.UserService;
import spark.Request;
import spark.Response;

public class RegisterHandler {
    private final JsonHandler jsonHandler = new JsonHandler();
    private final UserService userService = new UserService();

    public RegisterHandler() throws DataAccessException {
    }

    public String handleRequest(Request req, Response res) {
        try {
            RegisterRequest request = jsonHandler.fromJson(req, RegisterRequest.class);
            RegisterResult result = userService.register(request);

            if (result.message()!=null &&
                    result.message().contains("bad request")) {
                res.status(400);
            } else if (result.message()!=null &&
                    result.message().contains("already taken")) {
                res.status(403);
            } else if (result.authToken()!=null) {
                res.status(200);
            }
            return jsonHandler.toJson(result);

        } catch (Exception e) {
            res.status(500);
            throw new RuntimeException(e);
        }
    }
}
