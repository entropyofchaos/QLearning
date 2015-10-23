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
    private Vector<Pair<Integer, Integer>> mWalls;
    private MutablePair<Integer, Integer> mGoal;

    /**
     * Default constructor
     */
    public Grid(String path, MutablePair<Integer, Integer> goal){

        mCols = 0;
        mRows = 0;
        mWalls = new Vector<>();
        mGoal = goal;

        readFile(path);
    }

    public int getNumColumns(){

        return mCols;
    }

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
        Charset charset = Charset.forName("ISO-8859-1");

        try {
            // Read all lines from a file into a list of strings, then
            // append the list of strings to our vector of strings. This
            // vector represents the lines of our map file.
            List<String> lines = Files.readAllLines(filePath, charset);
            mWorld = this.<MutablePair<Character, State>>get2DArray(mWorld.getClass(), lines.size(),
                    lines.get(0).length());

            for (int x = 0; x < mWorld.length; ++x)
            {
                String oneLine = lines.get(x).trim();
                int lineSize = oneLine.length();
                for (int y = 0; y < lineSize; ++y){
                    mWorld[x][y].setLeft(oneLine.charAt(y));
                    State temp = new State(MutablePair.of(x, y));
                    temp.addTransitionAction("right", 0);
                    temp.addTransitionAction("left", 0);
                    temp.addTransitionAction("down", 0);
                    temp.addTransitionAction("up", 0);
                    mWorld[x][y].setRight(temp);
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
        for (int x = 0; x < mWorld.length; ++x){
            for (int y = 0; y < mWorld[x].length; ++y){
                if (mWorld[x][y].getLeft() == 'x'){
                    mWalls.add(new MutablePair<>(x, y));
                }
            }
        }
    }

    /**
     * Helper function to retrieve all valid adjacent cells to the current cell.
     * All adjacent cells that would be an invalid location, such as a wall are
     * not added to the vector.
     * @param loc The location you are looking at
     * @return A vector of valid adjacent cells where the first parameter is the
     * direction name and the second is the actual State
     */
    Vector<MutablePair<String, State>> getNeighbors(Pair<Integer, Integer> loc){

        Vector<MutablePair<String, State>>  neighbors = new Vector<>();
        int x = loc.getLeft();
        int y = loc.getLeft();

        // Look at cell to the right
        if(x + 1 < mCols && !mWalls.contains(new MutablePair<>(x + 1, y))) {
            neighbors.add(new MutablePair<>("right", mWorld[x + 1][y].getRight()));
        }

        // Look at cell to the left
        if(x - 1 >= 0 && !mWalls.contains(new MutablePair<>(x - 1, y))) {
            neighbors.add(new MutablePair<>("left", mWorld[x - 1][y].getRight()));
        }

        // Look at cell below
        if(y + 1 < mRows && !mWalls.contains(new MutablePair<>(x, y + 1))) {
            neighbors.add(new MutablePair<>("down", mWorld[x][y + 1].getRight()));
        }

        // Look at cell above
        if(y - 1 >= 0 && !mWalls.contains(new MutablePair<>(x, y - 1))) {
            neighbors.add(new MutablePair<>("up", mWorld[x][y - 1].getRight()));
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
        for (int x = 0; x < mWorld.length; ++x){
            for (int y = 0; y < mWorld[x].length; ++y){
                System.out.println(mWorld[x][y].getLeft());
            }
        }
    }

    /**
     * Prints the grid's stored rewards to the console.
     */
    public void printRewards() {

        for (int x = 0; x < mWorld.length; ++x){
            for (int y = 0; y < mWorld[x].length; ++y){
                System.out.print(mWorld[x][y].getRight().getReward() + " ");
            }
            System.out.println();
        }
    }

    //please change this function after updating state
    public void printQTable() {
        String str = null;
        for (int x = 0; x < mWorld.length; ++x) {
            for (int y = 0; y < mWorld[x].length; ++y) {
                Set<String> actions = mWorld[x][y].getRight().getActions();
                for (String action : actions) {
                    str += action + " - " + mWorld[x][y].getRight().getTransitionActionReward(action);
                }

                str = "State (" + x + ", " + y + ")'s Actions: " + str;
                System.out.println(str);
            }
        }
    }

    public <E> E[][] get2DArray(Class<? extends Pair[][]> clazz, int firstD, int secondD) {
        @SuppressWarnings("unchecked")
        E[][] arr = (E[][]) Array.newInstance(clazz, firstD, secondD);

        return arr;
    }


}
