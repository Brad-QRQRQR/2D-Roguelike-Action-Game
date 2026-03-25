package app.data.scripts.game.map;

import app.data.scripts.engine.entity.Entity;
import app.data.scripts.engine.entity.EntityGroup;


public class TileGroup extends EntityGroup {
    public void validateEntityType(Entity e) {
        if (e instanceof Tile == false) throw new IllegalArgumentException("Tile entity must be of type Tile.");
    }
}