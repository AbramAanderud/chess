package chess.allmoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesCalculator extends MovementHelper{
    private static final int[][] DIAGONALS = {
            {1, 1},
            {1, -1},
            {-1, 1},
            {-1, -1}
    };

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int currRow = myPosition.getRow();
        int currCol = myPosition.getColumn();

        calculateMoves(board, moves, currRow, currCol, DIAGONALS);
        return moves;
    }
}
