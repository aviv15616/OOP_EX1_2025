import java.util.ArrayList;
import java.util.List;

public class GameLogic implements PlayableLogic {
    private Disc[][] board;
    private final int boardSize = 8;
    private int MAX = Integer.MAX_VALUE;
    private Player p1, p2;
    private Player currentTurn; // Track whose turn it is
    private boolean gameFinished = false; // Track if the game is finished
    private int scoreP1, scoreP2; // Track the scores of both players
    private List<Move> moveHistory; // For undo functionality

    public boolean isValidBoard() {
        if (board == null || boardSize == 0) return false;
        return true;
    }

    @Override
    public boolean locate_disc(Position a, Disc disc) {
        int row = a.getRow();
        int col = a.getCol();
        if (!isValidBoard() || board[row][col] == null || !board[row][col].equals(disc))
            return false; // Checks if the disc exists, and whether it's the same disc as given.
        return true;
    }

    @Override
    public Disc getDiscAtPosition(Position position) {
        int row = position.getRow();
        int col = position.getCol();
        if (!isValidBoard() || board[row][col] == null) return null;
        return board[row][col];
    }

    @Override
    public int getBoardSize() {
        return boardSize;
    }


    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < boardSize && col >= 0 && col < boardSize;
    }
    public boolean isValidMove(Move m1){
        return countFlips(m1.getPosition())>0;
    }
    public boolean isValidMove(Position a){
        return countFlips(a)>0;
    }


    // חישוב מספר דיסקים שהתהפכו בכיוון מסוים
    private int countFlipsInDirection(Position a,Disc[][] board, int boardSize, int dx, int dy) {
        int x = a.getCol() + dx;
        int y = a.getRow() + dy;
        int flips = 0;

        while (x >= 0 && x < boardSize&& y >= 0 && y < boardSize) {
            Disc currentDisc = board[x][y];
            if (currentDisc == null || currentDisc.getOwner() == null) {
                return 0;
            } else if (currentDisc.getOwner() == currentTurn||currentDisc.getType()=="Unflip") {
                return flips;
            } else {
                flips++;
            }
            x += dx;
            y += dy;
        }
        return 0;
    }


    @Override
    public List<Position> ValidMoves() {
        List <Position> Valid= new ArrayList<>();
        for (int i = 0; i <boardSize ; i++) {
            for (int j = 0; j < boardSize; j++) {
                Disc d1= board[i][j];
                Position p1= new Position(i,j);
                if(isValidMove(p1))Valid.add(p1);
            }
        }
        return Valid;
    }


    @Override
    public int countFlips(Position a) {
        int totalFlips = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                totalFlips += countFlipsInDirection(a,board, boardSize, dx, dy);
            }
        }
        return totalFlips;
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
        if (!moveHistory.isEmpty()) {
            Move lastMove = moveHistory.get(moveHistory.size() - 1);
            // Undo the move (details depend on your Move class implementation)
            moveHistory.remove(lastMove);
        }
    }
}
