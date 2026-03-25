package app.data.scripts.engine.collision;

import app.data.scripts.engine.entity.Entity;
import app.data.scripts.engine.entity.EntityGroup;
import app.data.scripts.engine.entity.Rect;

public class RectToRect extends Collision {
    public static void checkType(Entity entity1, Entity entity2) {
        if (entity1 instanceof Rect == false || entity2 instanceof Rect == false) {
            throw new IllegalArgumentException("Entities must be Rect type");
        }
    }
    
    public static Boolean isCollided(Entity entity1, Entity entity2) {
        checkType(entity1, entity1);
        Rect rect1 = (Rect)entity1;
        Rect rect2 = (Rect)entity2;        
        return (rect1.getX() + rect1.getWidth() > rect2.getX() && rect2.getX() + rect2.getWidth() > rect1.getX())
            && (rect1.getY() + rect1.getHeight() > rect2.getY() && rect2.getY() + rect2.getHeight() > rect1.getY());
    }
    
    public static void checkCollision(EntityGroup grp1, EntityGroup grp2, HandleCollision func) {
        if (grp1.getGroup().isEmpty() || grp2.getGroup().isEmpty()) return;
        
        for (Entity e1 : grp1.getGroup()) {
            for (Entity e2 : grp2.getGroup()) {
                func.handle(e1, e2);
            }
        }
    }

    public static void checkCollision(Entity entity, EntityGroup grp2, HandleCollision func) {
        if (grp2.getGroup().isEmpty()) return;

        for (Entity other : grp2.getGroup()) {
            if (isCollided(entity, other)) {
                func.handle(entity, other);
            }
        }
    }
}
