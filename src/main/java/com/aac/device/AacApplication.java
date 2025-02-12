package com.aac.device;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class AacApplication extends Application {
    private MainComponent mainComponent;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AacApplication.class.getResource("aac-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("AAC Device");
        stage.setScene(scene);

        AacController aacController = fxmlLoader.getController();
        mainComponent = new MainComponent(aacController, stage);
        scene.setOnKeyReleased(keyEvent -> mainComponent.onKeyReleased(keyEvent));
        stage.show();

        try {
            mainComponent.load();
        }
        catch(Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(ex.getMessage());
            alert.showAndWait();
            try {
                stage.close();
            }
            catch(Exception e) {
            }
        }
    }

    @Override
    public void stop() throws Exception {
        this.mainComponent.close();
        super.stop();
    }

    public static void main(String[] args) {
        launch();
    }
}
