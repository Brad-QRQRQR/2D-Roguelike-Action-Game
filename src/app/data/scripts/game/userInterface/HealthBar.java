package app.data.scripts.game.userInterface;

import app.data.scripts.engine.entity.EntityAnimation;
import app.data.scripts.engine.entity.Rect;
import app.data.scripts.engine.entity.UpdateStrategy;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class HealthBar extends Rect {
    private EntityAnimation barAnim = new EntityAnimation(
        8,
        new String[] {
            "app/data/images/PixelArtPackSideScroller3/UI/HealthBar/HP/HP_01.png",
            "app/data/images/PixelArtPackSideScroller3/UI/HealthBar/HP/HP_02.png",
            "app/data/images/PixelArtPackSideScroller3/UI/HealthBar/HP/HP_03.png",
            "app/data/images/PixelArtPackSideScroller3/UI/HealthBar/HP/HP_04.png",
            "app/data/images/PixelArtPackSideScroller3/UI/HealthBar/HP/HP_05.png",
            "app/data/images/PixelArtPackSideScroller3/UI/HealthBar/HP/HP_06.png",
            "app/data/images/PixelArtPackSideScroller3/UI/HealthBar/HP/HP_07.png",
            "app/data/images/PixelArtPackSideScroller3/UI/HealthBar/HP/HP_08.png"
        },
        5
    );

    private Image contour = new Image(getClass().getResource("/app/data/images/PixelArtPackSideScroller3/UI/HealthBar/Contour_HP_01.png").toExternalForm());

    public HealthBar(double x, double y, double width, double height) {
        super(x, y, width, height);
    }

    @Override
    public void update(UpdateStrategy upd) {
        barAnim.update(((HealthBarUpdateStrategy)upd).getDt());
        upd.perform();
    }

    @Override
    public void draw(GraphicsContext display, int scroll[]) {
        barAnim.draw(display, getX(), getY(), getWidth(), false);
        display.drawImage(contour, getX(), getY());
    }
}
