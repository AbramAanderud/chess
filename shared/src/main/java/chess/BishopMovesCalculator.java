package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesCalculator {
    private static final int[][] Diagonals = {
            {1, 1},
            {1, -1},
            {-1, 1},
            {-1,-1}
    };

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {


        Collection<ChessMove> moves = new ArrayList<>();
        int currRow = myPosition.getRow();
        int currCol = myPosition.getColumn();

        //System.out.println("Current Row: " + currRow + ", Current Column: " + currCol);


        for(int[] direction : Diagonals) {
            getMoves(board, moves, currRow, currCol, direction[0], direction[1]);
        }

        return moves;

    }

    private void getMoves(ChessBoard board, Collection<ChessMove> moves, int row, int col, int rowInc, int colInc) {
        int nextRow = row + rowInc;
        int nextCol = col + colInc;

        while(InBoard(nextRow, nextCol)) {
            ChessPosition startPosition = new ChessPosition(row, col);
            ChessPosition newPosition = new ChessPosition(nextRow, nextCol);

            if(board.isEmpty(newPosition)) {
                moves.add(new ChessMove(startPosition, newPosition, null));
            } else if(board.getPiece(newPosition).getTeamColor() != board.getPiece(new ChessPosition(row, col)).getTeamColor()) {
                moves.add(new ChessMove(startPosition, newPosition, null));
                break;
            } else {
                break;
            }

            /*
            System.out.println(row + "th row and " + col + "th col as start position");
            System.out.println(nextRow + "th row next and " + nextCol + "th col as new postions");
            System.out.println("now we inc:");
            */
            nextRow += rowInc;
            nextCol += colInc;

            /*
            System.out.println(row + "th row and " + col + "th col as start position");
            System.out.println(nextRow + "th row next and " + nextCol + "th col as new postions");
            */
        }
    }

    private boolean InBoard(int row, int col) {
        return col <= 8 && row <= 8 && col >= 1 && row >= 1;
    }

}