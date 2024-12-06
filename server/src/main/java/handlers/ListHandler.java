package handlers;

import client.requests.ListRequest;
import client.result.ListResult;
import dataaccess.DataAccessException;
import service.GameService;
import spark.Request;
import spark.Response;

public class ListHandler {
    private final JsonHandler jsonHandler = new JsonHandler();
    private final GameService gameService = new GameService();

    public ListHandler() throws DataAccessException {
    }

    public String handleRequest(Request req, Response res) {
        try {
            String authToken = req.headers("authorization");

            ListRequest request = new ListRequest(authToken);
            ListResult result = gameService.listGames(request);

            if (result.message()!=null &&
                    result.message().contains("unauthorized")) {
                res.status(401);
            } else if (result.message()==null) {
                res.status(200);
            }
            return jsonHandler.toJson(result);

        } catch (Exception e) {
            res.status(500);
            throw new RuntimeException(e);
        }
    }
}
