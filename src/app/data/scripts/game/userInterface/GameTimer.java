package app.data.scripts.game.userInterface;

import app.data.scripts.Config;
import app.data.scripts.engine.entity.Rect;
import app.data.scripts.engine.entity.UpdateStrategy;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class GameTimer extends Rect {
    public static final int MAX_TIME = Config.FPS * Config.ROUND_TIME_SEC;
    public static final String COLOR = "#880015";

    private double timer = 0;
    
    public GameTimer(double x, double y, double width, double height) {
        super(x, y, width, height);
    }

    public void decTimer(double dt) {
        setTimer(getTimer() - dt);
    }

    public void setTimer(double val) {
        timer = Math.max(0, val);
    }

    public Boolean isOver() {
        return timer <= 0;
    }

    public double getTimer() {
        return timer;
    }

    @Override
    public void update(UpdateStrategy upd) {
        decTimer(((GameTimerUpdateStrategy)upd).getDt());
        upd.perform();
    }

    @Override
    public void draw(GraphicsContext display, int[] scroll) {
        //System.out.println(timer);
        //stroke color
        display.setStroke(Color.web(COLOR));
        display.strokeText(Integer.toString((int)timer / Config.FPS) , getX(), getHeight(), getWidth());
    }
}
