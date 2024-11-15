package service;

import dataaccess.DataAccessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import client.requests.*;
import client.result.*;

public class ServiceTests {
    private GameService gameService;
    private UserService userService;
    private ClearService clearService;

    @BeforeEach
    public void setUp() throws DataAccessException {
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
        RegisterRequest firstUserRegisterRequest = new RegisterRequest("TestUser", "password", "email");
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
    public void listGamesValidToken() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("TestUser", "password", "email");
        userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("TestUser", "password");
        LoginResult loginResult = userService.login(loginRequest);
        String validAuthToken = loginResult.authToken();

        CreateGameRequest createGameRequest = new CreateGameRequest("Game 1");
        gameService.createGame(createGameRequest, validAuthToken);

        ListRequest listRequest = new ListRequest(validAuthToken);
        ListResult listResult = gameService.listGames(listRequest);
        Assertions.assertNotNull(listResult.games(), "Games should not be null");
        Assertions.assertFalse(listResult.games().isEmpty(), "There should be at least one game listed");
    }

    @Test
    public void listGamesInvalidToken() throws DataAccessException {
        ListRequest listRequest = new ListRequest("invalidToken");
        ListResult listResult = gameService.listGames(listRequest);
        Assertions.assertNull(listResult.games(), "Games should be null with an invalid token");
        Assertions.assertEquals("error: unauthorized", listResult.message(), "should be error unauthorized");
    }

    @Test
    public void invalidGameIdJoinGame() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("TestUser", "password", "email");
        userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("TestUser", "password");
        LoginResult loginResult = userService.login(loginRequest);
        String validAuthToken = loginResult.authToken();

        JoinRequest joinRequest = new JoinRequest("WHITE", null);
        JoinResult joinResult = gameService.joinGame(joinRequest, validAuthToken);
        Assertions.assertEquals("error: bad request", joinResult.message(), "should be error bad request");
    }

    @Test
    public void nullColorJoinGame() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("TestUser", "password", "email");
        userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("TestUser", "password");
        LoginResult loginResult = userService.login(loginRequest);
        String validAuthToken = loginResult.authToken();

        CreateGameRequest createGameRequest = new CreateGameRequest("Game 1");
        CreateGameResult createGameResult = gameService.createGame(createGameRequest, validAuthToken);
        Integer gameID = createGameResult.gameID();

        JoinRequest joinRequest = new JoinRequest(null, gameID);
        JoinResult joinResult = gameService.joinGame(joinRequest, validAuthToken);
        Assertions.assertEquals("error: bad request", joinResult.message(), "should be error bad request");
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
    public void nullUsernameRegister() throws DataAccessException {
        RegisterRequest request = new RegisterRequest(null, "password", "email");
        RegisterResult result = userService.register(request);
        Assertions.assertNull(result.authToken(), "AuthToken should be null");
        Assertions.assertEquals("error: bad request", result.message(), "should be error bad request");
    }

    @Test
    public void nullPasswordRegister() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("TestUser", null, "email");
        RegisterResult result = userService.register(request);
        Assertions.assertNull(result.authToken(), "AuthToken should be null");
        Assertions.assertEquals("error: bad request", result.message(), "should be error bad request");
    }

    @Test
    public void nullEmailRegister() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("TestUser", "password", null);
        RegisterResult result = userService.register(request);
        Assertions.assertNull(result.authToken(), "AuthToken should be null");
        Assertions.assertEquals("error: bad request", result.message(), "should be error bad request");
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
    public void nullUsernameLogin() throws DataAccessException {
        LoginRequest request = new LoginRequest(null, "password");
        LoginResult result = userService.login(request);
        Assertions.assertNull(result.authToken(), "AuthToken should be null");
        Assertions.assertEquals("error: unauthorized", result.message(), "should be error unauthorized");
    }

    @Test
    public void nullPasswordLogin() throws DataAccessException {
        LoginRequest request = new LoginRequest("existingUser", null);
        LoginResult result = userService.login(request);
        Assertions.assertNull(result.authToken(), "AuthToken should be null");
        Assertions.assertEquals("error: unauthorized", result.message(), "should be error unauthorized");
    }

    @Test
    public void successfulLogout() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("TestUser", "password", "email");
        userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("TestUser", "password");
        LoginResult loginResult = userService.login(loginRequest);
        String validAuthToken = loginResult.authToken();

        LogoutRequest logoutRequest = new LogoutRequest(validAuthToken);
        LogoutResult logoutResult = userService.logout(logoutRequest);
        Assertions.assertNull(logoutResult.message(), "Message should be null on successful logout");
    }

    @Test
    public void unauthorizedLogout() throws DataAccessException {
        LogoutRequest request = new LogoutRequest("invalidToken");
        LogoutResult result = userService.logout(request);
        Assertions.assertEquals("error: unauthorized", result.message(), "should be error unauthorized");
    }

    @Test
    public void successClearAll() throws DataAccessException {
        ClearResult result = clearService.clearAll();
        Assertions.assertNull(result.message(), "Message should be null on success");
    }


}
