package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import model.GameData;
import requests.CreateGameRequest;
import requests.JoinRequest;
import requests.ListRequest;
import result.CreateGameResult;
import result.JoinResult;
import result.ListResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GameService {
    private static int ogGameID = 1111;
    private final AuthDAO authDAO = MemoryAuthDAO.getInstance();
    private final GameDAO gameDAO = MemoryGameDAO.getInstance();

    public ListResult listGames(ListRequest req) {
        if (!authDAO.isValidAuth(req.authToken())) {
            return new ListResult(null, "error: unauthorized");
        }

        Collection<GameData> gameData = gameDAO.listGames();
        List<ListResult.GameInfo> gameResults = new ArrayList<>();

        for (GameData currData : gameData) {
            gameResults.add(new ListResult.GameInfo(
                    currData.gameID(),
                    currData.whiteUsername(),
                    currData.blackUsername(),
                    currData.gameName())
            );
        }
        return new ListResult(gameResults, null);
    }

    public CreateGameResult createGame(CreateGameRequest req, String authToken) {
        if (!authDAO.isValidAuth(authToken)) {
            return new CreateGameResult(null, "error: unauthorized");
        }
        if (req.gameName() == null) {
            return new CreateGameResult(null, "error: bad request");
        }

        int newID = ++ogGameID;
        GameData newGame = new GameData(newID, null, null, req.gameName(), null);

        gameDAO.createGame(newGame);

        return new CreateGameResult(newID, null);
    }

    public JoinResult joinGame(JoinRequest req, String authToken) {
        if (!authDAO.isValidAuth(authToken)) {
            return new JoinResult("error: unauthorized");
        }
        if (req.gameID() == null || req.playerColor() == null
                || (!req.playerColor().equals("WHITE")
                && !req.playerColor().equals("BLACK"))) {
            return new JoinResult("error: bad request");
        }

        GameData gameData = gameDAO.getGame(req.gameID());

        if (req.playerColor().equals("WHITE")) {
            if (gameData.whiteUsername() != null) {
                return new JoinResult("error: already taken");
            }
        }
        if (req.playerColor().equals("BLACK")) {
            if (gameData.blackUsername() != null) {
                return new JoinResult("error: already taken");
            }
        }

        String username = authDAO.getUsernameByAuth(authToken);

        gameDAO.joinGameRequest(req.gameID(), username, req.playerColor());

        return new JoinResult(null);
    }

}
