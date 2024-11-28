import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomAI extends AIPlayer {

    public RandomAI(boolean isPlayerOne) {
        super(isPlayerOne);
    }
    public Disc randDisc(PlayableLogic gameStatus) {
        ArrayList<String> options = new ArrayList<>();

        // Add options based on available items
        if (number_of_bombs > 0) {
            options.add("BombDisc");
        }
        if (number_of_unflippedable > 0) {
            options.add("Unflip");
        }
        options.add("Simple"); // Simple disc is always an option

        // Randomly select one of the options
        Random random = new Random();
        String selectedType = options.get(random.nextInt(options.size()));

        // Return a new instance of the selected type of Disc
        switch (selectedType) {
            case "BombDisc":
                return new BombDisc(isPlayerOne ? gameStatus.getFirstPlayer() : gameStatus.getSecondPlayer()); // Create and return a BombDisc instance
            case "Unflip":
                return new UnflippableDisc(isPlayerOne ? gameStatus.getFirstPlayer() : gameStatus.getSecondPlayer()); // Create and return an UnflippableDisc instance
            default:
                return new SimpleDisc(isPlayerOne ? gameStatus.getFirstPlayer() : gameStatus.getSecondPlayer()); // Default to a SimpleDisc instance
        }
    }

    @Override
    public Move makeMove(PlayableLogic gameStatus) {
            List<Position> moves=gameStatus.ValidMoves();
            int randomIndex = (int) (Math.random() * moves.size());  // Math.random() ge
            Disc randDisc=randDisc(gameStatus);
            return  new Move(moves.get(randomIndex),randDisc);
        }

}
