package service;

import dataaccess.DataAccessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import requests.CreateGameRequest;
import result.CreateGameResult;

public class ServiceTests {

    @Test
    public void testCreateGame_Success() throws DataAccessException {
        // Arrange
        String validAuthToken = "validAuth";
        CreateGameRequest request = new CreateGameRequest("Chess Game");

        // Act
        CreateGameResult result = gameService.createGame(request, validAuthToken);

        // Assert
        Assertions.assertNotNull(result.getGameID(), "GameID should not be null when game creation succeeds");
        Assertions.assertNull(result.getMessage(), "Message should be null on successful creation");
    }

    @Test
    public void testCreateGame_Unauthorized() throws DataAccessException {
        // Arrange
        String invalidAuthToken = "invalidAuth";
        CreateGameRequest request = new CreateGameRequest("Chess Game");

        // Act
        CreateGameResult result = gameService.createGame(request, invalidAuthToken);

        // Assert
        Assertions.assertNull(result.getGameID(), "GameID should be null on failure");
        Assertions.assertEquals("error: unauthorized", result.getMessage(), "Error message should indicate unauthorized access");
    }

    @Test
    public void testJoinGame_Success() throws DataAccessException {
        // Arrange
        String validAuthToken = "validAuth";
        JoinRequest request = new JoinRequest(1234, "WHITE");

        // Act
        JoinResult result = gameService.joinGame(request, validAuthToken);

        // Assert
        Assertions.assertNull(result.getMessage(), "Message should be null when joining succeeds");
    }

    @Test
    public void testJoinGame_AlreadyTaken() throws DataAccessException {
        // Arrange
        String validAuthToken = "validAuth";
        JoinRequest request = new JoinRequest(1234, "WHITE"); // Assuming WHITE is already taken

        // Act
        JoinResult result = gameService.joinGame(request, validAuthToken);

        // Assert
        Assertions.assertEquals("error: already taken", result.getMessage(), "Error message should indicate that the color is already taken");
    }




}
