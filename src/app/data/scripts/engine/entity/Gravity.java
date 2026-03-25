package app.data.scripts.engine.entity;

public class Gravity {
    private final double MAX_GRAVITY;
    private double gravity = 0;
    private double gravityAcceleration = 0.1;

    Entity entity;
    GravityToZero toZero;

    public Gravity(double MAX_GRAVITY, Entity entity, GravityToZero toZero) {
        this.MAX_GRAVITY = MAX_GRAVITY;
        this.entity = entity;
        this.toZero = toZero;
    }

    public void zeroGravity() {
        gravity = 0;
    }

    private void incGravity() {
        toZero.toZero(entity, this);
        gravity += gravityAcceleration;
        gravity = Math.min(gravity, MAX_GRAVITY);
    }

    public void fall(SpeedVector speed) {
        incGravity();
        speed.yAcceleration += +1 * gravity;
    }
}
