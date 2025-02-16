package com.aac.device.utils;

import com.aac.device.AacApplication;
import com.aac.device.AacController;
import com.aac.device.MainComponent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneFactory {
    public static void createMainWindow(Stage stage) {
        FXMLLoader fxmlLoader = new FXMLLoader(AacApplication.class.getResource("aac-view.fxml"));
        Scene scene;

        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        stage.setTitle("AAC Device");
        stage.setScene(scene);

        AacController aacController = fxmlLoader.getController();
        MainComponent mainComponent = new MainComponent();
        mainComponent.setController(aacController);

        // Scales components to window size
        scene.widthProperty().addListener((obs, oldVal, newVal) ->
                aacController.resizeComponents(scene.getWidth(), scene.getHeight()));
        scene.heightProperty().addListener((obs, oldVal, newVal) ->
                aacController.resizeComponents(scene.getWidth(), scene.getHeight()));

        // Initial resize
        stage.showingProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                aacController.resizeComponents(scene.getWidth(), scene.getHeight());
            }
        });

        stage.setFullScreen(true);
        scene.setOnKeyReleased(keyEvent -> mainComponent.onKeyReleased(keyEvent));
        stage.show();

        try {
            mainComponent.load();
        }
        catch(Exception ex) {
            showErrorAndClose(stage,ex);
        }
    }

    private static void showErrorAndClose(Stage stage, Exception ex){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(ex.getMessage());
        alert.showAndWait();
        try {
            stage.close();
        }
        catch(Exception ignored) {
        }
    }
}
