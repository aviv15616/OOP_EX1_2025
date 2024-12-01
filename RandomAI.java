import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents a random AI player that selects its moves and disc types at random.
 * This AI randomly chooses a valid move and then selects a disc type (Bomb, Unflippable, or Simple)
 * to place on the board.
 */
public class RandomAI extends AIPlayer {

    /**
     * Constructs a RandomAI player with the specified player side.
     *
     * @param isPlayerOne A boolean indicating whether this is Player One.
     *                    If {@code true}, the player is Player One; otherwise, Player Two.
     */
    public RandomAI(boolean isPlayerOne) {
        super(isPlayerOne);
    }

    /**
     * Randomly selects a disc type based on the available options (BombDisc, UnflippableDisc, or SimpleDisc).
     * The disc type is chosen based on the availability of certain discs and a random selection.
     *
     * @param gameStatus The current game state, used to check available discs and players.
     * @return A randomly selected disc based on available options. This can be a BombDisc, UnflippableDisc, or SimpleDisc.
     */
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

    /**
     * Makes a random move by selecting a valid move from the list of available moves and
     * randomly choosing a disc type to place.
     *
     * @param gameStatus The current game state, used to retrieve valid moves and the status of the game.
     * @return A Move object representing the randomly selected move. The move includes a randomly selected position
     * and disc type.
     */
    @Override
    public Move makeMove(PlayableLogic gameStatus) {
        // Get a list of valid moves from the game state
        List<Position> moves = gameStatus.ValidMoves();

        // Randomly select a valid move
        int randomIndex = (int) (Math.random() * moves.size());  // Select a random index from the list of valid moves

        // Select a random disc type to place for the move
        Disc randDisc = randDisc(gameStatus);

        // Return a new Move object with the randomly selected position and disc
        return new Move(moves.get(randomIndex), randDisc);
    }
}
