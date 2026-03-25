package app.data.scripts.game.userInterface;

import app.data.scripts.engine.entity.Rect;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class SkillDemo extends Rect {
    public static final String COLOR = "#FFFFFF";
    private Image[] skillImg = {
        new Image(getClass().getResource("/app/data/images/card/attack.png").toExternalForm()),
        new Image(getClass().getResource("/app/data/images/card/block.png").toExternalForm()),
        new Image(getClass().getResource("/app/data/images/card/heavy.png").toExternalForm()),
        new Image(getClass().getResource("/app/data/images/card/counter.png").toExternalForm()),
        new Image(getClass().getResource("/app/data/images/card/heal.png").toExternalForm()),
        new Image(getClass().getResource("/app/data/images/card/longblock.png").toExternalForm()),
        new Image(getClass().getResource("/app/data/images/card/aoe.png").toExternalForm())
    };

    private int[] numbers = {0, 0, 0, 0, 0, 0, 0};
    private Font font = new Font(8);

    public SkillDemo(double x, double y, double width, double height) {
        super(x, y, width, height);
    }

    public void setNumberAt(int id, int val) {
        numbers[id] = val;
    }

    @Override
    public void draw(GraphicsContext display, int scroll[]) {
        int shift = 0;
        for (int i = 0; i < 7; i++) {
            display.setFont(font);
            display.setFill(Color.web(COLOR));
            display.strokeText(Integer.toString(numbers[i]) , getX() + shift, getY(), getWidth());
            display.fillText(Integer.toString(numbers[i]), getX() + shift, getY());
            display.drawImage(skillImg[i], getX() + shift, getY());
            shift += getWidth();
        }
    }
}
