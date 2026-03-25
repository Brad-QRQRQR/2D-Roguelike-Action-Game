package app.data.scripts;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class GameInfo {
    public static JSONArray mapData;
    public static ArrayList<BackGroundObj>[] background = new ArrayList[4];
    public static int sx;
    public static int sy;
    public static int ex;
    public static int ey;

    public static int round = 0;
    public static int totalTime = 0;
    public static int cardsUsed = 0;
    public static int cardsRemaining = 0;

    public static int playerHealth = 100;

    public static void saveState(int playerHealth) {
        GameInfo.playerHealth = playerHealth;
    }

    public static void init() {
        totalTime = 0;
        cardsUsed = 0;
        cardsRemaining = 0;
        round = 0;
        playerHealth = 100;
    }

    public static void load() {
        try(InputStream input = GameInfo.class.getResourceAsStream("/app/data/info/temp.json")) {

            String content = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            JSONObject json = new JSONObject(content);

            JSONArray layers = json.getJSONArray("layers");
            mapData = layers.getJSONObject(0).getJSONArray("data");

            JSONArray temp;
            for (int i = 1; i < 5; i++) {
                temp = layers.getJSONObject(i).getJSONArray("objects");
                background[i - 1] = new ArrayList<>();
                for (int j = 0; j < temp.length(); j++) {
                    background[i - 1].add(new BackGroundObj(
                        temp.getJSONObject(j).getString("type"),
                        temp.getJSONObject(j).getInt("x"),
                        temp.getJSONObject(j).getInt("y") - temp.getJSONObject(j).getInt("height")
                    ));
                }
            }
            temp = layers.getJSONObject(5).getJSONArray("objects");
            sx = temp.getJSONObject(0).getInt("x");
            sy = temp.getJSONObject(0).getInt("y");
            ex = temp.getJSONObject(1).getInt("x");
            ey = temp.getJSONObject(1).getInt("y");
        }catch (IOException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class BackGroundObj {
        public final String type;
        public final int x, y;

        public BackGroundObj(String type, int x, int y) {
            this.type = type;
            this.x = x;
            this.y = y;
        }
    }
}
