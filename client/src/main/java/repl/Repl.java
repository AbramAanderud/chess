package repl;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import chessClient.ChessClient;

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
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.evalUnsignedIn(line);
                System.out.print(SET_TEXT_COLOR_WHITE + result);
                if (result.startsWith("Logged in as")) {
                    runSignedIn();
                }
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
        System.exit(0);
    }

    public void runSignedIn() {
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.startsWith("Logged out")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.evalSignedIn(line);
                System.out.print(SET_TEXT_COLOR_WHITE + result);
                if (result.startsWith("Game joined") || (result.startsWith("observing game"))) {
                    runPlayGame();
                }
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        runUnsignedIn();
        System.out.println();
    }

    public void runPlayGame() {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        System.out.println(toStringBoardWhite(board));
        System.out.println(toStringBoardBlack(board));

    }

    private StringBuilder toStringBoardWhite(ChessBoard board) {
        StringBuilder sb = new StringBuilder();

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

        for (int i = 8; i >= 1; i--) {
            sb.append(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE);
            sb.append(" " + (i) + " ");

            for (int j = 1; j <= 8; j++) {
                if ((i + j) % 2==0) {
                    sb.append(SET_BG_COLOR_DARK_GREEN);
                } else {
                    sb.append(SET_BG_COLOR_LIGHT_GREY);
                }

                ChessPosition pos = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(pos);
                sb.append(getPrintPiece(piece));

            }

            sb.append(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE);
            sb.append(" " + (i) + " ");
            sb.append(RESET_BG_COLOR + RESET_TEXT_COLOR);
            sb.append("\n");

        }

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

    private StringBuilder toStringBoardBlack(ChessBoard board) {
        StringBuilder sb = new StringBuilder();

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

        for (int i = 1; i <= 8; i++) {
            sb.append(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE);
            sb.append(" " + (i) + " ");

            for (int j = 1; j <= 8; j++) {
                if ((i + j) % 2==0) {
                    sb.append(SET_BG_COLOR_DARK_GREEN);
                } else {
                    sb.append(SET_BG_COLOR_LIGHT_GREY);
                }

                ChessPosition pos = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(pos);
                sb.append(getPrintPiece(piece));

            }

            sb.append(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE);
            sb.append(" " + (i) + " ");
            sb.append(RESET_BG_COLOR + RESET_TEXT_COLOR);
            sb.append("\n");

        }

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

    private void printPrompt() {
        System.out.print("\n" + SET_TEXT_COLOR_BLACK);
    }
}
