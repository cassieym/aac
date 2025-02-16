package com.aac.device;

import com.aac.device.utils.SceneFactory;
import javafx.application.Application;
import javafx.stage.Stage;

public class AacApplication extends Application {

    @Override
    public void start(Stage stage) {
        SceneFactory.createMainWindow(stage);
    }

    @Override
    public void stop() throws Exception {
        super.stop(); // stop application
    }

    public static void main(String[] args) {
        launch();
    }
}
