package app.data.scripts.game.map;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import app.data.scripts.Config;
import app.data.scripts.engine.entity.RectChunkGroupManager;

public class TileGroupManager extends RectChunkGroupManager {
    private int[][] mapMatrix;
    private final int voidTpId;
    private final int mapWidth;
    private final int mapHeight;

    public TileGroupManager(int mapWidth, int mapHeight, int voidTpId) {
        super(mapWidth, mapHeight);
        this.voidTpId = voidTpId;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
    }

    @Override
    public void initChuck() {
        for (int x = 0; x < content.length; x++) {
            for (int y = 0; y < content[0].length; y++) {
                content[x][y] = new TileGroup();
            }
        }
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public void loadFromCsv(String filePath) {
        ArrayList<ArrayList<Integer>> mapData = new ArrayList<ArrayList<Integer>>();;
        try(
            InputStream input = getClass().getClassLoader().getResourceAsStream(filePath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        ) {
            
            String line;
            int x = 0, y = 0;
            while ((line = reader.readLine()) != null) {
                String[] row = line.split(",");
                mapData.add(new ArrayList<Integer>());
                for (String chr : row) {
                    int tp = Integer.parseInt(chr);
                    mapData.getLast().add(tp);
                    if (tp == voidTpId) continue;
                    add(
                        x * Config.TILE_SIZE,
                        y * Config.TILE_SIZE,
                        new Tile(x * Config.TILE_SIZE, y * Config.TILE_SIZE, tp)
                    );
                    x++;
                }
                y++;
                x = 0;
            }
            mapMatrix = new int[mapData.size()][mapData.getLast().size()];
            for (int i = 0; i < mapMatrix.length; i++) {
                for (int j = 0; j < mapMatrix[i].length; j++) {
                    mapMatrix[i][j] = mapData.get(i).get(j);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
