package app.data.scripts.game.enemies;

import java.util.ArrayList;

import app.data.scripts.engine.entity.Entity;
import app.data.scripts.engine.entity.Rect;
import app.data.scripts.engine.entity.UpdateStrategy;
import app.data.scripts.engine.entity.EntityGroup;

public class EnemyGroup extends EntityGroup {
    public void validateEntityType(Entity e) {
        if (e instanceof Rect == false) throw new IllegalArgumentException("Enemy entity must be of type Rect.");
    }

    @Override
    public void update(UpdateStrategy upd) {
        for (Entity e : group) {
            EnemyUpdateStrategy nupd = (EnemyUpdateStrategy)upd;
            if (e instanceof Swordsman) {
                SwordsmanUpdateStrategy supd = new SwordsmanUpdateStrategy(nupd.getPlayer(), nupd.getTileGroupManager(), nupd.getDt());
                supd.setSelf(e);
                e.update(supd);
            }else if (e instanceof Gunsman) {
                GunsmanUpdateStrategy gupd = new GunsmanUpdateStrategy(nupd.getPlayer(), nupd.getTileGroupManager(), nupd.getDt());
                gupd.setSelf(e);
                e.update(gupd);
            }
        }
    }

    public void removeDead() {
        ArrayList<Entity> removeList = new ArrayList<>();
        ArrayList<Entity> newList = new ArrayList<>();

        for (Entity e : group) {
            if (((Enemy)e).getHealth() <= 0) {
                removeList.add(e);
            }
        }
        
        for (int i = 0, j = 0; i < group.size(); i++) {
            if (j < removeList.size() && group.get(i) == removeList.get(j)) {
                j++;
                continue;
            }
            newList.add(group.get(i));
        }
        group = newList;
        // 寫生命值歸零後，移除 entity
        // 歸零的放 id 在 revmoe list
        // new 一個新的 list
        // old list 東西加入 new list，但是略過 remove list
        // old list = new list
    }
}
