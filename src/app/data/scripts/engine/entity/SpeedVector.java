package app.data.scripts.engine.entity;

public class SpeedVector {
    public double xAcceleration = 0;
    public double yAcceleration = 0;

    private final double MAX_X_VELOCITY;
    private final double MAX_Y_VELOCITY;

    private double xVelocity = 0;
    private int xDirection = 1;
    private double yVelocity = 0;
    private int yDirection = 1;

    public SpeedVector(double MAX_X_VELOCITY, double MAX_Y_VELOCITY) {
        this.MAX_X_VELOCITY = MAX_X_VELOCITY;
        this.MAX_Y_VELOCITY = MAX_Y_VELOCITY;
    }

    public void initAcceleration() {
        xAcceleration = yAcceleration = 0;
    }

    public void initXSpeed() {
        xVelocity = 0;
        xDirection = 1;
    }

    public void initYSpeed() {
        yVelocity = 0;
        yDirection = 1;
    }

    public void setXSpeed() {
        xVelocity = xVelocity * xDirection + xAcceleration;
        xAcceleration = 0;
        if (xVelocity >= 0) {
            xDirection = 1;
        }else {
            xDirection = -1;
            xVelocity *= -1;
        }
        if (xVelocity > MAX_X_VELOCITY) {
            xVelocity = MAX_X_VELOCITY;
        }
    }

    public double getXSpeed() {
        return xVelocity * xDirection;
    }

    public void setYSpeed() {
        yVelocity = yVelocity * yDirection + yAcceleration;
        yAcceleration = 0;
        if (yVelocity >= 0) {
            yDirection = 1;
        }else {
            yDirection = -1;
            yVelocity *= -1;
        }
        if (yVelocity > MAX_Y_VELOCITY) {
            yVelocity = MAX_Y_VELOCITY;
        }
    }

    public double getYSpeed() {
        return yVelocity * yDirection;
    }
}
