import java.util.Scanner;

/**
 * Created by phil on 10/19/15.
 */
public class gameTest {




    public static void main(String[] args) {

        boolean aStarInteractive = false;
        int algorithm = 5;
        int userStartX = 0;
        int userStartY = 0;
        int userGoalX = 0;
        int userGoalY = 0;
        Grid g = new Grid();

        g.readFile("world.txt");
        Scanner in = new Scanner(System.in);
        System.out.println("Enter the X coordinate of the start state.");
        userStartX = in.nextInt();
        System.out.println("Enter the Y coordinate of the start state.");
        userStartY = in.nextInt();
        System.out.println("Enter the X coordinate of the goal state.");
        userGoalX = in.nextInt();
        System.out.println("Enter the Y coordinate of the start state.");
        userGoalY = in.nextInt();
        //g.qLearning(userStartX, userStartY, userGoalX,userGoalY);

    }
}