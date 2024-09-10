package chess;

public class ChessPositionBuilder {
    private int row;
    private int col;

    public ChessPositionBuilder setRow(int row) {
        this.row = row;
        return this;
    }

    public ChessPositionBuilder setCol(int col) {
        this.col = col;
        return this;
    }

    public ChessPosition createChessPosition() {
        return new ChessPosition(row, col);
    }
}