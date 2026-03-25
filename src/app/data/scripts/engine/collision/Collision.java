package app.data.scripts.engine.collision;

import app.data.scripts.engine.entity.Entity;
import app.data.scripts.engine.entity.EntityGroup;

/**
 * 碰撞抽象類別：定義碰撞檢查與處理的標準。
 * 這些方法實際上是由具體類別實作。
 */
public abstract class Collision {
    public static void checkType(Entity entity1, Entity entity2) {}
    public static Boolean isCollided(Entity entity1, Entity entity2) { return false; }
    public static void checkCollision(EntityGroup grp1, EntityGroup grp2, HandleCollision func) {}
    public static void checkCollision(Entity entity, EntityGroup grp2, HandleCollision func) {}
}