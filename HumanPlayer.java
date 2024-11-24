public class HumanPlayer  extends Player{
    @Override
    boolean isHuman() {
        return true;
    }
    public HumanPlayer(boolean isPlayerOne) {
        super(isPlayerOne);
    }

}
