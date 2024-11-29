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
    public void setFlippedDiscs(List <Position> a){
        this.flippedDiscs=a;
    }

    public Position position() {
        return this.position;
    }
    public List <Position> getFlippedDiscs(){
        return flippedDiscs;
    }

    public Disc disc() {
        return disc;
    }


}
