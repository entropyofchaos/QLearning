import org.apache.commons.lang3.tuple.MutablePair;

import java.util.Scanner;

/**
 * Created by phil on 10/19/15.
 */
public class GameTest {

    public static void main(String[] args) {

        int userStartX ;
        int userStartY ;
        int userGoalX ;
        int userGoalY ;

        Scanner in = new Scanner(System.in);
        System.out.println("Enter the X coordinate of the start state.");
        userStartX = in.nextInt();
        System.out.println("Enter the Y coordinate of the start state.");
        userStartY = in.nextInt();
        System.out.println("Enter the X coordinate of the goal state.");
        userGoalX = in.nextInt();
        System.out.println("Enter the Y coordinate of the start state.");
        userGoalY = in.nextInt();
        new QLearning (new MutablePair<>(userStartX, userStartY), new MutablePair<>(userGoalX, userGoalY));

    }
}
