import java.util.List;

public class MiniMaxAI extends AIPlayer {


    public MiniMaxAI(boolean isPlayerOne) {
        super(isPlayerOne);
    }


    public Position chooseBestMove(GameLogic gameStatus, int depth) {
        List<Position> validMoves = gameStatus.ValidMoves();

        if (validMoves.isEmpty()) {
            return null; // No valid moves
        }

        Position bestMove = null;
        Disc bestDisc = null;
        int bestValue = Integer.MIN_VALUE;

        // Iterate through all possible valid positions
        for (Position pos : validMoves) {
            // Create a copy of the game status to simulate the move
            GameLogic simulatedGame = gameStatus.cloneGame();

            // Define all the disc types to evaluate
            Disc[] discs = {
                    new SimpleDisc(this),
                    new UnflippableDisc(this),
                    new BombDisc(this)
            };

            int currentBestValue = Integer.MIN_VALUE;
            Disc currentBestDisc = null;

            // Evaluate all disc types
            for (Disc disc : discs) {
                // Simulate the move with this disc
                simulatedGame = gameStatus.cloneGame();  // Reset game before simulating the next move
                simulatedGame.locate_disc(pos, disc);
                Move move = new Move(pos, disc);

                // Simulate the move and evaluate it with minimax
                int moveValue = minimax(simulatedGame, depth - 1, false);
                simulatedGame.flipDiscs(move); // Reset the board after evaluating this move

                // Update the best value and disc if needed
                if (moveValue > currentBestValue) {
                    currentBestValue = moveValue;
                    currentBestDisc = disc;
                }
            }

            // After evaluating all three disc types, check if this is the best move overall
            if (currentBestValue > bestValue) {
                bestValue = currentBestValue;
                bestMove = pos;
                bestDisc = currentBestDisc;
            }
        }

        if (bestMove != null && bestDisc != null) {
            return bestMove;  // Return the best position
        }

        return null;  // Return null if no valid move found
    }

    private int minimax(GameLogic gameStatus, int depth, boolean isMaximizingPlayer) {
        // Base case: if the depth is 0 or game is over, evaluate the board
        if (depth == 0 || gameStatus.isGameFinished()) {
            return evaluateBoard(gameStatus); // Return the heuristic value
        }

        List<Position> validMoves = gameStatus.ValidMoves();
        if (validMoves.isEmpty()) {
            return isMaximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        }

        int bestValue;

        if (isMaximizingPlayer) {
            bestValue = Integer.MIN_VALUE;
            for (Position pos : validMoves) {
                // Evaluate with all disc types
                for (Disc disc : new Disc[] { new SimpleDisc(this), new UnflippableDisc(this), new BombDisc(this) }) {
                    GameLogic simulatedGame = gameStatus.cloneGame();
                    simulatedGame.locate_disc(pos, disc); // Place the disc on the board
                    Move move = new Move(pos, disc);
                    int moveValue = minimax(simulatedGame, depth - 1, false); // Recursively evaluate
                    simulatedGame.flipDiscs(move); // Reset the board after evaluating this move

                    // Take the maximum value from all disc types
                    bestValue = Math.max(bestValue, moveValue);
                }
            }
        } else {
            bestValue = Integer.MAX_VALUE;
            for (Position pos : validMoves) {
                // Evaluate with all disc types for opponent
                for (Disc disc : new Disc[] { new SimpleDisc(null), new UnflippableDisc(null), new BombDisc(null) }) {
                    GameLogic simulatedGame = gameStatus.cloneGame();
                    simulatedGame.locate_disc(pos, disc); // Place the opponent's disc
                    Move move = new Move(pos, disc);
                    int moveValue = minimax(simulatedGame, depth - 1, true); // Recursively evaluate
                    simulatedGame.flipDiscs(move); // Reset the board after evaluating this move

                    // Take the minimum value from all disc types
                    bestValue = Math.min(bestValue, moveValue);
                }
            }
        }

        return bestValue;
    }

    private int evaluateBoard(GameLogic gameStatus) {
        // Basic evaluation: count discs for AI ("A") and opponent ("B")
        int aiScore = gameStatus.countPlayerDiscs(gameStatus.getFirstPlayer());
        int opponentScore = gameStatus.countPlayerDiscs(gameStatus.getSecondPlayer());

        return aiScore - opponentScore; // Heuristic: difference in disc count
    }




    // Execute the AI's move
    @Override
    public Move makeMove(PlayableLogic gameStatus) {
        Position bestMove = chooseBestMove((GameLogic) gameStatus, 3);
        Disc bestDisc = null;

        if (bestMove != null) {
            // Find the best disc (Simple, Unflippable, Bomb) that should be placed
            Disc[] discs = {
                    new SimpleDisc(this),
                    new UnflippableDisc(this),
                    new BombDisc(this)
            };

            int bestValue = Integer.MIN_VALUE;

            for (Disc disc : discs) {
                GameLogic simulatedGame = ((GameLogic) gameStatus).cloneGame();
                simulatedGame.locate_disc(bestMove, disc);
                Move move = new Move(bestMove, disc);

                // Use minimax evaluation to find the best disc to place
                int moveValue = minimax(simulatedGame, 3, false);
                if (moveValue > bestValue) {
                    bestValue = moveValue;
                    bestDisc = disc;
                }
            }

            if (bestDisc != null) {
                System.out.println("AI played at position: " + bestMove);
                return new Move(bestMove, bestDisc);  // Make the move with the correct disc
            }
        }

        System.out.println("AI has no valid moves.");
        return null;
    }


}


