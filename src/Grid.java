import java.io.IOException;
import java.nio.file.Files;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Vector;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;

/**
 * Class to represent our grid mWorld for the QLearning algorithm.
 */
public class Grid {

    private GridCell[][] mWorld;
    private int mCols;
    private int mRows;
    private Position mGoal;
    private LockType mLockType;

    private class GridCell{
        boolean mIsWall;
        State mSate;

        public void setIsWall(boolean mIsWall) {
            this.mIsWall = mIsWall;
        }

        public void setState(State mSate) {
            this.mSate = mSate;
        }

        public boolean isWall() {
            return mIsWall;
        }

        public State getSate() {
            return mSate;
        }
    }

    /**
     * Default constructor
     */
    public Grid(String path, Position goal, LockType lockType){

        mCols = 0;
        mRows = 0;
        mGoal = goal;
        mLockType = lockType;

        readFile(path);
    }

    /**
     * Gets the number of columns in your grid. Max x value.
     * @return Returns the number of columns
     */
    public int getNumColumns(){

        return mCols;
    }

    /**
     * Gets the number of rows in your grid. Max y value.
     * @return Returns the number of columns
     */
    public int getNumRows(){

        return mRows;
    }

    /**
     * Reads the grid mWorld file into the class.
     * @param path The path to the grid mWorld file
     */
    private void readFile(String path){

        Path filePath = Paths.get(path);
        System.out.println(filePath);
        Charset charset = Charset.forName("ISO-8859-1");

        try {
            // Read all lines from a file into a list of strings, then
            // append the list of strings to our vector of strings. This
            // vector represents the lines of our map file.
            List<String> lines = Files.readAllLines(filePath, charset);
            mWorld = new GridCell[lines.size()][lines.get(0).length()];

            for (int y = 0; y < mWorld.length; ++y)
            {
                String oneLine = lines.get(y).trim();
                int lineSize = oneLine.length();
                for (int x = 0; x < lineSize; ++x){
                    mWorld[y][x] = new GridCell();
                    mWorld[y][x].setIsWall(oneLine.charAt(x) == 'x');
                    State temp = createState(mLockType, new Position(y, x));
                    temp.addTransitionAction("right", 0);
                    temp.addTransitionAction("left", 0);
                    temp.addTransitionAction("down", 0);
                    temp.addTransitionAction("up", 0);
                    mWorld[y][x].setState(temp);
                }
            }

            State tempState = mWorld[mGoal.getY()][mGoal.getX()].getSate();
            tempState.setReward(100);
            mWorld[mGoal.getY()][mGoal.getX()].setState(tempState);

            mRows = mWorld.length;
            mCols = mWorld[0].length;

        } catch (IOException e){
            System.out.println(e.toString());
        }
    }

    /**
     * Helper function to retrieve all valid adjacent cells to the current cell.
     * All adjacent cells that would be an invalid location, such as a wall are
     * not added to the vector. Also, if the position provided is a wall, no neighbors
     * will be returned.
     * @param state The state to neighbors from
     * @return A vector of valid adjacent cells where the first parameter is the
     * direction name and the second is the actual State
     */
    Vector<StatePair> getNeighbors(State state){

        Vector<StatePair>  neighbors = new Vector<>();
        Position loc = state.getPosition();
        int x = loc.getX();
        int y = loc.getY();

        // Check that provided state is not a wall
        if (!locationIsWall(loc)) {

            // Look at cell to the right
            if (x + 1 < mCols && !locationIsWall(new Position(y, x + 1))) {
                neighbors.add(new StatePair("right", mWorld[y][x + 1].getSate()));
            }

            // Look at cell to the left
            if (x - 1 >= 0 && !locationIsWall(new Position(y, x - 1))) {
                neighbors.add(new StatePair("left", mWorld[y][x - 1].getSate()));
            }

            // Look at cell below
            if (y + 1 < mRows && !locationIsWall(new Position(y + 1, x))) {
                neighbors.add(new StatePair("down", mWorld[y + 1][x].getSate()));
            }

            // Look at cell above
            if (y - 1 >= 0 && !locationIsWall(new Position(y - 1, x))) {
                neighbors.add(new StatePair("up", mWorld[y - 1][x].getSate()));
            }
        }
        return neighbors;
    }

    /**
     * Returns if the location provided is a wall
     * @param pos The location to check
     * @return True if the location is a wall and false if it is not
     */
    boolean locationIsWall(Position pos)
    {
        return mWorld[pos.getY()][pos.getX()].isWall();
    }

    public double getReward(Position position){

        return getState(position).getReward();
    }

    public State getState(Position position)
    {
        return mWorld[position.getY()][position.getX()].getSate();
    }

    /**
     * Prints the grid world to the console.
     */
    public void printWorld(){
        System.out.println("size is " + getNumRows() + "x" + getNumColumns());
        for (int y = 0; y < mWorld.length; ++y){
            for (int x = 0; x < mWorld[y].length; ++x){
                if (mWorld[y][x].isWall()) {
                    System.out.print('x');
                }else{
                    System.out.print('-');
                }
            }
            System.out.println();
        }
    }

    /**
     * Prints the grid's stored rewards to the console.
     */
    public void printRewards() {

        for (int y = 0; y < mWorld.length; ++y){
            for (int x = 0; x < mWorld[y].length; ++x){
                System.out.print(mWorld[y][x].getSate().getReward() + " ");
            }
            System.out.println();
        }
    }

    //please change this function after updating state
    public void printQTable() {
        String str;
        for (int y = 0; y < mWorld.length; ++y) {
            for (int x = 0; x < mWorld[y].length; ++x) {
                str = "";
                Set<String> actions = mWorld[y][x].getSate().getActions();
                for (String action : actions) {
                    str += action + " - " + mWorld[y][x].getSate().getTransitionActionReward(action) + " ";
                }

                str = "State (" + x + ", " + y + ")'s Actions: " + str;
                System.out.println(str);
            }
        }
    }

    public enum LockType {
        None, SemaphoreLocked, Synchronized, TTAS
    }

    private State createState(LockType lockType, Position position) {

        State returnState;
        switch (lockType) {
            case None:
                returnState = new State(position);
                break;
            case SemaphoreLocked:
                returnState = new StateSemaphoreLocked(position);
                break;
            case Synchronized:
                returnState = new StateSynchronized(position);
                break;
            case TTAS:
                returnState = new StateTtasLocked(position);
                break;
            default:
                returnState = new State(position);
                break;
        }

        return returnState;
    }


}
