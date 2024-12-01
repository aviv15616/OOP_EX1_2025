/**
 * Represents a specialized type of Disc called a BombDisc.
 * This class extends the Disc class and overrides the getType method to
 * return a bomb emoji, indicating the type of this disc as a "bomb".
 */
public class BombDisc extends Disc {

    /**
     * Constructor for the BombDisc class.
     * Initializes the BombDisc object and sets the owner of the disc.
     *
     * @param owner The player who owns this BombDisc.
     */
    public BombDisc(Player owner) {
        super(owner);
    }

    /**
     * Gets the type of the disc.
     * Overrides the getType method from the parent Disc class to return a bomb emoji,
     * representing this disc as a "bomb".
     *
     * @return A string containing the bomb emoji ("💣").
     */
    @Override
    public String getType() {
        return "💣";
    }
}