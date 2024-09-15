package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private final int row;
    private final int col;
    // since it is final, it will never be changed.

    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }
    // initialized(초기화) row and col

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return col;
    }

    @Override
    public String toString() {
        return "ChessPosition{" +
                "row=" + row +
                ", col=" + col +
                '}';
    }
    // it will show like ChessPosition{row=3, col=4}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPosition that = (ChessPosition) o;
        return row == that.row && col == that.col;
    }

//     "this" means ChessPosition itself, and it compares Object o. If they are the same, it returns true.
//     if o is null, it means there is nothing to compare, so it is false.
//     if its class and o's class are different, it is false. || means "or"
//     o changes itself as ChessPosition type and it is saved in that.
//     compare this.row(ChessPosition's) and that.row(o's), likewise this.col and that.col

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
    // it is useful when it is also used by HashMap or HashSet. This hashcode helps finding this very rapidly.
    // if I used override, I can still use all part of the parent class feature,
    // but I can change specific part where I need to change if necessary.

}
