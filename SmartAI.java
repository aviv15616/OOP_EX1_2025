import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SmartAI extends AIPlayer {

    public SmartAI(boolean isPlayerOne) {
        super(isPlayerOne);
    }

    // Define the corner, xSquares, and cSquares
    private static final int[][] xSquares = {{1, 1}, {1, 6}, {6, 1}, {6, 6}};
    private static final int[][] cSquares = {
            {0, 1}, {0, 6}, {1, 0}, {1, 7},
            {6, 0}, {6, 7}, {7, 1}, {7, 6}
    };
    private static final int[][] corners = {
            {0, 0}, {0, 7}, {7, 0}, {7, 7}
    };

    // Helper methods to identify special positions
    private boolean isCorner(Position pos) {
        for (int[] corner : corners) {
            if (corner[0] == pos.row() && corner[1] == pos.col()) {
                return true;
            }
        }
        return false;
    }

    private boolean isXSquare(Position pos) {
        for (int[] xSquare : xSquares) {
            if (xSquare[0] == pos.row() && xSquare[1] == pos.col()) {
                return true;
            }
        }
        return false;
    }

    private boolean isCSquare(Position pos) {
        for (int[] cSquare : cSquares) {
            if (cSquare[0] == pos.row() && cSquare[1] == pos.col()) {
                return true;
            }
        }
        return false;
    }

    private boolean isEdge(Position pos) {
        return pos.row() == 0 || pos.row() == 7 || pos.col() == 0 || pos.col() == 7;
    }

    private boolean isCenter(Position pos) {
        return pos.row() >= 2 && pos.row() <= 5 && pos.col() >= 2 && pos.col() <= 5;
    }

    // Check if the adjacent corner is secure
    private boolean isCornerSecured(Position corner, PlayableLogic gameStatus) {
        Disc disc = gameStatus.getDiscAtPosition(corner);
        return disc != null && disc.getOwner().equals(this);
    }
    private boolean isEdgeStable(GameLogic gameStatus, Position pos) {
        GameLogic gameClone = gameStatus.cloneGame(isPlayerOne);
        Disc[][] board = gameStatus.getBoard();
        int x = pos.row();
        int y = pos.col();

        // Count the number of discs already placed along the edge (top/bottom row or left/right column)
        int discCount = 0;

        // If the position is on the top or bottom row (x == 0 or x == 7)
        if (x == 0 || x == 7) {
            for (int i = 0; i < 8; i++) {
                if (board[x][i] != null) {
                    discCount++;
                }
            }
        }

        // If the position is on the left or right column (y == 0 or y == 7)
        if (y == 0 || y == 7) {
            for (int i = 0; i < 8; i++) {
                if (board[i][y] != null) {
                    discCount++;
                }
            }
        }

        // If there are fewer than 7 discs already on the edge, return false
        if (discCount < 7) {
            return false;
        }

        // Now simulate the move by placing the disc on the edge
        Disc disc = new SimpleDisc(this);
        gameClone.locate_disc(pos, disc);

        // Check if all discs along the edge belong to the current player
        if (x == 0 || x == 7) { // Check top or bottom row
            for (int i = 0; i < 8; i++) {
                if (board[x][i] != null && !board[x][i].getOwner().equals(this)) {
                    return false; // If any disc on the edge is not owned by the current player
                }
            }
        }

        if (y == 0 || y == 7) { // Check left or right column
            for (int i = 0; i < 8; i++) {
                if (board[i][y] != null && !board[i][y].getOwner().equals(this)) {
                    return false; // If any disc on the edge is not owned by the current player
                }
            }
        }

        return true; // If all discs on the edge belong to the current player
    }
    private int howManyFriend(Position pos,GameLogic gameStatus){
        return getNeighbors(pos,gameStatus).size();
    }


    // Enhanced makeMove logic
    @Override
    public Move makeMove(PlayableLogic gameStatus) {
        List<Position> moves = gameStatus.ValidMoves();

        if (moves.isEmpty()) {
            throw new IllegalStateException("No valid moves available");
        }

        Position bestPos = moves.stream()
                .max(Comparator.comparingInt((Position pos) -> {
                            int flips = gameStatus.countFlips(pos);
                            if(canWinInNextMove((GameLogic) gameStatus,pos))return flips+2000;
                            // Corner priority
                            if (isCorner(pos)) return flips + 1000;
                            // Penalize X-squares unless adjacent corner is secured
                            if (isXSquare(pos)) {
                                Position adjacentCorner = findAdjacentCorner(pos);
                                if (adjacentCorner == null || !isCornerSecured(adjacentCorner, gameStatus)) {
                                    return flips - 3; // High penalty for X-squares
                                }
                            }
                            // Penalize C-squares unless adjacent edge is stable
                            if (isCSquare(pos)) {
                                Position adjacentEdge = findAdjacentEdge(pos);
                                if (adjacentEdge == null || !isEdgeStable((GameLogic) gameStatus, adjacentEdge)) {
                                    return flips - -2; // Moderate penalty for C-squares
                                }
                            }
                            // Reward stable edges
                            if (isEdgeStable((GameLogic) gameStatus, pos)) {
                                return flips + 10;
                            }
                            // Reward moves on edges (unstable)
                            if (isEdge(pos)) {
                                return flips + 2;
                            }

                            // Encourage center moves in the early game
                            if (isCenter(pos) ) {
                                return flips + 3;
                            }

                            // Default score based on flips
                            return flips;
                        })
                        .thenComparingInt(Position::col)
                        .thenComparingInt(Position::row)) // Tie-breaking
                .orElseThrow(() -> new IllegalStateException("No valid moves available"));

        // Create the disc with the appropriate player
        Disc disc;
        if(howManyFriend(bestPos,(GameLogic)gameStatus)>=3&&this.number_of_unflippedable!=0){
            disc = new UnflippableDisc(isPlayerOne ? gameStatus.getFirstPlayer() : gameStatus.getSecondPlayer());
        }
        else {
            disc = new SimpleDisc(isPlayerOne ? gameStatus.getFirstPlayer() : gameStatus.getSecondPlayer());
        }
        // Return the best move
        return new Move(bestPos, disc);
    }
    private List<Position> getNeighbors(Position pos,GameLogic gameStatus) {
        List<Position> neighbors = new ArrayList<>();
        int[] dRow = {-1, -1, -1, 0, 1, 1, 1, 0};
        int[] dCol = {-1, 0, 1, 1, 1, 0, -1, -1};

        for (int i = 0; i < 8; i++) {
            int newRow = pos.row() + dRow[i];
            int newCol = pos.col() + dCol[i];
            if (gameStatus.isValidPosition(newRow, newCol)&& gameStatus.getBoard()[newRow][newCol]!=null) {
                neighbors.add(new Position(newRow, newCol));
            }
        }
        return neighbors;
    }
    public boolean canWinInNextMove(GameLogic gameStatus,Position pos) {
        // Get the list of valid moves for the current player


        // Iterate through each valid move

        // Simulate the move
        GameLogic gameClone = gameStatus.cloneGame(isPlayerOne); // Clone the game to test the move
        Disc currentDisc = new SimpleDisc(isPlayerOne ? gameStatus.getFirstPlayer() : gameStatus.getSecondPlayer());
        gameClone.locate_disc(pos, currentDisc); // Apply the move on the clone

        // Simulate the flip operation for this move

        // Check if this move results in a win
        if (isWinningState(gameClone)&& gameClone.ValidMoves().isEmpty()) {
            return true; // If the move results in a win, return true
        }
        return false;
    }

    private boolean isWinningState(GameLogic gameStatus) {
        // Check if the current player has more discs than the opponent or if they have all discs
        Player p1=gameStatus.getFirstPlayer();
        Player p2=gameStatus.getSecondPlayer();
        int p1Discs = gameStatus.countPlayerDiscs(p1);
        int p2Discs = gameStatus.countPlayerDiscs(p2);

        // A player wins if they have more discs than the opponent
        return ((p1Discs > p2Discs)&&isPlayerOne)||((p1Discs < p2Discs)&&!isPlayerOne);
    }
    private Position findAdjacentCorner(Position pos) {
        for (int[] corner : corners) {
            if (Math.abs(corner[0] - pos.row()) <= 1 && Math.abs(corner[1] - pos.col()) <= 1) {
                return new Position(corner[0], corner[1]);
            }
        }
        return null;
    }

    private Position findAdjacentEdge(Position pos) {
        if (pos.row() == 0 || pos.row() == 7) {
            return new Position(pos.row(), (pos.col() == 0 || pos.col() == 7) ? 1 : pos.col());
        } else if (pos.col() == 0 || pos.col() == 7) {
            return new Position((pos.row() == 0 || pos.row() == 7) ? 1 : pos.row(), pos.col());
        }
        return null;
    }
}