package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    ChessGame.TeamColor turn;
    ChessBoard board;


    public ChessGame() {
        this.board = new ChessBoard();
        this.turn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
         turn = team;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame chessGame = (ChessGame) o;
        return turn == chessGame.turn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(turn, board);
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece currChessPiece = this.board.getPiece(startPosition);

        if(currChessPiece == null) {
            return null;
        }

        Collection<ChessMove> moves = currChessPiece.pieceMoves(board, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();

        TeamColor teamColor = currChessPiece.getTeamColor();

        for(ChessMove move : moves) {
            ChessBoard boardCopy = (ChessBoard) board.CopyBoard();

            if(move.getPromotionPiece() == null) {
                boardCopy.makeMoveOnBoard(move);
            } else {
                boardCopy.makePromoMoveOnBoard(move);
            }

            if(!isInCheckWithBoard(teamColor, boardCopy)) {
                validMoves.add(move);
            }
        }
        return validMoves;
    }


    private ChessPosition findKingPosition(TeamColor teamColor, ChessBoard board) {
        for(int i = 1; i < 8; i++) {
            for(int j = 1; j < 8; j++) {
                ChessPosition currPos = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(currPos);

                if(piece != null && board.getPiece(currPos).getTeamColor() == teamColor && board.getPiece(currPos).getPieceType() == ChessPiece.PieceType.KING) {
                    return currPos;
                }
            }
        }
        throw new RuntimeException("King not found on the board.");
    }


    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());

        if (validMoves == null || !validMoves.contains(move)) {
            throw new InvalidMoveException("Invalid move.");
        }

        this.board.makeMoveOnBoard(move);

        if(turn == TeamColor.WHITE) {
            setTeamTurn(TeamColor.BLACK);
        } else {
            setTeamTurn(TeamColor.WHITE);
        }

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = findKingPosition(teamColor, board);
        ChessBoard boardCopy = (ChessBoard) board.CopyBoard();

        for(int i = 1; i < 8; i++) {
            for(int j = 1; j < 8; j++) {
                ChessPiece piece = board.getPiece(new ChessPosition(i, j));
                Collection<ChessMove> moves = piece.pieceMoves(boardCopy, new ChessPosition(i,j));

                for(ChessMove move : moves) {
                    if(move.getEndPosition().equals(kingPos)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if(!isInCheck(teamColor)) {
            return false;
        }
        for(int i = 1; i < 8; i++) {
            for(int j = 1; j < 8; j++) {

                ChessPiece piece = board.getPiece(new ChessPosition(i,j));
                if(piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = piece.pieceMoves(board, new ChessPosition(i,j));

                    for(ChessMove move : moves) {
                        ChessBoard boardCopy = (ChessBoard) board.CopyBoard();

                        boardCopy.makeMoveOnBoard(move);

                        if(!isInCheckWithBoard(teamColor, boardCopy)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public boolean isInCheckWithBoard(TeamColor teamColor, ChessBoard board) {
        ChessPosition kingPos = findKingPosition(teamColor, board);

        for(int i = 1; i < 8; i++) {
            for(int j = 1; j < 8; j++) {
                ChessPiece piece = board.getPiece(new ChessPosition(i, j));

                if (piece != null && piece.getTeamColor() != teamColor) {
                    Collection<ChessMove> moves = piece.pieceMoves(board, new ChessPosition(i, j));

                    for (ChessMove move : moves) {
                        if (move.getEndPosition().equals(kingPos)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if(isInCheck(teamColor)) {
            return false;
        }

        for (int i = 1; i < 8; i++) {
            for (int j = 1; j < 8; j++) {
                ChessPosition currentPosition = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(currentPosition);

                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> validMoves = validMoves(currentPosition);

                    if (validMoves != null && !validMoves.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }


}
