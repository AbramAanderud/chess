package chess.allmoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalculator {
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

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int currRow = myPosition.getRow();
        int currCol = myPosition.getColumn();

        for (int[] direction : KNIGHT_MOVES) {
            getMoves(board, moves, currRow, currCol, direction[0], direction[1]);
        }

        return moves;
    }

    private void getMoves(ChessBoard board, Collection<ChessMove> moves, int row, int col, int rowInc, int colInc) {
        ChessPosition startPosition = new ChessPosition(row, col);
        int nextCol = col + colInc;
        int nextRow = row + rowInc;

        ChessPosition newPosition = new ChessPosition(nextRow, nextCol);

        if (inBoard(nextRow, nextCol)) {
            if (board.isEmpty(newPosition)) {
                moves.add(new ChessMove(startPosition, newPosition, null));
            } else if (board.getPiece(newPosition).getTeamColor() != board.getPiece(startPosition).getTeamColor()) {
                moves.add(new ChessMove(startPosition, newPosition, null));
            }
        }
    }

    private boolean inBoard(int row, int col) {
        return col <= 8 && row <= 8 && col >= 1 && row >= 1;
    }
}
