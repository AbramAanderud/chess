package handlers;

import requests.CreateGameRequest;
import result.CreateGameResult;
import service.GameService;
import spark.Request;
import spark.Response;

public class CreateGameHandler {
    private final JsonHandler jsonHandler = new JsonHandler();
    private final GameService gameService = new GameService();

    public String handleRequest(Request req, Response res) {
        try {
            String authToken = req.headers("authorization");

            System.out.println(authToken);

            CreateGameRequest request = jsonHandler.fromJson(req, CreateGameRequest.class);
            CreateGameResult result = gameService.createGame(request, authToken);

            System.out.println(request.gameName());

            if (result.message() != null && result.message().contains("bad request")) {
                res.status(400);
            } else if (result.message() != null && result.message().contains("unauthorized")) {
                res.status(401);
            } else if (result.gameID() != null) {
                res.status(200);
            }
            return jsonHandler.toJson(result);

        } catch (Exception e) {
            res.status(500);
            throw new RuntimeException(e);
        }
    }
}
