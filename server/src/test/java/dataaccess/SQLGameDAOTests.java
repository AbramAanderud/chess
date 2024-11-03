package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.Collection;


public class SQLGameDAOTests {
    private SQLGameDAO gameDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        Connection connection = DatabaseManager.getConnection();
        gameDAO = new SQLGameDAO(connection);
        gameDAO.clearAllGameData();
    }

    @Test
    public void successCreateGame() throws DataAccessException {
        ChessGame game = new ChessGame();
        GameData gameData = new GameData(0, "whitePlayer", "blackPlayer", "testGame", game);
        int gameID = gameDAO.createGame(gameData);

        Assertions.assertTrue(gameID > 0, "Game ID should be greater than 0 and exist");
    }

    @Test
    public void failCreateGameWithInvalidData() {
        GameData gameData = new GameData(0, null, null, null, null);

        Assertions.assertThrows(DataAccessException.class, () -> {
            gameDAO.createGame(gameData);
        }, "Making a game with null name should give DataAccessException");
    }

    @Test
    public void successGetGame() throws DataAccessException {
        ChessGame game = new ChessGame();
        GameData gameData = new GameData(0, "whitePlayer", "blackPlayer", "testGame", game);
        int gameID = gameDAO.createGame(gameData);

        GameData retrievedGame = gameDAO.getGame(gameID);
        Assertions.assertNotNull(retrievedGame, "Game should not be null");
        Assertions.assertEquals("testGame", retrievedGame.gameName(), "Game name should match");
    }

    @Test
    public void failGetGameWithInvalidID() throws DataAccessException {
        GameData retrievedGame = gameDAO.getGame(-1);
        Assertions.assertNull(retrievedGame, "Should give null since isn't a real ID");
    }

    @Test
    public void successListGames() throws DataAccessException {
        ChessGame game1 = new ChessGame();
        GameData gameData1 = new GameData(0, "whitePlayer1", "blackPlayer1", "testGame", game1);
        gameDAO.createGame(gameData1);

        ChessGame game2 = new ChessGame();
        GameData gameData2 = new GameData(0, "whitePlayer2", "blackPlayer2", "testGame2", game2);
        gameDAO.createGame(gameData2);

        Collection<GameData> games = gameDAO.listGames();
        Assertions.assertEquals(2, games.size(), "Should be 2 games in the list");
    }

    @Test
    public void failListGamesWhenNoGamesExist() throws DataAccessException {
        Collection<GameData> games = gameDAO.listGames();
        Assertions.assertTrue(games.isEmpty(), "The list of games should be empty none have been made");
    }

    @Test
    public void successJoinGameRequest() throws DataAccessException {
        ChessGame game = new ChessGame();
        GameData gameData = new GameData(0, null, "blackPlayer", "justBlack", game);
        int gameID = gameDAO.createGame(gameData);

        gameDAO.joinGameRequest(gameID, "newPlayerWhite", "WHITE");

        GameData updatedGame = gameDAO.getGame(gameID);
        Assertions.assertEquals("newPlayerWhite", updatedGame.whiteUsername(), "White player should be the player we added");
    }

    @Test
    public void failJoinGameRequestWithInvalidColor() throws DataAccessException {
        ChessGame game = new ChessGame();
        GameData gameData = new GameData(0, null, "BlackPlayer", "testGame", game);
        int gameID = gameDAO.createGame(gameData);

        Assertions.assertThrows(DataAccessException.class, () -> {
            gameDAO.joinGameRequest(gameID, "newPlayerBlue", "BLUE");
        }, "Joining a game with a non chess color should give DataAccessException");
    }

    @Test
    public void successClearAllGameData() throws DataAccessException {
        ChessGame game1 = new ChessGame();
        GameData gameData1 = new GameData(0, "whitePlayer1", "blackPlayer1", "testGame", game1);
        gameDAO.createGame(gameData1);

        ChessGame game2 = new ChessGame();
        GameData gameData2 = new GameData(0, "whitePlayer2", "blackPlayer2", "testGame2", game2);
        gameDAO.createGame(gameData2);

        gameDAO.clearAllGameData();

        Collection<GameData> games = gameDAO.listGames();
        Assertions.assertTrue(games.isEmpty(), "All games should be cleared");
    }

    @Test
    public void successUpdateGameAfterMove() throws DataAccessException, InvalidMoveException {
        ChessGame initialGame = new ChessGame();
        GameData gameData = new GameData(0, "whitePlayer", "blackPlayer", "TestGame", initialGame);
        int gameID = gameDAO.createGame(gameData);

        ChessMove move = new ChessMove(new ChessPosition(2, 5), new ChessPosition(4, 5), null);
        initialGame.makeMove(move);
        gameDAO.updateGame(gameID, initialGame);

        GameData updatedGameData = gameDAO.getGame(gameID);
        ChessGame retrievedGame = updatedGameData.game();

        Assertions.assertNull(retrievedGame.getBoard().getPiece(new ChessPosition(2, 5)), "Original position e2 should be empty");
        Assertions.assertNotNull(retrievedGame.getBoard().getPiece(new ChessPosition(4, 5)), "New position e4 should have that piece");
    }

    @Test
    public void successJoinGameAndMakeMoves() throws DataAccessException, InvalidMoveException {
        ChessGame initialGame = new ChessGame();
        GameData gameData = new GameData(0, null, null, "JoinAndPlay", initialGame);
        int gameID = gameDAO.createGame(gameData);

        gameDAO.joinGameRequest(gameID, "misterWhite", "WHITE");
        gameDAO.joinGameRequest(gameID, "sirBlack", "BLACK");

        GameData joinedGameData = gameDAO.getGame(gameID);
        Assertions.assertEquals("misterWhite", joinedGameData.whiteUsername(), "White player should be 'WhitePlayer'.");
        Assertions.assertEquals("sirBlack", joinedGameData.blackUsername(), "Black player should be 'BlackPlayer'.");

        ChessMove firstMove = new ChessMove(new ChessPosition(2, 5), new ChessPosition(4, 5), null);
        ChessMove secondMove = new ChessMove(new ChessPosition(7, 5), new ChessPosition(5, 5), null);
        initialGame.makeMove(firstMove);
        initialGame.makeMove(secondMove);

        gameDAO.updateGame(gameID, initialGame);

        GameData updatedGameData = gameDAO.getGame(gameID);
        ChessGame retrievedGame = updatedGameData.game();

        Assertions.assertNull(retrievedGame.getBoard().getPiece(new ChessPosition(2, 5)), "e2 should be empty");
        Assertions.assertNotNull(retrievedGame.getBoard().getPiece(new ChessPosition(4, 5)), "e4 should have White piece");
        Assertions.assertNull(retrievedGame.getBoard().getPiece(new ChessPosition(7, 5)), "e7 should be empty after Black move");
        Assertions.assertNotNull(retrievedGame.getBoard().getPiece(new ChessPosition(5, 5)), "e5 should have Black piece");
    }

    @Test
    public void failUpdateGameWithInvalidID() {
        ChessGame updatedGame = new ChessGame();
        ChessMove move = new ChessMove(new ChessPosition(2, 5), new ChessPosition(4, 5), null);

        Assertions.assertThrows(DataAccessException.class, () -> {
            updatedGame.makeMove(move);
            gameDAO.updateGame(-1, updatedGame);
        }, "Updating a game using invalid game ID should throw a DataAccessException");
    }

    @Test
    public void failUpdateGameWithInvalidMove() throws DataAccessException {
        ChessGame initialGame = new ChessGame();
        GameData gameData = new GameData(0, "whitePlayer", "blackPlayer", "InvalidMoveGame", initialGame);
        int gameID = gameDAO.createGame(gameData);

        ChessMove invalidMove = new ChessMove(new ChessPosition(2, 5), new ChessPosition(1, 5), null);

        Assertions.assertThrows(InvalidMoveException.class, () -> {
            initialGame.makeMove(invalidMove);
        }, "Invalid move should result in a InvalidMoveException");

        GameData retrievedGameData = gameDAO.getGame(gameID);
        ChessGame retrievedGame = retrievedGameData.game();

        Assertions.assertEquals(initialGame, retrievedGame, "The game state in the database should remain unchanged after an invalid move");
    }


}
