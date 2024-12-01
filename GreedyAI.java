import java.util.Comparator;
import java.util.List;

/**
 * A greedy AI player that makes a move based on the maximum number of flips it can achieve.
 * This class extends the AIPlayer class and implements the makeMove method to select the
 * best move based on the game state.
 */
public class GreedyAI extends AIPlayer {

    /**
     * Constructor for the GreedyAI class.
     * Initializes the GreedyAI player, setting whether they are player one or player two.
     *
     * @param isPlayerOne A boolean indicating if this AI is player one (true) or player two (false).
     */
    public GreedyAI(boolean isPlayerOne) {
        super(isPlayerOne);
    }

    /**
     * Makes the best move for the greedy AI player.
     * This method evaluates all valid moves and selects the one that maximizes the number of
     * flips (captures) the AI can make. If multiple moves result in the same number of flips,
     * the rightmost (highest X-coordinate) and bottommost (highest Y-coordinate) positions
     * are preferred.
     *
     * @param gameStatus The current state of the game, used to determine valid moves and the
     *                   number of flips for each move.
     * @return A Move object representing the best move for the AI, including the position and
     *         the disc to be played.
     * @throws IllegalStateException If no valid moves are available, indicating that the AI cannot
     *                               make a move.
     */
    @Override
    public Move makeMove(PlayableLogic gameStatus) {
        List<Position> moves = gameStatus.ValidMoves();

        // Find the maximum position using a comparator
        Position maxPos = moves.stream()
                .max(Comparator.comparingInt(gameStatus::countFlips)
                        .thenComparingInt(Position::col) // Rightmost position (higher X)
                        .thenComparingInt(Position::row) // Bottommost position (higher Y))
                )
                .orElseThrow(() -> new IllegalStateException("No valid moves available"));

        // Create the disc with appropriate player
        Disc simple = new SimpleDisc(isPlayerOne ? gameStatus.getFirstPlayer() : gameStatus.getSecondPlayer());

        // Return the move
        return new Move(maxPos, simple);
    }
}