package handlers;

import result.ClearResult;
import service.ClearService;
import spark.Response;

public class ClearHandler {
    private final JsonHandler jsonHandler = new JsonHandler();
    private final ClearService clearService = new ClearService();

    public String handleRequest(Response res) {
        ClearResult result = clearService.clearAll();

        if (result.message() != null) {
            res.status(500);
        } else {
            res.status(200);
        }
        return jsonHandler.toJson(result);
    }
}
