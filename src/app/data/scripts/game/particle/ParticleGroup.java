package app.data.scripts.game.particle;

import app.data.scripts.engine.entity.Entity;
import app.data.scripts.engine.entity.EntityGroup;

public class ParticleGroup extends EntityGroup {
    public void validateEntityType(Entity e) {
        if (e instanceof Particle == false) {
            throw new IllegalArgumentException("Particle entity must be of type Particle.");
        }
    }
}
