public class SimpleDisc implements Disc
{

    private Player p1;
    private final String type="Simple";

    public SimpleDisc(Player p1){
        this.p1=p1;

    }
    @Override
    public Player getOwner() {
        return p1;
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
