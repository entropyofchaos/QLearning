import java.io.IOException;
import java.nio.file.Files;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Vector;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class Grid {

    private Vector<String> world;
    private int cols;
    private int rows;
    private Vector<Pair<Integer, Integer>> walls;

    public Grid(){
        cols = 0;
        rows = 0;
        world = new Vector<>();
        walls = new Vector<>();
    }

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

        for (int x = 0; x < world.size(); ++x){
            for (int y = 0; y < world.elementAt(x).length(); ++y){
                if (world.elementAt(x).charAt(y) == 'x'){
                    walls.add(new MutablePair<>(x, y));
                }
            }
        }
    }

    void printWorld()
    {
        world.forEach(string -> System.out.println(string));
    }

    Vector<Pair<Integer, Integer>> adjacent(Pair<Integer, Integer> loc){

        Vector<Pair<Integer, Integer>> adj = new Vector<>();
        int x = loc.getLeft();
        int y = loc.getLeft();

        if(x + 1 < cols && !walls.contains(new MutablePair<>(x + 1, y))) {
            adj.add(new MutablePair<>(x + 1, y));
        }
        if(x - 1 >= 0 && !walls.contains(new MutablePair<>(x - 1, y))) {
            adj.add(new MutablePair<>(x - 1, y));
        }
        if(y + 1 < rows && !walls.contains(new MutablePair<>(x, y + 1))) {
            adj.add(new MutablePair<>(x, y + 1));
        }
        if(y - 1 >= 0 && !walls.contains(new MutablePair<>(x, y - 1))) {
            adj.add(new MutablePair<>(x, y - 1));
        }

        return adj;
    }

}
