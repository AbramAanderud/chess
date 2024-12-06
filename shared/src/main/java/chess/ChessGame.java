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
    private boolean gameOver;


    public ChessGame() {
        this.board = new ChessBoard();
        board.resetBoard();
        this.turn = TeamColor.WHITE;
        this.gameOver = false;
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

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    @Override
    public boolean equals(Object o) {
        if (this==o) {
            return true;
        }
        if (o==null || getClass()!=o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return turn==chessGame.turn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(turn, board);
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

        if (currChessPiece==null) {
            return null;
        }

        Collection<ChessMove> moves = currChessPiece.pieceMoves(board,
                startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();
        TeamColor teamColor = currChessPiece.getTeamColor();

        for (ChessMove move : moves) {
            ChessBoard boardCopy = board.copyBoard();

            if (move.getPromotionPiece()==null) {
                boardCopy.makeMoveOnBoard(move);
            } else {
                boardCopy.makePromoMoveOnBoard(move);
            }

            if (currChessPiece.getPieceType()==ChessPiece.PieceType.KING
                    && currChessPiece.getTeamColor()==teamColor) {
                ChessPosition newKingPosition = move.getEndPosition();
                if (!newKingPositionThreat(newKingPosition, teamColor)) {
                    if (!isInCheckWithBoard(teamColor, boardCopy)) {
                        validMoves.add(move);
                    }
                }
            } else if (!isInCheckWithBoard(teamColor, boardCopy)) {
                validMoves.add(move);
            }
        }
        return validMoves;
    }

    private boolean newKingPositionThreat(ChessPosition position,
                                          TeamColor teamColor) {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPiece piece = board.getPiece(new ChessPosition(i, j));

                if (isOpponentPiece(piece, teamColor)) {
                    Collection<ChessMove> opponentMoves = piece.pieceMoves(
                            board, new ChessPosition(i, j));

                    if (isPositionThreatened(opponentMoves, position)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isOpponentPiece(ChessPiece piece, TeamColor teamColor) {
        return piece!=null && piece.getTeamColor()!=teamColor;
    }

    private boolean isPositionThreatened(Collection<ChessMove> opponentMoves,
                                         ChessPosition position) {
        for (ChessMove move : opponentMoves) {
            if (move.getEndPosition().equals(position)) {
                return true;
            }
        }
        return false;
    }


    private ChessPosition findKingPosition(TeamColor teamColor,
                                           ChessBoard board) {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition currPos = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(currPos);

                if (piece!=null && board.getPiece(currPos).getTeamColor()
                        ==teamColor && board.getPiece(currPos).getPieceType()
                        ==ChessPiece.PieceType.KING) {
                    return currPos;
                }
            }
        }
        return null;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());

        if (piece==null) {
            throw new InvalidMoveException("No piece in that spot");
        }

        if (piece.getTeamColor()!=this.getTeamTurn()) {
            throw new InvalidMoveException("Piece Doesn't exist or isn't yours to move");
        }

        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());

        if (validMoves==null || !validMoves.contains(move)) {
            throw new InvalidMoveException("Invalid move: not in valid moves");
        }

        try {
            ChessBoard boardCopy = board.copyBoard();
            if (move.getPromotionPiece()!=null && piece.getPieceType()==ChessPiece.PieceType.PAWN) {
                boardCopy.makePromoMoveOnBoard(move);
            } else {
                boardCopy.makeMoveOnBoard(move);
            }
            if (isInCheckWithBoard(this.getTeamTurn(), boardCopy)) {
                throw new InvalidMoveException("Move would cause check");
            }
        } catch (RuntimeException e) {
            throw new InvalidMoveException("Runtime exception occurred: " + e.getMessage());
        }

        // Make the move on the actual board
        if (move.getPromotionPiece()!=null && piece.getPieceType()==ChessPiece.PieceType.PAWN) {
            board.makePromoMoveOnBoard(move);
        } else {
            board.makeMoveOnBoard(move);
        }

        if (isInCheckWithBoard(this.getTeamTurn(), board)) {
            throw new InvalidMoveException("Move would cause check");
        }

        // Update the turn
        if (turn==TeamColor.WHITE) {
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
        ChessBoard boardCopy = board.copyBoard();

        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPiece piece = board.getPiece(new ChessPosition(i, j));

                if (isOpponentPiece(piece, teamColor)) {
                    Collection<ChessMove> moves = piece.pieceMoves(boardCopy,
                            new ChessPosition(i, j));

                    if (isKingInCheck(moves, kingPos)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isKingInCheck(Collection<ChessMove> moves,
                                  ChessPosition kingPos) {
        for (ChessMove move : moves) {
            if (move.getEndPosition().equals(kingPos)) {
                return true;
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
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPiece piece = this.board.getPiece(new ChessPosition(i, j));

                if (isTeamPiece(piece, teamColor)) {
                    Collection<ChessMove> moves = validMoves(new
                            ChessPosition(i, j));

                    if (canAvoidCheckmate(teamColor, moves)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean isTeamPiece(ChessPiece piece, TeamColor teamColor) {
        return piece!=null && piece.getTeamColor()==teamColor;
    }

    private boolean canAvoidCheckmate(TeamColor teamColor,
                                      Collection<ChessMove> moves) {
        for (ChessMove move : moves) {
            ChessBoard boardCopy = board.copyBoard();
            boardCopy.makeMoveOnBoard(move);
            ChessPosition currKingPos = findKingPosition(
                    TeamColor.BLACK, board);
            System.out.println("Board after move:");
            System.out.println("curr king pos " + currKingPos);

            if (!isInCheckWithBoard(teamColor, boardCopy)) {
                return true;
            }
        }
        return false;
    }


    public boolean isInCheckWithBoard(TeamColor teamColor, ChessBoard board) {
        ChessPosition kingPos = findKingPosition(teamColor, board);

        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPiece piece = board.getPiece(new ChessPosition(i, j));

                if (isOpponentPiece(piece, teamColor)) {
                    if (isKingThreatenedByPiece(kingPos, piece, board,
                            new ChessPosition(i, j))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isKingThreatenedByPiece(ChessPosition kingPos,
                                            ChessPiece piece, ChessBoard board,
                                            ChessPosition position) {
        Collection<ChessMove> moves = piece.pieceMoves(board, position);

        for (ChessMove move : moves) {
            if (move.getEndPosition().equals(kingPos)) {
                return true;
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
        if (isInCheck(teamColor)) {
            return false;
        }
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition currentPosition = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(currentPosition);

                if (piece!=null && piece.getTeamColor()==teamColor) {
                    Collection<ChessMove> validMoves = validMoves(currentPosition);

                    if (validMoves!=null && !validMoves.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
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
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }


}
