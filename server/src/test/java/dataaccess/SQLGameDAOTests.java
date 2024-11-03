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
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

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
        GameData gameData = new GameData(0, "TestUser1", "TestUser2", "Game1", new ChessGame());
        int gameID = gameDAO.createGame(gameData);
        Assertions.assertTrue(gameID > 0, "Game ID should be greater than 0");
    }

    @Test
    public void failureCreateGame() {
        GameData gameData = new GameData(0, null, null, "", new ChessGame());

        Exception exception = Assertions.assertThrows(DataAccessException.class, () -> {
            gameDAO.createGame(gameData);
        });

        String expectedMessage = "Failed to create new game";
        Assertions.assertTrue(exception.getMessage().contains(expectedMessage), "Expected DataAccessException message not found");
    }





}
