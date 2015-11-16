/**
 * Created by brian on 11/15/15.
 */
public class Position {
    private int y;
    private int x;

    Position(int y, int x){
        this.y = y;
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public boolean equals(Position other)
    {
        return (this.x == other.x && this.y == other.y);
    }
}
