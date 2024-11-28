public abstract class Disc {
    protected Player owner;

    /**
     * Constructor to initialize a Disc with a given owner.
     *
     * @param owner the player who owns this disc.
     */
    public Disc(Player owner) {
        this.owner = owner;
    }

    /**
     * Get the player who owns the Disc.
     *
     * @return The player who is the owner of this game disc.
     */
    public Player getOwner() {
        return owner;
    }

    /**
     * Set the player who owns the Disc.
     *
     * @param player the new owner of this disc.
     */
    public void setOwner(Player player) {
        this.owner = player;
    }

    /**
     * Get the type of the disc as a symbol.
     * Each subclass will provide its unique symbol.
     *
     * @return the symbol representing the type of the disc.
     */
    public abstract String getType();

    /**
     * Returns a string representation of the disc, indicating the owner.
     *
     * @return a string with the symbol of the disc and the owner's info.
     */
    @Override
    public String toString() {
        return getType() + " owned by " + (owner != null ? owner : "none");
    }
}