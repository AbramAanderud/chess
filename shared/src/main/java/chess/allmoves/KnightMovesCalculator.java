package chess.allmoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalculator extends MovementHelper {
    private static final int[][] KNIGHT_MOVES = {
            {2, 1},
            {2, -1},
            {1, 2},
            {-1, 2},
            {-2, 1},
            {-2, -1},
            {1, -2},
            {-1, -2},
    };

    public Collection<ChessMove> pieceMoves(ChessBoard board,
                                            ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int currRow = myPosition.getRow();
        int currCol = myPosition.getColumn();

        calculateKMoves(board, moves, currRow, currCol, KNIGHT_MOVES);
        return moves;
    }
}
