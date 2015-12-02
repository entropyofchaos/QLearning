import java.util.Scanner;

/**
 * Created by phil on 10/19/15.
 */
public class GameTest {

    public static void main(String[] args) {

        int userStartX = 0;
        int userStartY = 0;
        int userGoalX = 0;
        int userGoalY = 0;
        Grid.LockType lockType;
        int numThreads = 1;

        Scanner in = new Scanner(System.in);
        System.out.println("Enter the X coordinate of the start state.");
        userStartX = in.nextInt();
        System.out.println("Enter the Y coordinate of the start state.");
        userStartY = in.nextInt();
        System.out.println("Enter the X coordinate of the goal state.");
        userGoalX = in.nextInt();
        System.out.println("Enter the Y coordinate of the start state.");
        userGoalY = in.nextInt();
        System.out.println("Enter the number of the locking type to use.");
        System.out.println("0 - None");
        System.out.println("1 - SemaphoreLocked");
        System.out.println("2 - Synchronized");
        lockType = Grid.LockType.values()[in.nextInt()];
        System.out.println("Enter how many threads to use?");
        numThreads = in.nextInt();
        System.out.println(lockType);

        new QLearning (new Position(userStartY, userStartX), new Position(userGoalY, userGoalX), lockType, numThreads);

    }
}
