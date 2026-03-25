package app.data.scripts.engine.tools;

public class ActionTimer {
    private double timer = 0;

    public ActionTimer() {}

    public void decTimer(double dt) {
        setTimer(getTimer() - dt);
    }

    public void setTimer(double val) {
        timer = Math.max(0, val);
    }

    public double getTimer() {
        return timer;
    }

    public Boolean isOver() {
        return timer <= 0;
    }
}
