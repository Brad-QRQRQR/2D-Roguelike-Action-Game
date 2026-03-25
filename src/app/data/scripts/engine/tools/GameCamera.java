package app.data.scripts.engine.tools;

public class GameCamera {
    private int speedFactor = 10;

    private double cameraX = 0;
    private double cameraY = 0;

    private final double cameraWidth;
    private final double cameraHeight;
    private final double centerX;
    private final double centerY;
    private final double leftTopCornerX;
    private final double leftTopCornerY;
    private final double rightBottomCornerX;
    private final double rightBottomCornerY;

    private double scrollX = 0;
    private double scrollY = 0;

    public GameCamera(
        double leftTopCornerX,
        double leftTopCornerY,
        double rightBottomCornerX,
        double rightBottomCornerY,
        double cameraWidth,
        double cameraHeight,
        double scale
    ) {
        this.leftTopCornerX = leftTopCornerX;
        this.leftTopCornerY = leftTopCornerY;
        this.rightBottomCornerX = rightBottomCornerX;
        this.rightBottomCornerY = rightBottomCornerY;
        this.cameraWidth = cameraWidth;
        this.cameraHeight = cameraHeight;
        this.centerX = cameraWidth / scale / 2;
        this.centerY = cameraHeight / scale / 2;
        this.cameraX = 0;
        this.cameraY = 0;
    }

    public void update(double playerX, double playerY) {
        setSrollX(
            Math.min(
                Math.max(playerX, leftTopCornerX), rightBottomCornerX
            )
            - centerX
        );
        setSrollY(
            Math.min(
                Math.max(playerY, leftTopCornerY), rightBottomCornerY
            )
            - centerY
        );
    }

    public void setSrollX(double diff) {
        scrollX += (diff - scrollX) / speedFactor;
        setCameraX();
    }

    public int getSrollX() {
        return (int)scrollX;
    }

    public void setSrollY(double diff) {
        scrollY += (diff - scrollY) / speedFactor;
        setCameraY();
    }

    public int getSrollY() {
        return (int)scrollY;
    }

    public int[] getSroll() {
        int[] scroll = {getSrollX(), getSrollY()};
        return scroll;
    }

    public void setSpeedFactor(int val) {
        if (val == 0) {
            throw new IllegalArgumentException("Speed factor must be positive integer.");
        }
        speedFactor = val;
    }

    public int getSpeedFactor() {
        return speedFactor;
    }

    public int getCameraX() {
        return (int)cameraX;
    }

    public void setCameraX() {
        cameraX = scrollX;
    }

    public int getCameraY() {
        return (int)cameraY;
    }

    public void setCameraY() {
        cameraY = scrollY;
    }

    public double getCameraWidth() {
        return cameraWidth;
    }

    public double getCameraHeight() {
        return cameraHeight;
    }
}
