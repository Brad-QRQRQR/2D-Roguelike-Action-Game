package app.data.scripts.game.userInterface;

import java.io.IOException;

import app.data.scripts.CardGameController;
import app.data.scripts.Config;
import app.data.scripts.GameInfo;
import app.data.scripts.engine.entity.UpdateStrategy;
import app.data.scripts.game.player.Player;
import app.data.scripts.game.player.PlayerInput;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GameTimerUpdateStrategy extends UpdateStrategy {
    private GameTimer gameTimer;
    private double dt;
    private Stage stage;
    private AnimationTimer clock;
    private Player player;
    
    public GameTimerUpdateStrategy(GameTimer gameTimer, Player player, AnimationTimer clock, Stage stage, double dt) {
        this.gameTimer = gameTimer;
        this.dt = dt;
        this.clock = clock;
        this.stage = stage;
        this.player = player;
    }

    public double getDt() {
        return dt;
    }
    
    @Override
    public void perform() {
        //System.out.println(gameTimer.getTimer() + " " + gameTimer.isOver());
        if (gameTimer.isOver()) {
            try {
                clock.stop();
                GameInfo.saveState(player.getHealth());
                GameInfo.totalTime += Config.FPS;
                GameInfo.round++;
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/data/scripts/CardGame.fxml"));
                Parent root = loader.load();                
                Scene scene = new Scene(root, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
                CardGameController controller = loader.getController();
                controller.setInitialCounts(
                    player.cardA + Config.CRAD_A_BASIC_PROFIT + player.bonus * Config.CARD_A_BONUS,
                    player.cardB + Config.CRAD_B_BASIC_PROFIT + player.bonus * Config.CARD_B_BONUS,
                    player.skillCount.get(PlayerInput.hit),
                    player.skillCount.get(PlayerInput.block),
                    player.skillCount.get(PlayerInput.parry),
                    player.skillCount.get(PlayerInput.criticalHit),
                    player.skillCount.get(PlayerInput.blockHeal),
                    player.skillCount.get(PlayerInput.heldBlock),
                    player.skillCount.get(PlayerInput.parryBurst)
                );
                stage.setScene(scene);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
