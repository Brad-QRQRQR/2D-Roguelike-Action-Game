package app.data.scripts.engine.entity;

import javafx.scene.canvas.GraphicsContext;

/**
 * 抽象的實體類別，代表遊戲畫面中的一個物件。
 * 所有具體的實體應繼承此類別並實作其行為。
 */
public abstract class Entity {
    public void checkUpdateStrategy(UpdateStrategy upd) {
        if (upd == null) {
            throw new IllegalArgumentException("The update strategy should not be null.");
        }
    }

    public void update() {}

    public void update(UpdateStrategy upd) {
        checkUpdateStrategy(upd);
        upd.perform();
    }

    public void draw(GraphicsContext display, int[] scroll) {}
}