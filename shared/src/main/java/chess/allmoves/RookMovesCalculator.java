package chess.allmoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class RookMovesCalculator extends MovementHelper {
    private static final int[][] STRAIGHTS = {
            {1, 0},
            {0, 1},
            {-1, 0},
            {0, -1}
    };

    public Collection<ChessMove> pieceMoves(ChessBoard board,
                                            ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int currRow = myPosition.getRow();
        int currCol = myPosition.getColumn();

        calculateMoves(board, moves, currRow, currCol, STRAIGHTS);
        return moves;
    }

}
