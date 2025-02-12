package com.aac.device;

import com.aac.device.utils.SceneFactory;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.stage.Stage;

import java.io.IOException;

public class AacController {
    @FXML
    private Label displayText;

    @FXML
    private GridPane leftGridPane;

    @FXML
    private Pane rightPane;

    @FXML
    private Button menuButton;
    @FXML
    private Button saveSettingsButton;
    @FXML
    private ListView cardsListView;
    @FXML
    private ListView categoryListView;

    private Stage stage;
    private Scene scene;
    private Parent root;

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


//    public void clickMenuButton(MouseEvent e) throws IOException{
//        System.out.println("Clicked");
//        try {
//            FXMLLoader menuFXML = new FXMLLoader(AacApplication.class.getResource("settings.fxml"));
//            stage = (Stage)((Node)e.getSource()).getScene().getWindow();
////            this.stage.setTitle("Menu Window");
//            scene = new Scene(menuFXML);
//            stage.setScene(new Scene(menuFXML.load()));
//            stage.show();
//
//            SettingsController controller = menuFXML.getController();
//            controller.setMainComponent(this.mainComponent);
//
//            // Hide this current window (if this is what you want)
////            ((Node)(e.getSource())).getScene().getWindow().hide();
//
//
//        } catch (IOException ex) {
//            throw new RuntimeException(ex);
//        }
//    }

    public void clickMenuButton(MouseEvent e){
        System.out.println("Clicked");
        try {
            root = FXMLLoader.load(AacApplication.class.getResource("settings.fxml"));
            stage = (Stage)((Node)e.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void clickSaveButton(MouseEvent f) throws IOException {
        System.out.println("Saved");
        stage = (Stage)((Node)f.getSource()).getScene().getWindow();
        SceneFactory.createMainWindow(stage);
    }
}


