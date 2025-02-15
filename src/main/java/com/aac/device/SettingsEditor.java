package com.aac.device;

import com.aac.device.model.Card;
import com.aac.device.model.Category;
import com.aac.device.model.CategoryGroup;
import com.aac.device.utils.SceneFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.FileWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SettingsEditor {
    private List<CategoryGroup> categoryGroups = new ArrayList<>();
    private VBox mainContainer;
    private ObjectMapper mapper = new ObjectMapper();

    public void start(Stage primaryStage) {
        primaryStage.setTitle("AAC Device Settings");

        // Create main scroll container
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setPadding(new Insets(10));

        mainContainer = new VBox(10);
        mainContainer.setPadding(new Insets(10));
        mainContainer.prefWidthProperty().bind(scrollPane.widthProperty().subtract(20)); // Ensures responsiveness //

        // Load data
        loadData();

        // Create UI elements
        updateUI();

        scrollPane.setContent(mainContainer);

        Scene scene = new Scene(scrollPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }
    private HBox getButtonContainer(){
        // Bottom buttons
        HBox buttonContainer = new HBox(10);
        buttonContainer.setPadding(new Insets(10));
        buttonContainer.setStyle("-fx-background-color: #f4f4f4;");
        buttonContainer.setAlignment(javafx.geometry.Pos.CENTER);

        // Cancel button
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> cancel(e));
        // Save button
        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> saveChanges(e));

        // Dynamically resizes
        HBox.setHgrow(saveButton, Priority.ALWAYS);
        HBox.setHgrow(cancelButton, Priority.ALWAYS);
        saveButton.setMaxWidth(Double.MAX_VALUE);
        cancelButton.setMaxWidth(Double.MAX_VALUE);

        buttonContainer.getChildren().addAll(cancelButton, saveButton);
        return buttonContainer;
    }

    private void loadData() {
        try {
            File file = getCategoryFile();
            categoryGroups = mapper.readValue(file, new TypeReference<>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error loading data: " + e.getMessage());
        }
    }

    private void updateUI() {
        mainContainer.getChildren().clear();

        for (int groupIndex = 0; groupIndex < categoryGroups.size(); groupIndex++) {
            CategoryGroup group = categoryGroups.get(groupIndex);
            VBox groupBox = createGroupBox(group, groupIndex);
            mainContainer.getChildren().add(groupBox);
        }
        HBox buttonContainer = getButtonContainer();
        mainContainer.getChildren().add(buttonContainer);
    }

    private VBox createGroupBox(CategoryGroup group, int groupIndex) {
        VBox groupBox = new VBox(10);
        groupBox.getStyleClass().add("group-box");
        groupBox.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5; -fx-padding: 10;");
        groupBox.prefWidthProperty().bind(mainContainer.widthProperty().subtract(20));

        // Group header
        HBox groupHeader = new HBox(10);
        groupHeader.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        // Title
        TextField groupTitle = new TextField(group.getTitle());
        groupTitle.prefWidthProperty().bind(groupBox.widthProperty().multiply(0.5));
        groupTitle.textProperty().addListener((obs, old, newValue) -> group.setTitle(newValue));

        TitledPane groupPane = new TitledPane();
        groupPane.setText("");
        VBox categoriesBox = new VBox(5);
        categoriesBox.prefWidthProperty().bind(groupBox.widthProperty().subtract(20));
        groupPane.setContent(categoriesBox);

        for (int categoryIndex = 0; categoryIndex < group.getCategories().size(); categoryIndex++) {
            Category category = group.getCategories().get(categoryIndex);
            VBox categoryBox = createCategoryBox(category, groupIndex, categoryIndex);
            categoriesBox.getChildren().add(categoryBox);
        }

        // Add Category Button
        Button addCategoryButton = new Button("Add Category");
        addCategoryButton.setOnAction(e -> {
            Category newCategory = new Category();
            newCategory.setTitle("New Category");
            newCategory.setCards(new ArrayList<>());
            group.getCategories().add(newCategory);
            int categoryIndex = group.getCategories().size() - 1;
            VBox categoryBox = createCategoryBox(newCategory, groupIndex, categoryIndex);
            categoriesBox.getChildren().add(categoryBox);
        });

        // CategoryGroup Buttons
        HBox categoryGroupControlButtons = new HBox(5);

        // Add CategoryGroup Button
        Button addCategoryGroupButton = new Button("Add Category Group");
        addCategoryGroupButton(addCategoryGroupButton, groupIndex);
        categoryGroupControlButtons.getChildren().add(addCategoryGroupButton);

        // Delete CategoryGroup Button
        Button deleteCategoryGroupButton = new Button("Delete Category Group");
        deleteCategoryGroupButton.setOnAction(e -> {
            Stage settingsStage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            if (deleteAlert("category group", settingsStage)){
                categoryGroups.remove(groupIndex);
                updateUI();
            }

            settingsStage.setFullScreen(true);
        });
        categoryGroupControlButtons.getChildren().add(deleteCategoryGroupButton);

        groupHeader.getChildren().addAll(groupTitle, addCategoryButton);
        groupBox.getChildren().addAll(groupHeader, groupPane, categoryGroupControlButtons);

        return groupBox;
    }

    private boolean deleteAlert (String navigationLevel, Stage settingsStage){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Confirmation");
        alert.setContentText("Are you sure you want to delete this " + navigationLevel + " ?");

        // Ensure the alert appears on top of the fullscreen settings window
        alert.initOwner(settingsStage);
        alert.initModality(Modality.APPLICATION_MODAL); // Prevents interaction with settings until closed

        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.OK) {
            return true;
        }
        else{
            return false;
        }
    }
    private void addCategoryGroupButton(Button addButton, int groupIndex){
        addButton.setOnAction(e -> {
            CategoryGroup newCategoryGroup = new CategoryGroup();
            newCategoryGroup.setTitle("New Category Group");
            newCategoryGroup.setCategories(new ArrayList<>());
            int newCategoryGroupIndex = groupIndex + 1;
            categoryGroups.add(newCategoryGroupIndex, newCategoryGroup);
            updateUI();
        });
    }
    private VBox createCategoryBox(Category category, int groupIndex, int categoryIndex) {
        VBox categoryBox = new VBox(5);
        categoryBox.setStyle("-fx-padding: 5;");

        // Category header
        HBox categoryHeader = new HBox(10);
        TextField categoryTitle = new TextField(category.getTitle());
        TextField categoryImage = new TextField(category.getImageFile());

        // Dynamic scaling
        categoryTitle.prefWidthProperty().bind(categoryBox.widthProperty().multiply(0.4));
        categoryImage.prefWidthProperty().bind(categoryBox.widthProperty().multiply(0.4));

        categoryTitle.textProperty().addListener((obs, old, newValue) -> category.setTitle(newValue));
        categoryImage.textProperty().addListener((obs, old, newValue) -> category.setImageFile(newValue));

        TitledPane categoryPane = new TitledPane();
        VBox cardsBox = new VBox(5);
        categoryPane.setContent(cardsBox);

        // Add cards
        for (int cardIndex = 0; cardIndex < category.getCards().size(); cardIndex++) {
            Card card = category.getCards().get(cardIndex);
            HBox cardBox = createCardBox(card, groupIndex, categoryIndex, cardIndex);
            cardsBox.getChildren().add(cardBox);
        }

        // Add card button
        Button addCardButton = new Button("Add Card");
        addCardButton.setOnAction(e -> {
            Card newCard = new Card();
            category.getCards().add(newCard);
            HBox cardBox = createCardBox(newCard, groupIndex, categoryIndex, category.getCards().size() - 1);
            cardsBox.getChildren().add(cardBox);
        });


        // Delete category button
        Button deleteCategoryButton = new Button("Delete Category");
        deleteCategoryButton.setOnAction(e -> {
            Stage settingsStage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            if (deleteAlert("category", settingsStage)){
                categoryGroups.get(groupIndex).getCategories().remove(categoryIndex);
                updateUI();
            }
        });

        categoryHeader.getChildren().addAll(categoryTitle, categoryImage, deleteCategoryButton);
        categoryBox.getChildren().addAll(categoryHeader, categoryPane, addCardButton);
        return categoryBox;
    }

    private HBox createCardBox(Card card, int groupIndex, int categoryIndex, int cardIndex) {
        HBox cardBox = new HBox(5);
        cardBox.setStyle("-fx-padding: 5;");//

        TextField cardTitle = new TextField(card.getTitle());
        TextField cardText = new TextField(card.getText());
        TextField cardImage = new TextField(card.getImageFile());

        // Dynamic scaling
        HBox.setHgrow(cardTitle, Priority.ALWAYS);
        HBox.setHgrow(cardText, Priority.ALWAYS);
        HBox.setHgrow(cardImage, Priority.ALWAYS);

        cardTitle.textProperty().addListener((obs, old, newValue) -> card.setTitle(newValue));
        cardText.textProperty().addListener((obs, old, newValue) -> card.setText(newValue));
        cardImage.textProperty().addListener((obs, old, newValue) -> card.setImageFile(newValue));

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            categoryGroups.get(groupIndex).getCategories().get(categoryIndex).getCards().remove(cardIndex);
            updateUI();
        });

        cardBox.getChildren().addAll(cardTitle, cardText, cardImage, deleteButton);
        return cardBox;
    }

    private void saveChanges(ActionEvent event) {
        Stage settingsStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        try {
            String jsonString = mapper.writeValueAsString(categoryGroups);
            File file = getCategoryFile();
            try(FileWriter writer = new FileWriter(file)) {
                writer.write(jsonString);
                writer.flush();
            }
            showAlert("Changes saved successfully!", settingsStage);
        } catch (Exception e) {
            showAlert("Error saving changes: " + e.getMessage());
        }

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        SceneFactory.createMainWindow(stage);
    }

    private void cancel(ActionEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        SceneFactory.createMainWindow(stage);
    }

    private void showAlert(String message, Stage stage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(stage);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("ERROR");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private File getCategoryFile() throws URISyntaxException {
        String directory = SettingsEditor.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        String filePath = directory + "category_card.json";
        return new File(filePath);
    }

}
