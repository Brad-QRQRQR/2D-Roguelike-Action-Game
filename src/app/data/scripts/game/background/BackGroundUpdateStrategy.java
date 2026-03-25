package app.data.scripts.game.background;

import app.data.scripts.engine.entity.UpdateStrategy;

public class BackGroundUpdateStrategy extends UpdateStrategy {
    private double dt;

    public BackGroundUpdateStrategy(double dt) {
        setSelf(null);
        this.dt = dt;
    }

    public double getDt() {
        return dt;
    }

    @Override
    public void perform() {

    }
}
