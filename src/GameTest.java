import java.io.FileWriter;
import java.io.IOException;
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
                System.out.println("4 - Coarse");
                lockType = Grid.LockType.values()[in.nextInt()];
            } else {
                lockType = Grid.LockType.None;
            }

            QLearning qLearningObj = new QLearning(new Position(userGoalY, userGoalX), lockType, numThreads,
                    gridDivisionType);
            qLearningObj.doQLearning(numEpisodes, numThreads, gridDivisionType);
            qLearningObj.traverseGrid(new Position(userStartY, userStartX), new Position(userGoalY, userGoalX));
        } else {
            FileWriter fileWriter = null;

            try {
                fileWriter = new FileWriter("output" + System.currentTimeMillis() + ".csv");
                String toPrint = ",1 Thread,2 Threads,4 Threads,8 Threads\n";
                fileWriter.append(toPrint);
                fileWriter.flush();
                System.out.print(toPrint);

                int numEpisodes = 1000000;
                int numRuns = 100;
                int numThreadsArray[] = {2, 4, 8};
                Grid.LockType[] lockTypes = Grid.LockType.values();
                Grid.GridDivisionType gridDivisionTypes[] = Grid.GridDivisionType.values();

//                Position goal, Grid.LockType lockType, int numThreads,
//                Grid.GridDivisionType gridDivisionType
                QLearning qLearningObj;
                double advRunTime;


                qLearningObj = new QLearning(new Position(8, 8), Grid.LockType.None, 1, Grid.GridDivisionType.None);
                advRunTime = 0;
                for (int i = 0; i < numRuns; ++i) {
                    advRunTime += qLearningObj.doQLearning(numEpisodes, 1, Grid.GridDivisionType.None);
                    System.out.println("SingleThreaded num runs = " + i);
                }
                advRunTime /= numRuns;
                toPrint = "SingleThreaded," + advRunTime + ",,,\n";
                fileWriter.append(toPrint);
                fileWriter.flush();
                System.out.println(toPrint);

                for (Grid.LockType lockType : lockTypes) {
                    for (Grid.GridDivisionType gridDivisionType : gridDivisionTypes) {
                        toPrint = gridDivisionType.toString() + ",";
                        fileWriter.append(toPrint);
                        fileWriter.flush();
                        System.out.print(toPrint);
                        for (int numThreads : numThreadsArray) {
                            if (gridDivisionType == Grid.GridDivisionType.RecursiveGrid) {
                                if (lockType == Grid.LockType.None) {
                                    qLearningObj = new QLearning(new Position(8, 8), lockType, numThreads,
                                            gridDivisionType);
                                    advRunTime = 0;
                                    for (int i = 0; i < numRuns; ++i) {
                                        advRunTime += qLearningObj.doQLearning(numEpisodes, numThreads,
                                                gridDivisionType);
                                        System.out.println(gridDivisionType.toString() + " " + lockType.toString() +
                                                " " + numThreads + " num runs = " + i);
                                    }
                                    advRunTime /= numRuns;
                                }
                                // Do nothing if GridDivisionType == RecursiveGrid and LockType != None
                            } else {
                                qLearningObj = new QLearning(new Position(8, 8), lockType, numThreads,
                                        gridDivisionType);
                                advRunTime = 0;
                                for (int i = 0; i < numRuns; ++i) {
                                    advRunTime += qLearningObj.doQLearning(numEpisodes, numThreads, gridDivisionType);
                                    System.out.println(gridDivisionType.toString() + " " + lockType.toString() +
                                            " " + numThreads + " num runs = " + i);
                                }
                                advRunTime /= numRuns;
                            }
                            toPrint = "," + advRunTime;
                            fileWriter.append(toPrint);
                            fileWriter.flush();
                            System.out.print(toPrint);
                        }
                        fileWriter.append("\n");
                        fileWriter.flush();
                        System.out.println();
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
