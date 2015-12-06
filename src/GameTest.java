import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by phil on 10/19/15.
 */
public class GameTest {

    public static void main(String[] args) {

        if (args.length == 1) {
            int userStartX = 0;
            int userStartY = 0;
            int userGoalX = 0;
            int userGoalY = 0;
            Grid.LockType lockType;
            int numThreads = 1;
            int numEpisodes = 0;
            Grid.GridDivisionType gridDivisionType;

            Scanner in = new Scanner(System.in);
            System.out.println("Enter the X coordinate of the start state.");
            userStartX = in.nextInt();
            System.out.println("Enter the Y coordinate of the start state.");
            userStartY = in.nextInt();
            System.out.println("Enter the X coordinate of the goal state.");
            userGoalX = in.nextInt();
            System.out.println("Enter the Y coordinate of the start state.");
            userGoalY = in.nextInt();
            System.out.println("Enter the number of episodes to run.");
            numEpisodes = in.nextInt();

            System.out.println("Enter how many threads to use?");
            numThreads = in.nextInt();
            System.out.println("Enter the type of Grid Divisions to use");
            System.out.println("0 - None");
            System.out.println("1 - EdgePeak");
            System.out.println("2 - RecursiveGrid");
            gridDivisionType = Grid.GridDivisionType.values()[in.nextInt()];

            if (gridDivisionType != Grid.GridDivisionType.RecursiveGrid) {
                System.out.println("Enter the number of the locking type to use.");
                System.out.println("0 - None");
                System.out.println("1 - SemaphoreLocked");
                System.out.println("2 - Synchronized");
                System.out.println("3 - TTAS");
                lockType = Grid.LockType.values()[in.nextInt()];
            } else {
                lockType = Grid.LockType.None;
            }

            QLearning qLearningObj = new QLearning();
            qLearningObj.doQLearning(new Position(userStartY, userStartX), new Position(userGoalY, userGoalX), numEpisodes, lockType,
                    numThreads, gridDivisionType);
        } else {
            FileWriter fileWriter = null;

            try {
                fileWriter = new FileWriter("output" + System.currentTimeMillis() + ".csv");
                fileWriter.append(",1 Thread,2 Threads,4 Threads,8 Threads\n");

                int numRuns = 100;
                int numThreadsArray[] = {2, 4, 8};
                Grid.LockType[] lockTypes = Grid.LockType.values();
                Grid.GridDivisionType gridDivisionTypes[] = Grid.GridDivisionType.values();

                Random generator = new Random();
                QLearning qLearningObj = new QLearning();
                double advRunTime;


                advRunTime = 0;
                for (int i = 0; i < numRuns; ++i) {
                    advRunTime += qLearningObj.doQLearning(new Position(generator.nextInt(),
                            generator.nextInt()), new Position(generator.nextInt(), generator.nextInt()),
                            1000000, Grid.LockType.None, 1, Grid.GridDivisionType.None);
                }
                advRunTime /= numRuns;
                fileWriter.append("SingleThreaded," + advRunTime + ",,,\n");

                for (Grid.LockType lockType : lockTypes) {
                    for (Grid.GridDivisionType gridDivisionType : gridDivisionTypes) {
                        fileWriter.append(gridDivisionType.toString());
                        for (int numThreads : numThreadsArray) {
                            if (gridDivisionType == Grid.GridDivisionType.RecursiveGrid) {
                                if (lockType == Grid.LockType.None) {
                                    advRunTime = 0;
                                    for (int i = 0; i < numRuns; ++i) {
                                        advRunTime += qLearningObj.doQLearning(new Position(generator.nextInt(),
                                                generator.nextInt()), new Position(generator.nextInt(),
                                                generator.nextInt()), 1000000, lockType, numThreads,
                                                gridDivisionType);
                                    }
                                    advRunTime /= numRuns;
                                }
                                // Do nothing if GridDivisionType == RecursiveGrid and LockType != None
                            } else {
                                advRunTime = 0;
                                for (int i = 0; i < numRuns; ++i) {
                                    advRunTime += qLearningObj.doQLearning(new Position(generator.nextInt(),
                                            generator.nextInt()), new Position(generator.nextInt(),
                                            generator.nextInt()), 1000000, lockType, numThreads, gridDivisionType);
                                }
                                advRunTime /= numRuns;
                            }
                            fileWriter.append("," + advRunTime);
                        }
                        fileWriter.append("\n");
                    }
                }
            } catch (Exception e) {
                System.out.println("Error in CsvFileWriter !!!");
                e.printStackTrace();
            } finally {
                try {
                    fileWriter.flush();
                    fileWriter.close();
                } catch (IOException e) {
                    System.out.println("Error while flushing/closing fileWriter !!!");
                    e.printStackTrace();
                }
            }
        }
    }
}
