import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Vector;

public class QLearning {

    private double alpha = 0;
    private double gamma = 0.8;
    Vector<Vector<State>> q_table;
//    public static void main (String[] args) {
//        System.out.println("Hello!");
//    }

    public QLearning(Grid grid, Pair<Integer, Integer> start, Pair<Integer, Integer> goal) {
        q_table = new Vector<>();
        double alpha = 1;
        double gamma = 0.8;
        int i,x,y;

        qInit(q_table, grid.getWorld(),goal);
        printQTable(q_table);
        printRewards(q_table);

        for(i=0;i<1000;i++)
        {
            y =(int)Math.random()*q_table.size();
            x =(int)Math.random()*q_table.elementAt(y).size();
            while(grid.getWorld().elementAt(y).charAt(x)=='x' || q_table.elementAt(y).elementAt(x).getReward() ==100 ){
                y =(int)Math.random()*q_table.size();
                x =(int)Math.random()*q_table.elementAt(y).size();
            }
            //episode(grid,q_table,q_table.elementAt(y).charAt(x),0,gamma,alpha);

        }
        printQTable(q_table);
        //traverseGrid(start,goal,q_table,grid);
    }

   public Vector<State> getNeighbors(Vector<String> world, Vector<Vector<State>> qt, State state){
       Vector<State> neighbours = new Vector<>();
       MutablePair pos = state.getPosition();
       int x = (int)pos.getLeft(), y = (int)pos.getRight(),n = qt.size(), m = qt.elementAt(0).size();
       if(x+1 < m && world.elementAt(y).charAt(x+1) != 'x')
           neighbours.add(qt.elementAt(y).elementAt(x + 1));
       if(x-1 >= 0 && world.elementAt(y).charAt(x-1) != 'x')
           neighbours.add(qt.elementAt(y).elementAt(x - 1));
       if(y+1 < n && world.elementAt(y+1).charAt(x) != 'x')
           neighbours.add(qt.elementAt(y + 1).elementAt(x));
       if(y-1 >=0 && world.elementAt(y-1).charAt(x) != 'x')
           neighbours.add(qt.elementAt(y-1).elementAt(x));



       return  neighbours;
   }



    public void printRewards(Vector<Vector<State>> q_table) {
        int x,y;
        String str= new String();
        for(y=0;y<q_table.size();y++) {
            str="";
            for (x = 0; x < q_table.elementAt(0).size(); x++) {
                str += q_table.elementAt(y).elementAt(x).getReward() + " ";
                System.out.println(str);
            }
        }
    }



    //please change this function after updating state
    public void printQTable(Vector<Vector<State>> q_table) {
        int x,y;
        String str= new String();
        for(y=0;y<q_table.size();y++)
            for(x=0;x<q_table.elementAt(0).size();x++)
            {
                str = "State " + (q_table.elementAt(y).elementAt(x).getPosition()) + "'s Actions: " + (q_table.elementAt(y).elementAt(x).numTransitionActionsTaken("right"));
                System.out.println(str);
            }

    }

    public void qInit(Vector<Vector<State>> qt,Vector<String> world,Pair<Integer, Integer> goal){
        int n = world.size();
        int m  = world.elementAt(0).length();
        int y,x;
        Vector<State> ls;
        State temp;
        for(y=0;y<n;y++)
        {
            ls = new Vector<>();
            for(x=0;x<m;x++)
            {
                temp = new State(MutablePair.of(x,y));
                if(x+1 < m && world.elementAt(y).charAt(x+1) != 'x')
                    temp.addTransitionAction("right", 0);
                if(x-1 >= 0 && world.elementAt(y).charAt(x-1) != 'x')
                    temp.addTransitionAction("left", 0);
                if(y+1 < n && world.elementAt(y+1).charAt(x) != 'x')
                    temp.addTransitionAction("down", 0);
                if(y-1 >=0 && world.elementAt(y-1).charAt(x) != 'x')
                    temp.addTransitionAction("up", 0);

                //Yuksel/ Joe please check the validity here
                if(x == goal.getLeft() && y == goal.getRight())
                    temp.setReward(100);

                ls.add(temp);

            }
            qt.add(ls);
        }

    }
}