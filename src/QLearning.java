import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

public class QLearning {

    private Grid q_table;

    public QLearning(Grid grid, Pair<Integer, Integer> start, MutablePair<Integer, Integer> goal) {

        q_table = new Grid("world.txt", goal);
        double alpha = 1;
        double gamma = 0.8;
        int x;
        int y;

        q_table.printWorld();
        q_table.printRewards();

        for(int i = 0; i < 1000; ++i)
        {
            Vector<MutablePair<String, State>> neighbors;
            MutablePair<Integer, Integer> randomLocation;
            do{
                y = (int)(Math.random() * (double)q_table.getNumColumns());
                x = (int)(Math.random() * (double)q_table.getNumRows());
                randomLocation = new MutablePair<>(x, y);
                neighbors = q_table.getNeighbors(randomLocation);
            } while(neighbors.size() == 0 || q_table.getReward(randomLocation) == 100);

            episode(q_table.getState(randomLocation), 0, gamma, alpha);

        }
        q_table.printQTable();
        traverseGrid(start, goal, q_table, grid.getWorld());
    }

    private Vector<Pair> traverseGrid(Pair<Integer, Integer> start, Pair<Integer, Integer> goal,
                                      Vector<Vector<State>> q_table, Vector<String> grid) {
        Pair<Integer,Integer> local_start = start;
        Pair<Integer,Integer> local_goal = goal;
        Set<String> neighbors;
        double reward;
        Vector<Pair> paths = new Vector<>();
        String direction;
        boolean reachedEnd = false;
        double maxReward = 0;
        int index = 0,i = 0;
        //paths
        while(!reachedEnd){
            // Check if we found the goal
            if(local_goal.getLeft().equals(local_goal.getLeft()) &&
                    local_start.getRight().equals(local_goal.getRight()))
            {
                reachedEnd = true;
            }
            else{
                // Else we haven't found the goal yet.
                neighbors = q_table.elementAt(local_start.getRight()).elementAt(local_start.getLeft()).getActions();
                direction = neighbors.iterator().next();
                for(String x : neighbors)
                {
                    reward = q_table.elementAt(local_start.getRight()).elementAt(local_start.getLeft()).
                            getTransitionActionReward(x);
                    if(reward > maxReward)
                    {
                        maxReward = reward;
                        index = i;
                        direction = x;
                    }
                    i += 1;
                }

                System.out.println("Agent selects direction" + direction);

                if(Math.random() * 9 < 6)
                {
                    String wanted = direction;
                    while(direction.equals(wanted) && neighbors.size() > 1)
                    {
                        int pick = (int)(Math.random() * neighbors.size());
                        direction =  getElementFromSet(neighbors, pick);
                    }

                }

                System.out.println("Agent moves in direction:" + direction);
                State next_pos = null;
                if(direction.equals("left"))
                    next_pos=q_table.elementAt(local_start.getRight()).elementAt(local_start.getLeft()-1);
                else if(direction.equals("right"))
                    next_pos=q_table.elementAt(local_start.getRight()).elementAt(local_start.getLeft()+1);
                else if(direction.equals("up"))
                    next_pos=q_table.elementAt(local_start.getRight()-1).elementAt(local_start.getLeft()+1);
                else if(direction.equals("down"))
                    next_pos=q_table.elementAt(local_start.getRight()+1).elementAt(local_start.getLeft()+1);
                if (next_pos != null) {
                    local_start = next_pos.getPosition();
                    paths.add(local_start);
                }
            }
        }
        return paths;
    }

    // gets neighbors, updates the reward, and then moves to the next state
    // returns if depth exceeded, goal reached, or an invalid state reached
    public void episode(State state, int depth, double gamma, double alpha){

        Vector<MutablePair<String, State>> neighbors = q_table.getNeighbors(state.getPosition());
        MutablePair<State, String> next_state;
        double reward;
        String direction;

        if(depth > 150)
          return;
        if(state.getReward() == 100)
          return;

        next_state = nextState(qt, neigh,state);
        state.takeTransitionAction(next_state.getRight());

        Vector<Double> actionRewards = new Vector<>();
        for (MutablePair<String, State> neighbor : neighbors) {
          actionRewards.add(next_state.getLeft().getTransitionActionReward(neighbor.getLeft()));
        }

        direction = next_state.getRight();
        reward = state.getTransitionActionReward(direction) + alpha *
              (
                  next_state.getLeft().getReward() + gamma * findMax(actionRewards) -
                  state.getTransitionActionReward(direction)
              );

        state.setTransitionActionReward(direction,
                findMax(reward, next_state.getLeft().getTransitionActionReward(direction)));

        episode(next_state.getLeft(), depth + 1, gamma, alpha);

  }

    private MutablePair<String, State> nextState(Vector<Vector<State>> qt, Set<String> neigh, State curr) {
        MutablePair<State, String> statePair = new MutablePair<>();
        MutablePair<Integer,Integer> xy;
        int index= (int)(Math.random()*neigh.size());
        Iterator<String> it=neigh.iterator();
        for(int i=0;i<index+1;i++)
            statePair.setRight(it.next());
        statePair.setLeft(null);
        xy = curr.getPosition();
        if(statePair.getRight().equals("left"))
                statePair.setLeft(qt.elementAt(xy.getRight()).elementAt(xy.getLeft() - 1));
        else if(statePair.getRight().equals("right"))
            statePair.setLeft(qt.elementAt(xy.getRight()).elementAt(xy.getLeft()+1));
        else if(statePair.getRight().equals("up"))
            statePair.setLeft(qt.elementAt(xy.getRight()-1).elementAt(xy.getLeft()));
        else if(statePair.getRight().equals("down"))
            statePair.setLeft(qt.elementAt(xy.getRight()+1).elementAt(xy.getLeft()));

        return statePair;
    }

    public String getElementFromSet(Set<String> neigh, int index)
    {
        Iterator<String>it = neigh.iterator();
        String ret = null;
        for(int i=0; i<= index; i++)
        {
            ret= it.next();
        }
        return ret;
    }

    private double findMax(Vector<Double> vals) {
        double max = Double.NEGATIVE_INFINITY;

        for (double d : vals) {
            if (d > max) max = d;
        }

        return max;
    }

    private double findMax(double... vals) {
        double max = Double.NEGATIVE_INFINITY;

        for (double d : vals) {
            if (d > max) max = d;
        }

        return max;
    }
}