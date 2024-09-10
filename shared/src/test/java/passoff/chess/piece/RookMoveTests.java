package passoff.chess.piece;

import chess.ChessPositionBuilder;
import org.junit.jupiter.api.Test;

import static passoff.chess.TestUtilities.validateMoves;

public class RookMoveTests {

    @Test
    public void rookMoveUntilEdge() {

        validateMoves("""
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | |R| | | | | |
                        | | | | | | | | |
                        """,
                new ChessPositionBuilder().setRow(2).setCol(3).createChessPosition(),
                new int[][]{
                        {2, 4}, {2, 5}, {2, 6}, {2, 7}, {2, 8},
                        {2, 2}, {2, 1},
                        {1, 3},
                        {3, 3}, {4, 3}, {5, 3}, {6, 3}, {7, 3}, {8, 3},
                }
        );
    }


    @Test
    public void rookCaptureEnemy() {
        validateMoves("""
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        |N| | | | | | | |
                        |r| | | | |B| | |
                        | | | | | | | | |
                        |q| | | | | | | |
                        | | | | | | | | |
                        """,
                new ChessPositionBuilder().setRow(4).setCol(1).createChessPosition(),
                new int[][]{
                        {5, 1},
                        {3, 1},
                        {4, 2}, {4, 3}, {4, 4}, {4, 5}, {4, 6},
                }
        );
    }


    @Test
    public void rookBlocked() {
        validateMoves("""
                        | | | | | | |n|r|
                        | | | | | | | |p|
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        """,
                new ChessPositionBuilder().setRow(8).setCol(8).createChessPosition(),
                new int[][]{}
        );
    }

}
