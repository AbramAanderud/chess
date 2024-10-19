package handlers;
import requests.RegisterRequest;
import result.RegisterResult;
import service.UserService;
import spark.*;

public class RegisterHandler {
    private final JsonHandler jsonHandler = new JsonHandler();
    private final UserService userService = new UserService();

    public String handleRequest(Request req, Response res) {
        try {
            RegisterRequest request = jsonHandler.fromJson(req, RegisterRequest.class);
            RegisterResult result = userService.register(request);

            if(result.authToken() != null) {
                res.status(200);
                return jsonHandler.toJson(result);
            } else if(result.message() != null && result.message().contains("already taken")) {
                res.status(403);
            } else {
                res.status(400);
            }
            return jsonHandler.toJson(result);

        } catch (Exception e) {
            res.status(500);
            throw new RuntimeException(e);
        }
    }
}
