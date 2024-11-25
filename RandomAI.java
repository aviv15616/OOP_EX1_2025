public class RandomAI extends Player {
    public RandomAI(boolean isPlayerOne) {
        super(isPlayerOne);
    }

    @Override
    boolean isHuman() {
        return false;
    }
}
