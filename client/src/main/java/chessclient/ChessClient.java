package chessclient;

import chess.ChessMove;
import chess.ChessPosition;
import repl.State;
import client.requests.*;
import client.result.*;
import serverfacade.ResponseException;
import serverfacade.ServerFacade;
import websocket.messages.ServerMessage;
import websocketfacade.ServerMessageObserver;
import websocketfacade.WebSocketFacade;

import java.util.Arrays;

import static repl.State.*;
import static ui.EscapeSequences.*;

public class ChessClient implements ServerMessageObserver{
    private final ServerFacade serverFacade;
    private State state = SIGNEDOUT;
    private String currAuthToken;
    private String currTeamColor;
    private Integer currGameID;
    private final String serverURL;
    private WebSocketFacade ws;
    private final ServerMessageObserver serverMessageObserver;

    public ChessClient(String serverURL, ServerMessageObserver serverMessageObserver) {
        this.serverURL = serverURL;
        this.serverMessageObserver = serverMessageObserver;
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

    public String evalPlayGame(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0]:"help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "redraw chess board" -> drawBoard(params);
                case "leave" -> leave(params);
                case "make move" -> makeMove(params);
                case "resign" -> resign(params);
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }


    public String makeMove(String... params) throws ResponseException {
        if (params.length== 1) {
            String move = params[0];

            if (move.length() != 4) {
                throw new IllegalArgumentException("Format should be like e2e4");
            }

            char startColChar = move.charAt(0);
            char startRowChar = move.charAt(1);
            char endColChar = move.charAt(2);
            char endRowChar = move.charAt(3);

            int startCol = startColChar - 'a';
            int startRow = Character.getNumericValue(startRowChar) - 1;
            int endCol = endColChar - 'a';
            int endRow = Character.getNumericValue(endRowChar) - 1;

            ChessPosition startPos = new ChessPosition(startRow, startCol);
            ChessPosition endPos = new ChessPosition(endRow, endCol);
            ChessMove moveToMake = new ChessMove(startPos, endPos, null);

            ws = new WebSocketFacade(serverURL, serverMessageObserver);
            ws.makeMove(currAuthToken, currGameID, moveToMake);
            return "made move";
        }
        throw new ResponseException(400, "Bad request");
    }

    public String resign(String... params) throws ResponseException {
        if (params.length==0) {
            ws = new WebSocketFacade(serverURL, serverMessageObserver);
            ws.resign(currAuthToken, currGameID);
            state = SIGNEDIN;
            return "game resigned";
        }
        throw new ResponseException(400, "Bad request");
    }

    public String drawBoard(String... params) throws ResponseException {
        if (params.length==0) {
            return "draw board";
        }
        throw new ResponseException(400, "Bad request");
    }

    public String leave(String... params) throws ResponseException {
        if (params.length==0) {
            ws = new WebSocketFacade(serverURL, serverMessageObserver);
            ws.leave(currAuthToken, currGameID);
            state = SIGNEDIN;
            return "game left";
        }
        throw new ResponseException(400, "Bad request");
    }



    //from here down are my http

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
                return "Error logging in " + e.getMessage();
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
                return "Error signing in " + e.getMessage();
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
                                .append(games.blackUsername() + RESET_TEXT_COLOR + RESET_TEXT_BOLD_FAINT);

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
            int gameID;

            try {
                gameID = Integer.parseInt(gameIDString);
            } catch (NumberFormatException e) {
                return "join expects: <gameID> <[BLACK][WHITE]>";
            }

            JoinRequest joinRequest = new JoinRequest(teamColor, gameID);

            try {
                JoinResult joinResult = serverFacade.joinGame(joinRequest);
                if (joinResult.message()!=null) {
                    return "Error joining due to " + joinResult.message();
                } else {
                    state = PLAYINGGAME;
                    currGameID = gameID;
                    currTeamColor = teamColor;

                    ws = new WebSocketFacade(serverURL, serverMessageObserver);
                    ws.connect(currAuthToken, currGameID);

                    return "Game joined \n";
                }
            } catch (ResponseException e) {
                return e.getMessage();
            }
        }
        throw new ResponseException(400, "join expects: <gameID> <[BLACK][WHITE]>");
    }

    public String getCurrTeamColor() {
        return currTeamColor;
    }

    public void setStatusSignedIn() {
        state = SIGNEDIN;
    }

    public String observe(String... params) throws ResponseException {
        if (params.length == 1) {
            String gameIDString = params[0];
            int gameID;

            try {
                gameID = Integer.parseInt(gameIDString);
            } catch (NumberFormatException e) {
                return "observe expects: <gameID>";
            }
            currGameID = gameID;
            state = PLAYINGGAME;

            ws = new WebSocketFacade(serverURL, serverMessageObserver);
            ws.connect(currAuthToken, currGameID);

            return "Observing game: " + gameIDString;
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
        if (state== State.PLAYINGGAME) {
            System.out.print(RESET_TEXT_COLOR);
            return """
                    Options:
                    redraw - the chessboard
                    leave - the game
                    make move start and end <col><row><col><row> - to make a move
                    resign - to resign
                    highlight legal moves <CHESS PIECE> - to show possible moves
                    help - with possible commands
                    """;
        } if (state==State.SIGNEDOUT) {
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
                help - with possible commands
                """;
    }

    @Override
    public void notify(ServerMessage serverMessage) {
        System.out.println(serverMessage);
    }
}
