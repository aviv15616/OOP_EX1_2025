import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GreedyAI extends AIPlayer {
    public GreedyAI(boolean isPlayerOne) {
        super(isPlayerOne);
    }

    @Override
    public Move makeMove(PlayableLogic gameStatus) {
        List<Position> moves = gameStatus.ValidMoves();

        // Find the maximum position using a comparator
        Position maxPos = moves.stream()
                .max(Comparator.comparingInt(gameStatus::countFlips)
                        .thenComparingInt(Position::getCol) // Rightmost position (higher X)
                        .thenComparingInt(Position::getRow) // Bottommost position (higher Y))
                )
                .orElseThrow(() -> new IllegalStateException("No valid moves available"));

        // Create the disc with appropriate player
        Disc simple = new SimpleDisc(isPlayerOne ? gameStatus.getFirstPlayer() : gameStatus.getSecondPlayer());

        // Return the move
        return new Move(maxPos, simple);


    }
}
