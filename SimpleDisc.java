/**
 * Represents a simple disc in the game.
 * A SimpleDisc is a type of disc owned by a player, and its representation is a filled circle (⬤).
 *
 * This class extends from {@link Disc} and provides an implementation of the {@link Disc#getType()} method.
 */
public class SimpleDisc extends Disc {

    /**
     * Constructs a new SimpleDisc with the given owner.
     *
     * @param owner The player who owns this disc.
     */
    public SimpleDisc(Player owner) {
        super(owner);
    }

    /**
     * Returns the type of the disc as a string.
     * In the case of SimpleDisc, this is the filled circle character (⬤).
     *
     * @return A string representing the type of the disc, which is "⬤" for SimpleDisc.
     */
    @Override
    public String getType() {
        return "⬤";
    }
}