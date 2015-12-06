import org.apache.commons.lang3.tuple.MutablePair;

import java.util.Random;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

public class QLearning {

    private Grid mQTable;
    private Random mNextStateGenerator = new Random();

    private class EpisodeRunner implements Runnable {

        double mGamma;
        double mAlpha;
        Random mGenerator;
        Position mTopLeft;
        Position mBottomRight;
        int mNumEpisodes;
        Grid.GridDivisionType mGridDivisionType;
        boolean mComplete;
        AtomicBoolean mShutdown;

        EpisodeRunner(double alpha, double gamma, Position topLeft, Position bottomRight, int numEpisodes,
                      Grid.GridDivisionType gridDivisionType) {
            mAlpha = alpha;
            mGamma = gamma;
            mTopLeft = topLeft;
            mBottomRight = bottomRight;
            mGenerator = new Random();
            mNumEpisodes = numEpisodes;
            mGridDivisionType = gridDivisionType;
            mComplete = false;
            mShutdown = new AtomicBoolean(false);
        }

        public void setBottomRight(Position bottomRight) {
            mBottomRight = bottomRight;
        }

        public void setTopLeft(Position topLeft) {
            mTopLeft = topLeft;
        }

        public synchronized void waitUntilComplete() {
            if (!mComplete) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void shutdown() {
            mShutdown.set(true);
            waitUntilComplete();

            synchronized (this) {
                notifyAll();
            }
        }

        @Override
        public void run() {
            int x;
            int y;

            while (!mShutdown.get()) {
                mComplete = false;

                for (int i = 0; i < mNumEpisodes; ++i) {
                    Position randomLocation;
                    do {
                        x = getRandomNumber(mGenerator, mTopLeft.getX(), mBottomRight.getX() + 1);
                        y = getRandomNumber(mGenerator, mTopLeft.getY(), mBottomRight.getY() + 1);
                        randomLocation = new Position(y, x);

                    } while (mQTable.locationIsWall(randomLocation) || mQTable.getReward(randomLocation) > 99.999);

                    if (mGridDivisionType != Grid.GridDivisionType.RecursiveGrid) {
                        episode(mQTable.getState(randomLocation), 0, mGamma, mAlpha);
                    } else {
                        episode(mQTable.getState(randomLocation), 0, mGamma, mAlpha, mTopLeft, mBottomRight);
                    }
                }

                mComplete = true;
                synchronized (this) {
                    notifyAll();
                }

                // Code finished. If we aren't set to shutdown, put thread to sleep. The thread's parameters can then be
                // changed and allowed to rerun again saving on thread creation time.
                if (!mShutdown.get()) {
                    synchronized (this) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public double doQLearning(Position start, Position goal, int numEpisodes, Grid.LockType lockType, int numThreads,
                     Grid.GridDivisionType gridDivisionType) {

        //mQTable = new Grid("worldSmall.txt", goal);
        mQTable = new Grid("complexWorld.txt", goal, lockType, numThreads, gridDivisionType);

        mQTable.printWorld();
        mQTable.printRewards();

        long startTime = System.nanoTime();

        Vector<Thread> threads = new Vector<>(numThreads);
        Vector<EpisodeRunner> episodeRunners = new Vector<>(numThreads);
        numEpisodes = numEpisodes / numThreads;
        learnFn(threads, episodeRunners, numEpisodes, numThreads, gridDivisionType, true);

        long endTime = System.nanoTime();

        traverseGrid(start, goal);

        double timeToLearn = (endTime-startTime) / 1000000000.0;
        System.out.println("Time to complete: " + timeToLearn);
        return timeToLearn;
    }

    private void learnFn(Vector<Thread> threads, Vector<EpisodeRunner> episodeRunners, int numEpisodes, int numDivisions,
                       Grid.GridDivisionType gridDivisionType, boolean firstTimeRun) {

        double alpha = 1;
        double gamma = 0.8;

        Vector<MutablePair<Position, Position>> subgridCorners = mQTable.getSubgridCorners(numDivisions);

        if (firstTimeRun) {
            // Create new threads and episode runners
            for (int i = 0; i < subgridCorners.size(); ++i) {

                EpisodeRunner episodeRunner = new EpisodeRunner(alpha, gamma, subgridCorners.elementAt(i).getLeft(),
                        subgridCorners.elementAt(i).getRight(), numEpisodes, gridDivisionType);

                episodeRunners.add(episodeRunner);
                Thread t = new Thread(episodeRunner);
                threads.add(t);
                t.start();
            }
        } else {
            // Reuse threads and episode runners, and rerun on new subdivisions
            for (int i = 0; i < subgridCorners.size(); ++i) {

                EpisodeRunner curRunner = episodeRunners.elementAt(i);
                curRunner.setTopLeft(subgridCorners.elementAt(i).getLeft());
                curRunner.setBottomRight(subgridCorners.elementAt(i).getRight());
                synchronized (curRunner) {
                    curRunner.notifyAll();
                }
            }
        }

        episodeRunners.forEach(EpisodeRunner::waitUntilComplete);

        if (gridDivisionType == Grid.GridDivisionType.RecursiveGrid && numDivisions != 1) {
            learnFn(threads, episodeRunners, numEpisodes, numDivisions / 2, gridDivisionType, false);
        } else {
            episodeRunners.forEach(EpisodeRunner::shutdown);
            episodeRunners.forEach(EpisodeRunner::waitUntilComplete);
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // gets neighbors, updates the reward, and then moves to the next state
    // returns if depth exceeded, goal reached, or an invalid state reached
    private void episode(State state, int depth, double gamma, double alpha) {

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

    private void episode(State state, int depth, double gamma, double alpha, Position topLeft, Position bottomRight) {

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
        next_state = nextState(state, topLeft, bottomRight);

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
        episode(next_state.getState(), depth + 1, gamma, alpha, topLeft, bottomRight);
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

    /**
     * Provides the next state to explore given the current state. The current state must not be surrounded by walls
     * @param state The current state
     * @return A pair with the next direction and its corresponding state
     */
    private StatePair nextState(State state) {

        // Get a vector of the next possible states from the current state
        Vector<StatePair> neighbors = mQTable.getNeighbors(state);

        // Choose a random direction to go next from the list of available directions
        int index = mNextStateGenerator.nextInt(neighbors.size());

        // Return the random state chosen
        return neighbors.elementAt(index);
    }

    /**
     * Overload of StatePair nextState(State state). This method is identical except that it restricts the next state
     * to states that are within the subgrid constrained by the provided top left and bottom right positions.
     * @param state The current state
     * @param topLeft Top left position of the subgrid
     * @param bottomRight Bottom right position of the subgrid
     * @return A pair with the next direction and its corresponding state
     */
    private StatePair nextState(State state, Position topLeft, Position bottomRight) {

        // Get a vector of the next possible states from the current state
        Vector<StatePair> neighbors = mQTable.getNeighbors(state, topLeft, bottomRight);

        // Choose a random direction to go next from the list of available directions
        int index = mNextStateGenerator.nextInt(neighbors.size());

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

        return generator.nextInt(high - low) + low;
    }
}