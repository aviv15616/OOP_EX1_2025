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

    @Override
    public boolean locate_disc(Position a, Disc disc) {
        int row = a.getRow();
        int col = a.getCol();
        if (disc instanceof BombDisc && isBomb0() || disc instanceof UnflippableDisc && isUnflip0())
            return false;
        if (!isValidBoard() || board[row][col] != null || !isValidMove(a) || !isValidPosition(row, col) || disc == null || disc.getOwner() == null)
            return false;
        else if (disc instanceof BombDisc) currentTurn.number_of_bombs--;
        else if (disc instanceof UnflippableDisc) currentTurn.number_of_unflippedable--;

        board[row][col] = disc;
        Move m1 = new Move(a, disc);
        List<Position> flipped = new ArrayList<>(getFlips(a));
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
                flipDisc(pos);
            }
        }
    }

    public boolean isBomb0() {
        return currentTurn.number_of_bombs == 0;
    }

    public boolean isUnflip0() {
        return currentTurn.number_of_unflippedable == 0;
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

    public Set<Position> getFlips(Position a) {
        Set<Position> flips = new HashSet<>();
        Set<Position> flippedBombs = new HashSet<>();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                flips.addAll(getFlipsInDir(a, dx, dy, flippedBombs));


                // Ensure only valid flips are added

            }
        }
        flips.removeIf(bombPosition -> {
            // Get the disc at bombPosition and check if its owner is the current player
            Disc bombDisc = board[bombPosition.getRow()][bombPosition.getCol()];  // Fetch the disc at bombPosition
            return bombDisc != null && bombDisc.getOwner().equals(currentTurn);  // Remove bombPosition if its owner matches currentTurn
        });
        return flips;
    }

    public Set<Position> getFlipsInDir(Position start, int dx, int dy, Set<Position> flippedBombs) {
        Set<Position> flips = new HashSet<>();
        Set<Position> potentialFlips = new HashSet<>();

        int x = start.getRow() + dx;
        int y = start.getCol() + dy;

        while (isValidPosition(x, y)) {
            Disc currD = board[x][y];
            Position currP = new Position(x, y);

            if (currD == null) {
                // Stop on empty space
                break;
            }
            else if (currD instanceof BombDisc) {
                if (!flippedBombs.contains(currP)) {
                    if (!isOwnDisc(currD)) {
                        // Trigger opponent bomb and collect flips
                        Set<Position> bombFlips = triggerBomb(currP, flippedBombs);
                        potentialFlips.add(currP);  // Include the bomb itself
                        potentialFlips.addAll(bombFlips);  // Include triggered flips
                    } else {
                        // Include player-owned bomb but stop the sequence
                        potentialFlips.add(currP);
                        flips.addAll(potentialFlips);
                        break;
                    }
                } else {
                    // Skip already processed bombs
                    break;
                }
            }
            else if (!isOwnDisc(currD) && !(currD instanceof UnflippableDisc)) {
                // Add opponent's flippable disc
                potentialFlips.add(currP);
            }
            else if (isOwnDisc(currD)) {
                // Chain is enclosed; add potential flips
                flips.addAll(potentialFlips);
                break;
            }
            else {
                // Invalid chain; stop
                break;
            }

            // Move in the direction (dx, dy)
            x += dx;
            y += dy;
        }

        return flips;
    }

    private Set<Position> triggerBomb(Position bomb, Set<Position> flippedBombs) {
        Set<Position> flips = new HashSet<>();
        if (flippedBombs.contains(bomb)) return new HashSet<>();

        Disc bombD = board[bomb.getRow()][bomb.getCol()];


        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue; // Skip the bomb's position
                int x = bomb.getRow() + dx;
                int y = bomb.getCol() + dy;

                if (isValidPosition(x, y)) {
                    Position adj = new Position(x, y);
                    Disc adjDisc = board[x][y];

                    if (adjDisc != null ) {
                        flips.add(adj); // Add adjacent disc

                        if (adjDisc instanceof BombDisc && !isOwnDisc(adjDisc) && adjDisc.getOwner().equals(bombD.getOwner())) {
                            flippedBombs.add(adj);

                            // Trigger adjacent bomb if conditions are met
                            flips.addAll(triggerBomb(adj, flippedBombs));

                        };
                    }
                }
            }
        }

        return flips;
    }

    private void flipDisc(Position a) {
        board[a.getRow()][a.getCol()].setOwner(
                board[a.getRow()][a.getCol()].getOwner().equals(p1) ? p2 : p1
        );
    }


    private boolean isEmpty(int x, int y) {
        return board[x][y] == null;
    }

    private boolean isOwnDisc(Disc disc) {
        return disc.getOwner().equals(currentTurn);
    }

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
        this.currentTurn = player1;
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
        p1.number_of_unflippedable = 2;
        p2.number_of_unflippedable = 2;
        p1.number_of_bombs = 2;
        p2.number_of_bombs = 2;

        // Initialize the board with starting discs
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
