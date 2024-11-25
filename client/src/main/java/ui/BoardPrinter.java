package ui;

import chess.*;
import static java.lang.System.out;
import static ui.EscapeSequences.*;

public class BoardPrinter {
    private final ChessBoard board;

    public BoardPrinter(ChessBoard board) {
        this.board = board;
    }

    public void printBoard() {
        out.println(generateBoardString());
    }

    private String generateBoardString() {
        StringBuilder output = new StringBuilder(SET_TEXT_BOLD);
        appendBoards(output);
        return output.append(RESET_TEXT_BOLD_FAINT).toString();
    }

    private void appendBoards(StringBuilder output) {
        appendBoard(output, true);
        output.append("\n");
        appendBoard(output, false);
    }

    private void appendBoard(StringBuilder output, boolean reversed) {
        output.append(createHeaderRow(reversed));
        appendChessRows(output, reversed);
        output.append(createHeaderRow(reversed));
    }

    private void appendChessRows(StringBuilder output, boolean reversed) {
        for (int i = 8; i > 0; i--) {
            int row = reversed ? (9 - i) : i;
            output.append(createChessRow(row, reversed));
        }
    }

    private String createHeaderRow(boolean reversed) {
        String letters = reversed ? "hgfedcba" : "abcdefgh";
        return String.format("%s%s    %s    %s%s\n",
                SET_BG_COLOR_BLACK, SET_TEXT_COLOR_BLUE, letters,
                RESET_BG_COLOR, RESET_TEXT_COLOR);

    }

    private String createChessRow(int row, boolean reversed) {
        StringBuilder rowString = new StringBuilder();
        appendRowNumber(rowString, row);
        appendSquares(rowString, row, reversed);
        appendRowNumber(rowString, row);
        return rowString.append("\n").toString();
    }

    private void appendRowNumber(StringBuilder rowString, int row) {
        rowString.append(String.format("%s%s %d %s%s",
                SET_BG_COLOR_BLACK, SET_TEXT_COLOR_BLUE, row,
                RESET_BG_COLOR, RESET_TEXT_COLOR));
// removed %s from %s%s%s at the beginning.
    }

    private void appendSquares(StringBuilder rowString, int row, boolean reversed) {
        for (int i = 1; i <= 8; i++) {
            int column = reversed ? (9 - i) : i;
            rowString.append(determineSquareColor(row, column));
            rowString.append(getPieceSymbol(row, column));
        }
    }

    private String determineSquareColor(int row, int column) {
        boolean isEvenSum = (row + column) % 2 == 0;
        return isEvenSum ? SET_BG_COLOR_RED : SET_BG_COLOR_LIGHT_GREY;
    }

    private String getPieceSymbol(int row, int column) {
        ChessPiece piece = board.getPiece(new ChessPosition(row, column));
        if (piece == null) {
            return "   ";
        }

        String color = (piece.getTeamColor() == ChessGame.TeamColor.WHITE)
                ? SET_TEXT_COLOR_WHITE : SET_TEXT_COLOR_BLACK;

        return color + " " + getPieceChar(piece.getPieceType()) + " ";
    }

    private char getPieceChar(ChessPiece.PieceType type) {
        return switch (type) {
            case QUEEN -> 'Q';
            case KING -> 'K';
            case BISHOP -> 'B';
            case KNIGHT -> 'N';
            case ROOK -> 'R';
            case PAWN -> 'P';
        };
    }
}