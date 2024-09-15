package chess;

import java.util.Arrays;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece[][] boardLayout;

    public ChessBoard() {
        boardLayout = new ChessPiece[8][8];
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        boardLayout[position.getColumn()-1][position.getRow()-1] = piece;
    }
    // -1 because normal chessboard starts from (1,1), but programing starts with (0,0)

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return boardLayout[position.getColumn()-1][position.getRow()-1];
    }
    // it returns the location.

    public ChessGame.TeamColor getTeamOfSquare(ChessPosition position) {
        if (getPiece(position) != null) {
            return getPiece(position).getTeamColor();
        }
        else return null;
    }
    // Bring the teamcolor from ChessGame.java to decide the teamcolor.
    // it returns the color of the selected piece

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        boardLayout = new ChessPiece[8][8];

        // add all white pieces
        addPiece(new ChessPosition(1,1), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(1,2), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1,3), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1,4), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(1,5), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(1,6), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1,7), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1,8), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        for (int i =1; i <=8; i++){
            addPiece(new ChessPosition(2, i), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        }

        // add all black pieces
        addPiece(new ChessPosition(8,1), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(8,2), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8,3), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8,4), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(8,5), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(8,6), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8,7), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8,8), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        for (int i =1; i <=8; i++){
            addPiece(new ChessPosition(7, i), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        }
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        for (int y = 7; y >= 0; y--){
            output.append("|");
            for (int x = 0; x < 8; x++){
                output.append(boardLayout[x][y] != null ? boardLayout[x][y].toString() : "");
                output.append("|");
            }
            output.append("\n");
        }
        return output.toString();
    }
    // the reason why y starts from 7 to 0, but x starts 1 to 7 is to make it look like the real chessboard.
//    |BR|BN|BB|BQ|BK|BB|BN|BR|
//    |BP|BP|BP|BP|BP|BP|BP|BP|
//    |  |  |  |  |  |  |  |  |
//    |  |  |  |  |  |  |  |  |
//    |  |  |  |  |  |  |  |  |
//    |  |  |  |  |  |  |  |  |
//    |WP|WP|WP|WP|WP|WP|WP|WP|
//    |WR|WN|WB|WQ|WK|WB|WN|WR|

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Arrays.deepEquals(boardLayout, that.boardLayout);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(boardLayout);
    }

    //since boardlayout is 2 dimension system, deep ways should be used.
}
