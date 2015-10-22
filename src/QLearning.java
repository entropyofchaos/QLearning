import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

public class QLearning {

    private Grid g;

    public QLearning(Grid grid, Pair<Integer, Integer> start, MutablePair<Integer, Integer> goal) {

        Grid g = new Grid("world.txt", goal);
        double alpha = 1;
        double gamma = 0.8;
        int x;
        int y;

        g.printWorld();
        g.printRewards();

        for(int i = 0; i < 1000; ++i)
        {
            Vector<State> neighbors;
            do{
                y = (int)(Math.random() * (double)g.getNumColumns());
                x = (int)(Math.random() * (double)g.getNumRows());
                neighbors = g.getNeighbors(MutablePair.of(x, y));
            } while(neighbors.size() == 0 || g.getReward(MutablePair.of(x, y)) == 100);

            episode(grid.getWorld(),q_table,q_table.elementAt(y).elementAt(x),0,gamma,alpha);

        }
        printQTable(q_table);
        traverseGrid(start, goal, q_table, grid.getWorld());
    }

    private Vector<Pair> traverseGrid(Pair<Integer, Integer> start, Pair<Integer, Integer> goal,
                                      Vector<Vector<State>> q_table, Vector<String> grid) {
        Pair<Integer,Integer> local_start = start;
        Pair<Integer,Integer> local_goal = goal;
        Set<String> neigh;
        double reward;
        Vector<Pair> paths=new Vector<>();
        String direction=new String();
        boolean reachedEnd = false;
        double maxReward = 0;
        int index=0,i=0;
        //paths
        while(!reachedEnd){
            if(local_goal.getLeft()==local_goal.getLeft() &&local_start.getRight()==local_goal.getRight())
            {
                reachedEnd=true;
                break;
            }
            else{
                neigh = q_table.elementAt(local_start.getRight()).elementAt(local_start.getLeft()).getActions();
                direction = neigh.iterator().next();
                for(Iterator<String>it=neigh.iterator();it.hasNext();)
                {
                    String x=it.next();
                    reward =    q_table.elementAt(local_start.getRight()).elementAt(local_start.getLeft()).
                            getTrasitionActionReward(x).getRight();
                    if(reward>maxReward)
                    {
                        maxReward=reward;
                        index=i;
                        direction= x;
                    }
                    i+=1;
                }
                System.out.println("Agent selects direction"+direction);
                if(Math.random()*9<6)
                {
                    String wanted=direction;
                    while(direction == wanted &&neigh.size()>1)
                    {
                        int pick = (int)(Math.random()*neigh.size());
                        direction =  getElementFromSet(neigh,pick);
                    }

                }
                System.out.println("Agent moves in direction:"+direction);
                State next_pos;
                if(direction=="left")
                    next_pos=q_table.elementAt(local_start.getRight()).elementAt(local_start.getLeft()-1);
                else if(direction=="right")
                    next_pos=q_table.elementAt(local_start.getRight()).elementAt(local_start.getLeft()+1);
                else if(direction=="up")
                    next_pos=q_table.elementAt(local_start.getRight()-1).elementAt(local_start.getLeft()+1);
                else if(direction=="down")
                    next_pos=q_table.elementAt(local_start.getRight()+1).elementAt(local_start.getLeft()+1);

            }
        paths.add(local_start);
        }
        return paths;

    }


    public void episode(State state, int depth, double gamma, double alpha){

      Set<String> neigh;
      State next_state;
      double reward;
      String direction;
      if(depth>150)
          return;
      if(state.getReward() == 100)
          return;

      neigh = state.getActions();
      MutablePair<State, String> n = nextState(qt, neigh,state);
      state.takeTransitionAction(n.getRight());
      //reward = state.getTrasitionActionReward(direction) + alpha*(next_state.getReward()+gamma*)
    episode(world,qt,n.getLeft(),depth+1,gamma,alpha);

  }

    private MutablePair<State, String> nextState(Vector<Vector<State>> qt, Set<String> neigh, State curr) {
        MutablePair<State, String> mp = new MutablePair<>();
        MutablePair<Integer,Integer> xy = new MutablePair<>();
        int index= (int)(Math.random()*neigh.size());
        Iterator<String> it=neigh.iterator();
        for(int i=0;i<index+1;i++)
            mp.setRight(it.next());
        mp.setLeft(null);
        xy = curr.getPosition();
        if(mp.getRight()=="left")
                mp.setLeft(qt.elementAt(xy.getRight()).elementAt(xy.getLeft()-1));
        else if(mp.getRight()=="right")
            mp.setLeft(qt.elementAt(xy.getRight()).elementAt(xy.getLeft()+1));
        else if(mp.getRight()=="up")
            mp.setLeft(qt.elementAt(xy.getRight()-1).elementAt(xy.getLeft()));
        else if(mp.getRight()=="down")
            mp.setLeft(qt.elementAt(xy.getRight()+1).elementAt(xy.getLeft()));

        return mp;
    }

    //please change this function after updating state
    public void printQTable(Vector<Vector<State>> q_table) {
        int x,y;
        String str= new String();
        for(y=0;y<q_table.size();y++)
            for(x=0;x<q_table.elementAt(0).size();x++)
            {
                str = "State " + (q_table.elementAt(y).elementAt(x).getPosition()) + "'s Actions: " +
                        (q_table.elementAt(y).elementAt(x).numTransitionActionsTaken("right"));
                System.out.println(str);
            }

    }

    public String getElementFromSet(Set<String> neigh, int index)
    {
        Iterator<String>it=neigh.iterator();
        String ret = new String();
        for(int i=0;i<=index;i++)
        {
            ret= it.next();
        }
        return ret;
    }
}