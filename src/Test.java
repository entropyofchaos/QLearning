import org.apache.commons.lang3.tuple.MutablePair;

import java.util.Scanner;

/**
 * Created by phil on 10/19/15.
 */
public class Test {

    public static void main(String[] args) {

        boolean aStarInteractive = false;
        int algorithm = 5;
        int userStartX = 0;
        int userStartY = 0;
        int userGoalX = 0;
        int userGoalY = 0;

        Scanner in = new Scanner(System.in);
        System.out.println("Enter the X coordinate of the start state.");
        userStartX = in.nextInt();
        System.out.println("Enter the Y coordinate of the start state.");
        userStartY = in.nextInt();
        System.out.println("Enter the X coordinate of the goal state.");
        userGoalX = in.nextInt();
        System.out.println("Enter the Y coordinate of the start state.");
        userGoalY = in.nextInt();
        new QLearning (new MutablePair<Integer, Integer>(userStartX, userStartY), new MutablePair<Integer, Integer>(userGoalX, userGoalY));

    }
}
