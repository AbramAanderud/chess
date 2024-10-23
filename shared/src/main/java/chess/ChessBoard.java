package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private final ChessPiece[][] squares = new ChessPiece[8][8];

    public ChessBoard() {

    }

    public ChessBoard copyBoard() {
        ChessBoard copy = new ChessBoard();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece piece = this.squares[i][j];

                if (this.squares[i][j] != null) {
                    copy.squares[i][j] = new ChessPiece(piece.getTeamColor(),
                            piece.getPieceType());
                } else {
                    copy.squares[i][j] = null;
                }
            }
        }
        return copy;
    }


    public void makeMoveOnBoard(ChessMove move) {
        ChessPosition startPos = move.getStartPosition();
        ChessPosition endPos = move.getEndPosition();

        ChessPiece piece = this.getPiece(startPos);

        if (piece == null) {
            throw new RuntimeException("No piece at the start pos");
        }

        this.addPiece(endPos, piece);
        this.removePiece(startPos, piece);

        //need to move these methods into the chessboard maybe
        //Other moves can move into a position in which they would
        //be allowed to still be captured but the king cannot
    }

    public void makePromoMoveOnBoard(ChessMove move) {
        ChessPosition startPos = move.getStartPosition();
        ChessPosition endPos = move.getEndPosition();

        ChessPiece piece = this.getPiece(startPos);
        if (piece == null) {
            throw new RuntimeException("No piece at the start pos");
        }

        this.addPiece(endPos, new ChessPiece(piece.getTeamColor(),
                move.getPromotionPiece()));
        this.removePiece(startPos, piece);
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    public void removePiece(ChessPosition position, ChessPiece piece) {
        if (squares[position.getRow() - 1]
                [position.getColumn() - 1].getPieceType()
                == piece.getPieceType() && squares[position.getRow() - 1]
                [position.getColumn() - 1].getTeamColor()
                == piece.getTeamColor()) {
            squares[position.getRow() - 1][position.getColumn() - 1] = null;
        }
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow() - 1][position.getColumn() - 1];
    }

    public boolean isEmpty(ChessPosition position) {
        return getPiece(position) == null;
    }


    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                squares[i][j] = null;
            }
        }

        for (int i = 0; i < 8; i++) {
            squares[1][i] = new ChessPiece(ChessGame.TeamColor.WHITE,
                    ChessPiece.PieceType.PAWN);
        }

        squares[0][0] = new ChessPiece(ChessGame.TeamColor.WHITE,
                ChessPiece.PieceType.ROOK);
        squares[0][1] = new ChessPiece(ChessGame.TeamColor.WHITE,
                ChessPiece.PieceType.KNIGHT);
        squares[0][2] = new ChessPiece(ChessGame.TeamColor.WHITE,
                ChessPiece.PieceType.BISHOP);
        squares[0][3] = new ChessPiece(ChessGame.TeamColor.WHITE,
                ChessPiece.PieceType.QUEEN);
        squares[0][4] = new ChessPiece(ChessGame.TeamColor.WHITE,
                ChessPiece.PieceType.KING);
        squares[0][5] = new ChessPiece(ChessGame.TeamColor.WHITE,
                ChessPiece.PieceType.BISHOP);
        squares[0][6] = new ChessPiece(ChessGame.TeamColor.WHITE,
                ChessPiece.PieceType.KNIGHT);
        squares[0][7] = new ChessPiece(ChessGame.TeamColor.WHITE,
                ChessPiece.PieceType.ROOK);

        for (int i = 0; i < 8; i++) {
            squares[6][i] = new ChessPiece(ChessGame.TeamColor.BLACK,
                    ChessPiece.PieceType.PAWN);
        }

        squares[7][0] = new ChessPiece(ChessGame.TeamColor.BLACK,
                ChessPiece.PieceType.ROOK);
        squares[7][1] = new ChessPiece(ChessGame.TeamColor.BLACK,
                ChessPiece.PieceType.KNIGHT);
        squares[7][2] = new ChessPiece(ChessGame.TeamColor.BLACK,
                ChessPiece.PieceType.BISHOP);
        squares[7][3] = new ChessPiece(ChessGame.TeamColor.BLACK,
                ChessPiece.PieceType.QUEEN);
        squares[7][4] = new ChessPiece(ChessGame.TeamColor.BLACK,
                ChessPiece.PieceType.KING);
        squares[7][5] = new ChessPiece(ChessGame.TeamColor.BLACK,
                ChessPiece.PieceType.BISHOP);
        squares[7][6] = new ChessPiece(ChessGame.TeamColor.BLACK,
                ChessPiece.PieceType.KNIGHT);
        squares[7][7] = new ChessPiece(ChessGame.TeamColor.BLACK,
                ChessPiece.PieceType.ROOK);

    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 7; i >= 0; i--) {
            sb.append("|");
            for (int j = 0; j < 8; j++) {
                ChessPiece piece = squares[i][j];
                sb.append(getPieceCharacter(piece)).append("|");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private char getPieceCharacter(ChessPiece piece) {
        if (piece == null) {
            return ' ';
        }
        char firstInitial = piece.getPieceType().toString().charAt(0);

        if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
            firstInitial = 'n';
        }

        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            return Character.toUpperCase(firstInitial);
        } else {
            return Character.toLowerCase(firstInitial);
        }
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }
}
