import java.util.Set;
import java.util.concurrent.Semaphore;

public class StateTtasLocked extends State {

    private TestTestAndSetLock mMutex;

    /**
     * Constructor for this state. A state is just a cell on the grid.
     * @param position The Y,X position of the State
     */
    public StateTtasLocked(Position position){

        super(position);
        mMutex = new TestTestAndSetLock();
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
    public void setTransitionActionReward(String direction, double rewardValue){

        mMutex.lock();
        super.setTransitionActionReward(direction, rewardValue);
        mMutex.unlock();
    }

    /**
     * Gets the action for the specified transaction.
     * @param direction The direction to lookup
     * @return The value of the reward
     */
    @Override
    public double getTransitionActionReward(String direction){

        double reward = 0;
        mMutex.lock();
        reward = super.getTransitionActionReward(direction);
        mMutex.unlock();

        return reward;
    }

    /**
     * Increment the number of times the transition action was taken for the specified direction
     * @param direction The direction of the action to update
     */
    @Override
    public void takeTransitionAction(String direction){

        mMutex.lock();
        super.takeTransitionAction(direction);
        mMutex.unlock();
    }

    /**
     * Get number of times a transition action was taken for the specified direction
     * @param direction The direction to lookup
     * @return The number of times the transition action was taken
     */
    @Override
    public int numTransitionActionsTaken(String direction){

        int numTransitionActions = 0;
        mMutex.lock();
        numTransitionActions = super.numTransitionActionsTaken(direction);
        mMutex.unlock();

        return numTransitionActions;
    }

    /**
     * Gets the position of this State.
     * @return The position of the State
     */
    @Override
    public Position getPosition(){

        Position pos = null;

        mMutex.lock();
        pos = super.getPosition();
        mMutex.unlock();

        return pos;
    }

    /**
     * Get all the possible transition actions for this state
     * @return The transition actions
     */
    @Override
    public Set<String> getActions(){

        Set<String> keys = null;

        mMutex.lock();
        keys = super.getActions();
        mMutex.unlock();

        return keys;
    }

    /**
     * Sets the reward value for this state
     * @param reward The reward value
     */
    @Override
    public void setReward(double reward){

        mMutex.lock();
        super.setReward(reward);
        mMutex.unlock();
    }

    /**
     * Gets the reward value for this state
     * @return The reward value
     */
    @Override
    public double getReward(){

        double reward = 0;

        mMutex.lock();
        reward = super.getReward();
        mMutex.unlock();

        return reward;
    }

}
