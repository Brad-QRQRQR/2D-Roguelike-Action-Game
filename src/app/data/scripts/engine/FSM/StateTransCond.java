package app.data.scripts.engine.FSM;

public interface StateTransCond<State extends Enum<State>> {
    public Boolean isSatisfied();
}
