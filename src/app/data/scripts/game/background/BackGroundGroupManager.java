package app.data.scripts.game.background;

import app.data.scripts.GameInfo;
import app.data.scripts.engine.entity.RectChunkGroupManager;

public class BackGroundGroupManager extends RectChunkGroupManager {
    public BackGroundGroupManager(int mapWidth, int mapHeight) {
        super(mapWidth, mapHeight);
    }

    @Override
    public void initChuck() {
        for (int x = 0; x < content.length; x++) {
            for (int y = 0; y < content[0].length; y++) {
                content[x][y] = new BackGroundGroup();
            }
        }
    }

    public void loadFromInfo(int id) {
        for (var obj : GameInfo.background[id]) {

            getCurrentChuckGroup(obj.x, obj.y).add(new BackGround(obj.x, obj.y, obj.type));
        }
    }
}
