package app;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import app.data.scripts.Config;
import app.data.scripts.GameInfo;

public class Main extends Application {
    public static void main(String[] args) throws Exception {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        GameInfo.load();
        Parent root = FXMLLoader.load(getClass().getResource("/app/data/scripts/Title.fxml"));
        Scene scene = new Scene(root, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Crazy Man");
        primaryStage.show();
    }
}