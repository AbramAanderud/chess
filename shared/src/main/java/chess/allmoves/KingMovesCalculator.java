package chess.allmoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalculator extends MovementHelper{
    private static final int[][] AROUND = {
            {1, 1},
            {1, 0},
            {0, 1},
            {-1, -1},
            {-1, 0},
            {0, -1},
            {-1, 1},
            {1, -1}
    };

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int currRow = myPosition.getRow();
        int currCol = myPosition.getColumn();

        calculateKMoves(board, moves, currRow, currCol, AROUND);
        return moves;
    }

}
