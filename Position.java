import java.util.Objects;

/**
 * Represents a position on a 2D grid (e.g., a board game grid).
 * The position is specified by a row and a column, where both row and column are integers
 * in the range of 0 to 7 for an 8x8 board (or any other grid).
 */
public class Position {

    private int row;  // The row index (0-7 for an 8x8 board)
    private int col;  // The column index (0-7 for an 8x8 board)

    /**
     * Constructs a Position object with a specified row and column.
     *
     * @param row The row index (between 0 and 7 for an 8x8 board).
     * @param col The column index (between 0 and 7 for an 8x8 board).
     */
    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Gets the row index of this position.
     *
     * @return The row index of the position.
     */
    public int row() {
        return row;
    }

    /**
     * Gets the column index of this position.
     *
     * @return The column index of the position.
     */
    public int col() {
        return col;
    }

    /**
     * Compares this position to another object for equality.
     * Two positions are considered equal if their row and column values are the same.
     *
     * @param obj The object to compare this position to.
     * @return {@code true} if the given object is a Position with the same row and column values, otherwise {@code false}.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;  // Check for reference equality
        if (obj == null || getClass() != obj.getClass()) return false;  // Check for null and class type
        Position position = (Position) obj;  // Cast the object to Position
        return row == position.row && col == position.col;  // Compare row and column values
    }

    /**
     * Returns the hash code of this position.
     * The hash code is computed using both the row and column indices.
     *
     * @return The hash code for this position.
     */
    @Override
    public int hashCode() {
        return Objects.hash(col, row); // Generate hash code based on row and column
    }

    /**
     * Returns a string representation of the position.
     * The string format is "row,column", representing the row and column indices.
     *
     * @return A string representing the position in the form of "row,column".
     */
    @Override
    public String toString() {
        return row + "," + col;  // Return position as a string in "row,column" format
    }
}