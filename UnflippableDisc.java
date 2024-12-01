/**
 * Represents an unflippable disc in the game. An unflippable disc behaves like a normal disc , but it
 * cannot be flipped during gameplay.
 * This class extends the Disc class and overrides the getType method to return a unique symbol.
 */
public class UnflippableDisc extends Disc {

    /**
     * Constructs a new UnflippableDisc with the specified owner.
     *
     * @param owner The player who owns the disc.
     */
    public UnflippableDisc(Player owner) {
        super(owner);
    }

    /**
     * Returns the type or symbol representing this unflippable disc.
     * In this case, it returns a special symbol "⭕" to differentiate it from regular discs.
     *
     * @return A string representing the type of this disc, which is "⭕".
     */
    @Override
    public String getType() {
        return "⭕";
    }
}