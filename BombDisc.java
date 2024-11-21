public class BombDisc implements Disc{
    private Player p1;
    private String type="Bomb"
    private Position currPos;
    @Override
    public Player getOwner() {
        return p1;
    }
    public Position getPos(){
        return currPos;
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
