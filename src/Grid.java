import java.io.IOException;
import java.nio.file.Files;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Vector;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class Grid {

    private Vector<String> world;
    private int cols;
    private int rows;
    private Vector<Pair<Integer, Integer>> walls;
    private HashMap<Integer, Integer> weights;

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

        for (int x = 0; x < walls.size(); ++x){
            for (int y = 0; y < walls.elementAt(x).size(); ++y){
                if (col == 'x'){
                    walls.add(MutablePair<Integer, Integer>(x, y));
                }
            }
        }
    }
}
