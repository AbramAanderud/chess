package service;

import chess.ChessGame;
import dataaccess.*;
import model.GameData;
import client.requests.CreateGameRequest;
import client.requests.JoinRequest;
import client.requests.ListRequest;
import client.result.CreateGameResult;
import client.result.JoinResult;
import client.result.ListResult;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

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

        ChessGame game = new ChessGame();

        GameData newGame = new GameData(null, null, null, req.gameName(), game);

        int newGameID = gameDAO.createGame(newGame);

        return new CreateGameResult(newGameID, null);
    }

    public JoinResult joinGame(JoinRequest req, String authToken) throws DataAccessException {
        System.out.println("gameID requested " + req.gameID());
        if (req.playerColor()==null) {
            return new JoinResult("error: bad request");
        }

        String playerColor = req.playerColor();
        playerColor = playerColor.toUpperCase();

        System.out.println("color requested " + playerColor);

        if (!authDAO.isValidAuth(authToken)) {
            return new JoinResult("error: unauthorized");
        }

        if (req.gameID()==null || !playerColor.equals("WHITE") && !playerColor.equals("BLACK")) {
            return new JoinResult("error: bad request");
        }
        String username = authDAO.getUsernameByAuth(authToken);

        GameData gameData = gameDAO.getGame(req.gameID());
        System.out.println(gameData);

        if (playerColor.equals("WHITE")) {
            System.out.println("reached");
            if (gameData.whiteUsername()!=null) {
                return new JoinResult("error: already taken");
            }
        }
        if (playerColor.equals("BLACK")) {
            if (gameData.blackUsername()!=null) {
                return new JoinResult("error: already taken");
            }
        }


        gameDAO.joinGameRequest(req.gameID(), username, playerColor);

        return new JoinResult(null);
    }

}
