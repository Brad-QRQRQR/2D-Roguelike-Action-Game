package app.data.scripts.engine.entity;

import app.data.scripts.engine.tools.TransparentImage;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * 此類別負責控制圖像序列動畫（如角色走路、攻擊等動畫）。
 */
public class EntityAnimation {
    public int size; // 圖片數量

    private int pointer = 0; // 當前播放的圖片索引
    private double counter = 0; // 幀延遲計數器
    private int[] frames; // 每張圖顯示幾幀（控制動畫速度）
    private Boolean infinity; // 是否為無限循環動畫
    private Image[] imgs; // 實際的圖片對象陣列
    private int totalFrames = 0;

    public EntityAnimation(int size, String[] paths, int[] frame, Boolean infinity) {
        this.size = size;
        loadImage(paths);
        this.frames = new int[size];
        setFrames(frame);
        this.infinity = infinity;
    }

    public EntityAnimation(int size, String[] paths, int frame, Boolean infinity) {
        this.size = size;
        loadImage(paths);
        this.frames = new int[size];
        setFramesToSame(frame);
        this.infinity = infinity;
    }

    public EntityAnimation(int size, String[] paths, int[] frame) {
        this.size = size;
        loadImage(paths);
        this.frames = new int[size];
        setFrames(frame);
        this.infinity = true;
    }

    public EntityAnimation(int size, String[] paths, int frame) {
        this.size = size;
        loadImage(paths);
        this.frames = new int[size];
        setFramesToSame(frame);
        this.infinity = true;
    }

    public EntityAnimation(String path, int frame, Boolean infinity) {
        this.size = 1;
        String[] paths = {path};
        loadImage(paths);
        this.frames = new int[size];
        setFramesToSame(frame);
        this.infinity = infinity;
    }

    public EntityAnimation(String path) {
        this.size = 1;
        String[] paths = {path};
        loadImage(paths);
        this.frames = new int[size];
        setFramesToSame(1);
        this.infinity = true;
    }

    /**
     * 確保初始化時指定的 size 與陣列長度一致。
     */
    private void checkSizeMatched(int len) {
        if (size != len) throw new IllegalArgumentException("Animation size does not match image count.");
    }

    /**
     * 載入圖片陣列。
     */
    public void loadImage(String[] paths) {
        checkSizeMatched(size);
        imgs = new Image[paths.length];
        for (int i = 0; i < size; i++) {
            imgs[i] = new Image(getClass().getResource("/" + paths[i]).toExternalForm());
        }
    }

    /**
     * 將指定的顏色透明化處理。
     */
    public void trasparent(Color colorkey) {
        for (int i = 0; i < size; i++) {
            imgs[i] = TransparentImage.convert(imgs[i], colorkey);
        }
    }

    /**
     * 所有幀統一使用同一個播放幀數。
     */
    public void setFramesToSame(int frame) {
        for (int i = 0; i < size; i++) {
            frames[i] = frame;
            totalFrames += frame;
        }
    }

    /**
     * 設定每張圖的播放幀數（frame durations）。
     */
    public void setFrames(int[] frame) {
        checkSizeMatched(size);
        for (int i = 0; i < size; i++) {
            frames[i] = frame[i];
            totalFrames += frame[i];
        }
    }

    /**
     * 回傳幀數設定的複本。
     */
    public int[] getFrames() {
        return frames.clone();
    }

    public int getTotalFrames() {
        return totalFrames;
    }

    public int getPointer() {
        return pointer;
    }

    public double getCounter() {
        return counter;
    }

    public void setImageAt(int pos, Image newImg) {
        if (pos >= size) {
            throw new IllegalArgumentException("Index " + pos + " out of bound (" + size + ").");
        }
        imgs[pos] = newImg;
    }

    public Image getImageAt(int pos) {
        if (pos >= size) {
            throw new IllegalArgumentException("Index " + pos + " out of bound (" + size + ").");
        }
        return imgs[pos];
    }

    public Image getLastImage() {
        return getImageAt(size - 1);
    }

    /**
     * 判斷動畫是否播放完畢。
     */
    public Boolean isOver() {
        return pointer == size;
    }

    /**
     * 重置動畫狀態（從頭播放）。
     */
    public void initAnimation() {
        pointer = 0;
        counter = 0;
    }

    /**
     * 更新動畫（每次呼叫都前進幀數，依據對應的延遲時間）。
     */
    public void update(double dt) {
        if (isOver()) {
            return;
        }
        counter += dt;
        if (counter >= frames[pointer]) {
            counter = 0;
            pointer++;
        }
        if (isOver() && infinity) {
            initAnimation();
        }
    }

    /**
     * 繪製當前動畫幀到畫面上指定位置。
     */
    public void draw(GraphicsContext display, double x, double y, Boolean flip) {
        int curPointer = pointer;
        if (isOver()) {
            curPointer = pointer - 1;
        }
        if (flip) {
            display.drawImage(
                imgs[curPointer],
                0,
                0,
                imgs[curPointer].getWidth(),
                imgs[curPointer].getHeight(),
                x + imgs[curPointer].getWidth(),
                y,
                -1.0 * imgs[curPointer].getWidth(),
                imgs[curPointer].getHeight()
            );
        }else {
            display.drawImage(imgs[curPointer], x, y);
        }
    }

    public void draw(GraphicsContext display, double x, double y, double width, Boolean flip) {
        int curPointer = pointer;
        if (isOver()) {
            curPointer = pointer - 1;
        }
        if (flip) {
            display.drawImage(
                imgs[curPointer],
                0,
                0,
                imgs[curPointer].getWidth(),
                imgs[curPointer].getHeight(),
                x + imgs[curPointer].getWidth(),
                y,
                -1.0 * imgs[curPointer].getWidth(),
                imgs[curPointer].getHeight()
            );
        }else {
            display.drawImage(
                imgs[curPointer],
                0,
                0,
                width,
                imgs[curPointer].getHeight(),
                x,
                y,
                width,
                imgs[curPointer].getHeight()
            );
        }
    }
}
