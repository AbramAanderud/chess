package handlers;

import dataaccess.DataAccessException;
import requests.JoinRequest;
import result.JoinResult;
import service.GameService;
import spark.Request;
import spark.Response;

public class JoinGameHandler {
    private final JsonHandler jsonHandler = new JsonHandler();
    private final GameService gameService = new GameService();

    public JoinGameHandler() throws DataAccessException {
    }

    public String handleRequest(Request req, Response res) {
        try {
            String authToken = req.headers("authorization");

            System.out.println(authToken);

            JoinRequest request = jsonHandler.fromJson(req, JoinRequest.class);
            JoinResult result = gameService.joinGame(request, authToken);


            if (result.message()!=null &&
                    result.message().contains("bad request")) {
                res.status(400);
            } else if (result.message()!=null &&
                    result.message().contains("unauthorized")) {
                res.status(401);
            } else if (result.message()!=null &&
                    result.message().contains("already taken")) {
                res.status(403);
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
