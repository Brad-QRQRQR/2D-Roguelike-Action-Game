package app.data.scripts.engine.tools;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PageSwitcher {
    public void switchPage(String targetPage, ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(targetPage));
        Parent root = loader.load();

        SceneController controller = loader.getController();
        controller.initialize();

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}

