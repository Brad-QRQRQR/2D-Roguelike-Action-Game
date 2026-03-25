package app.data.scripts.engine.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.scene.canvas.GraphicsContext;

public abstract class EntityGroup {
    protected ArrayList<Entity> group;

    public abstract void validateEntityType(Entity e);

    public EntityGroup() {
        group = new ArrayList<>();
    }

    public void add(Entity e) {
        validateEntityType(e);
        group.add(e);
    }

    public void remove(ArrayList<Entity> removeList) {
        ArrayList<Entity> newGroup = new ArrayList<>();
        for (int i = 0, j = 0; i < group.size(); i++) {
            if (j < removeList.size() && group.get(i) == removeList.get(j)) {
                j++;
                continue;
            }
            newGroup.add(group.get(i));
        }
        group = newGroup;
    }

    public void update() {
        for (Entity e : group) {
            e.update();
        }
    }

    public void update(UpdateStrategy upd) {
        for (Entity e : group) {
            upd.setSelf(e);
            e.update(upd);
        }
    }

    public void draw(GraphicsContext display, int[] scroll) {
        for (Entity e : group) {
            e.draw(display, scroll);
        }
    }

    public List<Entity> getGroup() {
        return Collections.unmodifiableList(group);
    }
}