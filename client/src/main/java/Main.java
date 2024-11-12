import chess.*;
import repl.Repl;
import serverfacade.ServerFacade;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        var serverURL = "http://localhost:";

        new Repl(serverURL).runUnsignedIn();

    }
}