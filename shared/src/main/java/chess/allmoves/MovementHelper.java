package chess.allmoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;

public class MovementHelper {
    protected boolean inBoard(int row, int col) {
        return col <= 8 && row <= 8 && col >= 1 && row >= 1;
    }

    protected void calculateMoves(ChessBoard board, Collection<ChessMove> moves, int row, int col, int[][] directions) {
        for (int[] direction : directions) {
            int rowInc = direction[0];
            int colInc = direction[1];
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
    }

    protected void calculateKMoves(ChessBoard board, Collection<ChessMove> moves, int row, int col, int[][] directions) {
        for (int[] direction : directions) {
            int rowInc = direction[0];
            int colInc = direction[1];
            int nextRow = row + rowInc;
            int nextCol = col + colInc;

            ChessPosition startPosition = new ChessPosition(row, col);
            ChessPosition newPosition = new ChessPosition(nextRow, nextCol);

            if (inBoard(nextRow, nextCol)) {
                if (board.isEmpty(newPosition)) {
                    moves.add(new ChessMove(startPosition, newPosition, null));
                } else if (board.getPiece(newPosition).getTeamColor() != board.getPiece(startPosition).getTeamColor()) {
                    moves.add(new ChessMove(startPosition, newPosition, null));
                }
            }
        }
    }
}
