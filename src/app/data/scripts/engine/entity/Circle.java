package app.data.scripts.engine.entity;

public class Circle extends Entity {
    private double x;
    private double y;
    private double radius;

    public Circle(double x, double y, double radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    public void setX(double val) {
        this.x = val;
    }

    public double getX() {
        return x;
    }

    public void setY(double val) {
        this.y = val;
    }

    public double getY() {
        return y;
    }

    public void setRadius(double val) {
        this.radius = Math.max(0, val);
    }

    public double getRadius() {
        return radius;
    }
}
