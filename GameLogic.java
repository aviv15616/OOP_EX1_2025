import java.util.*;

public class GameLogic implements PlayableLogic {
    private Disc[][] board;
    private final int boardSize = 8;
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
    public GameLogic cloneGame(boolean isPlayerOne) {
        GameLogic clonedGame = new GameLogic();

        // Clone the board (assuming board elements are not null)
        clonedGame.board = new Disc[boardSize][boardSize];
        for (int i = 0; i < boardSize; i++) {
            System.arraycopy(this.board[i], 0, clonedGame.board[i], 0, boardSize);
        }

        // Clone the players
        clonedGame.p1 =new HumanPlayer(isPlayerOne);
        clonedGame.p2 =new HumanPlayer(!isPlayerOne);
        clonedGame.p1.number_of_bombs=p1.number_of_bombs;
        clonedGame.p2.number_of_unflippedable=p1.number_of_unflippedable;




        // Set the current turn based on the original game's state
        clonedGame.currentTurn = (this.isFirstPlayerTurn()) ? clonedGame.p1 : clonedGame.p2;

        // Clone the move history (using new stack)
        clonedGame.moveHistory = new Stack<>();
        clonedGame.moveHistory.addAll(this.moveHistory);

        // Clone other relevant state
        clonedGame.gameFinished = this.gameFinished;

        return clonedGame;
    }

    public boolean isValidBoard() {
        if (board == null || boardSize == 0) return false;
        return true;
    }

    @Override
    public boolean locate_disc(Position a, Disc disc) {
        int row = a.row();
        int col = a.col();
        if (disc instanceof BombDisc && isBomb0() || disc instanceof UnflippableDisc && isUnflip0())
            return false;
        if (!isValidBoard() || board[row][col] != null || !isValidMove(a) || !isValidPosition(row, col) || disc == null || disc.getOwner() == null)
            return false;
        else if (disc instanceof BombDisc) currentTurn.number_of_bombs--;
        else if (disc instanceof UnflippableDisc) currentTurn.number_of_unflippedable--;

        board[row][col] = disc;
        printDiscLocate(disc.getType(), a);
        Move m1 = new Move(a, disc);
        List<Position> flipped = new ArrayList<>(getFlips(a));
        m1.setFlippedDiscs(flipped);
        flipDiscs(m1);
        printFlippedPlayer(flipped);
        moveHistory.push(m1);
        currentTurn = currentTurn.equals(p1) ? p2 : p1;

        updateGame();
        System.out.println();
        return true;
    }
    public Disc [][] getBoard(){
        return this.board;
    }



    private void printFlippedPlayer(List<Position> flipped) {

        for (Position pos : flipped) {
            String type = board[pos.row()][pos.col()].getType();
            System.out.println(
                    isFirstPlayerTurn() ?
                            "Player 1 flipped the " + type + " in (" + pos.row() + "," + pos.col() + ")" :
                            "Player 2 flipped the " + type + " in (" + pos.row() + "," + pos.col() + ")"
            );
        }
    }

    private void printFlippedUndo(List<Position> flipped) {

        for (Position pos : flipped) {
            Disc disc = board[pos.row()][pos.col()];
            if (disc != null) {
                String type = disc.getType();
                System.out.println("\tUndo: flipping back " + type + " in (" + pos.row() + "," + pos.col() + ")");
            }
        }
    }

    private void printDiscLocate(String type, Position a) {
        System.out.println(
                currentTurn == p1 ?
                        "Player 1 placed a " + type + " in (" + a.row() + "," + a.col() + ")" :
                        "Player 2 placed a " + type + " in (" + a.row() + "," + a.col() + ")"
        );
    }

    public void updateGame() {
        if (ValidMoves().isEmpty()) {
            this.gameFinished = true;
            int player1Score = countPlayerDiscs(p1);
            int player2Score = countPlayerDiscs(p2);
            if (player1Score > player2Score) {
                printWinner(p1, player1Score, p2, player2Score);
                p1.wins++;
            } else if (player2Score > player1Score) {
                printWinner(p2, player2Score, p1, player1Score);
                p2.wins++;
            } else {
                System.out.println("It's a tie! Both players have " + player1Score + " discs.");
            }
        }


    }

    public void printWinner(Player winner, int winScore, Player loser, int loseScore) {
        System.out.println("Player " + (winner.isPlayerOne() ? "1" : "2") +
                " wins with " + winScore + " discs! Player " +
                (loser.isPlayerOne() ? "1" : "2") + " had " + loseScore + " discs.");
    }

    public int countPlayerDiscs(Player p1) {
        int score = 0;
        for (Disc[] d1 : board) {
            for (Disc d2 : d1) {
                if (d2 != null) {

                    if (p1.equals(d2.getOwner())) score++;

                }
            }
        }
        return score;
    }


    public void flipDiscs(Move m1) {
        List<Position> toBeFlip = m1.getFlippedDiscs();

        for (Position pos : toBeFlip) {


            flipDisc(pos);


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
        int row = position.row();
        int col = position.col();
        if (!isValidBoard() || board[row][col] == null || !isValidPosition(row, col)) return null;
        return board[row][col];
    }

    @Override
    public int getBoardSize() {
        return boardSize;
    }

    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < boardSize && col >= 0 && col < boardSize;
    }

    public boolean isValidMove(Position a) {
        return isValidPosition(a.row(), a.col()) && board[a.row()][a.col()] == null && countFlips(a) > 0;
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
        Set<Position> flips = new HashSet<>();

        // Loop through all 8 directions
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                // Add flips from each direction to the set
                flips.addAll(getFlipsInDir(position, dx, dy));
            }
        }


        flips.removeIf(currD2 -> { //Extra check to make sure the current player isn't flipping his discs back.
            if (currD2 != null) {
                Disc discAtPos = board[currD2.row()][currD2.col()];
                return discAtPos != null && discAtPos.getOwner().equals(currentTurn);
            }
            return false;
        });
        removeDuplicates(flips);
        return flips;
    }

    public void removeDuplicates(Set<Position> positions) {
        Set<Position> uniquePositions = new HashSet<>(positions);
        positions.clear();  // Clear the original list
        positions.addAll(uniquePositions);  // Add the unique positions back
    }

    public Set<Position> getFlipsInDir(Position start, int dx, int dy) {
        Set<Position> processedBombs = new HashSet<>();
        Set<Position> flips = new HashSet<>();
        Set<Position> potentialFlips = new HashSet<>(); // Tracks the opponent's discs in the direction

        int x = start.row() + dx;
        int y = start.col() + dy;

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
                    for (Position pos : potentialFlips) {
                        Disc disc = board[pos.row()][pos.col()];
                        if (disc instanceof BombDisc) {
                            flips.remove(pos);
                            triggerBomb(pos, flips, processedBombs);
                        }
                    }

                }
                break;
            }
            x += dx;
            y += dy;
        }
        return flips;
    }
    private void triggerBomb(Position bomb, Set<Position> flips, Set<Position> processedBombs) {
        // Debug prints to track the current state of the sets


        // If this bomb has already been processed or flipped, return immediately
        if (processedBombs.contains(bomb) || flips.contains(bomb)) {
            return; // Stop recursion if this bomb has already been triggered
        }

        // Add bomb to the flips set and processed bombs set

        processedBombs.add(bomb);  // Mark as processed
        flips.add(bomb);


        // For each neighboring position of the bomb, check if it should trigger another bomb or flip discs.
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;  // Skip the bomb's own position.

                int x = bomb.row() + dx;
                int y = bomb.col() + dy;

                if (isValidPosition(x, y)) {
                    Position adj = new Position(x, y);
                    Disc adjDisc = board[x][y];

                    if (adjDisc != null && !(adjDisc instanceof UnflippableDisc)) {
                        // Debug print to track the adjacent position and its disc type


                        // Case 1: If adjDisc is NOT your disc AND it is a BombDisc
                        if (!isOwnDisc(adjDisc) && adjDisc instanceof BombDisc && !processedBombs.contains(adj)) {

                            triggerBomb(adj, flips, processedBombs);  // Recursively trigger the adjacent bomb
                        }
                        // Case 2: If adjDisc IS your disc AND it is a BombDisc
                        else if (isOwnDisc(adjDisc) && adjDisc instanceof BombDisc) {
                        }
                        // Case 3: If adjDisc is NOT your disc AND it is NOT a BombDisc
                        else if (!isOwnDisc(adjDisc) && !(adjDisc instanceof BombDisc)) {
                            flips.add(adj);  // Add to flips list if it's a valid disc
                        }
                        // Case 4: If adjDisc IS your disc AND it is NOT a BombDisc
                        else if (isOwnDisc(adjDisc) && !(adjDisc instanceof BombDisc)) {
                        }
                    }
                }

            }
        }
    }
    private void flipDisc(Position a) {
        Disc d1 = board[a.row()][a.col()];
        if (d1 != null) {
            if (d1.getOwner().equals(p1)) {
                d1.setOwner(p2);
            } else {
                d1.setOwner(p1);
            }
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
            System.out.println("\tNo previous move available to undo.");

        } else {
            System.out.println("Undoing last move:");
            Move lastMove = moveHistory.pop();
            Position p4 = lastMove.position();
            currentTurn = isFirstPlayerTurn() ? p2 : p1;
            if (lastMove.disc() instanceof BombDisc) currentTurn.number_of_bombs++;
            if (lastMove.disc() instanceof UnflippableDisc) currentTurn.number_of_unflippedable++;
            String type = board[p4.row()][p4.col()].getType();
            board[p4.row()][p4.col()] = null;
            System.out.println("\tUndo: removing " + type + " from (" + p4.row() + "," + p4.col() + ")");
            flipDiscs(lastMove);
            printFlippedUndo(lastMove.getFlippedDiscs());
            System.out.println();
        }


    }
}