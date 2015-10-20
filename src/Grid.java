import java.io.IOException;
import java.nio.file.Files;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Vector;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Class to represent our grid world for the QLearning algorithm.
 */
public class Grid {

    private Vector<String> world;
    private int cols;
    private int rows;
    private Vector<Pair<Integer, Integer>> walls;

    /**
     * Default constructor
     */
    public Grid(){
        cols = 0;
        rows = 0;
        world = new Vector<>();
        walls = new Vector<>();
    }

    /**
     * Reads the grid world file into the class. Determines which locations are
     * valid and which locations are walls.
     * @param path The path to the grid world file
     */
    void readFile(String path){

        Path filePath = Paths.get(path);
        Charset charset = Charset.forName("ISO-8859-1");

        try {
            // Read all lines from a file into a list of strings, then
            // append the list of strings to our vector of strings. This
            // vector represents the lines of our map file.
            world.addAll(Files.readAllLines(filePath, charset));
            for (int i = 0; i < world.size(); ++i)
            {
                world.set(i, world.get(i).trim());
            }

            rows = world.size();
            cols = world.elementAt(0).length();

        } catch (IOException e){
            System.out.println(e.toString());
        }

        // Go through all the characters in the grid map and see if we find any
        // x's. If we find one, it represents a wall, so we add it to the list
        // of walls.
        for (int x = 0; x < world.size(); ++x){
            for (int y = 0; y < world.elementAt(x).length(); ++y){
                if (world.elementAt(x).charAt(y) == 'x'){
                    walls.add(new MutablePair<>(x, y));
                }
            }
        }
    }

    /**
     * Prints the grid world to the console.
     */
    void printWorld()
    {
        world.forEach(string -> System.out.println(string));
    }

    /**
     * Helper function to retrieve all valid adjacent cells to the current cell.
     * All adjacent cells that would be an invalid location, such as a wall are
     * not added to the vector.
     * @param loc The location you are looking at
     * @return A vector of valid adjacent cells
     */
    Vector<Pair<Integer, Integer>> adjacent(Pair<Integer, Integer> loc){

        Vector<Pair<Integer, Integer>> adj = new Vector<>();
        int x = loc.getLeft();
        int y = loc.getLeft();

        // Look at cell to the right
        if(x + 1 < cols && !walls.contains(new MutablePair<>(x + 1, y))) {
            adj.add(new MutablePair<>(x + 1, y));
        }

        // Look at cell to the left
        if(x - 1 >= 0 && !walls.contains(new MutablePair<>(x - 1, y))) {
            adj.add(new MutablePair<>(x - 1, y));
        }

        // Look at cell below
        if(y + 1 < rows && !walls.contains(new MutablePair<>(x, y + 1))) {
            adj.add(new MutablePair<>(x, y + 1));
        }

        // Look at cell above
        if(y - 1 >= 0 && !walls.contains(new MutablePair<>(x, y - 1))) {
            adj.add(new MutablePair<>(x, y - 1));
        }

        return adj;
    }



}
