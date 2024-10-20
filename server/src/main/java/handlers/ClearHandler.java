package handlers;

import requests.RegisterRequest;
import result.ClearResult;
import result.RegisterResult;
import service.ClearService;
import service.UserService;
import spark.Request;
import spark.Response;

public class ClearHandler {
    private final JsonHandler jsonHandler = new JsonHandler();
    private final ClearService clearService = new ClearService();

    public String handleRequest(Request req, Response res) {
        ClearResult result = clearService.clearAll();

        if(result.message() != null) {
            res.status(500);
            return jsonHandler.toJson(result);
        } else {
            res.status(200);
            return jsonHandler.toJson(result);
        }
    }
}
