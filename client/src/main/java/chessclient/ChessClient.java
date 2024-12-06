package chessclient;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import client.requests.*;
import client.result.*;
import com.google.gson.Gson;
import model.GameData;
import repl.State;
import serverfacade.ResponseException;
import serverfacade.ServerFacade;
import websocketfacade.ServerMessageObserver;
import websocketfacade.WebSocketFacade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import static repl.State.*;
import static ui.EscapeSequences.*;

public class ChessClient {
    private final ServerFacade serverFacade;
    private final String serverURL;
    private final ServerMessageObserver serverMessageObserver;
    String currMove;
    private GameData currentGameData;
    private State state = SIGNEDOUT;
    private String currAuthToken;
    private String currTeamColor;
    private Integer currGameID;
    private WebSocketFacade ws;
    private Collection<ChessMove> legalMoves = new ArrayList<>();


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
            var tokens = input.toLowerCase().trim().split("\\s+");
            if (tokens.length==0) {
                return help();
            }

            String cmd;
            String[] params;

            if (input.toLowerCase().startsWith("make move")) {
                cmd = "make move";
                params = Arrays.copyOfRange(tokens, 2, tokens.length);
            } else if (input.toLowerCase().startsWith("highlight legal moves")) {
                cmd = "highlight legal moves";
                params = Arrays.copyOfRange(tokens, 3, tokens.length);
            } else {
                cmd = tokens[0];
                params = Arrays.copyOfRange(tokens, 1, tokens.length);
            }

            return switch (cmd) {
                case "redraw" -> drawBoard(params);
                case "leave" -> leave(params);
                case "make move" -> makeMove(params);
                case "resign" -> resign(params);
                case "highlight legal moves" -> highlightLegalMoves(params);
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String makeMove(String... params) throws ResponseException {
        if (params.length==1) {
            String move = params[0];

            if (move.length()!=4 && move.length()!=5) {
                throw new IllegalArgumentException("Format should be like e2e4 e7e8q for promotion");
            }

            char startColChar = move.charAt(0);
            char startRowChar = move.charAt(1);
            char endColChar = move.charAt(2);
            char endRowChar = move.charAt(3);

            if (startColChar < 'a' || startColChar > 'h' || endColChar < 'a' || endColChar > 'h') {
                throw new IllegalArgumentException("Columns must be letters between 'a' and 'h'");
            }
            if (startRowChar < '1' || startRowChar > '8' || endRowChar < '1' || endRowChar > '8') {
                throw new IllegalArgumentException("Rows must be numbers between '1' and '8'");
            }

            int startCol = startColChar - 'a';
            int startRow = Character.getNumericValue(startRowChar);
            int endCol = endColChar - 'a';
            int endRow = Character.getNumericValue(endRowChar);

            startCol = startCol + 1;
            endCol = endCol + 1;

            ChessPosition startPos = new ChessPosition(startRow, startCol);
            ChessPosition endPos = new ChessPosition(endRow, endCol);

            ChessPiece.PieceType promotionPiece = null;

            if (move.length()==5) {
                char promotionChar = move.charAt(4);
                promotionPiece = getPromotionPieceType(promotionChar);
                if (promotionPiece==null) {
                    throw new IllegalArgumentException("Invalid promotion piece type must be q, r, b, or n");
                }
            }

            ChessMove moveToMake = new ChessMove(startPos, endPos, promotionPiece);

            currMove = move;

            this.ws = new WebSocketFacade(serverURL, serverMessageObserver);
            ws.makeMove(currAuthToken, currGameID, moveToMake);

            return "Move made";
        }
        throw new ResponseException(400, "Make move expects: make move e2e4");
    }

    private ChessPiece.PieceType getPromotionPieceType(char promotionChar) {
        return switch (Character.toLowerCase(promotionChar)) {
            case 'q' -> ChessPiece.PieceType.QUEEN;
            case 'r' -> ChessPiece.PieceType.ROOK;
            case 'b' -> ChessPiece.PieceType.BISHOP;
            case 'n' -> ChessPiece.PieceType.KNIGHT;
            default -> null;
        };
    }

    public String highlightLegalMoves(String... params) throws ResponseException {
        if (params.length==1) {
            String move = params[0];

            if (move.length()!=2) {
                throw new IllegalArgumentException("Format should be like e2");
            }

            char startColChar = move.charAt(0);
            char startRowChar = move.charAt(1);

            int startCol = startColChar - 'a';
            int startRow = Character.getNumericValue(startRowChar);
            startCol = startCol + 1;

            ChessPosition startPos = new ChessPosition(startRow, startCol);
            currentGameData = getCurrentGameData();

            if (currentGameData==null) {
                throw new ResponseException(400, "No game data available");
            }

            ChessGame game = currentGameData.game();
            Collection<ChessMove> possibleMoves = game.validMoves(startPos);

            if (possibleMoves==null || possibleMoves.isEmpty()) {
                return "No valid moves available for the given position";
            }

            setLegalMoves(possibleMoves);
            return "Highlighted";
        }
        throw new ResponseException(400, "Highlight legal move expects: highlight legal moves e2");
    }

    public Collection<ChessMove> getLegalMoves() {
        return legalMoves;
    }

    public void setLegalMoves(Collection<ChessMove> moves) {
        legalMoves = moves;
    }

    public String resign(String... params) throws ResponseException {
        if (params.length==0) {
            ws = new WebSocketFacade(serverURL, serverMessageObserver);
            ws.resign(currAuthToken, currGameID);
            return "game resigned";
        }
        throw new ResponseException(400, "Bad request");
    }

    public String drawBoard(String... params) throws ResponseException {
        if (params.length==0) {
            currentGameData = getCurrentGameData();
            Gson gson = new Gson();
            return gson.toJson(currentGameData);
        }
        throw new ResponseException(400, "Bad request");
    }

    public String leave(String... params) throws ResponseException {
        if (params.length==0) {
            ws = new WebSocketFacade(serverURL, serverMessageObserver);
            ws.leave(currAuthToken, currGameID, Objects.equals(currTeamColor, "observer"));

            state = SIGNEDIN;
            return "game left \n";
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
                    ws.connect(currAuthToken, currGameID, currTeamColor, false);

                    return "Game joined \n";
                }
            } catch (ResponseException e) {
                return e.getMessage();
            }
        }
        throw new ResponseException(400, "join expects: <gameID> <[BLACK][WHITE]>");
    }

    public String observe(String... params) throws ResponseException {
        if (params.length==1) {
            String gameIDString = params[0];
            int gameID;

            try {
                gameID = Integer.parseInt(gameIDString);
            } catch (NumberFormatException e) {
                return "observe expects: <gameID>";
            }
            currGameID = gameID;
            state = PLAYINGGAME;
            currTeamColor = "observer";

            ws = new WebSocketFacade(serverURL, serverMessageObserver);
            ws.connect(currAuthToken, currGameID, null, true);

            return "Observing game: " + gameIDString + "\n";
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
        System.out.print(RESET_TEXT_COLOR);
        if (state==State.PLAYINGGAME) {
            return """
                    Options:
                    redraw - the chessboard
                    leave - the game
                    make move <start<col><row>><end<col><row>> - to make a move
                    resign - to resign
                    highlight legal moves <col><row> - to show possible moves
                    help - with possible commands
                    """;
        }
        if (state==State.SIGNEDOUT) {
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

    public String getUserColorOrObserver() {
        return currTeamColor;
    }

    public GameData getCurrentGameData() {
        return currentGameData;
    }

    public void setCurrentGameData(GameData gameData) {
        this.currentGameData = gameData;
    }

}
