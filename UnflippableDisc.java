public class UnflippableDisc extends Disc {
    public UnflippableDisc(Player owner) {
        super(owner);
    }

    @Override
    public String getType() {
        return "Unflip";
    }
}