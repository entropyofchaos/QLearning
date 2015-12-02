import java.util.Set;
import java.util.concurrent.Semaphore;

public class StateSemaphoreLocked extends State {

    private Semaphore mMutex;

    /**
     * Constructor for this state. A state is just a cell on the grid.
     * @param position The Y,X position of the State
     */
    public StateSemaphoreLocked(Position position){

        super(position);
        mMutex = new Semaphore(1);
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

        try {
            mMutex.acquire();
            super.addTransitionAction(direction, rewardValue);
        } catch(InterruptedException ie) {
            // ...
        } finally {
            mMutex.release();
        }
    }

    /**
     * Set the reward for the specified action.
     * @param direction The name of the direction for the action action to update
     * @param rewardValue The value to set the reward to
     */
    @Override
    public void setTransitionActionReward(String direction, double rewardValue){

        try {
            mMutex.acquire();
            super.setTransitionActionReward(direction, rewardValue);
        } catch(InterruptedException ie) {
            // ...
        } finally {
            mMutex.release();
        }
    }

    /**
     * Gets the action for the specified transaction.
     * @param direction The direction to lookup
     * @return The value of the reward
     */
    @Override
    public double getTransitionActionReward(String direction){

        double reward = 0;
        try {
            mMutex.acquire();
            reward = super.getTransitionActionReward(direction);
        } catch(InterruptedException ie) {
            // ...
        } finally {
            mMutex.release();
        }

        return reward;
    }

    /**
     * Increment the number of times the transition action was taken for the specified direction
     * @param direction The direction of the action to update
     */
    @Override
    public void takeTransitionAction(String direction){

        try {
            mMutex.acquire();
            super.takeTransitionAction(direction);
        } catch(InterruptedException ie) {
            // ...
        } finally {
            mMutex.release();
        }
    }

    /**
     * Get number of times a transition action was taken for the specified direction
     * @param direction The direction to lookup
     * @return The number of times the transition action was taken
     */
    @Override
    public int numTransitionActionsTaken(String direction){

        int numTransitionActions = 0;

        try {
            mMutex.acquire();
            numTransitionActions = super.numTransitionActionsTaken(direction);
        } catch(InterruptedException ie) {
            // ...
        } finally {
            mMutex.release();
        }

        return numTransitionActions;
    }

    /**
     * Gets the position of this State.
     * @return The position of the State
     */
    @Override
    public Position getPosition(){

        Position pos = null;

        try {
            mMutex.acquire();
            pos = super.getPosition();
        } catch(InterruptedException ie) {
            // ...
        } finally {
            mMutex.release();
        }

        return pos;
    }

    /**
     * Get all the possible transition actions for this state
     * @return The transition actions
     */
    @Override
    public Set<String> getActions(){

        Set<String> keys = null;

        try {
            mMutex.acquire();
            keys = super.getActions();
        } catch(InterruptedException ie) {
            // ...
        } finally {
            mMutex.release();
        }

        return keys;
    }

    /**
     * Sets the reward value for this state
     * @param reward The reward value
     */
    @Override
    public void setReward(double reward){

        try {
            mMutex.acquire();
            super.setReward(reward);
        } catch(InterruptedException ie) {
            // ...
        } finally {
            mMutex.release();
        }
    }

    /**
     * Gets the reward value for this state
     * @return The reward value
     */
    @Override
    public double getReward(){

        double reward = 0;

        try {
            mMutex.acquire();
            reward = super.getReward();
        } catch(InterruptedException ie) {
            // ...
        } finally {
            mMutex.release();
        }

        return reward;
    }

}
