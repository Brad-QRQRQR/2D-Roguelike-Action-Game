package app.data.scripts.game.particle;

import app.data.scripts.engine.entity.Circle;
import javafx.scene.canvas.GraphicsContext;

public class Particle extends Circle {
    public Particle(double x, double y, double radius) {
        super(x, y, radius);
    }

    @Override
    public void draw(GraphicsContext display, int[] scroll) {

    }
};