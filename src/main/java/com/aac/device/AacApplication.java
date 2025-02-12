package com.aac.device;

import com.aac.device.utils.SceneFactory;
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
        SceneFactory.createMainWindow(stage);
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
