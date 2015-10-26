import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Random;
import java.util.Scanner;
import java.util.Vector;

public class QLearning {

    private Grid q_table;

    public QLearning(MutablePair<Integer, Integer> start, MutablePair<Integer, Integer> goal) {

        q_table = new Grid("world.txt", goal);
        double alpha = 1;
        double gamma = 0.8;
        int x;
        int y;

        q_table.printWorld();
        q_table.printRewards();

        Random generator = new Random();

        for(int i = 0; i < 1000; ++i)
        {
            Vector<MutablePair<String, State>> neighbors;
            MutablePair<Integer, Integer> randomLocation;
            do{
                x = generator.nextInt(q_table.getNumRows());
                y = generator.nextInt(q_table.getNumColumns());
                randomLocation = new MutablePair<>(x, y);

                neighbors = q_table.getNeighbors(q_table.getState(randomLocation));
            } while(neighbors.size() == 0 || q_table.getReward(randomLocation) == 100);

            episode(q_table.getState(randomLocation), 0, gamma, alpha);

        }
        q_table.printQTable();
        Scanner in = new Scanner(System.in);
        System.out.println("Press any key to begin traversal...");
        in.next();

        traverseGrid(q_table.getState(start).getPosition(), q_table.getState(goal).getPosition());
    }

    // gets neighbors, updates the reward, and then moves to the next state
    // returns if depth exceeded, goal reached, or an invalid state reached
    private void episode(State state, int depth, double gamma, double alpha){

        Vector<MutablePair<String, State>> neighbors = q_table.getNeighbors(state);
        MutablePair<String, State> next_state;
        double reward;
        String direction;

        if(depth > 150)
          return;
        if(state.getReward() == 100)
          return;

        next_state = nextState(state);
        state.takeTransitionAction(next_state.getLeft());

        Vector<Double> actionRewards = new Vector<>();
        for (MutablePair<String, State> neighbor : neighbors) {
          actionRewards.add(next_state.getRight().getTransitionActionReward(neighbor.getLeft()));
        }

        direction = next_state.getLeft();
        reward = state.getTransitionActionReward(direction) + alpha *
              (
                  next_state.getRight().getReward() + gamma * findMax(actionRewards) -
                  state.getTransitionActionReward(direction)
              );

        state.setTransitionActionReward(direction,
                findMax(reward, next_state.getRight().getTransitionActionReward(direction)));

        episode(next_state.getRight(), depth + 1, gamma, alpha);

  }

    private MutablePair<String, State> nextState(State state) {

        Vector<MutablePair<String, State>> neighbors = q_table.getNeighbors(state);

        // Choose a random direction to go next from the list of available directions
        Random generator = new Random();
        int index = generator.nextInt(neighbors.size());

        return neighbors.elementAt(index);
    }

    public Vector<Pair<String, State>> traverseGrid(MutablePair<Integer, Integer> start,
                                                    MutablePair<Integer, Integer> goal) {
        Vector<Pair<String, State>> thePathTaken = new Vector<>();
        Vector<MutablePair<String, State>> neighbors;
        double reward;
        Vector<Pair> paths = new Vector<>();
        boolean reachedEnd = false;
        double maxReward = 0;

        // Traverse the grid until we reach our goal. QLearning will have created a path that leads us directly
        // to the goal.
        while(!reachedEnd){
            // Check if we found the goal
            if(start == goal)
            {
                reachedEnd = true;
            }
            else{
                // Else we haven't found the goal yet.
                State curState = q_table.getState(start);
                neighbors = q_table.getNeighbors(curState);
                MutablePair<String, State> nextNeighbor = null;

                for(MutablePair<String, State> neighbor : neighbors)
                {
                    nextNeighbor = neighbor;
                    reward = neighbor.getRight().getTransitionActionReward(neighbor.getLeft());
                    if(reward > maxReward)
                    {
                        maxReward = reward;
                        nextNeighbor = neighbor;
                    }
                }

                assert nextNeighbor != null;
                System.out.println("Agent moves in direction:" + nextNeighbor.getLeft());

                thePathTaken.add(nextNeighbor);
            }
        }
        return thePathTaken;
    }

    double findMax(Vector<Double> vals) {
        double max = Double.NEGATIVE_INFINITY;

        for (double d : vals) {
            if (d > max) max = d;
        }

        return max;
    }

    double findMax(double... vals) {
        double max = Double.NEGATIVE_INFINITY;

        for (double d : vals) {
            if (d > max) max = d;
        }

        return max;
    }
}