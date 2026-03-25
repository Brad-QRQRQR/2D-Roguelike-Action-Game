package app.data.scripts.engine.entity;

import java.util.ArrayList;

import javafx.scene.canvas.GraphicsContext;

public abstract class ChunkGroupManager {
    private static int chunkSize;

    public EntityGroup[][] content;

    public ChunkGroupManager(int mapWidth, int mapHeight) {
        //this.chunkSize = chunkSize;
        int width = mapWidth / chunkSize + 1;
        int height = mapHeight / chunkSize + 1;
        this.content = new EntityGroup[width][height];
        initChuck();
    }

    public abstract void initChuck();

    public EntityGroup getCurrentChuckGroup(double x, double y) {
        /*if ((int)x / chunkSize >= content.length || (int)y / chunkSize >= content[0].length) {
            throw new Error("WTF: " + (int)x / chunkSize + " " + content.length + " " + (int)y / chunkSize + " " + content[0].length + " " + x + " " + y);
        }*/
        int xid = Math.min(content.length - 1, Math.max(0, (int)x / chunkSize));
        int yid = Math.min(content[0].length - 1, Math.max(0, (int)y / chunkSize));
        return content[xid][yid];
    }

    public static void setChunkSize(int val) {
        chunkSize = val;
    }

    public int getChuckSize() {
        return chunkSize;
    }

    public void add(double x, double y, Entity e) {
        getCurrentChuckGroup(x, y).add(e);
    }

    public void remove(double x, double y, ArrayList<Entity> revmoeList) {
        getCurrentChuckGroup(x, y).remove(revmoeList);
    }

    public void updateOneChunck(double x, double y) {
        getCurrentChuckGroup(x, y).update();
    }

    public void updateOneChunck(double x, double y, UpdateStrategy upd) {
        getCurrentChuckGroup(x, y).update(upd);
    }

    public void drawOneChunck(double x, double y, GraphicsContext display, int[] scroll) {
        getCurrentChuckGroup(x, y).draw(display, scroll);
    }

    public void draw(double lx, double ly, double rx, double ry, GraphicsContext display, int[] scroll) {
        for (double x = Math.max(0, lx - 1); x <= rx+1; x += chunkSize) {
            for (double y = Math.max(0, ly - 1); y <= ry+1; y += chunkSize) {
                //System.out.println("draw: " + (int)x / chunkSize + " " + (int)y / chunkSize + " " + getCurrentChuckGroup(x, y).getGroup().size());
                drawOneChunck(x, y, display, scroll);
            }
        }
    }

    public void update(double lx, double ly, double rx, double ry) {
        for (double x = lx; x < rx; x += chunkSize) {
            for (double y = ly; y < ry; y += chunkSize) {
                updateOneChunck(x, y);
            }
        }
    }

    public void update(double lx, double ly, double rx, double ry, UpdateStrategy upd) {
        for (double x = lx; x < rx; x += chunkSize) {
            for (double y = ly; y < ry; y += chunkSize) {
                updateOneChunck(x, y, upd);
            }
        }
    }

    public void updateAll() {
        for (int x = 0; x < content.length; x++) {
            for (int y = 0; y < content[0].length; y++) {
                content[x][y].update();
            }
        }
    }

    public void updateAll(UpdateStrategy upd) {
        for (int x = 0; x < content.length; x++) {
            for (int y = 0; y < content[0].length; y++) {
                content[x][y].update(upd);
            }
        }
    }
}
