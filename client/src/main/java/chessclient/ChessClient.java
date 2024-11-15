package chessclient;

import repl.State;
import reqres.requests.*;
import reqres.result.*;
import serverfacade.ResponseException;
import serverfacade.ServerFacade;

import java.util.Arrays;

import static repl.State.SIGNEDIN;
import static repl.State.SIGNEDOUT;
import static ui.EscapeSequences.*;

public class ChessClient {
    private final ServerFacade serverFacade;
    private State state = SIGNEDOUT;
    private String currAuthToken;

    public ChessClient(String serverURL) {
        serverFacade = new ServerFacade(serverURL);
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
        if (params.length==2) {
            String username = params[0];
            String password = params[1];

            LoginRequest loginRequest = new LoginRequest(username, password);

            try {
                LoginResult loginResult = serverFacade.login(loginRequest);

                if (loginResult.message()!=null) {
                    return loginResult.message();
                }
                if (loginResult.authToken()!=null) {
                    currAuthToken = loginResult.authToken();
                    state = SIGNEDIN;
                    serverFacade.setLastStoredAuth(currAuthToken);
                    return String.format("Logged in as %s", username);
                }
            } catch (ResponseException e) {
                return "Error logging in";
            }
        }
        throw new ResponseException(400, "Expects <username> <passsword>");
    }

    public String register(String... params) throws ResponseException {
        if (params.length==3) {
            String username = params[0];
            String password = params[1];
            String email = params[2];

            RegisterRequest registerRequest = new RegisterRequest(username, password, email);

            try {
                RegisterResult registerResult = serverFacade.register(registerRequest);

                if (registerResult.message()!=null) {
                    return registerResult.message();
                }
                if (registerResult.authToken()!=null) {
                    currAuthToken = registerResult.authToken();
                    state = SIGNEDIN;
                    serverFacade.setLastStoredAuth(currAuthToken);
                    return String.format("Logged in as %s ", username);
                }
            } catch (ResponseException e) {
                return "Error signing in" + e.getMessage();
            }
        }
        throw new ResponseException(400, "Expects <username> <password> <email>");
    }

    public String create(String... params) throws ResponseException {
        if (params.length==1) {
            String gameName = params[0];

            CreateGameRequest createGameRequest = new CreateGameRequest(gameName);

            try {
                CreateGameResult gameResult = serverFacade.createGame(createGameRequest);

                if (gameResult.message()!=null) {
                    System.out.println(SET_TEXT_COLOR_RED);
                    return gameResult.message();
                }
                if (gameResult.gameID()!=null) {
                    return String.format("Created game named %s", gameName);
                }
            } catch (ResponseException e) {
                return "Error creating game" + e.getMessage();
            }
        }
        System.out.print(SET_TEXT_COLOR_RED);
        throw new ResponseException(400, "Expected: <gameName>");
    }

    public String list(String... params) throws ResponseException {
        if (params.length==0) {
            ListRequest listRequest = new ListRequest(currAuthToken);

            try {
                ListResult listResult = serverFacade.listGames(listRequest);

                if (!listResult.games().isEmpty() && listResult.message()==null) {
                    StringBuilder sb = new StringBuilder();
                    for (ListResult.GameInfo games : listResult.games()) {

                        sb.append(" * game: ").append(games.gameID()).append(" - name: ")
                                .append(games.gameName()).append(" - white player: " + SET_TEXT_COLOR_WHITE + SET_TEXT_BOLD)
                                .append(games.whiteUsername())
                                .append(RESET_TEXT_COLOR + " - black player: " + SET_TEXT_COLOR_WHITE + SET_TEXT_BOLD)
                                .append(games.blackUsername() + RESET_TEXT_COLOR);

                        sb.append("\n");
                    }
                    return "Games: \n" + sb;
                } else {
                    return "no games yet";
                }
            } catch (ResponseException e) {
                return "Error listing due to" + e.getMessage();
            }
        }
        throw new ResponseException(400, "Bad request");
    }

    public String join(String... params) throws ResponseException {
        if (params.length==2) {
            String gameIDString = params[0];
            String teamColor = params[1];

            int gameID = Integer.parseInt(gameIDString);

            JoinRequest joinRequest = new JoinRequest(teamColor, gameID);

            try {
                JoinResult joinResult = serverFacade.joinGame(joinRequest);


                if (joinResult.message()!=null) {
                    return "Error joining due to " + joinResult.message();
                } else {
                    return "Game joined \n";
                }
            } catch (ResponseException e) {
                return "Error joining game" + e.getMessage();
            }
        }
        throw new ResponseException(400, "Expected: <gameID> <[BLACK][WHITE]>");
    }

    public String observe(String... params) throws ResponseException {
        if (params.length==1) {
            return "Observing game: " + params[0];
        } else {
            throw new ResponseException(400, "Expected: <gameID>");
        }

    }

    public String logout(String... params) throws ResponseException {
        if (params.length==0) {

            LogoutRequest logoutRequest = new LogoutRequest(currAuthToken);

            try {
                LogoutResult logoutResult = serverFacade.logout(logoutRequest);

                if (logoutResult.message()==null) {
                    state = SIGNEDOUT;
                    return "Logged out";
                }
                return logoutResult.message();
            } catch (ResponseException e) {
                return "Can't logout" + e.getMessage();
            }
        }
        throw new ResponseException(400, "Bad request");
    }


    public String help() {
        if (state==State.SIGNEDOUT) {
            System.out.print(RESET_TEXT_COLOR);
            return """
                    Options:
                    register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                    login <USERNAME> <PASSWORD> - to play chess
                    quit - playing chess
                    help - with possible commands
                    """;
        }
        System.out.print(RESET_TEXT_COLOR);
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
