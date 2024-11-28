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


                flipDisc(pos);
            System.out.println("flipping pos:("+pos.getRow()+","+pos.getCol());


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
    public Disc [][] getBoard(){
        return this.board;
    }

    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < boardSize && col >= 0 && col < boardSize;
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


    public Set<Position> getFlips(Position position) {
        // Create a set to store the positions that should be flipped
       Set <Position> flips =new HashSet<>();

        // Loop through all 8 directions
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                // Add flips from each direction to the set
                flips.addAll(getFlipsInDir(position, dx, dy));
            }
        }



        flips.removeIf(currD2 -> {
            if (currD2 != null) {
                Disc discAtPos = board[currD2.getRow()][currD2.getCol()];
                return discAtPos != null && discAtPos.getOwner().equals(currentTurn);
            }
            return false;
        });
        removeDuplicates(flips);
       return flips;
    }
    // The isPosSame method is already defined as:

    public boolean isPosSame(Position a, Position b){
        return a.getRow()==b.getRow()&&a.getCol()==b.getCol();
    }
    public void removeDuplicates(Set<Position> positions) {
        Set<Position> uniquePositions = new HashSet<>(positions);
        positions.clear();  // Clear the original list
        positions.addAll(uniquePositions);  // Add the unique positions back
    }

    public Set<Position> getFlipsInDir(Position start, int dx, int dy) {
        Set<Position> processedBombs=new HashSet<>();
        Set<Position> flips = new HashSet<>();
        Set<Position> potentialFlips = new HashSet<>(); // Tracks the opponent's discs in the direction

        int x = start.getRow() + dx;
        int y = start.getCol() + dy;

        // Loop until we either go out of bounds (end of board) or encounter an empty space
        while (isValidPosition(x, y)) {
            Disc currD = board[x][y]; // Get the disc at the current position
            Position currP = new Position(x, y); // Current position

            if (currD == null) {
                break; // We stop the sequence when encountering an empty space
            }

            if (!isOwnDisc(currD) && !(currD instanceof UnflippableDisc)) {
                // Add opponent's discs to potential flips
                potentialFlips.add(currP);


            } else if (isOwnDisc(currD)) {
                // When we encounter our own disc
                if (!potentialFlips.isEmpty()) {
                    // Commit the opponent discs (if any) to flips
                    flips.addAll(potentialFlips);

                    // Set to track bombs that we need to process
                    for(Position pos:potentialFlips){
                        Disc disc=board[pos.getRow()][pos.getCol()];
                        if(disc instanceof BombDisc) {
                            flips.remove(pos);
                            triggerBomb(pos, flips, processedBombs);
                        }
                    }

                    // Process any bombs in the flips set after collecting all positions

                    // Remove flips that are not actually owned by the current player


                    // Print the positions being committed
                }

                break; // Stop processing when our own disc is encountered
            }

            // Move to the next position in the given direction
            x += dx;
            y += dy;
        }

        return flips;
    }


    private void triggerBomb(Position bomb, Set<Position> flips, Set<Position> processedBombs) {
        // Debug prints to track the current state of the sets
        System.out.println("Current flips set: " + flips);
        System.out.println("Current processedBombs set: " + processedBombs);

        // If this bomb has already been processed or flipped, return immediately
        if (processedBombs.contains(bomb) || flips.contains(bomb)) {
            System.out.println("Bomb at (" + bomb.getRow() + ", " + bomb.getCol() + ") already processed or flipped.");
            return; // Stop recursion if this bomb has already been triggered
        }

        // Add bomb to the flips set and processed bombs set

        processedBombs.add(bomb);  // Mark as processed
flips.add(bomb);
        System.out.println("Current flips set after: " + flips);
        System.out.println("Current processedBombs set after: " + processedBombs);
        // Debug print to confirm bomb is being added to the flips and processed sets
        System.out.println("Bomb at (" + bomb.getRow() + ", " + bomb.getCol() + ") added to flips and processed.");

        // For each neighboring position of the bomb, check if it should trigger another bomb or flip discs.
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;  // Skip the bomb's own position.

                int x = bomb.getRow() + dx;
                int y = bomb.getCol() + dy;

                if (isValidPosition(x, y)) {
                    Position adj = new Position(x, y);
                    Disc adjDisc = board[x][y];

                    if (adjDisc != null && !(adjDisc instanceof UnflippableDisc)) {
                        // Debug print to track the adjacent position and its disc type
                        System.out.println("Checking adjacent position: (" + x + ", " + y + "), Disc: " + adjDisc.getClass().getSimpleName());


                            // Case 1: If adjDisc is NOT your disc AND it is a BombDisc
                            if (!isOwnDisc(adjDisc) && adjDisc instanceof BombDisc && !processedBombs.contains(adj)) {
                                System.out.println("Adjacent bomb found at (" + x + ", " + y + "). Triggering...");
                                triggerBomb(adj, flips, processedBombs);  // Recursively trigger the adjacent bomb
                            }
                            // Case 2: If adjDisc IS your disc AND it is a BombDisc
                            else if (isOwnDisc(adjDisc) && adjDisc instanceof BombDisc) {
                                System.out.println("This is your BombDisc at (" + x + ", " + y + "). No action taken.");
                            }
                            // Case 3: If adjDisc is NOT your disc AND it is NOT a BombDisc
                            else if (!isOwnDisc(adjDisc) && !(adjDisc instanceof BombDisc)) {
                                flips.add(adj);  // Add to flips list if it's a valid disc
                            }
                            // Case 4: If adjDisc IS your disc AND it is NOT a BombDisc
                            else if (isOwnDisc(adjDisc) && !(adjDisc instanceof BombDisc)) {
                                System.out.println("This is your regular disc at (" + x + ", " + y + "). No action taken.");
                            }
                        }
                    }

            }
        }
    }








    private void flipDisc(Position a) {
        Disc d1 = board[a.getRow()][a.getCol()];
        if (d1.getOwner().equals(p1)) {
            d1.setOwner(p2);
        } else {
            d1.setOwner(p1);
        }
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
        p1.number_of_bombs = 3;
        p2.number_of_bombs = 3;

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
            currentTurn = currentTurn.equals(p1) ? p2 : p1;
            if(lastMove.getDisc() instanceof BombDisc)currentTurn.number_of_bombs++;
            if(lastMove.getDisc() instanceof UnflippableDisc)currentTurn.number_of_unflippedable++;
            board[p4.getRow()][p4.getCol()] = null;


            flipDiscs(lastMove);



        }


    }
}
