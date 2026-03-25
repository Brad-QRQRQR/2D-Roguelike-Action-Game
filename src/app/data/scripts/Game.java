package app.data.scripts;

import javafx.animation.AnimationTimer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import app.data.scripts.engine.entity.ChunkGroupManager;
import app.data.scripts.engine.tools.GameCamera;
import app.data.scripts.game.background.BackGroundGroupManager;
import app.data.scripts.game.background.BackGroundUpdateStrategy;
import app.data.scripts.game.enemies.EnemyGroupManager;
import app.data.scripts.game.enemies.EnemyUpdateStrategy;
import app.data.scripts.game.map.TileGroupManager;
import app.data.scripts.game.player.Player;
import app.data.scripts.game.player.PlayerInput;
import app.data.scripts.game.player.PlayerUpdateStrategy;
import app.data.scripts.game.userInterface.GameTimer;
import app.data.scripts.game.userInterface.GameTimerUpdateStrategy;
import app.data.scripts.game.userInterface.HealthBar;
import app.data.scripts.game.userInterface.HealthBarUpdateStrategy;
import app.data.scripts.game.userInterface.SkillDemo;
import app.data.scripts.game.userInterface.SkillDemoUpdateStrategy;

public class Game {

    private double deltaTime = 0;
    private long lastTick = 0;

    private GraphicsContext display;
    private Player player;
    private TileGroupManager gameMap;
    private GameCamera gameCamera;
    private EnemyGroupManager enemies;
    private HealthBar playerHealthBar;
    private GameTimer gameTimer;
    private SkillDemo skillDemo;
    private BackGroundGroupManager[] background = new BackGroundGroupManager[4];

    private Stage stage;
    private AnimationTimer clock;

    private Font defaultFont = Font.font(Config.FONT_SIZE);

    @FXML private Canvas canva;
    
    @FXML
    public void initialize() {
        //playMusic();
        display = canva.getGraphicsContext2D();
        display.setImageSmoothing(false);
        display.setFont(defaultFont);

        ChunkGroupManager.setChunkSize(Config.CHUNCK_SIZE);
        gameMap = new TileGroupManager(
            Config.MAP_WIDTH,
            Config.MAP_HEIGHT,
            0
        );
        gameCamera = new GameCamera(
            Config.WINDOW_WIDTH / Config.SCALE_SIZE / 2,
            Config.WINDOW_HEIGHT / Config.SCALE_SIZE / 2,
            Config.MAP_WIDTH - Config.WINDOW_WIDTH / Config.SCALE_SIZE / 2,
            Config.MAP_HEIGHT - Config.WINDOW_WIDTH / Config.SCALE_SIZE / 2,
            Config.WINDOW_WIDTH,
            Config.WINDOW_HEIGHT,
            Config.SCALE_SIZE
        );
        gameMap.loadFromCsv("app/data/info/gameMap.csv");

        for (int i = 0; i < 4; i++) {
            background[i] = new BackGroundGroupManager(Config.MAP_WIDTH, Config.MAP_HEIGHT);
            background[i].loadFromInfo(i);
        }

        player = new Player(GameInfo.sx, GameInfo.sy, 6, 29);
        player.setHealth(GameInfo.playerHealth);

        enemies = new EnemyGroupManager(Config.MAP_WIDTH, Config.MAP_HEIGHT);
        
        playerHealthBar = new HealthBar(Config.HEALTH_BAR_X, Config.HEALTH_BAR_Y, 20, Config.HEALTH_BAR_HEIGHT);

        gameTimer = new GameTimer(Config.TIMER_X - Config.FONT_SIZE, Config.TIMER_Y, Config.FONT_SIZE * 2, Config.FONT_SIZE);
        gameTimer.setTimer(GameTimer.MAX_TIME);

        skillDemo = new SkillDemo(Config.SKILL_DEMO_X, Config.SKILL_DEOO_Y, 32, 32);

        handleEvent();
        
        clock = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastTick == 0) {
                    stage = (Stage)canva.getScene().getWindow();
                    lastTick = now;
                    return;
                }
                long nanos = now - lastTick;
                if (nanos >= Config.freshTime) {
                    lastTick = now;
                    deltaTime = nanos / 1e9 * Config.FPS;
                    double totalTime = nanos / 1e9 * Config.FPS;
                    while (totalTime > 0) {
                        deltaTime = Math.min(totalTime, Config.fpsTime);
                        totalTime -= deltaTime;
                        run();
                        if (player.getHealth() <= 0) break;
                    }
                }
            }
        };

        clock.start();
    }

    public void receive(
        int cardA,
        int cardB,
        int hit,
        int criticalHit,
        int block,
        int blockHeal,
        int parry,
        int heldBlock,
        int parryBurst) {
        
        player.cardA = cardA;
        player.cardB = cardB;
        player.skillCount.put(PlayerInput.hit, hit);
        player.skillCount.put(PlayerInput.block, block);
        player.skillCount.put(PlayerInput.parry, parry);
        player.skillCount.put(PlayerInput.criticalHit, criticalHit);
        player.skillCount.put(PlayerInput.blockHeal, blockHeal);
        player.skillCount.put(PlayerInput.heldBlock, heldBlock);
        player.skillCount.put(PlayerInput.parryBurst, parryBurst);
    }

    private void run() {
        update();
        draw();
    }

    private void update() {
        //System.out.println("camera: " + gameCamera.getCameraX() + " " + gameCamera.getCameraY());
        //System.out.println("player: " + player.getX() + " " + player.getY());
        //System.out.println("scroll: " + gameCamera.getSrollX() + " " + gameCamera.getSrollY());
        gameTimer.update(new GameTimerUpdateStrategy(gameTimer, player, clock, stage, deltaTime));

        for (int i = 0; i < 4; i++) {
            background[i].update(0, 0, Config.MAP_WIDTH, Config.MAP_HEIGHT, new BackGroundUpdateStrategy(deltaTime));
        }

        //System.out.println(gameTimer.getTimer() + "------------------------------");

        enemies.generateEnemy(player, deltaTime);
        enemies.update(0, 0, Config.MAP_WIDTH, Config.MAP_HEIGHT, new EnemyUpdateStrategy(player, gameMap, deltaTime));
        enemies.removeDead(0, 0, Config.MAP_WIDTH, Config.MAP_HEIGHT);

        player.update(new PlayerUpdateStrategy(player, gameMap, enemies, deltaTime));
        gameCamera.update(player.getX(), player.getY());
        
        playerHealthBar.update(new HealthBarUpdateStrategy(playerHealthBar, player, gameTimer, stage, clock, deltaTime));
        skillDemo.update(new SkillDemoUpdateStrategy(skillDemo, player));
    }

    private void draw() {
        // erase
        display.setFill(Color.web(Config.BACKROUND_COLOR));
        display.fillRect(0, 0, canva.getWidth(), canva.getHeight());

        // scale
        display.save();
        display.scale(Config.SCALE_SIZE, Config.SCALE_SIZE);
    
        // draw
        int[] scroll = gameCamera.getSroll();
        double cameraLx = gameCamera.getCameraX();
        double cameraLy = gameCamera.getCameraY();
        double cameraRx = Math.min(Config.MAP_WIDTH, gameCamera.getCameraX() + gameCamera.getCameraWidth());
        double cameraRy = Math.min(Config.MAP_HEIGHT, gameCamera.getCameraY() + gameCamera.getCameraHeight());
        gameMap.draw(cameraLx, cameraLy, cameraRx, cameraRy, display, scroll);
        for (int i = 0; i < 4; i++) {
            background[i].draw(cameraLx, cameraLy, cameraRx, cameraRy, display, scroll);
        }
        enemies.draw(cameraLx, cameraLy, cameraRx, cameraRy, display, scroll);
        player.draw(display, scroll);
        playerHealthBar.draw(display, scroll);
        
        skillDemo.draw(display, scroll);
        display.setFont(defaultFont);
        gameTimer.draw(display, scroll);

        // restore to normal size
        display.restore();
    }

    private void handleEvent() {
        canva.sceneProperty().addListener(new ChangeListener<Scene>() {
            @Override
            public void changed(ObservableValue<? extends Scene> obs, Scene oldScene, Scene newScene) {
                if (newScene == null) {
                    return;
                }
                canva.requestFocus();

                canva.setOnKeyPressed(e -> {
                    if (e.getCode() == KeyCode.W) {
                        player.setInput(PlayerInput.jump, true);
                    }
                    if (e.getCode() == KeyCode.D) {
                        player.setInput(PlayerInput.moveRight, true);
                    }
                    if (e.getCode() == KeyCode.A) {
                        player.setInput(PlayerInput.moveLeft, true);
                    }
                    if (e.getCode() == KeyCode.J) {
                        player.setInput(PlayerInput.skill, true);
                        player.setInput(PlayerInput.hit, true);
                    }
                    if (e.getCode() == KeyCode.K) {
                        player.setInput(PlayerInput.skill, true);
                        player.setInput(PlayerInput.block, true);
                    }
                    if (e.getCode() == KeyCode.DIGIT1) {
                        player.setInput(PlayerInput.skill, true);
                        player.setInput(PlayerInput.criticalHit, true);
                    }
                    if (e.getCode() == KeyCode.DIGIT2) {
                        player.setInput(PlayerInput.skill, true);
                        player.setInput(PlayerInput.parry, true);
                    }
                    if (e.getCode() == KeyCode.DIGIT3) {
                        player.setInput(PlayerInput.skill, true);
                        player.setInput(PlayerInput.blockHeal, true);
                    }
                    if (e.getCode() == KeyCode.DIGIT4) {
                        player.setInput(PlayerInput.skill, true);
                        player.setInput(PlayerInput.heldBlock, true);
                    }
                    if (e.getCode() == KeyCode.DIGIT5) {
                        player.setInput(PlayerInput.skill, true);
                        player.setInput(PlayerInput.parryBurst, true);
                    }
                });
                canva.setOnKeyReleased(e -> {
                    if (e.getCode() == KeyCode.W) {
                        player.setInput(PlayerInput.jump, false);
                    }
                    if (e.getCode() == KeyCode.D) {
                        player.setInput(PlayerInput.moveRight, false);
                    }
                    if (e.getCode() == KeyCode.A) {
                        player.setInput(PlayerInput.moveLeft, false);
                    }
                    if (e.getCode() == KeyCode.J) {
                        player.setInput(PlayerInput.skill, false);
                        player.setInput(PlayerInput.hit, false);
                    }
                    if (e.getCode() == KeyCode.K) {
                        player.setInput(PlayerInput.skill, false);
                        player.setInput(PlayerInput.block, false);
                    }
                    if (e.getCode() == KeyCode.DIGIT1) {
                        player.setInput(PlayerInput.skill, false);
                        player.setInput(PlayerInput.criticalHit, false);
                    }
                    if (e.getCode() == KeyCode.DIGIT2) {
                        player.setInput(PlayerInput.skill, false);
                        player.setInput(PlayerInput.parry, false);
                    }
                    if (e.getCode() == KeyCode.DIGIT3) {
                        player.setInput(PlayerInput.skill, false);
                        player.setInput(PlayerInput.blockHeal, false);
                    }
                    if (e.getCode() == KeyCode.DIGIT4) {
                        player.setInput(PlayerInput.skill, false);
                        player.setInput(PlayerInput.heldBlock, false);
                    }
                    if (e.getCode() == KeyCode.DIGIT5) {
                        player.setInput(PlayerInput.skill, false);
                        player.setInput(PlayerInput.parryBurst, false);
                    }
                });

                canva.sceneProperty().removeListener(this);
            }
        });
        
    }
}
