package app.data.scripts.engine.entity;

import java.util.ArrayList;

public abstract class RectChunkGroupManager extends ChunkGroupManager {
    public RectChunkGroupManager(int mapWidth, int mapHeight) {
        super(mapWidth, mapHeight);
        initChuck();
    }

    @Override
    public void updateOneChunck(double x, double y) {
        EntityGroup curGroup = getCurrentChuckGroup(x, y);
        curGroup.update();
        ArrayList<Entity> moveList = new ArrayList<>();
        int curXid = (int)x / getChuckSize();
        int curYid = (int)y / getChuckSize();
        for (var entity : curGroup.getGroup()) {
            Rect rect = (Rect)entity;
            Boolean outOfBounds = rect.getX() < curXid * getChuckSize() || rect.getX() > (curXid + 1) * getChuckSize() ||
                rect.getY() < curYid * getChuckSize() || rect.getY() > (curYid + 1) * getChuckSize();
            if (outOfBounds) {
                moveList.add(entity);
            }
        }
        curGroup.remove(moveList);
        for (var entity : moveList) {
            Rect rect = (Rect)entity;
            getCurrentChuckGroup(rect.getX(), rect.getY()).add(entity);
        }
    }

    @Override
    public void updateOneChunck(double x, double y, UpdateStrategy upd) {
        //System.out.println("AA");
        EntityGroup curGroup = getCurrentChuckGroup(x, y);
        curGroup.update(upd);
        ArrayList<Entity> moveList = new ArrayList<>();
        int curXid = (int)x / getChuckSize();
        int curYid = (int)y / getChuckSize();
        for (var entity : curGroup.getGroup()) {
            Rect rect = (Rect)entity;
            Boolean outOfBounds = rect.getX() < curXid * getChuckSize() || rect.getX() > (curXid + 1) * getChuckSize() ||
                rect.getY() < curYid * getChuckSize() || rect.getY() > (curYid + 1) * getChuckSize();
            if (outOfBounds) {
                moveList.add(entity);
            }
        }
        //System.out.println("remove list size: " + moveList.size());
        curGroup.remove(moveList);
        for (var entity : moveList) {
            Rect rect = (Rect)entity;
            getCurrentChuckGroup(rect.getX(), rect.getY()).add(entity);
        }
    }
}
