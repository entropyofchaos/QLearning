public class StatePair{
    private String direction;
    private State state;

    public StatePair(String direction, State state){
        this.direction = direction;
        this.state = state;
    }

    public String getDirection(){
        return direction;
    }
    
    public State getState(){
        return state;
    }
}
