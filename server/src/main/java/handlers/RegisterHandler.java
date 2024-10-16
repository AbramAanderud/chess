package handlers;
import requests.RegisterRequest;
import result.RegisterResult;
import service.UserService;
import spark.*;

public class RegisterHandler {
    private final JsonHandler jsonHandler = new JsonHandler();
    private final UserService userService = new UserService();

    public String handleRequest(Request req, Response res) {
        RegisterRequest request = jsonHandler.fromJson(req, RegisterRequest.class);
        RegisterResult result = userService.register(request);

        return jsonHandler.toJson(result);
    }
}
