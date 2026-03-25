package app.data.scripts.gameResult;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import app.data.scripts.Config;
import app.data.scripts.GameInfo;

public class ResultController implements Initializable {

    @FXML private ImageView backgroundImage;
    @FXML private Label timeLabel;
    @FXML private Label cardsHeldLabel;
    @FXML private Label cardsUsedLabel;
    @FXML private Label totalScoreLabel;
    @FXML private Button restartButton;
    @FXML private AnchorPane rootPane;

    private int survivalTime;
    private int cardsHeld;
    private int cardsUsed;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load the background image
        try {
            Image bgImage = new Image(getClass().getResourceAsStream("resultScreen.png"));
            backgroundImage.setImage(bgImage);
        } catch (Exception e) {
            System.err.println("Error loading background image: " + e.getMessage());
        }

        setGameStats(GameInfo.totalTime, GameInfo.cardsRemaining, GameInfo.cardsUsed);
    }

    public void setGameStats(int survivalTime, int cardsHeld, int cardsUsed) {
        this.survivalTime = survivalTime;
        this.cardsHeld = cardsHeld;
        this.cardsUsed = cardsUsed;

        updateLabels();
    }

    private void updateLabels() {
        timeLabel.setText("存活時間: " + survivalTime + " 秒");
        cardsHeldLabel.setText("持有卡牌: " + cardsHeld);
        cardsUsedLabel.setText("已使用卡牌: " + cardsUsed);

        int totalScore = survivalTime + cardsHeld + cardsUsed;
        totalScoreLabel.setText("總分: " + totalScore);
    }

    @FXML
    private void handleRestart() throws IOException {
        Stage stage = (Stage) restartButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/app/data/scripts/Title.fxml"));
        stage.setScene(new Scene(root, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT));
    }

    @FXML
    private void handleExit() {
        System.out.println("Exit button clicked");
        System.exit(0);
    }
}