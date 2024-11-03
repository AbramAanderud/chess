package service;

import dataaccess.*;
import model.GameData;
import requests.CreateGameRequest;
import requests.JoinRequest;
import requests.ListRequest;
import result.CreateGameResult;
import result.JoinResult;
import result.ListResult;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameService() throws DataAccessException {
        Connection connection = DatabaseManager.daoConnectors(); // Obtain a connection
        this.authDAO = new SQLAuthDAO(connection);
        this.gameDAO = new SQLGameDAO(connection);
    }

    public ListResult listGames(ListRequest req) throws DataAccessException {
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

    public CreateGameResult createGame(CreateGameRequest req, String authToken) throws DataAccessException {
        if (!authDAO.isValidAuth(authToken)) {
            return new CreateGameResult(null, "error: unauthorized");
        }
        if (req.gameName()==null) {
            return new CreateGameResult(null, "error: bad request");
        }

        GameData newGame = new GameData(null, null, null, req.gameName(), null);

        int newGameID = gameDAO.createGame(newGame);

        return new CreateGameResult(newGameID, null);
    }

    public JoinResult joinGame(JoinRequest req, String authToken) throws DataAccessException {
        System.out.println("trying to join from join game service");
        System.out.println("gameID requested " + req.gameID());

        if (!authDAO.isValidAuth(authToken)) {
            return new JoinResult("error: unauthorized");
        }

        if (req.gameID()==null || req.playerColor()==null
                || (!req.playerColor().equals("WHITE")
                && !req.playerColor().equals("BLACK"))) {
            return new JoinResult("error: bad request");
        }

        GameData gameData = gameDAO.getGame(req.gameID());
        System.out.println(gameData); //its getting null

        if (req.playerColor().equals("WHITE")) {
            if (gameData.whiteUsername()!=null) {
                return new JoinResult("error: already taken");
            }
        }
        if (req.playerColor().equals("BLACK")) {
            if (gameData.blackUsername()!=null) {
                return new JoinResult("error: already taken");
            }
        }

        String username = authDAO.getUsernameByAuth(authToken);

        gameDAO.joinGameRequest(req.gameID(), username, req.playerColor());

        return new JoinResult(null);
    }

}
