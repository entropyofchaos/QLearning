import java.util.HashMap;
import java.util.Set;
import org.apache.commons.lang3.tuple.MutablePair;

public class State {

    private MutablePair<Integer, Integer> mPosition;
    /**
     * Map of actions that can be taken. The key is the name of the action (left, right, up, down).
     * The value is a pair of values where the first is the number of times this action was taken
     * and the second value is the reward value for going that direction.
     */
    private HashMap<String, MutablePair<Integer, Double>> mActions;

    /**
     * The reward value for this state.
     */
    private double mReward;

    /**
     * Constructor for this state. A state is just a cell on the grid.
     * @param position The Y,X position of the State
     */
    public State(MutablePair<Integer, Integer> position){

        mPosition = position;
        mActions = new HashMap<>();
        mReward = 0;
    }

    /**
     * Adds a valid transition (movement) action to the list of valid actions an agent
     * can take along with the reward value for taking that action.
     * @param direction The direction of the transition
     * @param rewardValue The reward value for going the specified direction. The higher the
     *                    reward, the more enticing the direction should be to the agent
     */
    public void addTransitionAction(String direction, double rewardValue){

        // When adding a transition, we set the first variable of the pair (num times taken)
        // to 0 since we have never taken this action yet and the second value to the reward
        // value for taking this action.
        mActions.put(direction, new MutablePair<>(0, rewardValue));
    }

    /**
     * Set the reward for the specified action.
     * @param direction The name of the direction for the action action to update
     * @param rewardValue The value to set the reward to
     */
    public void setTransitionActionReward(String direction, double rewardValue){

        // Update reward value for this direction and set the updated pair back into the hashmap
        MutablePair<Integer, Double> oldValue = mActions.get(direction);
        oldValue.setRight(rewardValue);
        mActions.put(direction, oldValue);
    }

    /**
     * Gets the action for the specified transaction.
     * @param direction The direction to lookup
     * @return The value of the reward
     */
    public double getTransitionActionReward(String direction){

        return mActions.get(direction).getRight();
    }

    /**
     * Increment the number of times the transition action was taken for the specified direction
     * @param direction The direction of the action to update
     */
    public void takeTransitionAction(String direction){

        // Update the number of times  value for this direction and set the updated pair back into the hashmap
        MutablePair<Integer, Double> oldValue = mActions.get(direction);
        oldValue.setLeft(oldValue.getLeft() + 1);
        mActions.put(direction, oldValue);
    }

    /**
     * Get number of times a transition action was taken for the specified direction
     * @param direction The direction to lookup
     * @return The number of times the transition action was taken
     */
    public int numTransitionActionsTaken(String direction){

        return mActions.get(direction).getLeft();
    }

    /**
     * Gets the position of this State.
     * @return The position of the State
     */
    public MutablePair<Integer, Integer> getPosition(){

        return mPosition;
    }

    /**
     * Get all the possible transition actions for this state
     * @return The transition actions
     */
    public Set<String> getActions(){

        return mActions.keySet();
    }

    /**
     * Sets the reward value for this state
     * @param reward The reward value
     */
    public void setReward(double reward){

        mReward = reward;
    }

    /**
     * Gets the reward value for this state
     * @return The reward value
     */
    public double getReward(){

        return mReward;
    }

}
