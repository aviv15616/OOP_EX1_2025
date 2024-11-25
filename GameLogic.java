import java.util.*;

public class GameLogic implements PlayableLogic {
    private Disc[][] board;
    private final int boardSize = 8;
    private int MAX = Integer.MAX_VALUE;
    private Player p1, p2;
    private Player currentTurn; // Track whose turn it is
    private boolean gameFinished = false; // Track if the game is finished
    private Stack<Move> moveHistory; // For undo functionality

    public GameLogic() {
        board = new Disc[boardSize][boardSize];
        moveHistory = new Stack<>();
        this.p1 = new HumanPlayer(true);
        this.p2 = new HumanPlayer(false);
        reset();
    }


    public boolean isValidBoard() {
        if (board == null || boardSize == 0) return false;
        return true;
    }

    public boolean hasBombFlipped(Move m1) {
        List<Position> flipped = m1.getFlippedDiscs();
        for (Position a : flipped) {
            int row = a.getRow();
            int col = a.getCol();
            if (board[row][col] instanceof BombDisc) return true;
        }
        return false;
    }


    @Override
    public boolean locate_disc(Position a, Disc disc) {
        int row = a.getRow();
        int col = a.getCol();
        if (disc instanceof BombDisc && isBomb0() || disc instanceof UnflippableDisc && isUnflip0())
            return false;
        if (!isValidBoard() || board[row][col] != null || !isValidMove(a) || !isValidPosition(row, col) || disc == null || disc.getOwner() == null)
            return false; // Checks if the disc exists, and whether it's the same disc as given.
        else if(disc instanceof BombDisc)currentTurn.number_of_bombs--;
        else if(disc instanceof UnflippableDisc)currentTurn.number_of_unflippedable--;

        board[row][col] = disc;
        Move m1 = new Move(a, disc);
        List<Position> flipped = new ArrayList<>();
        flipped = getFlips(a);
        m1.setFlippedDiscs(flipped);
        flipDiscs(m1);
        moveHistory.push(m1);
        currentTurn = currentTurn.equals(p1) ? p2 : p1;
        updateGame();
        return true;
    }

    public void updateGame() {
        if (ValidMoves().isEmpty()) {
            this.gameFinished = true;
            int player1Score = countPlayerDiscs(p1);
            int player2Score = countPlayerDiscs(p2);
            if (player1Score > player2Score) {
                showWinner(p1, player1Score, p2, player2Score);
                p1.wins++;
            } else if (player2Score > player1Score) {
                showWinner(p2, player2Score, p1, player1Score);
                p2.wins++;
            } else {
                System.out.println("It's a tie! Both players have " + player1Score + " discs.");
            }
        }
    }

    public void showWinner(Player winner, int winScore, Player loser, int loseScore) {
        System.out.println("Player " + (winner.isPlayerOne() ? "1" : "2") +
                " wins with " + winScore + " discs! Player " +
                (loser.isPlayerOne() ? "1" : "2") + " had " + loseScore + " discs.");
    }



    public int countPlayerDiscs(Player p1) {
        int score = 0;
        for (Disc[] d1 : board) {
            for (Disc d2 : d1) {
                if (p1.equals(d2.getOwner())) score++;
            }
        }
        return score;
    }

    public void flipDiscs(Move m1) {
        List<Position> toBeFlip = m1.getFlippedDiscs();
        for (Position pos : toBeFlip) {
            int row = pos.getRow();
            int col = pos.getCol();
            Disc currD = board[row][col];
            if (currD != null) {
                currD.setOwner(currD.getOwner().equals(p1) ? p2 : p1);
            }
        }
    }

    public boolean isBomb0() {
        return currentTurn.number_of_bombs ==0;
    }

    public boolean isUnflip0() {
        return currentTurn.number_of_unflippedable ==0;
    }


    @Override
    public Disc getDiscAtPosition(Position position) {
        int row = position.getRow();
        int col = position.getCol();
        if (!isValidBoard() || board[row][col] == null || !isValidPosition(row, col)) return null;
        return board[row][col];
    }

    @Override
    public int getBoardSize() {
        return boardSize;
    }


    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < boardSize && col >= 0 && col < boardSize;
    }

    public boolean isValidMove(Move m1) {
        return countFlips(m1.getPosition()) > 0;
    }

    public boolean isValidMove(Position a) {
        return isValidPosition(a.getRow(), a.getCol()) && board[a.getRow()][a.getCol()] == null && countFlips(a) > 0;
    }

    public List<Position> handleBomb(Position bombPosition, Set<Position> flippedBombs) {
        List<Position> affectedPositions = new ArrayList<>();

        // If this bomb has already been visited in the current direction, stop further processing
        if (flippedBombs.contains(bombPosition)) {
            System.out.println("Skipping already processed bomb at: " + bombPosition.getRow()+" ,"+bombPosition.getCol());
            return affectedPositions; // Return an empty list to prevent further processing
        }

        // Mark the bomb as visited to prevent infinite loops
        flippedBombs.add(bombPosition);
        System.out.println("Processing bomb at: " + bombPosition.getRow()+" ,"+bombPosition.getCol());

        int bombRow = bombPosition.getRow();
        int bombCol = bombPosition.getCol();

        // Add positions around the bomb (adjacent tiles)
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                // Skip the bomb's own position
                if (dx == 0 && dy == 0) continue;

                int newRow = bombRow + dx;
                int newCol = bombCol + dy;

                if (isValidPosition(newRow, newCol) && board[newRow][newCol] != null) {
                    Position adjacentPos = new Position(newRow, newCol);
                    Disc adjacentDisc = board[newRow][newCol];

                    System.out.println("Adding affected position: " + adjacentPos.getRow()+" ,"+adjacentPos.getCol());
                    affectedPositions.add(adjacentPos); // Add affected positions

                    if (adjacentDisc instanceof BombDisc && !flippedBombs.contains(adjacentPos)) {
                        // Trigger another bomb of a different color
                        if (!adjacentDisc.getOwner().equals(board[bombRow][bombCol].getOwner())) {
                            System.out.println("Triggering bomb at: " + adjacentPos.getRow()+" ,"+adjacentPos.getCol());
                            affectedPositions.addAll(handleBomb(adjacentPos, flippedBombs)); // Recursively trigger bombs
                        }
                    }
                }
            }
        }

        // Log the positions to be flipped
        System.out.println("Positions to be flipped after processing bomb at " + bombPosition.getRow()+" ,"+bombPosition.getCol() + ": " + affectedPositions);
        return affectedPositions;
    }






    @Override
    public List<Position> ValidMoves() {
        List<Position> Valid = new ArrayList<>();
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                Position p1 = new Position(i, j);
                if (isValidMove(p1)) Valid.add(p1);
            }
        }
        return Valid;
    }


    @Override
    public int countFlips(Position a) {
        return getFlips(a).size();
    }

    public List<Position> getFlips(Position a) {
        List<Position> flips = new ArrayList<>();

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue; // Skip the current position
                flips.addAll(getFlipsInDir(a, dx, dy)); // Get flips in each direction
            }
        }

        return flips;
    }

    public List<Position> getFlipsInDir(Position start, int dx, int dy) {
        List<Position> flips = new ArrayList<>();
        List<Position> potentialFlips = new ArrayList<>(); // Tracks the opponent's discs in the direction

        int x = start.getRow() + dx;
        int y = start.getCol() + dy;

        // Loop until we either go out of bounds (end of board) or encounter an empty space
        while (isValidPosition(x, y)) {
            Disc currD = board[x][y]; // Get the disc at the current position
            Position currP = new Position(x, y); // Current position

            if (currD == null) {
                // Encountering an empty space invalidates the sequence
                break; // We stop the sequence when encountering an empty space
            }

            if (isOpponentDisc(currD) && !(currD instanceof UnflippableDisc)) {
                // If it's an opponent's disc, add it to potential flips
                potentialFlips.add(currP);
            } else if (!isOpponentDisc(currD)) {
                // If it's our own disc, we can commit the flips
                if (!potentialFlips.isEmpty()) {
                    flips.addAll(potentialFlips); // Commit the flips if we found opponent discs
                }
                // Stop looking in this direction (either our own disc or an invalid sequence)
                break;
            }

            // Move to the next position in the given direction
            x += dx;
            y += dy;
        }

        // Return the flips collected
        return flips;
    }

    private List<Position> triggerBombEffects(Position bomb, Set<Position> flippedBombs) {
        List<Position> flips = new ArrayList<>();

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;

                int x = bomb.getRow() + dx;
                int y = bomb.getCol() + dy;

                if (isValidPosition(x, y)) {
                    Position adj = new Position(x, y);
                    Disc adjDisc = board[x][y];

                    if (adjDisc != null ) {
                        flips.add(adj);
                        if (adjDisc instanceof BombDisc&&!flippedBombs.contains(adj)) {
                            flippedBombs.add(adj); // Mark as flipped to avoid recursion
                            flips.addAll(triggerBombEffects(adj, flippedBombs)); // Recursively trigger bombs
                        }
                    }
                }
            }
        }

        return flips;
    }







    private boolean isEmpty(int x, int y) {
        return board[x][y]==null;
    }

    private boolean isOpponentDisc(Disc disc) {
        return !disc.getOwner().equals(currentTurn);
    }


    /**
     * Flips all discs surrounding a bomb disc to the current player's color.
     *
     * @param bombPos The position of the bomb disc to process
     */



    @Override
    public Player getFirstPlayer() {
        return p1;
    }

    @Override
    public Player getSecondPlayer() {
        return p2;
    }

    @Override
    public void setPlayers(Player player1, Player player2) {
        this.p1 = player1;
        this.p2 = player2;
        this.currentTurn = player1; // Set player1 as the first player to play
    }

    @Override
    public boolean isFirstPlayerTurn() {
        return currentTurn.equals(p1);
    }

    @Override
    public boolean isGameFinished() {
            return gameFinished;
    }

    @Override
    public void reset() {
        board = new Disc[boardSize][boardSize];
        gameFinished = false;
        moveHistory.clear();
        p1.number_of_unflippedable=2;
        p2.number_of_unflippedable=2;
        p1.number_of_bombs=3;
        p2.number_of_bombs=3;

        if (p1 == null || p2 == null) {
            throw new IllegalStateException("No Players");
        }

        board[3][3] = new SimpleDisc(p1);
        board[3][4] = new SimpleDisc(p2);
        board[4][3] = new SimpleDisc(p2);
        board[4][4] = new SimpleDisc(p1);
        currentTurn = p1;
    }

    @Override
    public void undoLastMove() {
        // Undo the last move using moveHistory
        if (moveHistory.isEmpty()) {
            System.out.println("No Previous moves");
        } else {

            Move lastMove = moveHistory.pop();

            Position p4 = lastMove.getPosition();
            board[p4.getRow()][p4.getCol()] = null;
            if(lastMove.getDisc() instanceof BombDisc)currentTurn.number_of_bombs++;
            else if(lastMove.getDisc() instanceof UnflippableDisc)currentTurn.number_of_unflippedable++;

            flipDiscs(lastMove);
            currentTurn = currentTurn.equals(p1) ? p2 : p1;

        }
    }

}
