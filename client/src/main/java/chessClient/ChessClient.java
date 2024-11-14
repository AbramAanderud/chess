package chessClient;

import dataaccess.DataAccessException;
import repl.State;
import requests.*;
import result.*;
import serverfacade.ResponseException;
import serverfacade.ServerFacade;

import java.util.Arrays;

import static repl.State.SIGNEDIN;
import static repl.State.SIGNEDOUT;
import static ui.EscapeSequences.RESET_TEXT_COLOR;

public class ChessClient {
    private final ServerFacade server;
    private State state = SIGNEDOUT;
    private String currAuthToken;

    public ChessClient(String serverURL) {
        server = new ServerFacade(serverURL);
    }

    public String evalUnsignedIn(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0]:"help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String evalSignedIn(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0]:"help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "create" -> create(params);
                case "list" -> list();
                case "join" -> join(params);
                case "observe" -> observe(params);
                case "logout" -> logout(params);
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String login(String... params) throws ResponseException {
        if (params.length >= 2) {
            String username = params[0];
            String password = params[1];

            LoginRequest loginRequest = new LoginRequest(username, password);

            try {
                LoginResult loginResult = server.login(loginRequest);

                if (loginResult.authToken()!=null) {
                    currAuthToken = loginResult.authToken();
                    state = SIGNEDIN;
                    server.setLastStoredAuth(currAuthToken);
                    return String.format("Logged in as %s \n", username);
                } else {
                    return "Login failed: " + (loginResult.message());
                }
            } catch (DataAccessException e) {
                return "Error logging in";
            }
        }
        throw new ResponseException(400, "Expected: <yourname>");
    }

    public String register(String... params) throws ResponseException {
        if (params.length >= 3) {
            String username = params[0];
            String password = params[1];
            String email = params[2];

            RegisterRequest registerRequest = new RegisterRequest(username, password, email);

            try {
                RegisterResult registerResult = server.register(registerRequest);

                if (registerResult.authToken()!=null) {
                    currAuthToken = registerResult.authToken();
                    state = SIGNEDIN;
                    server.setLastStoredAuth(currAuthToken);
                    return String.format("Logged in as %s \n", username);
                } else {
                    return "Login failed: " + (registerResult.message());
                }
            } catch (DataAccessException e) {
                return "Error signing in" + e.getMessage();
            }
        }
        throw new ResponseException(400, "Expected: <yourname>");
    }

    public String create(String... params) throws ResponseException {
        if (params.length >= 1) {
            String gameName = params[0];

            CreateGameRequest createGameRequest = new CreateGameRequest(gameName);

            try {
                CreateGameResult gameResult = server.createGame(createGameRequest);

                if (gameResult.gameID()!=null) {
                    return String.format("Created game named %s", gameName);
                } else {
                    return "Creation failed: " + (gameResult.message());
                }
            } catch (DataAccessException e) {
                return "Error creating game" + e.getMessage();
            }
        }
        throw new ResponseException(400, "Expected: <gameName>");
    }

    public String list(String... params) throws ResponseException {
        if (params.length==0) {
            ListRequest listRequest = new ListRequest(currAuthToken);

            try {
                ListResult listResult = server.listGames(listRequest);

                if (listResult.games()!=null) {
                    StringBuilder sb = new StringBuilder();

                    for (ListResult.GameInfo games : listResult.games()) {

                        sb.append(" * game: " + games.gameID() + " - name: "
                                + games.gameName() + " - white player: "
                                + games.whiteUsername() + " - black player: "
                                + games.blackUsername());

                        sb.append("\n");
                    }
                    return "Games: \n" + sb;

                } else {
                    return "No games yet";
                }
            } catch (DataAccessException e) {
                return "Error listing" + e.getMessage();
            }
        }
        return null;
    }

    public String join(String... params) throws ResponseException {
        if (params.length >= 2) {
            String gameIDString = params[0];
            String teamColor = params[1];

            int gameID = Integer.parseInt(gameIDString);

            JoinRequest joinRequest = new JoinRequest(teamColor, gameID);

            try {
                JoinResult joinResult = server.joinGame(joinRequest);

                if (joinResult.message()!=null) {
                    return String.format("Error joining due to %s", joinResult.message());
                } else {
                    return "Game joined \n";
                }
            } catch (DataAccessException e) {
                return "Error joining game" + e.getMessage();
            }
        }
        throw new ResponseException(400, "Expected: <gameID> <[BLACK][WHITE]>");
    }

    public String observe(String... params) throws ResponseException {
        if (params.length >= 1) {
            return "observing game " + params[0] + '\n';
        } else {
            return "Bad Arguments";
        }
    }

    public String logout(String... params) throws ResponseException {
        if (params.length==0) {

            LogoutRequest logoutRequest = new LogoutRequest(currAuthToken);

            try {
                LogoutResult logoutResult = server.logout(logoutRequest);

                if (logoutResult.message()==null) {
                    state = SIGNEDOUT;
                    return "Logged out \n" ;

                }
            } catch (DataAccessException e) {
                return "Error signing in" + e.getMessage();
            }
        }
        throw new ResponseException(400, "Expected: <yourname>");
    }


    public String help() {
        System.out.println(RESET_TEXT_COLOR);
        if (state==State.SIGNEDOUT) {
            return """
                    Options:
                    register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                    login <USERNAME> <PASSWORD> - to play chess
                    quit - playing chess
                    help - with possible commands
                    """;
        }
        System.out.println(RESET_TEXT_COLOR);
        return """
                Options:
                create <NAME> - a game
                list - games
                join <ID> [WHITE|BLACK] - a game
                observe <ID> - a game
                logout - when you are done
                quit - playing chess
                help - with possible commands
                """;
    }
}
