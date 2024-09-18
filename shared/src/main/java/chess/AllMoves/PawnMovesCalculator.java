package chess.AllMoves;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator {

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int currRow = myPosition.getRow();
        int currCol = myPosition.getColumn();

        ChessGame.TeamColor color = board.getPiece(myPosition).getTeamColor();

        if(color == ChessGame.TeamColor.BLACK) {
            getBlackMoves(board, moves, currRow, currCol);
        } else if(color == ChessGame.TeamColor.WHITE) {
            getWhiteMoves(board, moves, currRow, currCol);
        }

        return moves;
    }

    private void getWhiteMoves(ChessBoard board, Collection<ChessMove> moves, int currRow, int currCol) {
        if(InBoard(currRow + 1, currCol)) {
            ChessPosition startPosition = new ChessPosition(currRow, currCol);
            ChessPosition oneInFront = new ChessPosition(currRow + 1, currCol);
            ChessPosition twoInFront = new ChessPosition(currRow + 2, currCol);
            ChessPosition lefty = new ChessPosition(currRow + 1, currCol - 1);
            ChessPosition righty = new ChessPosition(currRow + 1, currCol + 1);

            if (currRow == 2 && board.isEmpty(oneInFront) && board.isEmpty(twoInFront)) {
                moves.add(new ChessMove(startPosition, twoInFront, null));
                moves.add(new ChessMove(startPosition, oneInFront, null));
            } else if (board.isEmpty(oneInFront)) {
                if(oneInFront.getRow() == 8) {
                    Promotion(startPosition, oneInFront, moves);
                } else {
                    moves.add(new ChessMove(startPosition, oneInFront, null));
                }
            }

            if ((!board.isEmpty(lefty)) && board.getPiece(lefty).getTeamColor() != board.getPiece(startPosition).getTeamColor()) {
                if(lefty.getRow() == 8) {
                    Promotion(startPosition, lefty, moves);
                } else {
                    moves.add(new ChessMove(startPosition, lefty, null));
                }
            } else if ((!board.isEmpty(righty)) && board.getPiece(righty).getTeamColor() != board.getPiece(startPosition).getTeamColor()) {
                if(righty.getRow() == 8) {
                    Promotion(startPosition, righty, moves);
                } else {
                    moves.add(new ChessMove(startPosition, righty, null));
                }
            }
        }
    }

    private void getBlackMoves(ChessBoard board, Collection<ChessMove> moves, int currRow, int currCol) {
        if(InBoard(currRow - 1, currCol)) {
            ChessPosition startPosition = new ChessPosition(currRow, currCol);
            ChessPosition oneInFront = new ChessPosition(currRow - 1, currCol);
            ChessPosition twoInFront = new ChessPosition(currRow - 2, currCol);
            ChessPosition lefty = new ChessPosition(currRow - 1, currCol - 1);
            ChessPosition righty = new ChessPosition(currRow - 1, currCol + 1);

            if(currRow == 7 && board.isEmpty(oneInFront) && board.isEmpty(twoInFront)) {
                moves.add(new ChessMove(startPosition, twoInFront, null));
                moves.add(new ChessMove(startPosition, oneInFront, null));
            } else if(board.isEmpty(oneInFront)) {
                if(oneInFront.getRow() == 1) {
                    Promotion(startPosition, oneInFront, moves);
                } else {
                    moves.add(new ChessMove(startPosition, oneInFront, null));
                }
            }

            if((!board.isEmpty(lefty)) && board.getPiece(lefty).getTeamColor() != board.getPiece(startPosition).getTeamColor()) {
                if(lefty.getRow() == 1) {
                    Promotion(startPosition, lefty, moves);
                } else {
                    moves.add(new ChessMove(startPosition, lefty, null));
                }
            } else if((!board.isEmpty(righty)) && board.getPiece(righty).getTeamColor() != board.getPiece(startPosition).getTeamColor()) {
                if(righty.getRow() == 1) {
                    Promotion(startPosition, righty, moves);
                } else {
                    moves.add(new ChessMove(startPosition, righty, null));
                }
            }
        }

    }

    private boolean InBoard(int row, int col) {
        return col <= 8 && row <= 8 && col >= 1 && row >= 1;
    }

    private void Promotion(ChessPosition startPosition, ChessPosition end, Collection<ChessMove> moves) {
        moves.add(new ChessMove(startPosition, end, ChessPiece.PieceType.KNIGHT));
        moves.add(new ChessMove(startPosition, end, ChessPiece.PieceType.ROOK));
        moves.add(new ChessMove(startPosition, end, ChessPiece.PieceType.QUEEN));
        moves.add(new ChessMove(startPosition, end, ChessPiece.PieceType.BISHOP));

    }


}
