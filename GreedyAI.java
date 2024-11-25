public class GreedyAI extends Player{
    public GreedyAI(boolean isPlayerOne) {
        super(isPlayerOne);
    }

    @Override
    boolean isHuman() {
        return false;
    }
}
