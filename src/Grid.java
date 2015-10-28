import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Vector;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Class to represent our grid mWorld for the QLearning algorithm.
 */
public class Grid {

    private MutablePair<Character, State>[][] mWorld;
    private int mCols;
    private int mRows;
    private Vector<MutablePair<Integer, Integer>> mWalls;
    private MutablePair<Integer, Integer> mGoal;

    /**
     * Default constructor
     */
    public Grid(String path, MutablePair<Integer, Integer> goal){

        mCols = 0;
        mRows = 0;
        mWalls = new Vector<>();
        mGoal = goal;
        mGoal = goal;

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
     * Reads the grid mWorld file into the class. Determines which locations are
     * valid and which locations are mWalls.
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
            MutablePair<Character, State> arrayParam = new MutablePair<>();
            mWorld = this.<MutablePair<Character, State>>get2DArray(arrayParam.getClass(), lines.size(),
                    lines.get(0).length());

            for (int y = 0; y < mWorld.length; ++y)
            {
                String oneLine = lines.get(y).trim();
                int lineSize = oneLine.length();
                for (int x = 0; x < lineSize; ++x){
                    mWorld[y][x] = new MutablePair<>();
                    mWorld[y][x].setLeft(oneLine.charAt(x));
                    State temp = new State(MutablePair.of(y, x));
                    temp.addTransitionAction("right", 0);
                    temp.addTransitionAction("left", 0);
                    temp.addTransitionAction("down", 0);
                    temp.addTransitionAction("up", 0);
                    mWorld[y][x].setRight(temp);
                }
            }

            State tempState = mWorld[mGoal.getLeft()][mGoal.getRight()].getRight();
            tempState.setReward(100);
            mWorld[mGoal.getLeft()][mGoal.getRight()].setRight(tempState);

            mRows = mWorld.length;
            mCols = mWorld[0].length;

        } catch (IOException e){
            System.out.println(e.toString());
        }

        // Go through all the characters in the grid map and see if we find any
        // x's. If we find one, it represents a wall, so we add it to the list
        // of mWalls.
        for (int y = 0; y < mWorld.length; ++y){
            for (int x = 0; x < mWorld[y].length; ++x){
                if (mWorld[y][x].getLeft() == 'x'){
                    mWalls.add(new MutablePair<>(y, x));
                }
            }
        }
    }

    /**
     * Helper function to retrieve all valid adjacent cells to the current cell.
     * All adjacent cells that would be an invalid location, such as a wall are
     * not added to the vector.
     * @param state The state to neighbors from
     * @return A vector of valid adjacent cells where the first parameter is the
     * direction name and the second is the actual State
     */
    Vector<MutablePair<String, State>> getNeighbors(State state){

        Vector<MutablePair<String, State>>  neighbors = new Vector<>();
        Pair<Integer, Integer> loc = state.getPosition();
        int x = loc.getLeft();
        int y = loc.getLeft();

        // Look at cell to the right
        if(x + 1 < mCols && !mWalls.contains(new MutablePair<>(y, x + 1))) {
            neighbors.add(new MutablePair<>("right", mWorld[y][x + 1].getRight()));
        }

        // Look at cell to the left
        if(x - 1 >= 0 && !mWalls.contains(new MutablePair<>(y, x - 1))) {
            neighbors.add(new MutablePair<>("left", mWorld[y][x - 1].getRight()));
        }

        // Look at cell below
        if(y + 1 < mRows && !mWalls.contains(new MutablePair<>(y + 1, x))) {
            neighbors.add(new MutablePair<>("down", mWorld[y + 1][x].getRight()));
        }

        // Look at cell above
        if(y - 1 >= 0 && !mWalls.contains(new MutablePair<>(y - 1, x))) {
            neighbors.add(new MutablePair<>("up", mWorld[y - 1][x].getRight()));
        }

        return neighbors;
    }

    public double getReward(MutablePair<Integer, Integer> loc){

        return getState(loc).getReward();
    }

    public State getState(MutablePair<Integer, Integer> loc)
    {
        return mWorld[loc.getLeft()][loc.getRight()].getRight();
    }

    /**
     * Prints the grid world to the console.
     */
    public void printWorld(){
        for (int y = 0; y < mWorld.length; ++y){
            for (int x = 0; x < mWorld[y].length; ++x){
                System.out.print(mWorld[y][x].getLeft());
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
                System.out.print(mWorld[y][x].getRight().getReward() + " ");
            }
            System.out.println();
        }
    }

    //please change this function after updating state
    public void printQTable() {
        String str;
        for (int x = 0; x < mWorld.length; ++x) {
            for (int y = 0; y < mWorld[x].length; ++y) {
                str = "";
                Set<String> actions = mWorld[x][y].getRight().getActions();
                for (String action : actions) {
                    str += action + " - " + mWorld[x][y].getRight().getTransitionActionReward(action) + " ";
                }

                str = "State (" + x + ", " + y + ")'s Actions: " + str;
                System.out.println(str);
            }
        }
    }

    private <E> E[][] get2DArray(Class<? extends MutablePair> clazz, int firstD, int secondD) {
        @SuppressWarnings("unchecked")
        E[][] arr = (E[][]) Array.newInstance(clazz, firstD, secondD);

        return arr;
    }


}
