package com.aac.device;

import com.aac.device.model.Card;
import com.aac.device.model.Category;
import com.aac.device.model.CategoryGroup;
import com.aac.device.utils.CategoryCardLoader;
import com.aac.device.utils.SceneFactory;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SettingsEditor {
    private List<CategoryGroup> categoryGroups = new ArrayList<>();
    private VBox mainContainer;
    private final ObjectMapper mapper = new ObjectMapper();

    // MAIN METHOD
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

    private void loadData() {
        try {
            categoryGroups = CategoryCardLoader.loadCategories();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error loading data: " + e.getMessage(), null, Alert.AlertType.ERROR);
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


    // CREATE UI ELEMENTS
    private VBox createGroupBox(CategoryGroup group, int groupIndex) {
        // Settings Group Container
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

        // Category Toggle Container
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
        Button addCategoryButton = getAddCategoryButton(group, groupIndex, categoriesBox);

        // CategoryGroup Buttons
        HBox categoryGroupControlButtons = new HBox(5);
        // Add CategoryGroup Button 
        Button addCategoryGroupButton = getAddCategoryGroupButton(groupIndex);  
        categoryGroupControlButtons.getChildren().add(addCategoryGroupButton);  
        // Delete CategoryGroup Button
        Button deleteCategoryGroupButton = getDeleteCategoryGroupButton(groupIndex);
        categoryGroupControlButtons.getChildren().add(deleteCategoryGroupButton);

        // Add to layout
        groupHeader.getChildren().addAll(groupTitle, addCategoryButton); // Top row
        groupBox.getChildren().addAll(groupHeader, groupPane, categoryGroupControlButtons); // Bottom row 

        return groupBox;
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

        // TextField Listeners
        categoryTitle.textProperty().addListener((obs, old, newValue) -> category.setTitle(newValue));
        categoryImage.textProperty().addListener((obs, old, newValue) -> category.setImageFile(newValue));

        // Category Content Container
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
        Button addCardButton = getAddCardButton(cardsBox,category, groupIndex, categoryIndex);

        // Delete category button
        Button deleteCategoryButton = getDeleteCategoryButton(groupIndex, categoryIndex);

        // Add to layout
        categoryHeader.getChildren().addAll(categoryTitle, categoryImage, deleteCategoryButton); // Top row
        categoryBox.getChildren().addAll(categoryHeader, categoryPane, addCardButton); // Bottom row
        return categoryBox;
    }

    private HBox createCardBox(Card card, int groupIndex, int categoryIndex, int cardIndex) {
        // Card Container
        HBox cardBox = new HBox(5);
        cardBox.setStyle("-fx-padding: 5;");//

        // Card Text Fields
        TextField cardTitle = new TextField(card.getTitle());
        TextField cardText = new TextField(card.getText());
        TextField cardImage = new TextField(card.getImageFile());

        // TextField Listeners
        cardTitle.textProperty().addListener((obs, old, newValue) -> card.setTitle(newValue));
        cardText.textProperty().addListener((obs, old, newValue) -> card.setText(newValue));
        cardImage.textProperty().addListener((obs, old, newValue) -> card.setImageFile(newValue));

        // Dynamic scaling
        HBox.setHgrow(cardTitle, Priority.ALWAYS);
        HBox.setHgrow(cardText, Priority.ALWAYS);
        HBox.setHgrow(cardImage, Priority.ALWAYS);

        // Delete card button
        Button deleteButton = getDeleteCardButton(groupIndex, categoryIndex, cardIndex);

        cardBox.getChildren().addAll(cardTitle, cardText, cardImage, deleteButton);
        return cardBox;
    }


    // ADD/DELETE UI ELEMENTS
    private Button getAddCategoryButton(CategoryGroup group, int groupIndex, VBox categoriesBox) {
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
        return addCategoryButton;
    }

    private Button getDeleteCategoryButton(int groupIndex, int categoryIndex) {
        Button deleteCategoryButton = new Button("Delete Category");
        deleteCategoryButton.setOnAction(e -> {
            Stage settingsStage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            if (deleteAlert("category", settingsStage)){
                categoryGroups.get(groupIndex).getCategories().remove(categoryIndex);
                updateUI();
            }
        });
        return deleteCategoryButton;
    }

    private Button getAddCategoryGroupButton(int groupIndex){
        Button addButton = new Button("Add Category Group");
        addButton.setOnAction(e -> {
            CategoryGroup newCategoryGroup = new CategoryGroup();
            newCategoryGroup.setTitle("New Category Group");
            newCategoryGroup.setCategories(new ArrayList<>());
            int newCategoryGroupIndex = groupIndex + 1;
            categoryGroups.add(newCategoryGroupIndex, newCategoryGroup);
            updateUI();
        });
        return addButton;
    }

    private Button getDeleteCategoryGroupButton(int groupIndex){
        Button deleteButton = new Button("Delete Category Group");
        deleteButton.setOnAction(e -> {
            Stage settingsStage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            if (deleteAlert("category group", settingsStage)){
                categoryGroups.remove(groupIndex);
                updateUI();
            }
            settingsStage.setFullScreen(true);
        });
        return deleteButton;
    }

    private Button getAddCardButton(VBox cardsBox, Category category, int groupIndex, int categoryIndex){
        Button addButton = new Button("Add Card");
        addButton.setOnAction(e -> {
            Stage settingsStage = (Stage) ((Node)e.getSource()).getScene().getWindow();
            Card newCard = new Card();
            category.getCards().add(newCard);
            HBox cardBox = createCardBox(newCard, groupIndex, categoryIndex, category.getCards().size() - 1);
            cardsBox.getChildren().add(cardBox);
            showAlert("Caution: Card URL/file must end in .jpg or .png to work", settingsStage, Alert.AlertType.INFORMATION);
        });
        return addButton;
    }

    private Button getDeleteCardButton(int groupIndex, int categoryIndex, int cardIndex){
        Button deleteButton = new Button("Delete Card");
        deleteButton.setOnAction(e -> {
            categoryGroups.get(groupIndex).getCategories().get(categoryIndex).getCards().remove(cardIndex);
            updateUI();
        });
        return deleteButton;
    }   

    // CANCEL/SAVE UI ELEMENT CHANGES
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

    private void cancel(ActionEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        SceneFactory.createMainWindow(stage);
    }

    private void saveChanges(ActionEvent event) {
        Stage settingsStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        try {
            CategoryCardLoader.saveCategories(categoryGroups);
            showAlert("Changes saved successfully!", settingsStage, Alert.AlertType.INFORMATION);
            SceneFactory.createMainWindow(settingsStage);
        } catch (Exception e) {
            showAlert("Error saving changes: " + e.getMessage(), settingsStage, Alert.AlertType.ERROR);
        }
    }


    // ALERT METHODS
    private void showAlert(String message, Stage owner, Alert.AlertType type) {
        Alert alert = new Alert(type);
        if (owner != null) {
            alert.initOwner(owner);
        }
        alert.setTitle(type == Alert.AlertType.ERROR ? "Error" : "Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean deleteAlert (String navigationLevel, Stage settingsStage){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Confirmation");
        alert.setContentText("Are you sure you want to delete this " + navigationLevel + " ?");

        // Ensure the alert appears on top of the fullscreen settings window
        alert.initOwner(settingsStage);
        alert.initModality(Modality.APPLICATION_MODAL); // Prevents interaction with settings until closed

        Optional<ButtonType> result = alert.showAndWait();

        return result.get() == ButtonType.OK;
    }

}
