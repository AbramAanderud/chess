package chessClient;

import com.sun.nio.sctp.NotificationHandler;
import dataaccess.DataAccessException;
import repl.State;
import requests.LoginRequest;
import requests.RegisterRequest;
import result.LoginResult;
import serverfacade.ResponseException;
import serverfacade.ServerFacade;

import java.util.Arrays;

import static repl.State.*;

public class ChessClient {
    private final ServerFacade server;
    private State state = SIGNEDOUT;

    public ChessClient(String serverURL) {
        server = new ServerFacade(serverURL);
    }

    public String evalUnsignedIn(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
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

    public String login(String... params) throws ResponseException {
        if (params.length >= 2) {
            String username = params[0];
            String password = params[1];

            LoginRequest loginRequest = new LoginRequest(username, password);

            try {
                LoginResult loginResult = server.login(loginRequest);

                if (loginResult.authToken() != null) {
                    state = SIGNEDIN;
                    return String.format("You signed in as %s.", username);
                } else {
                    return "Login failed: " + (loginResult.message());
                }
            } catch (DataAccessException e) {
                return "Error signing in";
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
                
            }
            state = SIGNEDIN;

            return String.format("You signed in as %s.", params[0]);
        }
        throw new ResponseException(400, "Expected: <yourname>");
    }



    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    Options:
                    register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                    login <USERNAME> <PASSWORD> - to play chess
                    quit - playing chess
                    help - with possible commands
                    """;
        }
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
