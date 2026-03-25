package app.data.scripts.engine.entity;

import app.data.scripts.engine.collision.RectCollisionType;

/**
 * 矩形實體，包含位置與邊界資訊。
 */
public class Rect extends Entity {
    private double x, y, width, height;
    private double top, right, bottom, left;
    private Boolean[] collisionType = new Boolean[4];

    public Rect(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.top = y;
        this.right = x + width;
        this.bottom = y + height;
        this.left = x;
        initCollisionType();
    }

    public void setX(double val) {
        x = val;
        right = x + width;
        left = x;
    }

    public double getX() {
        return x;
    }

    public void setY(double val) {
        y = val;
        bottom = y + height;
        top = y;
    }

    public double getY() {
        return y;
    }
    
    public void setWidth(double val) {
        width = val;
        right = x + width;
    }

    public double getWidth() {
        return width;
    }

    public void setHeight(double val) {
        height = val;
        bottom = y + val;
    }

    public double getHeight() {
        return height;
    }

    public void setTop(double val) {
        top = y = val;
    }

    public double getTop() {
        return top;
    }

    public void setRight(double val) {
        right = val;
        x = right - width;
    }

    public double getRight() {
        return right;
    }

    public void setBottom(double val) {
        bottom = val;
        y = bottom - height;
    }

    public double getBottom() {
        return bottom;
    }

    public void setLeft(double val) {
        left = x = val;
    }

    public double getLeft() {
        return left;
    }

    public void initCollisionType() {
        for (RectCollisionType tp : RectCollisionType.values()) {
            setCollisionType(tp, false);
        }
    }

    public void setCollisionType(RectCollisionType tp, Boolean bool) {
        collisionType[tp.ordinal()] = bool;
    }

    public Boolean getCollisionType(RectCollisionType tp) {
        return collisionType[tp.ordinal()];
    }
}
