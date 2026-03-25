package app.data.scripts.engine.FSM;

public class StateTransition<State extends Enum<State>> {
    private State nxtState;
    private StateTransCond<State> cond;

    public StateTransition(State nxtState, StateTransCond<State> cond) {
        this.nxtState = nxtState;
        this.cond = cond;
    }

    public State getNxtState() {
        return nxtState;
    }

    public Boolean isSatisfied() {
        return cond.isSatisfied();
    }
}
