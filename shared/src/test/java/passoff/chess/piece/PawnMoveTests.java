package passoff.chess.piece;

import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.ChessPositionBuilder;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static passoff.chess.TestUtilities.loadBoard;
import static passoff.chess.TestUtilities.validateMoves;

public class PawnMoveTests {

    @Test
    public void pawnMiddleOfBoardWhite() {
        validateMoves("""
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | |P| | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        """,
                new ChessPositionBuilder().setRow(4).setCol(4).createChessPosition(),
                new int[][]{{5, 4}}
        );
    }

    @Test
    public void pawnMiddleOfBoardBlack() {
        validateMoves("""
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | |p| | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        """,
                new ChessPositionBuilder().setRow(4).setCol(4).createChessPosition(),
                new int[][]{{3, 4}}
        );
    }


    @Test
    public void pawnInitialMoveWhite() {
        validateMoves("""
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | |P| | | |
                        | | | | | | | | |
                        """,
                new ChessPositionBuilder().setRow(2).setCol(5).createChessPosition(),
                new int[][]{{3, 5}, {4, 5}}
        );
    }

    @Test
    public void pawnInitialMoveBlack() {
        validateMoves("""
                        | | | | | | | | |
                        | | |p| | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        """,
                new ChessPositionBuilder().setRow(7).setCol(3).createChessPosition(),
                new int[][]{{6, 3}, {5, 3}}
        );
    }


    @Test
    public void pawnPromotionWhite() {
        validatePromotion("""
                        | | | | | | | | |
                        | | |P| | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        """,
                new ChessPositionBuilder().setRow(7).setCol(3).createChessPosition(),
                new int[][]{{8, 3}}
        );
    }


    @Test
    public void edgePromotionBlack() {
        validatePromotion("""
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | |p| | | | | |
                        | | | | | | | | |
                        """,
                new ChessPositionBuilder().setRow(2).setCol(3).createChessPosition(),
                new int[][]{{1, 3}}
        );
    }


    @Test
    public void pawnPromotionCapture() {
        validatePromotion("""
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | |p| | | | | | |
                        |N| | | | | | | |
                        """,
                new ChessPositionBuilder().setRow(2).setCol(2).createChessPosition(),
                new int[][]{{1, 1}, {1, 2}}
        );
    }


    @Test
    public void pawnAdvanceBlockedWhite() {
        validateMoves("""
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | |n| | | | |
                        | | | |P| | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        """,
                new ChessPositionBuilder().setRow(4).setCol(4).createChessPosition(),
                new int[][]{}
        );
    }

    @Test
    public void pawnAdvanceBlockedBlack() {
        validateMoves("""
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | |p| | | | |
                        | | | |r| | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        """,
                new ChessPositionBuilder().setRow(5).setCol(4).createChessPosition(),
                new int[][]{}
        );
    }


    @Test
    public void pawnAdvanceBlockedDoubleMoveWhite() {
        validateMoves("""
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | |p| |
                        | | | | | | | | |
                        | | | | | | |P| |
                        | | | | | | | | |
                        """,
                new ChessPositionBuilder().setRow(2).setCol(7).createChessPosition(),
                new int[][]{{3, 7}}
        );
    }

    @Test
    public void pawnAdvanceBlockedDoubleMoveBlack() {
        validateMoves("""
                        | | | | | | | | |
                        | | |p| | | | | |
                        | | |p| | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        """,
                new ChessPositionBuilder().setRow(7).setCol(3).createChessPosition(),
                new int[][]{}
        );
    }


    @Test
    public void pawnCaptureWhite() {
        validateMoves("""
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | |r| |N| | | |
                        | | | |P| | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        """,
                new ChessPositionBuilder().setRow(4).setCol(4).createChessPosition(),
                new int[][]{{5, 3}, {5, 4}}
        );
    }

    @Test
    public void pawnCaptureBlack() {
        validateMoves("""
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | |p| | | | |
                        | | | |n|R| | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        """,
                new ChessPositionBuilder().setRow(4).setCol(4).createChessPosition(),
                new int[][]{{3, 5}}
        );
    }

    private void validatePromotion(String boardText, ChessPosition startingPosition, int[][] endPositions) {
        var board = loadBoard(boardText);
        var testPiece = board.getPiece(startingPosition);
        var validMoves = new HashSet<ChessMove>();
        for (var endPosition : endPositions) {
            var end = new ChessPositionBuilder().setRow(endPosition[0]).setCol(endPosition[1]).createChessPosition();
            validMoves.add(new ChessMove(startingPosition, end, ChessPiece.PieceType.QUEEN));
            validMoves.add(new ChessMove(startingPosition, end, ChessPiece.PieceType.BISHOP));
            validMoves.add(new ChessMove(startingPosition, end, ChessPiece.PieceType.ROOK));
            validMoves.add(new ChessMove(startingPosition, end, ChessPiece.PieceType.KNIGHT));
        }

        validateMoves(board, testPiece, startingPosition, validMoves);
    }

}
