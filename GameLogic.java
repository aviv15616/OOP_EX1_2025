import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

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
    public boolean hasBombFlipped(Move m1){
        List <Position> flipped=m1.getFlippedDiscs();
        for (Position a:flipped) {
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
        if(disc instanceof BombDisc&&!isBombLess3(disc)||disc instanceof UnflippableDisc&&isUnflipLess2(disc))return false;
        if (!isValidBoard() || board[row][col] != null ||!isValidMove(a)||!isValidPosition(row,col)||disc==null||disc.getOwner()==null)
            return false; // Checks if the disc exists, and whether it's the same disc as given.
        board[row][col]=disc;
        Move m1= new Move(a,disc);
        List <Position> flipped =new ArrayList<>();
        flipped=getFlips(a);
        m1.setFlippedDiscs(flipped);
        flipDiscs(m1);
        moveHistory.push(m1);
        return true;
    }
    public void changeTurn(){
        if(currentTurn==p1)
        this.currentTurn=p2;
        else {
            this.currentTurn = p1;
        }
    }
    public void updateGame(){
    if(ValidMoves().isEmpty()) {
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
public int countPlayerDiscs(Player p1){
        int score=0;
        for(Disc[] d1:board){
            for(Disc d2:d1){
                if(p1.equals(d2.getOwner()))score++;
            }
            }
        return score;
        }




    public void flipDiscs(Move m1){
        List <Position> flipped=m1.getFlippedDiscs();
        for (int i = 0; i < flipped.size(); i++) {
            for (int j = 0; j <boardSize ; j++) {
                for (int k = 0; k <boardSize ; k++) {
                    Position p1=new Position(j,k);
                    if(flipped.get(i).equals(p1)){
                        board[j][k]=m1.getDisc();
                    }
                }
            }
        }
    }

    public boolean isBombLess3(Disc disc){
        return disc.getOwner().number_of_bombs <3;
    }
    public boolean isUnflipLess2(Disc disc){
        return disc.getOwner().number_of_unflippedable <2;
    }


    @Override
    public Disc getDiscAtPosition(Position position) {
        int row = position.getRow();
        int col = position.getCol();
        if (!isValidBoard() || board[row][col] == null||!isValidPosition(row,col)) return null;
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
    public List <Position> handleBomb(Position a,Disc disc){
    List <Position> flips=new ArrayList<>();
        int row=a.getRow();
        int col=a.getCol();
        for (int dx = -1; dx <=1 ; dx++) {
            for (int dy = -1; dy <=1 ; dy++) {
                if(dx==0&&dy==0)continue;
                Position p1=new Position(row+dx,col+dy);
                Disc currentDisc=board[row+dx][col+dy];
                if(currentDisc.getOwner()!=disc.getOwner())flips.add(p1);
            }
        }
        return flips;
    }





    @Override
    public List<Position> ValidMoves() {
        List <Position> Valid= new ArrayList<>();
        for (int i = 0; i <boardSize ; i++) {
            for (int j = 0; j < boardSize; j++) {
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
                totalFlips += getFlipsInDir(a,dx, dy).size();
            }
        }
        return totalFlips;
    }
    public List <Position> getFlips(Position a) {
        List <Position> flips=new ArrayList<>();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                flips.addAll(getFlipsInDir(a,dx,dy));
            }
        }
        return flips;
    }
    public List <Position> getFlipsInDir(Position a, int dx, int dy) {
        int x = a.getCol() + dx;
        int y = a.getRow() + dy;
        List<Position> flips = new ArrayList<>();

        while (x >= 0 && x < boardSize && y >= 0 && y < boardSize) {
            Disc currentDisc = board[x][y];
            Position p1 = new Position(x, y);
            if (currentDisc == null || currentDisc.getOwner() == null) {
                break;
            } else if (currentDisc.getOwner() == currentTurn || currentDisc instanceof UnflippableDisc) {
                return flips;
            } else if (currentDisc instanceof BombDisc && !flips.contains(p1)) {
                flips.addAll(handleBomb(p1, currentDisc));
                }
             else{
                    flips.add(p1);
                }
                x += dx;
                y += dy;
            }
            return flips;
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
        if (moveHistory.isEmpty()) {
            System.out.println("No Previous moves");
        }
            Move lastMove = moveHistory.pop();
            Position p1= lastMove.getPosition();
            board[p1.getRow()][p1.getCol()]=null;
            flipDiscs(lastMove);
        }

}
