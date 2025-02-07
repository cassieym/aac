package com.aac.device;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class AacController {
    @FXML
    private Label displayText;

    @FXML
    private GridPane leftGridPane;

    @FXML
    private Pane rightPane;

    public void setDisplayText(String text) {
        if(text == null) {
            text = "";
        }
        this.displayText.setText(text);
    }

    public GridPane getLeftGridPane() {
        return this.leftGridPane;
    }

    public Pane getRightPane() {
        return this.rightPane;
    }

}
