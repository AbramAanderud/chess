package client;

import dataaccess.DataAccessException;
import org.junit.jupiter.api.*;
import client.requests.*;
import client.result.*;
import server.Server;
import serverfacade.ResponseException;
import serverfacade.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {
    private static ServerFacade facade;
    private static Server server;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clearDatabase() throws ResponseException, DataAccessException {
        facade.clear();
    }

    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    public void successRegister() throws DataAccessException, ResponseException {
        RegisterRequest request = new RegisterRequest("TestUser", "password", "email");
        RegisterResult result = facade.register(request);

        assertNotNull(result.authToken(), "AuthToken should not be null for successful registration");
        assertNull(result.message(), "Message should be null on success");

        facade.setLastStoredAuth(result.authToken());
    }

    @Test
    public void duplicateUserRegister() {
        RegisterRequest request = new RegisterRequest("TestUser", "password", "email");
        assertDoesNotThrow(() -> facade.register(request));

        ResponseException exception = assertThrows(ResponseException.class, () -> {
            facade.register(request);
        });

        assertEquals("failure: 403", exception.getMessage(), "Exception message should indicate duplicate registration");
    }

    @Test
    public void successLogin() throws DataAccessException, ResponseException {
        RegisterRequest registerRequest = new RegisterRequest("TestUser", "password", "email");
        facade.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("TestUser", "password");
        LoginResult result = facade.login(loginRequest);

        assertNotNull(result.authToken(), "AuthToken should not be null for successful login");
        assertNull(result.message(), "Message should be null on success");

        facade.setLastStoredAuth(result.authToken());
    }

    @Test
    public void unauthorizedLogin() {
        LoginRequest request = new LoginRequest("unknownUser", "password");

        ResponseException exception = assertThrows(ResponseException.class, () -> {
            facade.login(request);
        });

        assertEquals("failure: 401", exception.getMessage(), "Exception message should indicate unauthorized login");
    }

    @Test
    public void successCreateGame() throws DataAccessException, ResponseException {
        RegisterRequest registerRequest = new RegisterRequest("TestUser", "password", "email");
        RegisterResult registerResult = facade.register(registerRequest);
        facade.setLastStoredAuth(registerResult.authToken());

        CreateGameRequest request = new CreateGameRequest("New Game");
        CreateGameResult result = facade.createGame(request);

        assertNotNull(result.gameID(), "GameID should not be null for successful game creation");
        assertNull(result.message(), "Message should be null on success");
    }

    @Test
    public void badAuthCreateGame() {
        facade.setLastStoredAuth(null);
        CreateGameRequest request = new CreateGameRequest("New Game");

        ResponseException exception = assertThrows(ResponseException.class, () -> {
            facade.createGame(request);
        });
        assertEquals("failure: 401", exception.getMessage(), "Exception message should indicate unauthorized access");
    }

    @Test
    public void successListGames() throws DataAccessException, ResponseException {
        RegisterRequest registerRequest = new RegisterRequest("TestUser", "password", "email");
        RegisterResult registerResult = facade.register(registerRequest);
        facade.setLastStoredAuth(registerResult.authToken());

        CreateGameRequest createGameRequest = new CreateGameRequest("New Game");
        facade.createGame(createGameRequest);

        ListRequest listRequest = new ListRequest(registerResult.authToken());
        ListResult result = facade.listGames(listRequest);

        assertNotNull(result.games(), "Games list should not be null on success");
        assertFalse(result.games().isEmpty(), "There should be at least one game listed");
        assertNull(result.message(), "Message should be null on success");
    }

    @Test
    public void unauthorizedListGames() {
        facade.setLastStoredAuth("invalidToken");

        ListRequest request = new ListRequest("invalidToken");

        ResponseException exception = assertThrows(ResponseException.class, () -> {
            facade.listGames(request);
        });

        assertEquals("failure: 401", exception.getMessage(), "Exception message should indicate unauthorized access");
    }

    @Test
    public void successLogout() throws DataAccessException, ResponseException {
        RegisterRequest registerRequest = new RegisterRequest("TestUser", "password", "email");
        RegisterResult registerResult = facade.register(registerRequest);
        facade.setLastStoredAuth(registerResult.authToken());

        LogoutRequest logoutRequest = new LogoutRequest(registerResult.authToken());
        LogoutResult result = facade.logout(logoutRequest);

        assertNull(result.message(), "Message should be null on successful logout");
    }

    @Test
    public void unauthorizedLogout() {
        facade.setLastStoredAuth("invalidToken");

        LogoutRequest request = new LogoutRequest("invalidToken");

        ResponseException exception = assertThrows(ResponseException.class, () -> {
            facade.logout(request);
        });

        assertEquals("failure: 401", exception.getMessage(), "Exception message should indicate unauthorized access");
    }

    @Test
    public void clearDatabaseSuccess() throws DataAccessException, ResponseException {
        ClearResult result = facade.clear();
        assertNull(result.message(), "Message should be null on successful clear");
    }
}