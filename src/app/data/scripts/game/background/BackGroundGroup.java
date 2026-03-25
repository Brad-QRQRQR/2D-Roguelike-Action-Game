package app.data.scripts.game.background;

import app.data.scripts.engine.entity.Entity;
import app.data.scripts.engine.entity.EntityGroup;
import app.data.scripts.engine.entity.Rect;

public class BackGroundGroup extends EntityGroup {
    @Override
    public void validateEntityType(Entity e) {
        if (e instanceof Rect == false) {
            throw new IllegalArgumentException("The background entity must be of type Rect");
        }
    }
}
