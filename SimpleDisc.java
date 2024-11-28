public class SimpleDisc extends Disc {
    public SimpleDisc(Player owner) {
        super(owner);
    }

    @Override
    public String getType() {
        return "Simple";
    }
}