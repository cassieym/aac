package com.aac.device;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
        System.out.println("Clicked");
        try {
            FXMLLoader menuFXML = new FXMLLoader(AacApplication.class.getResource("settings.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Menu Window");
            stage.setScene(new Scene(menuFXML.load(), 450, 450));
            stage.show();
            // Hide this current window (if this is what you want)
            ((Node)(e.getSource())).getScene().getWindow().hide();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}
