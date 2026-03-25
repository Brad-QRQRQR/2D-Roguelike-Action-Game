package app.data.scripts.engine.FSM;

public abstract class StateExec<State extends Enum<State>> {
    public abstract void onStateEnter();
    public abstract void onStateExec();
    public abstract void onStateExit();
}
