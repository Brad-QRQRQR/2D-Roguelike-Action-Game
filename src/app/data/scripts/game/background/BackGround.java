package app.data.scripts.game.background;

import app.data.scripts.engine.entity.EntityAnimation;
import app.data.scripts.engine.entity.Rect;
import app.data.scripts.engine.entity.UpdateStrategy;
import javafx.scene.canvas.GraphicsContext;

public class BackGround extends Rect {
    private EntityAnimation anim;
    private final String type;

    public BackGround(double x, double y, String tp) {
        super(x, y, 0, 0);
        type = tp;
        loadAnim(tp);
    }

    public void loadAnim(String tp) {
        switch (tp) {
            case "Fire":
                anim = new EntityAnimation(
                    16,
                    new String[]{
                        "app/data/images/PackCharacterPixelArt09/Environments/Fire/Fire_01.png",
                        "app/data/images/PackCharacterPixelArt09/Environments/Fire/Fire_02.png",
                        "app/data/images/PackCharacterPixelArt09/Environments/Fire/Fire_03.png",
                        "app/data/images/PackCharacterPixelArt09/Environments/Fire/Fire_04.png",
                        "app/data/images/PackCharacterPixelArt09/Environments/Fire/Fire_05.png",
                        "app/data/images/PackCharacterPixelArt09/Environments/Fire/Fire_06.png",
                        "app/data/images/PackCharacterPixelArt09/Environments/Fire/Fire_07.png",
                        "app/data/images/PackCharacterPixelArt09/Environments/Fire/Fire_08.png",
                        "app/data/images/PackCharacterPixelArt09/Environments/Fire/Fire_09.png",
                        "app/data/images/PackCharacterPixelArt09/Environments/Fire/Fire_10.png",
                        "app/data/images/PackCharacterPixelArt09/Environments/Fire/Fire_11.png",
                        "app/data/images/PackCharacterPixelArt09/Environments/Fire/Fire_12.png",
                        "app/data/images/PackCharacterPixelArt09/Environments/Fire/Fire_13.png",
                        "app/data/images/PackCharacterPixelArt09/Environments/Fire/Fire_14.png",
                        "app/data/images/PackCharacterPixelArt09/Environments/Fire/Fire_15.png",
                        "app/data/images/PackCharacterPixelArt09/Environments/Fire/Fire_16.png",
                    },
                    10,
                    true
                );
                break;
            case "Cloud01":
                anim = new EntityAnimation("app/data/images/PackCharacterPixelArt09/Environments/Clouds/Cloud01.png");
                break;
            case "Rock01":
                anim = new EntityAnimation("app/data/images/PackCharacterPixelArt09/Environments/Rocks/Rock01.png");
                break;
            case "Rock02":
                anim = new EntityAnimation("app/data/images/PackCharacterPixelArt09/Environments/Rocks/Rock02.png");
                break;
            case "Tree01":
                anim = new EntityAnimation("app/data/images/PackCharacterPixelArt09/Environments/Trees/Tree01.png");
                break;
            case "Tree02":
                anim = new EntityAnimation("app/data/images/PackCharacterPixelArt09/Environments/Trees/Tree02.png");
                break;
            case "Tree03":
                anim = new EntityAnimation("app/data/images/PackCharacterPixelArt09/Environments/Trees/Tree03.png");
                break;
            case "TreeBack01":
                anim = new EntityAnimation("app/data/images/PackCharacterPixelArt09/Environments/Trees/TreeBack01.png");
                break;
            case "TreeBack02":
                anim = new EntityAnimation("app/data/images/PackCharacterPixelArt09/Environments/Trees/TreeBack02.png");
                break;
            case "TreeBack03":
                anim = new EntityAnimation("app/data/images/PackCharacterPixelArt09/Environments/Trees/TreeBack03.png");
                break;
            case "GiantSword_01":
                anim = new EntityAnimation(
                    40,
                    getSwordPath(),
                    10,
                    true
                );
                break;
            default:
                break;
        }
    }

    private String[] getSwordPath() {
        String[] res = new String[40];
        for (int i = 0; i < 40; i++) {
            String digit = (i + 1 < 10 ? "0" : "") + Integer.toString(i + 1);
            res[i] = new String("app/data/images/PackCharacterPixelArt09/Environments/GiantSword/GiantSword_" + digit + ".png");
        }
        return res;
    }

    @Override
    public void update(UpdateStrategy upd) {
        if (type == "Fire") System.out.println(anim.getPointer());
        anim.update(((BackGroundUpdateStrategy)upd).getDt());
    }

    @Override
    public void draw(GraphicsContext display, int scroll[]) {
        if (type != "GiantSword_01") {
            display.setGlobalAlpha(0.7);
        }
        anim.draw(display, getX() - scroll[0], getY() - scroll[1], false);
        display.setGlobalAlpha(1.0);
    }
}
