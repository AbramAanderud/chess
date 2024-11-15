package repl;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import chessclient.ChessClient;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private final ChessClient client;

    public Repl(String serverUrl) {
        client = new ChessClient(serverUrl);
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
            printPrompSignedIn();
            String line = scanner.nextLine();

            try {
                result = client.evalSignedIn(line);

                if (result.startsWith("Game joined") || result.contains("Observing game")) {
                    System.out.print(RESET_TEXT_COLOR);
                    System.out.println(result);
                    runPlayGame();
                } else {
                    if (result.contains("500")) {
                        System.out.print(SET_TEXT_COLOR_RED + SET_TEXT_ITALIC);
                        System.out.println("Doesn't exist");
                    } else if (result.contains("403")) {
                        System.out.print(SET_TEXT_COLOR_RED + SET_TEXT_ITALIC);
                        System.out.println("already taken");
                    } else if (result.contains("Expected")) {
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
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        System.out.print(RESET_TEXT_COLOR);
        System.out.print(RESET_BG_COLOR);
        System.out.print(RESET_TEXT_ITALIC);
        System.out.println(toStringBoard(board, true));
        System.out.println(toStringBoard(board, false));
    }

    private StringBuilder toStringBoard(ChessBoard board, boolean isWhite) {
        StringBuilder sb = new StringBuilder();

        sb.append(printTopAlpha(sb));

        if (isWhite) {
            for (int i = 8; i >= 1; i--) {
                sb.append(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE);
                sb.append(" " + i + " ");

                for (int j = 1; j <= 8; j++) {
                    appendPiece(board, sb, i, j);
                }
                sb.append(edges(sb, i));
            }
            sb.append(printTopAlpha(sb));
        } else {
            for (int i = 1; i <= 8; i++) {
                sb.append(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE);
                sb.append(" " + i + " ");

                for (int j = 1; j <= 8; j++) {
                    appendPiece(board, sb, i, j);
                }
                sb.append(edges(sb, i));
            }
            sb.append(printBottAlpha(sb));
        }

        return sb;
    }

    private StringBuilder edges(StringBuilder sb, int i) {
        sb.append(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE);
        sb.append(" " + i + " ");
        sb.append(RESET_BG_COLOR + RESET_TEXT_COLOR);
        sb.append("\n");
        return sb;
    }

    private void appendPiece(ChessBoard board, StringBuilder sb, int i, int j) {
        if ((i + j) % 2 == 0) {
            sb.append(SET_BG_COLOR_DARK_GREEN);
        } else {
            sb.append(SET_BG_COLOR_LIGHT_GREY);
        }

        ChessPosition pos = new ChessPosition(i, j);
        ChessPiece piece = board.getPiece(pos);
        sb.append(getPrintPiece(piece));
    }

    private StringBuilder printTopAlpha(StringBuilder sb) {
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
        return sb;
    }

    private StringBuilder printBottAlpha(StringBuilder sb) {
        sb.append(SET_BG_COLOR_BLACK + "   ");
        sb.append(" a " + SET_TEXT_COLOR_WHITE);
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
        return sb;
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

    private void printPrompSignedIn() {
        System.out.print(SET_TEXT_COLOR_WHITE + "\n[signed in] >>> " + SET_TEXT_COLOR_BLACK);
    }
}
