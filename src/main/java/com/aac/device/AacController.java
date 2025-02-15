package com.aac.device;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
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
    @FXML
    private VBox VBoxContainer;
    private MainComponent mainComponent;
    private double lastWidth;
    private double lastHeight;
    private boolean isFullScreen;

    public void setMainComponent(MainComponent mainComponent) {
        this.mainComponent = mainComponent;
    }

    public void resizeComponents(double width, double height) {
        // Resize VBox container
        VBoxContainer.setPrefWidth(width);
        VBoxContainer.setPrefHeight(height);

        // Resize display text area (top section)
        displayText.setPrefWidth(width * 0.7);
        displayText.setPrefHeight(height * 0.1);
        settingButton.setPrefWidth(width * 0.15);

        // Resize grid panes
        leftGridPane.setPrefWidth(width * 0.6);
        leftGridPane.setPrefHeight(height * 0.7);

        rightPane.setPrefWidth(width * 0.35);
        rightPane.setPrefHeight(height * 0.7);

        // Resize all child GridPanes in leftGridPane
        for (Node node : leftGridPane.getChildren()) {
            if (node instanceof GridPane) {
                GridPane categoryGrid = (GridPane) node;
                categoryGrid.setPrefWidth(width * 0.18);
                categoryGrid.setPrefHeight(height * 0.32);

                // Resize cells within category grid
                for (Node cell : categoryGrid.getChildren()) {
                    if (cell instanceof Region) {
                        ((Region) cell).setPrefWidth(width * 0.05);
                        ((Region) cell).setPrefHeight(height * 0.09);
                    }
                }
            }
        }

        // Resize the cards grid in rightPane
        GridPane cardsGrid = (GridPane) ((StackPane) rightPane).getChildren().get(0);
        cardsGrid.setPrefWidth(width * 0.32);
        cardsGrid.setPrefHeight(height * 0.65);

        // Resize cells within cards grid
        for (Node cell : cardsGrid.getChildren()) {
            if (cell instanceof Region) {
                ((Region) cell).setPrefWidth(width * 0.07);
                ((Region) cell).setPrefHeight(height * 0.1);
            }
        }
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
        SettingsEditor settingsEditor = new SettingsEditor();
        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        settingsEditor.start(stage);

    }
}


