public class BombDisc implements Disc{
    private Player p1;
    private String type="⭕";

    public BombDisc(Player p1){
        this.p1=p1;
    }
    @Override
    public Player getOwner() {
        return p1;
    }

    @Override
    public void setOwner(Player player) {
        p1= player;
    }

    @Override
    public String getType() {
        return type;
    }
}
