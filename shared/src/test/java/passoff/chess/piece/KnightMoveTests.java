package passoff.chess.piece;

import chess.ChessPositionBuilder;
import org.junit.jupiter.api.Test;

import static passoff.chess.TestUtilities.validateMoves;

public class KnightMoveTests {

    @Test
    public void knightMiddleOfBoardWhite() {
        validateMoves("""
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | |N| | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        """,
                new ChessPositionBuilder().setRow(5).setCol(5).createChessPosition(),
                new int[][]{
                        {7, 6}, {6, 7}, {4, 7}, {3, 6}, {3, 4}, {4, 3}, {6, 3}, {7, 4},
                }
        );
    }

    @Test
    public void knightMiddleOfBoardBlack() {
        validateMoves("""
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | |n| | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        """,
                new ChessPositionBuilder().setRow(5).setCol(5).createChessPosition(),
                new int[][]{
                        {7, 6}, {6, 7}, {4, 7}, {3, 6}, {3, 4}, {4, 3}, {6, 3}, {7, 4},
                }
        );
    }


    @Test
    public void knightEdgeOfBoardLeft() {
        validateMoves("""
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        |n| | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        """,
                new ChessPositionBuilder().setRow(4).setCol(1).createChessPosition(),
                new int[][]{{6, 2}, {5, 3}, {3, 3}, {2, 2}}
        );
    }

    @Test
    public void knightEdgeOfBoardRight() {
        validateMoves("""
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | |n|
                        | | | | | | | | |
                        | | | | | | | | |
                        """,
                new ChessPositionBuilder().setRow(3).setCol(8).createChessPosition(),
                new int[][]{{1, 7}, {2, 6}, {4, 6}, {5, 7}}
        );
    }

    @Test
    public void knightEdgeOfBoardBottom() {
        validateMoves("""
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | |N| | |
                        """,
                new ChessPositionBuilder().setRow(1).setCol(6).createChessPosition(),
                new int[][]{{2, 4}, {3, 5}, {3, 7}, {2, 8}}
        );
    }

    @Test
    public void knightEdgeOfBoardTop() {
        validateMoves("""
                        | | |N| | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        """,
                new ChessPositionBuilder().setRow(8).setCol(3).createChessPosition(),
                new int[][]{{7, 5}, {6, 4}, {6, 2}, {7, 1}}
        );
    }


    @Test
    public void knightCornerOfBoardBottomRight() {
        validateMoves("""
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | |N|
                        """,
                new ChessPositionBuilder().setRow(1).setCol(8).createChessPosition(),
                new int[][]{{2, 6}, {3, 7}}
        );
    }

    @Test
    public void knightCornerOfBoardTopRight() {
        validateMoves("""
                        | | | | | | | |N|
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        """,
                new ChessPositionBuilder().setRow(8).setCol(8).createChessPosition(),
                new int[][]{{6, 7}, {7, 6}}
        );
    }

    @Test
    public void knightCornerOfBoardTopLeft() {
        validateMoves("""
                        |n| | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        """,
                new ChessPositionBuilder().setRow(8).setCol(1).createChessPosition(),
                new int[][]{{7, 3}, {6, 2}}
        );
    }

    @Test
    public void knightCornerOfBoardBottomLeft() {
        validateMoves("""
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        |n| | | | | | | |
                        """,
                new ChessPositionBuilder().setRow(1).setCol(1).createChessPosition(),
                new int[][]{{2, 3}, {3, 2}}
        );
    }


    @Test
    public void knightBlocked() {
        validateMoves("""
                        | | | | | | | | |
                        | | | |R| | | | |
                        | | | | | | |P| |
                        | | | | |N| | | |
                        | | |N| | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        """,
                new ChessPositionBuilder().setRow(5).setCol(5).createChessPosition(),
                new int[][]{{3, 4}, {3, 6}, {4, 7}, {7, 6}, {6, 3}}
        );
    }


    @Test
    public void knightCaptureEnemy() {
        validateMoves("""
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | |n| | | |
                        | | |N| | | | | |
                        | | | |P| |R| | |
                        | | | | | | | | |
                        | | | | | | | | |
                        """,
                new ChessPositionBuilder().setRow(5).setCol(5).createChessPosition(),
                new int[][]{{7, 6}, {6, 7}, {4, 7}, {3, 6}, {3, 4}, {4, 3}, {6, 3}, {7, 4}}
        );
    }
}