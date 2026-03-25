package app.data.scripts.game.map;

import app.data.scripts.Config;
import app.data.scripts.engine.entity.Rect;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Tile extends Rect {
    private static final String[] IMG_PATH = {
        "/app/data/images/map/tile_0.png",
        "/app/data/images/map/tile_1.png"
    };
    private Image img;

    public Tile(double x, double y, int tp) {
        super(x, y, Config.TILE_SIZE, Config.TILE_SIZE);
        img = new Image(getClass().getResource(IMG_PATH[tp]).toExternalForm());
    }

    @Override
    public void draw(GraphicsContext display, int[] scroll) {
        display.drawImage(img, getX() - scroll[0], getY() - scroll[1]);
    }
}
