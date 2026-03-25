package app.data.scripts.game.userInterface;

import java.io.IOException;

import app.data.scripts.Config;
import app.data.scripts.GameInfo;
import app.data.scripts.engine.entity.UpdateStrategy;
import app.data.scripts.game.player.Player;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HealthBarUpdateStrategy extends UpdateStrategy {
    private double dt;
    private Player player;
    private HealthBar healthBar;
    private Stage stage;
    private AnimationTimer clock;
    private GameTimer gameTimer;

    public HealthBarUpdateStrategy(HealthBar healthBar, Player player, GameTimer gameTimer, Stage stage, AnimationTimer clock, double dt) {
        this.healthBar = healthBar;
        this.dt = dt;
        this.player = player;
        this.gameTimer = gameTimer;
        this.stage = stage;
        this.clock = clock;
    }

    public double getDt() {
        return dt;
    }

    public double getHealth() {
        return player.getHealth();
    }

    @Override
    public void perform() {
        healthBar.setWidth((double)player.getHealth() / Player.MAX_HEALTH * Config.HEALTH_BAR_WIDTH);

        if (player.getHealth() <= 0) {
            try {
                clock.stop();
                GameInfo.cardsRemaining = player.cardA + player.cardB;
                GameInfo.totalTime += Config.ROUND_TIME_SEC - (int)gameTimer.getTimer() / Config.FPS;
                System.out.println(GameInfo.totalTime);

                Parent root = FXMLLoader.load(getClass().getResource("/app/data/scripts/gameResult/ResultController.fxml"));                
                Scene scene = new Scene(root, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
                stage.setScene(scene);
                stage.show();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
