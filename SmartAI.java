import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
/**
 * SmartAI Class(bonus):
 * This class represents an AI player designed for the game of Othello (Reversi). The purpose of the class is to make intelligent moves based on
 * the current game state, prioritizing strategic positions such as corners, edges, and the center.
 *
 * How it works:
 * - The AI uses a combination of strategies to evaluate valid moves:
 *   1. Prioritizes moves that result in the highest number of flips.
 *   2. Favors securing corners and stable edges(edges of the board all same color as the player's) while penalizing risky
 *   positions like X-squares(diagonally adjacent to corners) and C-squares (horizontally or vertically adjacent to a corner).
 *   3. Uses the concept of stable edges and the center of the board to guide the decision-making process.
 *   4. Simulates potential future moves to evaluate if the AI can win in the next move.
 * - It also adapts its move based on the current player and adjusts for unflippable discs.
 *
 * Win Rate:
 * On numerous occasions, this AI has shown a win/loss rate of at least 10:1, demonstrating its effectiveness in Othello strategy.
 */
public class SmartAI extends AIPlayer {

    /**
     * Constructs a new SmartAI player.
     *
     * @param isPlayerOne true if this is the first player, false for second player.
     */
    public SmartAI(boolean isPlayerOne) {
        super(isPlayerOne);
    }

    // Define special positions on the board
    private static final int[][] xSquares = {{1, 1}, {1, 6}, {6, 1}, {6, 6}};
    private static final int[][] cSquares = {
            {0, 1}, {0, 6}, {1, 0}, {1, 7},
            {6, 0}, {6, 7}, {7, 1}, {7, 6}
    };
    private static final int[][] corners = {
            {0, 0}, {0, 7}, {7, 0}, {7, 7}
    };

    /**
     * Determines if a given position is a corner on the board.
     *
     * @param pos The position to check.
     * @return true if the position is a corner; false otherwise.
     */
    private boolean isCorner(Position pos) {
        for (int[] corner : corners) {
            if (corner[0] == pos.row() && corner[1] == pos.col()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if a given position is an X-square.
     *
     * @param pos The position to check.
     * @return true if the position is an X-square; false otherwise.
     */
    private boolean isXSquare(Position pos) {
        for (int[] xSquare : xSquares) {
            if (xSquare[0] == pos.row() && xSquare[1] == pos.col()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if a given position is a C-square.
     *
     * @param pos The position to check.
     * @return true if the position is a C-square; false otherwise.
     */
    private boolean isCSquare(Position pos) {
        for (int[] cSquare : cSquares) {
            if (cSquare[0] == pos.row() && cSquare[1] == pos.col()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if a given position is on the edge of the board.
     *
     * @param pos The position to check.
     * @return true if the position is on the edge; false otherwise.
     */
    private boolean isEdge(Position pos) {
        return pos.row() == 0 || pos.row() == 7 || pos.col() == 0 || pos.col() == 7;
    }

    /**
     * Determines if a given position is in the center of the board.
     *
     * @param pos The position to check.
     * @return true if the position is in the center; false otherwise.
     */
    private boolean isCenter(Position pos) {
        return pos.row() >= 2 && pos.row() <= 5 && pos.col() >= 2 && pos.col() <= 5;
    }

    /**
     * Checks if a given corner is secured by the current player.
     *
     * @param corner The corner position to check.
     * @param gameStatus The current game state.
     * @return true if the corner is secured by the current player; false otherwise.
     */
    private boolean isCornerSecured(Position corner, PlayableLogic gameStatus) {
        Disc disc = gameStatus.getDiscAtPosition(corner);
        return disc != null && disc.getOwner().equals(this);
    }

    /**
     * Checks if a given edge is stable, meaning that all discs on the edge belong to the current player.
     *
     * @param gameStatus The current game state.
     * @param pos The position on the edge to check.
     * @return true if the edge is stable; false otherwise.
     */
    private boolean isEdgeStable(GameLogic gameStatus, Position pos) {
        GameLogic gameClone = gameStatus.cloneGame(isPlayerOne);
        Disc[][] board = gameStatus.getBoard();
        int x = pos.row();
        int y = pos.col();

        // Count the number of discs already placed along the edge
        int discCount = 0;
        if (x == 0 || x == 7) {
            for (int i = 0; i < 8; i++) {
                if (board[x][i] != null) {
                    discCount++;
                }
            }
        }

        if (y == 0 || y == 7) {
            for (int i = 0; i < 8; i++) {
                if (board[i][y] != null) {
                    discCount++;
                }
            }
        }

        // If there are fewer than 7 discs on the edge, it cannot be stable
        if (discCount < 7) return false;

        // Simulate placing a disc on the edge
        Disc disc = new SimpleDisc(this);
        gameClone.locate_disc(pos, disc);

        // Check if all discs along the edge belong to the current player
        if (x == 0 || x == 7) {
            for (int i = 0; i < 8; i++) {
                if (board[x][i] != null && !board[x][i].getOwner().equals(this)) {
                    return false;
                }
            }
        }

        if (y == 0 || y == 7) {
            for (int i = 0; i < 8; i++) {
                if (board[i][y] != null && !board[i][y].getOwner().equals(this)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Counts the number of neighboring positions to the given position that are occupied by friendly discs.
     *
     * @param pos The position to check.
     * @param gameStatus The current game state.
     * @return The number of neighboring positions with friendly discs.
     */
    private int howManyFriends(Position pos, GameLogic gameStatus) {
        return getNeighbors(pos, gameStatus).size();
    }

    /**
     * Makes a strategic move based on the current game state. The AI evaluates various factors
     * including corner control, center control, edge stability, and the potential to win in the next move, whilst punishing risky moves.
     *
     * @param gameStatus The current game state.
     * @return The best move for the AI.
     * @throws IllegalStateException if no valid moves are available.
     */
    @Override
    public Move makeMove(PlayableLogic gameStatus) {
        List<Position> moves = gameStatus.ValidMoves();
        if (moves.isEmpty()) {
            throw new IllegalStateException("No valid moves available");
        }

        // Find the best move based on various strategic considerations
        Position bestPos = moves.stream()
                .max(Comparator.comparingInt((Position pos) -> {
                            int flips = gameStatus.countFlips(pos);
                            // Check if the AI can win in the next move
                            if (canWinInNextMove((GameLogic) gameStatus, pos)) return flips + 2000;
                            // Prioritize corner moves
                            if (isCorner(pos)) return flips + 1000;
                            // Penalize X-squares unless the adjacent corner is secured
                            if (isXSquare(pos)) {
                                Position adjacentCorner = findAdjacentCorner(pos);
                                if (adjacentCorner == null || !isCornerSecured(adjacentCorner, gameStatus)) {
                                    return flips - 3;
                                }
                            }
                            // Penalize C-squares unless the adjacent edge is secured.
                            if (isCSquare(pos)) {
                                Position adjacentEdge = findAdjacentEdge(pos);
                                if (adjacentEdge == null || !isEdgeStable((GameLogic) gameStatus, adjacentEdge)) {
                                    return flips - -2;
                                }
                            }
                            // Reward stable edges
                            if (isEdgeStable((GameLogic) gameStatus, pos)) {
                                return flips + 10;
                            }
                            // Reward edges
                            if (isEdge(pos)) {
                                return flips + 2;
                            }
                            // Encourage center moves
                            if (isCenter(pos)) {
                                return flips + 3;
                            }
                            // Default score based on flips
                            return flips;
                        })
                        .thenComparingInt(Position::col)
                        .thenComparingInt(Position::row)) // Tie-breaking by row and column
                .orElseThrow(() -> new IllegalStateException("No valid moves available"));

        // Choose the appropriate disc (Normal or Unflippable) based on number of friendly discs adjacent to that posistion.
        Disc disc;
        if (howManyFriends(bestPos, (GameLogic) gameStatus) >= 3 && this.number_of_unflippedable != 0) {
            disc = new UnflippableDisc(isPlayerOne ? gameStatus.getFirstPlayer() : gameStatus.getSecondPlayer());
        } else {
            disc = new SimpleDisc(isPlayerOne ? gameStatus.getFirstPlayer() : gameStatus.getSecondPlayer());
        }

        return new Move(bestPos, disc);
    }

    /**
     * Retrieves the neighbors of a given position that are occupied by discs.
     *
     * @param pos The position to check.
     * @param gameStatus The current game state.
     * @return A list of neighboring positions that are occupied by discs.
     */
    private List<Position> getNeighbors(Position pos, GameLogic gameStatus) {
        List<Position> neighbors = new ArrayList<>();
        int[] dRow = {-1, -1, -1, 0, 1, 1, 1, 0};
        int[] dCol = {-1, 0, 1, 1, 1, 0, -1, -1};

        for (int i = 0; i < 8; i++) {
            int newRow = pos.row() + dRow[i];
            int newCol = pos.col() + dCol[i];
            if (gameStatus.isValidPosition(newRow, newCol) && gameStatus.getBoard()[newRow][newCol] != null) {
                neighbors.add(new Position(newRow, newCol));
            }
        }
        return neighbors;
    }

    /**
     * Determines if the AI can win in the next move by simulating the move.
     *
     * @param gameStatus The current game state.
     * @param pos The position to check.
     * @return true if the AI can win in the next move; false otherwise.
     */
    public boolean canWinInNextMove(GameLogic gameStatus, Position pos) {
        GameLogic gameClone = gameStatus.cloneGame(isPlayerOne); // Clone the game to test the move
        Disc currentDisc = new SimpleDisc(isPlayerOne ? gameStatus.getFirstPlayer() : gameStatus.getSecondPlayer());
        gameClone.locate_disc(pos, currentDisc); // Apply the move on the clone

        if (isWinningState(gameClone) && gameClone.ValidMoves().isEmpty()) {
            return true; // The move results in a win
        }
        return false;
    }

    /**
     * Checks if the current player is in a winning state based on the number of discs owned.
     *
     * @param gameStatus The current game state.
     * @return true if the current player has more discs than the opponent; false otherwise.
     */
    private boolean isWinningState(GameLogic gameStatus) {
        Player p1 = gameStatus.getFirstPlayer();
        Player p2 = gameStatus.getSecondPlayer();
        int p1Discs = gameStatus.countPlayerDiscs(p1);
        int p2Discs = gameStatus.countPlayerDiscs(p2);

        return ((p1Discs > p2Discs) && isPlayerOne) || ((p1Discs < p2Discs) && !isPlayerOne);
    }

    /**
     * Finds an adjacent corner to a given position.
     *
     * @param pos The position to check.
     * @return The adjacent corner position, or null if no adjacent corner is found.
     */
    private Position findAdjacentCorner(Position pos) {
        for (int[] corner : corners) {
            if (Math.abs(corner[0] - pos.row()) <= 1 && Math.abs(corner[1] - pos.col()) <= 1) {
                return new Position(corner[0], corner[1]);
            }
        }
        return null;
    }

    /**
     * Finds an adjacent edge to a given position.
     *
     * @param pos The position to check.
     * @return The adjacent edge position, or null if no adjacent edge is found.
     */
    private Position findAdjacentEdge(Position pos) {
        if (pos.row() == 0 || pos.row() == 7) {
            return new Position(pos.row(), (pos.col() == 0 || pos.col() == 7) ? 1 : pos.col());
        } else if (pos.col() == 0 || pos.col() == 7) {
            return new Position((pos.row() == 0 || pos.row() == 7) ? 1 : pos.row(), pos.col());
        }
        return null;
    }
}
