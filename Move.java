import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Move {

    private Position position;      // The position where the disc is placed
    private Disc disc;              // The disc being placed (could be normal, bomb, etc.)

    private List<Position> flippedDiscs = new ArrayList<>();


    // Constructor to initialize the move
    public Move(Position position, Disc disc) {
        this.position = position;
        this.disc = disc;
        this.flippedDiscs = new ArrayList<>();
    }

    public int countFlips(Disc[][] board, int boardSize){
        int flips=0;
        for (int dx = -1; dx <=1 ; dx++) {
            for (int dy= -1; dy<=1 ; dy++) {
                if(dx==0&&dy==0)continue;
                flips+=countFlipsInDirection(Disc[][] board, int boardSize,int dx, int dy);
            }
        }
        return flips;
    }
    public int countFlipsInDirection(Disc[][] board, int dx, int dy){
        int x=position.getRow()+dx;
        int y=position.getCol()+dy;
        int flips=0;

        Disc d1=board[x][y];

    }




    // Getters and setters
    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Disc getDisc() {
        return disc;
    }

    public void setDisc(Disc disc) {
        this.disc = disc;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getFlips() {
        return flips;
    }

    public void setFlips(int flips) {
        this.flips = flips;
    }

    @Override
    public String toString() {
        return "Move{" +
                "player=" + player +
                ", position=" + position +
                ", disc=" + disc +
                ", timestamp=" + timestamp +
                ", flips=" + flips +
                '}';
    }
}
