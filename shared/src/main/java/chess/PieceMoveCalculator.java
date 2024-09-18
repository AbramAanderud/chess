package chess;


import chess.AllMoves.*;

import java.util.Collection;

public class PieceMoveCalculator {
    ChessPiece.PieceType type;

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessPiece chessPiece) {
         type = chessPiece.getPieceType();

         if(type == ChessPiece.PieceType.BISHOP) {
            return new BishopMovesCalculator().pieceMoves(board, myPosition);
         } else if(type == ChessPiece.PieceType.KNIGHT) {
            return new KingMovesCalculator().pieceMoves(board, myPosition);
        } else if(type == ChessPiece.PieceType.ROOK) {
            return new RookMovesCalculator().pieceMoves(board, myPosition);
        } else if(type == ChessPiece.PieceType.QUEEN) {
            return new QueenMovesCalculator().pieceMoves(board, myPosition);
        } else if(type == ChessPiece.PieceType.KING) {
            return new KingMovesCalculator().pieceMoves(board, myPosition);
        } else if(type == ChessPiece.PieceType.PAWN) {
            return new PawnMovesCalculator().pieceMoves(board, myPosition);
        }
         else {
             return null;
         }



    }

}
