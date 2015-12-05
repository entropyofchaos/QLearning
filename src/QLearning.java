import java.lang.reflect.Method;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

public class QLearning {

    private Grid mQTable;
    private int mNumThreads;

    private class EpisodeRunner implements Runnable {

        double mGamma;
        double mAlpha;
        Random mGenerator;
        Position mTopLeft;
        Position mBottomRight;
        int mNumEpisodes;

        EpisodeRunner(double alpha, double gamma, Position topLeft, Position bottomRight, int numEpisodes) {
            mAlpha = alpha;
            mGamma = gamma;
            mTopLeft = topLeft;
            mBottomRight = bottomRight;
            mGenerator = new Random(5);
            mNumEpisodes = numEpisodes;
        }

        @Override
        public void run() {
            int x;
            int y;
            for(int i = 0; i < mNumEpisodes; ++i)
            {
                Position randomLocation;
                do{
                    x = getRandomNumber(mGenerator, mTopLeft.getX(), mBottomRight.getX() + 1);
                    y = getRandomNumber(mGenerator, mTopLeft.getY(), mBottomRight.getY() + 1);
                    randomLocation = new Position(y, x);

                } while(mQTable.locationIsWall(randomLocation) || mQTable.getReward(randomLocation) > 99.999);

                episode(mQTable.getState(randomLocation), 0, mGamma, mAlpha);
            }
        }
    }

    public QLearning(Position start, Position goal, Grid.LockType lockType, int numThreads) {

        mNumThreads = numThreads;

        //mQTable = new Grid("worldSmall.txt", goal);
        mQTable = new Grid("world.txt", goal, lockType);
        double alpha = 1;
        double gamma = 0.8;

        mQTable.printWorld();
        mQTable.printRewards();

        long startTime = System.nanoTime();

        Thread[] threads = new Thread[mNumThreads];
        int numEpisodes = 100000 / numThreads;

        // If we are subdividing our grid
        if (1 != 1)
        {
            int colSubgridBase = mQTable.getNumColumns() / mNumThreads;
            int colSubgridRemainder = mQTable.getNumColumns() % mNumThreads;
            int colSubgridRange = 0;
            Position topLeft;
            Position bottomRight;

            for (int i = 0; i < numThreads; ++i) {
                topLeft = new Position(0, colSubgridRange);

                if (colSubgridRemainder > 0) {
                    colSubgridRange += colSubgridBase + 1;
                    colSubgridRemainder--;
                } else {
                    colSubgridRange += colSubgridBase;
                }

                bottomRight = new Position(mQTable.getNumRows() - 1, colSubgridRange - 1);

                Thread t = new Thread(new EpisodeRunner(alpha, gamma, topLeft, bottomRight, numEpisodes));
                threads[i] = t;
            }
        } else {
            for (int i = 0; i < mNumThreads; ++i) {
                Thread t = new Thread(new EpisodeRunner(alpha, gamma, new Position(0, 0),
                        new Position(mQTable.getNumRows() - 1, mQTable.getNumColumns() - 1), numEpisodes));
                threads[i] = t;
            }
        }

        for (int i = 0; i < mNumThreads; ++i) {
            threads[i].start();
        }

        for (int i = 0; i < mNumThreads; ++i) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long endTime = System.nanoTime();

        traverseGrid(start, goal);

        System.out.println("Time to complete: " + ((endTime-startTime) * 1000.0));
    }

    // gets neighbors, updates the reward, and then moves to the next state
    // returns if depth exceeded, goal reached, or an invalid state reached
    private void episode(State state, int depth, double gamma, double alpha) {

//        Class<?> objClass = State.class.getClass();
//        Method setTransitionActionReward = null;
//        try {
//            setTransitionActionReward = objClass.getDeclaredMethod("setTransitionActionReward", String.class, double.class);
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        }
//        setTransitionActionReward.invoke(state, )

        StatePair next_state;
        double reward;
        String direction;

        if(depth > 150)
            return;
        if(state.getReward() > 99.999){
            //System.out.println("reached the goal");
            return;
        }

        // Figure out the next state to take based on the current state. This is chosen at random from the currents
        // states neighbors that aren't walls.
        next_state = nextState(state);

        // Take the action so we can keep track of how many times a direction was taken from this state
        state.takeTransitionAction(next_state.getDirection());

        // Go through the possible actions for the next state and get their action reward values. We will use this to
        // calculate the max action value for all possible actions from the next state
        direction = next_state.getDirection();
        reward = state.getTransitionActionReward(direction) + alpha *
              (
                  next_state.getState().getReward() +
                  gamma * maxActionReward(next_state.getState()) -
                  state.getTransitionActionReward(direction)
              );

        // Set the new transaction reward. We set it based on the max of the current reward for the transition and the
        // calculated reward. This is done to guarantee that the transition action reward value will only ever increase
        // and no decrease.
        state.setTransitionActionReward(direction,
                Math.max(reward, state.getTransitionActionReward(direction)));
        episode(next_state.getState(), depth + 1, gamma, alpha);
    }

    public double maxActionReward(State state){
        double max = 0.0;
        Set<String> actions = state.getActions();
        for(String s : actions){
            if(state.getTransitionActionReward(s) > max){
                max = state.getTransitionActionReward(s);
            }
        }
        return max;
    }

    static Random nextStateGenerator = new Random(5);

    /**
     * Provides the next state to explore given the current state. The current state must not be surrounded by walls
     * @param state The current state
     * @return A pair with the next direction and its corresponding state
     */
    private StatePair nextState(State state) {

        // Get a vector of the next possible states from the current state
        Vector<StatePair> neighbors = mQTable.getNeighbors(state);

        // Choose a random direction to go next from the list of available directions
        int index = nextStateGenerator.nextInt(neighbors.size());

        // Return the random state chosen
        return neighbors.elementAt(index);
    }

    public Vector<StatePair> traverseGrid(Position start, Position goal) {

        Vector<StatePair> thePathTaken = new Vector<>();
        Vector<StatePair> neighbors;
        boolean reachedEnd = false;

        // Traverse the grid until we reach our goal. QLearning will have created a path that leads us directly
        // to the goal.
        while(!reachedEnd){

            // Check if we found the goal
            if(start.equals(goal)){
                System.out.println("Reached the goal");
                reachedEnd = true;
            }else{
                // Else we haven't found the goal yet.
                State curState = mQTable.getState(start);
                neighbors = mQTable.getNeighbors(curState);
                StatePair nextNeighbor = null;
                System.out.println('(' + start.getX() + ',' + start.getY() + ')');
                double maxReward = 0;
                double reward;

                for(StatePair neighbor : neighbors)
                {
                    // Look at the reward values to transition to the next neighbor
                    reward = curState.getTransitionActionReward(neighbor.getDirection());
                    System.out.println("reward for " + neighbor.getDirection() + " is " + reward);
                    if(reward > maxReward)
                    {
                        maxReward = reward;
                        nextNeighbor = neighbor;
                    }
                }

                // If no neighbor was selected, this means all neighbors have a reward value of 0. So we select a
                // random neighbor.
                if (nextNeighbor == null) {
                    //Random generator = new Random();
                    //nextNeighbor = neighbors.get(generator.nextInt(neighbors.size()));
                    nextNeighbor = neighbors.get(0);
                }

                assert nextNeighbor != null;
                System.out.println("Agent moves in direction: " + nextNeighbor.getDirection());


                // Add state to vector representing the path taken
                thePathTaken.add(nextNeighbor);

                Position nextPosition = nextNeighbor.getState().getPosition();
                System.out.println("Moved from " + '(' + start.getX() + ',' + start.getY() + ')'
                        + " to " + '(' + nextPosition.getX() + ',' + nextPosition.getY() + ')');

                // Set the start position to the next position that was just found
                start = nextPosition;
            }
        }
        return thePathTaken;
    }

    /**
     * Retrieves a random number from a random number generator within the range provided.
     * @param generator The random number generator to use
     * @param low The low value (inclusive)
     * @param high The high value (exclusive)
     * @return The generated random number
     */
    public int getRandomNumber(Random generator, int low, int high) {
        int val = generator.nextInt(high - low) + low;
        System.out.println("low = " + low + " high = " + high + " random = " + val);
        return val;
    }
}