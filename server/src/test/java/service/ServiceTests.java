package service;

import dataaccess.DataAccessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.CreateGameRequest;
import requests.JoinRequest;
import requests.LoginRequest;
import requests.RegisterRequest;
import result.*;

public class ServiceTests {
    private GameService gameService;
    private UserService userService;
    private ClearService clearService;

    @BeforeEach
    public void setUp() {
        gameService = new GameService();
        userService = new UserService();
        clearService = new ClearService();
        clearService.clearAll();
    }

    @Test
    public void successCreateGame() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("TestUser", "password", "email");
        RegisterResult registerResult = userService.register(registerRequest);
        String validAuthToken = registerResult.authToken();

        CreateGameRequest request = new CreateGameRequest("New Game");

        CreateGameResult result = gameService.createGame(request, validAuthToken);

        Assertions.assertNotNull(result.gameID(), "GameID should have been created");
        Assertions.assertNull(result.message(), "Message should be null on success");
    }

    @Test
    public void badAuthCreateGame() throws DataAccessException {
        String invalidAuthToken = null;
        CreateGameRequest request = new CreateGameRequest("Chess Game");

        CreateGameResult result = gameService.createGame(request, invalidAuthToken);

        Assertions.assertNull(result.gameID(), "GameID should be null with a bad authToken");
        Assertions.assertEquals("error: unauthorized", result.message(), "should be error unauthorized access");
    }

    @Test
    public void successJoinGame() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("TestUser", "password", "email");
        RegisterResult registerResult = userService.register(registerRequest);
        String validAuthToken = registerResult.authToken();

        CreateGameRequest createGameRequest = new CreateGameRequest("Chess Game");
        CreateGameResult createGameResult = gameService.createGame(createGameRequest, validAuthToken);
        Integer gameID = createGameResult.gameID();

        Assertions.assertNotNull(gameID, "Game created");

        JoinRequest request = new JoinRequest("WHITE", gameID);
        JoinResult result = gameService.joinGame(request, validAuthToken);

        Assertions.assertNull(result.message(), "Message should be null on success");
    }

    @Test
    public void colorTakenJoinGame() throws DataAccessException {
        RegisterRequest firstUserRegisterRequest = new RegisterRequest("User1", "password", "email");
        RegisterResult firstUserRegisterResult = userService.register(firstUserRegisterRequest);
        String validAuthToken1 = firstUserRegisterResult.authToken();

        CreateGameRequest createGameRequest = new CreateGameRequest("Chess Game");
        CreateGameResult createGameResult = gameService.createGame(createGameRequest, validAuthToken1);
        Integer gameID = createGameResult.gameID();

        Assertions.assertNotNull(gameID, "Game created");

        JoinRequest firstUserJoinRequest = new JoinRequest("WHITE", gameID);
        JoinResult firstUserJoinResult = gameService.joinGame(firstUserJoinRequest, validAuthToken1);
        Assertions.assertNull(firstUserJoinResult.message(), "First join successful");

        RegisterRequest secondUserRegisterRequest = new RegisterRequest("User2", "password", "email2");
        RegisterResult secondUserRegisterResult = userService.register(secondUserRegisterRequest);
        String validAuthToken2 = secondUserRegisterResult.authToken();

        JoinRequest secondUserJoinRequest = new JoinRequest("WHITE", gameID);
        JoinResult secondUserJoinResult = gameService.joinGame(secondUserJoinRequest, validAuthToken2);

        Assertions.assertEquals("error: already taken", secondUserJoinResult.message(), "should get error already taken");
    }

    @Test
    public void successTestRegister() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("User", "password", "email");
        RegisterResult result = userService.register(request);

        Assertions.assertNotNull(result.authToken(), "AuthToken should not be null");
        Assertions.assertNull(result.message(), "Message should be null on success");
    }

    @Test
    public void userNameTakenRegister() throws DataAccessException {
        RegisterRequest firstUserRequest = new RegisterRequest("existingUser", "password", "email");
        userService.register(firstUserRequest);

        RegisterRequest secondUserRequest = new RegisterRequest("existingUser", "password2", "email2");
        RegisterResult result = userService.register(secondUserRequest);

        Assertions.assertNull(result.authToken(), "AuthToken should be null");
        Assertions.assertEquals("error: already taken", result.message(), "should be error already taken");

    }

    @Test
    public void successLogin() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("existingUser", "password", "email");
        userService.register(registerRequest);

        LoginRequest request = new LoginRequest("existingUser", "password");
        LoginResult result = userService.login(request);

        Assertions.assertNotNull(result.authToken(), "authToken should not be null");
        Assertions.assertNull(result.message(), "Message should be null");
    }

    @Test
    public void unauthorizedLogin() throws DataAccessException {
        LoginRequest request = new LoginRequest("unregisteredUser", "password");

        LoginResult result = userService.login(request);

        Assertions.assertNull(result.authToken(), "AuthToken should be null");
        Assertions.assertEquals("error: unauthorized", result.message(), "should be error unauthorized access");
    }

    @Test
    public void successClearAll() throws DataAccessException {
        ClearResult result = clearService.clearAll();
        Assertions.assertNull(result.message(), "Message should be null on success");
    }


}
