package repl;

import chess.*;
import chessclient.ChessClient;
import com.google.gson.Gson;
import model.GameData;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;
import websocketfacade.ServerMessageObserver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl implements ServerMessageObserver {
    private final ChessClient client;

    public Repl(String serverUrl) {
        client = new ChessClient(serverUrl, this);
    }

    public void runUnsignedIn() {
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            System.out.print(RESET_TEXT_ITALIC);
            printPromptUnsignedIn();
            String line = scanner.nextLine();

            try {
                result = client.evalUnsignedIn(line);

                if (result.startsWith("Logged in as")) {
                    System.out.println(SET_TEXT_COLOR_WHITE + result + " ");
                    runSignedIn();
                } else if (!result.startsWith("Options")) {
                    if (result.contains("401")) {
                        System.out.print(SET_TEXT_COLOR_RED + SET_TEXT_ITALIC);
                        System.out.println("Can't find user with that username/password");

                    } else if (result.contains("403")) {
                        System.out.print(SET_TEXT_COLOR_RED + SET_TEXT_ITALIC);
                        System.out.println("username taken");
                    } else {
                        System.out.print(SET_TEXT_COLOR_RED + SET_TEXT_ITALIC);
                        System.out.println(result);
                        System.out.print(RESET_TEXT_COLOR);
                    }

                } else {
                    System.out.print(RESET_TEXT_COLOR + result);
                }

            } catch (Throwable e) {
                System.out.print(SET_TEXT_ITALIC + SET_TEXT_COLOR_BLUE);
                System.out.print(e.getMessage());
            }
        }
        System.exit(0);
    }

    public void runSignedIn() {
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.startsWith("Logged out")) {
            System.out.print(RESET_TEXT_ITALIC);
            printPromptSignedIn();
            String line = scanner.nextLine();

            try {
                result = client.evalSignedIn(line);

                if (result.startsWith("Game joined")) {
                    System.out.print(SET_TEXT_COLOR_WHITE);
                    System.out.print(result + RESET_TEXT_COLOR);
                    runPlayGame();
                } else if (result.contains("Observing game")) {
                    System.out.print(RESET_TEXT_COLOR);
                    System.out.print(result);
                    runPlayGame();
                } else {
                    if (result.contains("500")) {
                        System.out.print(SET_TEXT_COLOR_RED + SET_TEXT_ITALIC);
                        System.out.println("Doesn't exist");
                    } else if (result.contains("403")) {
                        System.out.print(SET_TEXT_COLOR_RED + SET_TEXT_ITALIC);
                        System.out.println("already taken");
                    } else if (result.contains("400")) {
                        System.out.print(SET_TEXT_COLOR_RED + SET_TEXT_ITALIC);
                        System.out.println("Invalid color");
                    } else if (result.contains("Expected") || result.contains("expects")) {
                        System.out.print(SET_TEXT_COLOR_RED + SET_TEXT_ITALIC);
                        System.out.println(result);
                        System.out.print(RESET_TEXT_COLOR);
                    } else {
                        System.out.print(RESET_TEXT_COLOR);
                        System.out.println(result);
                    }
                }
            } catch (Throwable e) {
                System.out.print(SET_TEXT_ITALIC + SET_TEXT_COLOR_BLUE);
                System.out.print(e.getMessage());
            }
        }
        runUnsignedIn();
    }

    public void runPlayGame() {
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";

        while (!result.contains("left")) {
            System.out.print(RESET_TEXT_ITALIC);
            printPromptPlayingGame();
            String line = scanner.nextLine();

            try {
                result = client.evalPlayGame(line);

                if (result.contains("squares")) {
                    String teamColor = client.getUserColorOrObserver();
                    Gson gson = new Gson();
                    GameData gameData = gson.fromJson(result, GameData.class);
                    ChessBoard gameBoard = gameData.game().getBoard();

                    if (Objects.equals(teamColor, "white")) {
                        System.out.println(toStringBoard(gameBoard, true, new ArrayList<>(), null));
                    } else if (Objects.equals(teamColor, "black")) {
                        System.out.println(toStringBoard(gameBoard, false, new ArrayList<>(), null));
                    } else if (Objects.equals(teamColor, "observer")) {
                        System.out.println(toStringBoard(gameBoard, true, new ArrayList<>(), null));
                    }
                } else if (result.startsWith("Highlighted")) {
                    Gson gson = new Gson();

                    Collection<ChessMove> moves = client.getLegalMoves();
                    Collection<ChessPosition> endPositions = new ArrayList<>();

                    ChessPosition startPos = null;

                    for (ChessMove move : moves) {
                        endPositions.add(move.getEndPosition());
                        startPos = move.getStartPosition();
                    }

                    System.out.println("ends " + endPositions);

                    String gameStateJson = client.drawBoard();
                    GameData gameData = gson.fromJson(gameStateJson, GameData.class);
                    ChessBoard gameBoard = gameData.game().getBoard();
                    String teamColor = client.getUserColorOrObserver();

                    if (Objects.equals(teamColor, "white")) {
                        System.out.println(toStringBoard(gameBoard, true, endPositions, startPos));
                    } else if (Objects.equals(teamColor, "black")) {
                        System.out.println(toStringBoard(gameBoard, false, endPositions, startPos));
                    } else if (Objects.equals(teamColor, "observer")) {
                        System.out.println(toStringBoard(gameBoard, true, endPositions, startPos));
                    }
                } else {
                    System.out.println(SET_TEXT_COLOR_WHITE + result + RESET_TEXT_COLOR);
                }
            } catch (Throwable e) {
                System.out.print(SET_TEXT_ITALIC + SET_TEXT_COLOR_RED + "Error: ");
                System.out.println(e.getMessage());
            }
        }
        runSignedIn();
    }

    private StringBuilder toStringBoard(ChessBoard board, boolean isWhite, Collection<ChessPosition> positions, ChessPosition startPosition) {
        StringBuilder sb = new StringBuilder();

        if (isWhite) {
            printTopAlphaWHITE(sb);
            for (int i = 8; i >= 1; i--) {
                pieceLoop(sb, board, i, true, positions, startPosition);
            }
            printBottAlphaWHITE(sb);
        } else {
            printTopAlphaBLACK(sb);
            for (int i = 1; i <= 8; i++) {
                pieceLoop(sb, board, i, false, positions, startPosition);
            }
            printBottAlphaBlACK(sb);
        }
        return sb;
    }

    private void pieceLoop(StringBuilder sb, ChessBoard board, int i, boolean isWhite,
                           Collection<ChessPosition> positions, ChessPosition startPosition) {
        sb.append(SET_BG_COLOR_BLACK).append(SET_TEXT_COLOR_WHITE).append(" ").append(i).append(" ");

        if (isWhite) {
            for (int j = 1; j <= 8; j++) {
                appendPiece(board, sb, i, j, positions, startPosition);
            }
        } else {
            for (int j = 8; j >= 1; j--) {
                appendPiece(board, sb, i, j, positions, startPosition);
            }
        }

        sb.append(SET_BG_COLOR_BLACK).append(SET_TEXT_COLOR_WHITE).append(" ").append(i).append(" ");
        sb.append(RESET_BG_COLOR).append(RESET_TEXT_COLOR).append("\n");
    }

    private void appendPiece(ChessBoard board, StringBuilder sb, int i, int j, Collection<ChessPosition> positions, ChessPosition startPosition) {
        ChessPosition pos = new ChessPosition(i, j);
        if ((i + j) % 2==0) {
            sb.append(SET_BG_COLOR_DARK_GREEN);
            if (positions.contains(pos)) {
                sb.append(SET_BG_COLOR_MEDIUM_DARK_GREEN);
            }
        } else {
            sb.append(SET_BG_COLOR_LIGHT_GREY);
            if (positions.contains(pos)) {
                sb.append(SET_BG_COLOR_LIGHTER_GREY);
            }
        }

        if (startPosition!=null && startPosition.equals(pos)) {
            sb.append(SET_BG_COLOR_YELLOW);
        }

        ChessPiece piece = board.getPiece(pos);
        sb.append(getPrintPiece(piece));
    }

    private void printTopAlphaWHITE(StringBuilder sb) {
        sb.append(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE);
        sb.append("    a ");
        sb.append(" b ");
        sb.append(" c ");
        sb.append(" d ");
        sb.append(" e ");
        sb.append(" f ");
        sb.append(" g ");
        sb.append(" h ");
        sb.append("   ");
        sb.append(RESET_BG_COLOR + RESET_TEXT_COLOR);
        sb.append("\n");
    }

    private void printBottAlphaWHITE(StringBuilder sb) {
        sb.append(SET_BG_COLOR_BLACK + "   " + SET_TEXT_COLOR_WHITE);
        sb.append(" a ");
        sb.append(" b ");
        sb.append(" c ");
        sb.append(" d ");
        sb.append(" e ");
        sb.append(" f ");
        sb.append(" g ");
        sb.append(" h ");
        sb.append("   ");
        sb.append(RESET_BG_COLOR + RESET_TEXT_COLOR);
        sb.append("\n");
    }

    private void printTopAlphaBLACK(StringBuilder sb) {
        sb.append(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE);
        sb.append("    h ");
        sb.append(" g ");
        sb.append(" f ");
        sb.append(" e ");
        sb.append(" d ");
        sb.append(" c ");
        sb.append(" b ");
        sb.append(" a ");
        sb.append("   ");
        sb.append(RESET_BG_COLOR + RESET_TEXT_COLOR);
        sb.append("\n");
    }

    private void printBottAlphaBlACK(StringBuilder sb) {
        sb.append(SET_BG_COLOR_BLACK + "   " + SET_TEXT_COLOR_WHITE);
        sb.append(" h ");
        sb.append(" g ");
        sb.append(" f ");
        sb.append(" e ");
        sb.append(" d ");
        sb.append(" c ");
        sb.append(" b ");
        sb.append(" a ");
        sb.append("   ");
        sb.append(RESET_BG_COLOR + RESET_TEXT_COLOR);
        sb.append("\n");
    }

    private String getPrintPiece(ChessPiece piece) {
        if (piece==null) {
            return "   ";
        }
        ChessPiece.PieceType pieceType = piece.getPieceType();
        String pieceDesign = getReturnPiece(pieceType);

        if (piece.getTeamColor()==ChessGame.TeamColor.WHITE) {
            return SET_TEXT_COLOR_WHITE + pieceDesign + RESET_TEXT_COLOR;
        } else {
            return SET_TEXT_COLOR_BLACK + pieceDesign + RESET_TEXT_COLOR;
        }

    }

    private String getReturnPiece(ChessPiece.PieceType pieceType) {
        if (pieceType==ChessPiece.PieceType.KING) {
            return BLACK_KING;
        } else if (pieceType==ChessPiece.PieceType.QUEEN) {
            return BLACK_QUEEN;
        } else if (pieceType==ChessPiece.PieceType.ROOK) {
            return BLACK_ROOK;
        } else if (pieceType==ChessPiece.PieceType.PAWN) {
            return BLACK_PAWN;
        } else if (pieceType==ChessPiece.PieceType.BISHOP) {
            return BLACK_BISHOP;
        } else if (pieceType==ChessPiece.PieceType.KNIGHT) {
            return BLACK_KNIGHT;
        }
        return "";
    }

    private void printPromptUnsignedIn() {
        System.out.print(SET_TEXT_COLOR_WHITE + "\n[signed out] >>> " + SET_TEXT_COLOR_BLACK);
    }

    private void printPromptSignedIn() {
        System.out.print(SET_TEXT_COLOR_WHITE + "\n[signed in] >>> " + SET_TEXT_COLOR_BLACK);
    }

    private void printPromptPlayingGame() {
        System.out.print(SET_TEXT_COLOR_WHITE + "\n[in game] >>> " + SET_TEXT_COLOR_BLACK);
    }


    public void notify(ServerMessage message) {
        if (message instanceof LoadGameMessage loadGameMessage) {
            GameData gameData = loadGameMessage.getGameData();
            client.setCurrentGameData(gameData);
            ChessBoard gameBoard = gameData.game().getBoard();
            ChessGame.TeamColor turn = gameData.game().getTeamTurn();
            String teamColor = client.getUserColorOrObserver();

            System.out.print(RESET_TEXT_COLOR);
            System.out.print(RESET_BG_COLOR);
            System.out.print(RESET_TEXT_ITALIC + "\n");

            if (Objects.equals(teamColor, "white")) {
                System.out.println(toStringBoard(gameBoard, true, new ArrayList<>(), null));
                System.out.print(turn + " to move");
            } else if (Objects.equals(teamColor, "black")) {
                System.out.println(toStringBoard(gameBoard, false, new ArrayList<>(), null));
                System.out.print(turn + " to move");
            } else if (Objects.equals(teamColor, "observer")) {
                System.out.println(toStringBoard(gameBoard, true, new ArrayList<>(), null));
                System.out.println("observing");
            }
        } else if (message instanceof NotificationMessage notificationMessage) {
            System.out.println(SET_TEXT_COLOR_BLUE + notificationMessage.getMessage());

        } else if (message instanceof ErrorMessage errorMessage) {
            System.out.println(SET_TEXT_COLOR_RED + SET_TEXT_ITALIC + errorMessage.getMessage());
            System.out.print(RESET_TEXT_ITALIC);
        }
        printPromptPlayingGame();
    }
}
