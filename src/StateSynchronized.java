import java.util.Set;

public class StateSynchronized extends State {

    /**
     * Constructor for this state. A state is just a cell on the grid.
     * @param position The Y,X position of the State
     */
    public StateSynchronized(Position position){

        super(position);
    }

    /**
     * Adds a valid transition (movement) action to the list of valid actions an agent
     * can take along with the reward value for taking that action.
     * @param direction The direction of the transition
     * @param rewardValue The reward value for going the specified direction. The higher the
     *                    reward, the more enticing the direction should be to the agent
     */
    @Override
    public void addTransitionAction(String direction, double rewardValue){

        super.addTransitionAction(direction, rewardValue);
    }

    /**
     * Set the reward for the specified action.
     * @param direction The name of the direction for the action action to update
     * @param rewardValue The value to set the reward to
     */
    @Override
    public synchronized void setTransitionActionReward(String direction, double rewardValue){

        super.setTransitionActionReward(direction, rewardValue);
    }

    /**
     * Gets the action for the specified transaction.
     * @param direction The direction to lookup
     * @return The value of the reward
     */
    @Override
    public synchronized double getTransitionActionReward(String direction){

        return super.getTransitionActionReward(direction);
    }

    /**
     * Increment the number of times the transition action was taken for the specified direction
     * @param direction The direction of the action to update
     */
    @Override
    public synchronized void takeTransitionAction(String direction){

       super.takeTransitionAction(direction);
    }

    /**
     * Get number of times a transition action was taken for the specified direction
     * @param direction The direction to lookup
     * @return The number of times the transition action was taken
     */
    @Override
    public synchronized int numTransitionActionsTaken(String direction){

        return super.numTransitionActionsTaken(direction);
    }

    /**
     * Gets the position of this State.
     * @return The position of the State
     */
    @Override
    public Position getPosition(){

        return super.getPosition();
    }

    /**
     * Get all the possible transition actions for this state
     * @return The transition actions
     */
    @Override
    public synchronized Set<String> getActions(){

        return super.getActions();
    }

    /**
     * Sets the reward value for this state
     * @param reward The reward value
     */
    @Override
    public synchronized void setReward(double reward){

        super.setReward(reward);
    }

    /**
     * Gets the reward value for this state
     * @return The reward value
     */
    @Override
    public synchronized double getReward(){

        return super.getReward();
    }

}
