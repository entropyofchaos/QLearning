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
    private int cols = 0;
    private int rows = 0;
    private Pair<Integer, Integer> walls;
    private HashMap<Integer, Integer> weights;

    public Grid(){
        world = new Vector<>();
        walls = new MutablePair<>();
    }

    void readFile(String path){

        Path filePath = Paths.get(path);
        Charset charset = Charset.forName("ISO-8859-1");

        try {
            world.addAll(Files.readAllLines(filePath, charset));
            for (int i = 0; i < world.size(); ++i)
            {
                world.set(i, world.get(i).trim());
            }

        } catch (IOException e){
            System.out.println(e.toString());
        }
    }
}
