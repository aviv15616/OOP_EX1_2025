public class UnflippableDisc implements Disc {
    private Player p1;
    private Position currPos;
    private final String type="Unflip";
    @Override
    public Player getOwner() {
        return p1;
    }
    public Position getPos(){
        return currPos;
    }
    @Override
    public void setOwner(Player player) {
        this.p1=player;

    }

    @Override
    public String getType() {
        return type;

    }
}
