import java.util.ArrayList;
import java.util.List;

/**
 * Represents a move made in the game. A move consists of a position where the disc is placed,
 * the type of disc being placed (e.g., normal, bomb), and the discs that are flipped as a result
 * of the move.
 */
public class Move {

    private Position position;      // The position where the disc is placed
    private Disc disc;              // The disc being placed (could be normal, bomb, etc.)
    private List<Position> flippedDiscs = new ArrayList<>();

    /**
     * Constructor to initialize a move with a given position and disc.
     *
     * @param position The position on the board where the disc will be placed.
     * @param disc The disc that is being placed on the board (could be a normal disc or a special disc like bomb).
     */
    public Move(Position position, Disc disc) {
        this.position = position;
        this.disc = disc;
        this.flippedDiscs = new ArrayList<>();
    }

    /**
     * Sets the list of discs that are flipped as a result of this move.
     *
     * @param flippedDiscs A list of positions representing the discs that are flipped.
     */
    public void setFlippedDiscs(List<Position> flippedDiscs) {
        this.flippedDiscs = flippedDiscs;
    }

    /**
     * Gets the position of the move.
     *
     * @return The position where the disc is placed as a {@link Position} object.
     */
    public Position position() {
        return this.position;
    }

    /**
     * Gets the list of discs that were flipped as a result of this move.
     *
     * @return A list of positions of the discs that were flipped, or an empty list if no discs were flipped.
     */
    public List<Position> getFlippedDiscs() {
        return flippedDiscs;
    }

    /**
     * Gets the disc that was placed on the board during this move.
     *
     * @return The {@link Disc} object representing the disc that was placed.
     */
    public Disc disc() {
        return disc;
    }
}