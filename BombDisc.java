public class BombDisc extends Disc {
    public BombDisc(Player owner) {
        super(owner);
    }

    @Override
    public String getType() {
        return "💣";
    }
}
