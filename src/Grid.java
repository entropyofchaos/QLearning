import org.apache.commons.lang3.tuple.MutablePair;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 * Class to represent our grid mWorld for the QLearning algorithm.
 */
public class Grid {

    private GridCell[][] mWorld;
    private int mCols;
    private int mRows;
    private int mNumThreads;
    private GridDivisionType mGridDivisionType;
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
     * Construction
     * @param path File path of grid
     * @param goal Position of the goal
     * @param lockType The type of locking mechanism to use
     */
    public Grid(String path, Position goal, LockType lockType) {

        mCols = 0;
        mRows = 0;
        mNumThreads = 1;
        mGridDivisionType = GridDivisionType.None;
        mGoal = goal;
        mLockType = lockType;

        readFile(path);
    }

    public Grid(String path, Position goal, LockType lockType, int numThreads, GridDivisionType gridDivisionType) {

        mCols = 0;
        mRows = 0;
        mNumThreads = numThreads;
        mGridDivisionType = gridDivisionType;
        mGoal = goal;
        mLockType = lockType;

        readFile(path);
    }

    /**
     * Gets the number of columns in your grid. Max x value.
     * @return Returns the number of columns
     */
    public int getNumColumns() {

        return mCols;
    }

    /**
     * Gets the number of rows in your grid. Max y value.
     * @return Returns the number of columns
     */
    public int getNumRows() {

        return mRows;
    }

    /**
     * Reads the grid mWorld file into the class.
     * @param path The path to the grid mWorld file
     */
    private void readFile(String path) {

        Path filePath = Paths.get(path);
        System.out.println(filePath);
        Charset charset = Charset.forName("ISO-8859-1");

        try {
            // Read all lines from a file into a list of strings, then
            // append the list of strings to our vector of strings. This
            // vector represents the lines of our map file.
            List<String> lines = Files.readAllLines(filePath, charset);
            mWorld = new GridCell[lines.size()][lines.get(0).length()];

            mRows = lines.size();
            mCols = lines.get(0).length();

            for (int y = 0; y < mWorld.length; ++y) {
                State temp = null;
                String oneLine = lines.get(y).trim();
                int lineSize = oneLine.length();
                for (int x = 0; x < lineSize; ++x) {
                    mWorld[y][x] = new GridCell();
                    mWorld[y][x].setIsWall(oneLine.charAt(x) == 'x');

                    Position newPosition = new Position(y, x);

                    switch (mGridDivisionType) {
                        case None:
                            temp = createState(mLockType, newPosition);
                            break;
                        case EdgePeak:
                            Vector<MutablePair<Position, Position>> subgridCorners = getSubgridCorners(mNumThreads);
                            for (MutablePair<Position, Position> singleSubgridCorners : subgridCorners) {
                                if (isEdgePosition(singleSubgridCorners.getLeft(), singleSubgridCorners.getRight(),
                                        newPosition)) {
                                    temp = createState(mLockType, newPosition);
                                } else {
                                    temp = createState(LockType.None, newPosition);
                                }
                            }
                            break;
                        case RecursiveGrid:
                            temp = createState(mLockType, newPosition);
                            break;
                        default:
                            temp = createState(mLockType, newPosition);
                    }

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

        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    /**
     * Helper function to retrieve all valid adjacent cells to the current cell.
     * All adjacent cells that would be an invalid location, such as a wall are
     * not added to the vector. Also, if the position provided is a wall, no neighbors
     * will be returned.
     * @param state The state to neighbors from
     * @return A vector of valid adjacent cells
     */
    Vector<StatePair> getNeighbors(State state) {

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
     * Overload of Vector<StatePair> getNeighbors(State state). This method is identical except states outside of
     * subgrid defined by a top left and bottom right position are also not returned.
     * @param state The state to neighbors from
     * @param topLeft Top left position of the subgrid
     * @param bottomRight Bottom right position of the subgrid
     * @return A vector of valid adjacent cells
     */
    Vector<StatePair> getNeighbors(State state, Position topLeft, Position bottomRight) {

        Vector<StatePair> neighbors = getNeighbors(state);
        for (StatePair neighbor : neighbors){
            isOutsideSubgrid(neighbor.getState().getPosition(), topLeft, bottomRight);
        }
        return neighbors;
    }

    /**
     * Returns if the location provided is a wall
     * @param pos The location to check
     * @return True if the location is a wall and false if it is not
     */
    boolean locationIsWall(Position pos) {
        return mWorld[pos.getY()][pos.getX()].isWall();
    }

    public double getReward(Position position) {

        return getState(position).getReward();
    }

    public State getState(Position position) {
        return mWorld[position.getY()][position.getX()].getSate();
    }

    /**
     * Prints the grid world to the console.
     */
    public void printWorld() {
        System.out.println("size is " + getNumRows() + "x" + getNumColumns());
        for (int y = 0; y < mWorld.length; ++y) {
            for (int x = 0; x < mWorld[y].length; ++x) {
                if (mWorld[y][x].isWall()) {
                    System.out.print('x');
                } else {
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

        for (int y = 0; y < mWorld.length; ++y) {
            for (int x = 0; x < mWorld[y].length; ++x) {
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

    public enum GridDivisionType {
        None, EdgePeak, RecursiveGrid
    }

    public enum LockType {
        None, SemaphoreLocked, Synchronized, TTAS, Coarse
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
            case Coarse:
                returnState = new CoarseLocked(position);
                break;
            default:
                returnState = new State(position);
                break;
        }

        return returnState;
    }

    public Vector<MutablePair<Position, Position>> getSubgridCorners(int numSubdivisions) {

        Vector<MutablePair<Position, Position>> subgridCorners = new Vector<>();
        int colSubgridBase = mCols / numSubdivisions;
        int colSubgridRemainder = mCols % numSubdivisions;
        int colSubgridRange = 0;
        Position topLeft;
        Position bottomRight;

        for (int i = 0; i < numSubdivisions; ++i) {
            topLeft = new Position(0, colSubgridRange);

            if (colSubgridRemainder > 0) {
                colSubgridRange += colSubgridBase + 1;
                colSubgridRemainder--;
            } else {
                colSubgridRange += colSubgridBase;
            }

            bottomRight = new Position(mRows - 1, colSubgridRange - 1);

            subgridCorners.add(new MutablePair<>(topLeft, bottomRight));
        }

        return subgridCorners;
    }

    public boolean isEdgePosition(Position topLeft, Position bottomRight, Position positionToCheck) {

        boolean isEdgePosition;

        if (positionToCheck.getX() == topLeft.getX() || positionToCheck.getX() == bottomRight.getX() ||
                positionToCheck.getY() == topLeft.getY() || positionToCheck.getY() == bottomRight.getY())
        {
            isEdgePosition = true;
        } else {
            isEdgePosition = false;
        }

        return isEdgePosition;
    }

    public boolean isOutsideSubgrid(Position topLeft, Position bottomRight, Position positionToCheck) {

        boolean isOutsideSubgrid;

        if (positionToCheck.getX() < topLeft.getX() || positionToCheck.getX() > bottomRight.getX() ||
                positionToCheck.getY() < topLeft.getY() || positionToCheck.getY() > bottomRight.getY())
        {
            isOutsideSubgrid = true;
        } else {
            isOutsideSubgrid = false;
        }

        return isOutsideSubgrid;
    }
}
