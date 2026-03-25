package app.data.scripts.engine.FSM;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class StateMachine<State extends Enum<State>> {
    private State currentState;
    private HashMap<State, StateExec<State>> stateExec;
    private HashMap<State, ArrayList<StateTransition<State>>> stateTrans;

    public abstract void initState();
    public abstract void initStateTrans();

    public StateMachine(State initSta) {
        currentState = initSta;
        stateExec = new HashMap<State, StateExec<State>>();
        stateTrans = new HashMap<State, ArrayList<StateTransition<State>>>();
        initState();
        initStateTrans();
    }

    public void validateStateSet(State sta) {
        if (stateExec.containsKey(sta) == false) {
            throw new IllegalArgumentException("The state \"" + sta + "\" has not been set yet.");
        }
    }

    public void enter() {
        validateStateSet(currentState);
        stateExec.get(currentState).onStateEnter();
    }

    public void exec() {
        validateStateSet(currentState);
        stateExec.get(currentState).onStateExec();
    }

    public void exit() {
        validateStateSet(currentState);
        stateExec.get(currentState).onStateExit();
    }

    public State getCurrentState() {
        return currentState;
    }

    private Boolean transState(StateTransition<State> trans) {
        if (trans.isSatisfied()) {
            exit();
            currentState = trans.getNxtState();
            enter();
            return true;
        }
        return false;
    }

    private Boolean transState(StateTransition<State> trans, Boolean show) {
        if (trans.isSatisfied()) {
            if (show) {
                System.out.println("State Trans: " + currentState + " -> " + trans.getNxtState());
            }
            exit();
            currentState = trans.getNxtState();
            enter();
            return true;
        }
        return false;
    }

    public void listenTrans() {
        Boolean run = true;
        while (run) {
            run = false;
            if (stateTrans.get(currentState) == null) continue;
            for (var trans : stateTrans.get(currentState)) {
                if ((run = transState(trans))) {
                    break;
                }
            }
        }
    }

    public void listenTrans(Boolean show) {
        Boolean run = true;
        while (run) {
            run = false;
            for (var trans : stateTrans.get(currentState)) {
                if ((run = transState(trans, show))) {
                    break;
                }
            }
        }
    }

    public void setStateExec(State sta, StateExec<State> exec) {
        if (stateExec.containsKey(sta)) {
            throw new IllegalArgumentException("The state \"" + sta + "\" has already been set.");
        }
        stateExec.put(sta, exec);
        stateTrans.put(sta, new ArrayList<>());
    }

    public void setStateTrans(State start, StateTransition<State> trans) {
        /*if (stateTrans.get(start).contains(trans)) {
            throw new IllegalArgumentException(
                "The transition \"" + start + " -> " + trans.getNxtState() + "\" has already been set."
            );
        }*/
        stateTrans.get(start).add(trans);
    }

    public void showCurrentState() {
        System.out.println("Current State: " + currentState);
    }

    public void showStateTrans() {
        System.out.println("State Transition List start:");
        System.out.println("===========================");
        for (var sta : stateTrans.entrySet()) {
            for (var trans : sta.getValue()) {
                System.out.println(sta.getKey() + " -> " + trans.getNxtState());
            }
        }
        System.out.println("===========================");
    }
}
