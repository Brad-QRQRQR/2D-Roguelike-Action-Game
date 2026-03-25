
package app.data.scripts.game.enemies;

import app.data.scripts.Config;
import app.data.scripts.GameInfo;
import app.data.scripts.engine.entity.RectChunkGroupManager;
import app.data.scripts.engine.tools.ActionTimer;
import app.data.scripts.engine.tools.RangeRandom;
import app.data.scripts.game.player.Player;

public class EnemyGroupManager extends RectChunkGroupManager {
    private final int SWORDS_MAN_WIDTH = 8;
    private final int SWORDS_MAN_HEIGHT = 27;
    private final int GUNS_MAN_WIDTH = 6;
    private final int GUNS_MAN_HEIGHT = 46;
    private final double SWORDSMAN_GENERATE_TIME = Config.FPS * 8;
    private final double GUNSMAN_GENERATE_TIME = Config.FPS * 15;
    private final double DEC_TIME_RATE = 30;
    private final int mapWidth;
    private final int mapHeight;
    
    private ActionTimer swordsTimer = new ActionTimer();
    private ActionTimer gunsTimer = new ActionTimer();

    public EnemyGroupManager(int mapWidth, int mapHeight) {
        super(mapWidth, mapHeight);
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
    }

    @Override
    public void initChuck() {
        for (int x = 0; x < content.length; x++) {
            for (int y = 0; y < content[0].length; y++) {
                content[x][y] = new EnemyGroup();
            }
        }
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public void removeDead(double lx, double ly, double rx, double ry) {
        for (double x = lx; x < rx; x += getChuckSize()) {
            for (double y = ly; y < ry; y += getChuckSize()) {
                ((EnemyGroup)getCurrentChuckGroup(x, y)).removeDead();
            }
        }
    }

    public void generateEnemy(Player player, double dt) {
        swordsTimer.decTimer(dt);
        if (swordsTimer.isOver()) {
            double gx = Math.min(mapWidth, player.getX() + RangeRandom.random(getChuckSize(), getChuckSize() * 2));
            double gy = Math.max(0, player.getY() - RangeRandom.random(getChuckSize(), getChuckSize() * 2));
            //gy = player.getY();
            //gx = player.getX();
            add(gx, gy, new Swordsman(gx, gy, SWORDS_MAN_WIDTH, SWORDS_MAN_HEIGHT));
            swordsTimer.setTimer(SWORDSMAN_GENERATE_TIME - DEC_TIME_RATE * GameInfo.round);
        }
        gunsTimer.decTimer(dt);
        if (gunsTimer.isOver()) {
            double gx = Math.min(mapWidth, player.getX() + RangeRandom.random(getChuckSize(), getChuckSize() * 2));
            double gy = Math.max(0, player.getY() - RangeRandom.random(getChuckSize(), getChuckSize() * 2));
            //gy = player.getY();
            //gx = player.getX();
            add(gx, gy, new Gunsman(gx, gy, GUNS_MAN_WIDTH, GUNS_MAN_HEIGHT));
            gunsTimer.setTimer(GUNSMAN_GENERATE_TIME - DEC_TIME_RATE * GameInfo.round);
        }
    }
}
