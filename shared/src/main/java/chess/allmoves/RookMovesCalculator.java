package chess.allmoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class RookMovesCalculator {
    private static final int[][] STRAIGHTS = {
            {1, 0},
            {0, 1},
            {-1, 0},
            {0, -1}
    };

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int currRow = myPosition.getRow();
        int currCol = myPosition.getColumn();

        for (int[] direction : STRAIGHTS) {
            getMoves(board, moves, currRow, currCol, direction[0], direction[1]);
        }

        return moves;
    }

    private void getMoves(ChessBoard board, Collection<ChessMove> moves, int row, int col, int rowInc, int colInc) {
        int nextRow = row + rowInc;
        int nextCol = col + colInc;

        while (inBoard(nextRow, nextCol)) {
            ChessPosition startPosition = new ChessPosition(row, col);
            ChessPosition newPosition = new ChessPosition(nextRow, nextCol);

            if (board.isEmpty(newPosition)) {
                moves.add(new ChessMove(startPosition, newPosition, null));
            } else if (board.getPiece(newPosition).getTeamColor() != board.getPiece(startPosition).getTeamColor()) {
                moves.add(new ChessMove(startPosition, newPosition, null));
                break;
            } else {
                break;
            }

            nextRow += rowInc;
            nextCol += colInc;
        }
    }

    private boolean inBoard(int row, int col) {
        return col <= 8 && row <= 8 && col >= 1 && row >= 1;
    }
}
