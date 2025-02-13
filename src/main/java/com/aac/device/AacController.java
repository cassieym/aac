package com.aac.device;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.stage.Stage;

public class AacController {
    @FXML
    private Label displayText;

    @FXML
    private GridPane leftGridPane;

    @FXML
    private Pane rightPane;

    @FXML
    private Button settingButton;


    private MainComponent mainComponent;

    public void setMainComponent(MainComponent mainComponent) {
        this.mainComponent = mainComponent;
    }

    public void setDisplayText(String text) {
        if(text == null) {
            text = "";
        }
        if (this.displayText.getText() == ""){    // prevent indent for first word
            this.displayText.setText(text);
        }
        else {
            this.displayText.setText(this.displayText.getText() + " " + text);
        }

        this.displayText.setFont(Font.font("Verdana", FontPosture.REGULAR, 25));
        this.displayText.setWrapText(true); // allows for 2 lines of text
    }

    public void setDisplayText() { // clear text
        this.displayText.setText("");
    }

    public GridPane getLeftGridPane() {
        return this.leftGridPane;
    }

    public Pane getRightPane() {
        return this.rightPane;
    }

    public void clickMenuButton(MouseEvent e){
//        System.out.println("Clicked");
//        try {
//            root = FXMLLoader.load(AacApplication.class.getResource("settings.fxml"));
//            stage = (Stage)((Node)e.getSource()).getScene().getWindow();
//            scene = new Scene(root);
//            stage.setScene(scene);
//            stage.show();
//        } catch (IOException ex) {
//            throw new RuntimeException(ex);
//        }
        SettingsEditor settingsEditor = new SettingsEditor();
        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        settingsEditor.start(stage);

    }

//    public void clickSaveButton(MouseEvent f) throws IOException {
//        System.out.println("Saved");
//        stage = (Stage)((Node)f.getSource()).getScene().getWindow();
//        SceneFactory.createMainWindow(stage);
//    }
}


