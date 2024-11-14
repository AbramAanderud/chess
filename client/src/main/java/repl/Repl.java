package repl;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
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
                if(result.startsWith("Logged in as")) {
                    runSignedIn();
                }
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    public void runSignedIn() {
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.evalSignedIn(line);
                System.out.print(SET_TEXT_COLOR_WHITE + result);
                if(result.startsWith("Logged out")) {
                    runUnsignedIn();
                } else if (result.startsWith("Game joined") || (result.startsWith("observing game")) ){
                    runPlayGame();
                }
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    public void runPlayGame() {
        ChessBoard board = new ChessBoard();
        System.out.println(toStringBoard(board));

    }

    private StringBuilder toStringBoard(ChessBoard board) {
        StringBuilder sb = new StringBuilder();

        sb.append("    " + SET_BG_COLOR_BLACK);
        sb.append(" a  " + SET_TEXT_COLOR_WHITE);
        sb.append(" b ");
        sb.append(" c ");
        sb.append(" d ");
        sb.append(" e ");
        sb.append(" f ");
        sb.append(" g ");
        sb.append(" h ");
        sb.append("   \n");

        for (int i = 7; i >= 0; i--) {
            for (int j = 0; j < 8; j++) {
                sb.append(" " + i + " ");
                ChessPiece piece = new ChessPiece()
                sb.append(" " + getPrintPiece(ChessBoard[]))
            }
        }

        return sb;
    }

    private String getPrintPiece(ChessPiece piece) {
        if (piece == null) {
            return "   ";
        }

        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            return " " + " ";
        } else {
            return Character.toLowerCase(firstInitial);
        }

    }

    private void printPrompt() {
        System.out.print("\n" + SET_TEXT_COLOR_BLACK);
    }
}
