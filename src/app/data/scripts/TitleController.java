package app.data.scripts;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.Slider;

import java.io.IOException;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;

public class TitleController {
    @FXML
    private AnchorPane rootAnchorPane;
    @FXML
    private ImageView startI;
    @FXML
    private ImageView endI;
    @FXML
    private Label Mus;
    @FXML
    private ImageView titleImg;
    @FXML
    private ImageView player;
    @FXML
    private ImageView player2;
    @FXML
    private Slider vSlider;
    @FXML
    Button startBT;
    @FXML
    Button endBT;

    //private MediaPlayer mediaPlayer;
    private MediaPlayer mediaPlayerw;

    @FXML
    public void initialize() {
        Pane backgroundPane = new Pane();
        backgroundPane.toBack();
        backgroundPane.setMouseTransparent(true);

        int tileSize = 25;
        Image tileImage = new Image(getClass().getResourceAsStream("/app/data/images/map/tile_1.png"));
            for (int x = 0; x < 50; x++) {
                ImageView tile = new ImageView(tileImage);
                tile.setFitWidth(tileSize);
                tile.setFitHeight(tileSize);
                tile.setLayoutX(x * tileSize);
                tile.setLayoutY(156);
                backgroundPane.getChildren().add(tile);
            }
        tileSize=150;
        for (int x = 0; x < 50; x++) {
            ImageView tile = new ImageView(tileImage);
            tile.setFitWidth(tileSize);
            tile.setFitHeight(tileSize);
            tile.setLayoutX(x * tileSize);
            tile.setLayoutY(400);
            backgroundPane.getChildren().add(tile);
        }


        rootAnchorPane.getChildren().add(backgroundPane);
        /*
        Image image1 = new Image("/app/data/images/startbutton.jpg");
        ImageView imageView = new ImageView(image1);
        imageView.setFitWidth(120);
        imageView.setFitHeight(60);
        //startBT.setGraphic(imageView);
        //startBT.setStyle("-fx-background-color: transparent;");

        Image image2 = new Image("/app/data/images/endbutton.jpg");
        ImageView imageView2 = new ImageView(image2);
        imageView2.setFitWidth(85);
        imageView2.setFitHeight(40);
        endBT.setGraphic(imageView2);
        //startBT.setStyle("-fx-background-color: transparent;");
        */
        Mus.setOpacity(0);
        startI.setOpacity(0);
        endI.setOpacity(0);
        vSlider.setOpacity(0);
        startBT.setOpacity(0);
        endBT.setOpacity(0);

        TranslateTransition moveDown = new TranslateTransition(Duration.seconds(4), titleImg);
        moveDown.setFromY(-400);
        moveDown.setToY(-20);
        ScaleTransition grow = new ScaleTransition(Duration.seconds(4), titleImg);
        grow.setFromX(1);
        grow.setFromY(1);
        grow.setToX(1.8);
        grow.setToY(1.8);

        TranslateTransition runLeft = new TranslateTransition(Duration.seconds(1.5), player);
        runLeft.setFromX(0);
        runLeft.setToX(-1000);
        TranslateTransition runRight = new TranslateTransition(Duration.seconds(1), player2);
        runRight.setFromX(0);
        runRight.setToX(780);

        runLeft.setOnFinished(e->
        {
            runRight.play();
        });
        moveDown.setOnFinished(e->{
            vSlider.setOpacity(1);
            Mus.setOpacity(1);
            startI.setOpacity(1);
            endI.setOpacity(1);
        });
        runRight.setOnFinished(e->{
            player2.setScaleX(-1);
            ScaleTransition enlarge = new ScaleTransition(Duration.seconds(1), player2);
            enlarge.setFromX(-1);
            enlarge.setFromY(1);
            enlarge.setToX(-5);
            enlarge.setToY(5);
            enlarge.play();

            URL musicUrl = getClass().getResource("/app/data/music/Wow.mp3");
            Media media = new Media(musicUrl.toString());
            mediaPlayerw = new MediaPlayer(media);
            mediaPlayerw.setVolume(0.05);
            mediaPlayerw.setStopTime(Duration.seconds(1.9));
            //mediaPlayerw.play();
        });

        ParallelTransition pt = new ParallelTransition(moveDown, grow);

        runLeft.play();
        pt.play();
        vSlider.setValue(50);
        MusicPlayer.play();
        MusicPlayer.mediaPlayer.volumeProperty().bind(vSlider.valueProperty().divide(100));
        /*
        URL musicUrl = getClass().getResource("/app/data/music/DormantCraving.mp3");
        Media media = new Media(musicUrl.toString());
        mediaPlayer = new MediaPlayer(media);

        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.setStartTime(Duration.seconds(3.5));
        mediaPlayer.play();
        */
    }

    /*
    public void cleanup() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }
        if (mediaPlayerw != null) {
            mediaPlayerw.stop();
            mediaPlayerw.dispose();
        }
    }
    */

    @FXML
    public void startGame(){
        try {
            GameInfo.init();
            System.out.println(GameInfo.totalTime);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/data/scripts/CardGame.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) titleImg.getScene().getWindow();
            CardGameController controller = loader.getController();
            controller.setInitialCounts(
                Config.CRAD_A_BASIC_PROFIT,
                Config.CRAD_B_BASIC_PROFIT,
                0,
                0,
                0,
                0,
                0,
                0,
                0
            );
            //cleanup();
            stage.setScene(new Scene(root, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void EndGame(){
        System.exit(0);
    }
}