import java.util.Objects;

public class Position {
    private int row;  // The row index (0-7 for an 8x8 board)
    private int col;  // The column index (0-7 for an 8x8 board)

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }
    public int row() {
        return row;
    }

    public int col() {
        return col;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Position position = (Position) obj;
        return row == position.row && col == position.col;
    }
    @Override
    public int hashCode() {
        return Objects.hash(col,row); // Modify based on your fields
    }
    public String toString(){

        return row+","+col;
    }



}
