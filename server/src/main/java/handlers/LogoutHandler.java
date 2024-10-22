package handlers;

import requests.LogoutRequest;
import result.LogoutResult;
import service.UserService;
import spark.Request;
import spark.Response;

public class LogoutHandler {
    private final JsonHandler jsonHandler = new JsonHandler();
    private final UserService userService = new UserService();

    public String handleRequest(Request req, Response res) {
        try {

            String authToken = req.headers("authorization");

            System.out.println(authToken);

            LogoutRequest request = new LogoutRequest(authToken);
            LogoutResult result = userService.logout(request);

            if (result.message() != null && result.message().contains("unauthorized")) {
                res.status(401);
            } else if (result.message() == null) {
                res.status(200);
            }
            return jsonHandler.toJson(result);

        } catch (Exception e) {
            res.status(500);
            throw new RuntimeException(e);
        }
    }
}
