package com.aac.device;

import com.aac.device.model.Card;
import com.aac.device.model.Category;
import com.aac.device.model.CategoryGroup;
import com.aac.device.utils.SceneFactory;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.io.FileWriter;
import java.net.URISyntaxException;
import java.util.*;

public class SettingsEditor {
    private List<CategoryGroup> categoryGroups = new ArrayList<>();
    private VBox mainContainer;
    private ObjectMapper mapper = new ObjectMapper();

    public void start(Stage primaryStage) {
        primaryStage.setTitle("AAC Device Settings");

        // Create main scroll container
        ScrollPane scrollPane = new ScrollPane();
        mainContainer = new VBox(10);
        mainContainer.setPadding(new Insets(10));

        // Load data
        loadData();

        // Create UI elements
        updateUI();

        // Bottom buttons
        HBox buttonContainer = new HBox(10);
        mainContainer.getChildren().add(buttonContainer);
        // Save button
        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> saveChanges(e));
        buttonContainer.getChildren().add(saveButton);
        // Cancel button
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> cancel(e));
        buttonContainer.getChildren().add(cancelButton);

        scrollPane.setContent(mainContainer);
        scrollPane.setFitToWidth(true);

        Scene scene = new Scene(scrollPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadData() {
        try {
            File file = getCategoryFile();
            categoryGroups = mapper.readValue(file, new TypeReference<List<CategoryGroup>>() {});
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
    }

    private VBox createGroupBox(CategoryGroup group, int groupIndex) {
        VBox groupBox = new VBox(5);
        groupBox.getStyleClass().add("group-box");
        groupBox.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5; -fx-padding: 10;");


        // Group header
        HBox groupHeader = new HBox(5);

        // Title
        TextField groupTitle = new TextField(group.getTitle());
        groupTitle.setPrefWidth(200);
        groupTitle.textProperty().addListener((obs, old, newValue) -> {
            group.setTitle(newValue);
        });

        TitledPane groupPane = new TitledPane();
        groupPane.setText("");
        VBox categoriesBox = new VBox(5);
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
            newCategory.setTitle("New Title");
            newCategory.setCards(new ArrayList<>());
            group.getCategories().add(newCategory);
            int categoryIndex = group.getCategories().size() - 1;
            VBox categoryBox = createCategoryBox(newCategory, groupIndex, categoryIndex);
            categoriesBox.getChildren().add(categoryBox);
        });

        groupHeader.getChildren().addAll(groupTitle, addCategoryButton);
        groupBox.getChildren().addAll(groupHeader, groupPane);

        return groupBox;
    }

    private VBox createCategoryBox(Category category, int groupIndex, int categoryIndex) {
        VBox categoryBox = new VBox(5);
        categoryBox.setStyle("-fx-padding: 0 0 0 20;");

        // Category header
        HBox categoryHeader = new HBox(7);
        TextField categoryTitle = new TextField(category.getTitle());
        TextField categoryImage = new TextField(category.getImageFile());
        categoryTitle.setPrefWidth(200);
        categoryImage.setPrefWidth(200);

        categoryTitle.textProperty().addListener((obs, old, newValue) -> {
            category.setTitle(newValue);
        });
        categoryImage.textProperty().addListener((obs, old, newValue) -> {
            category.setImageFile(newValue);
        });

        TitledPane categoryPane = new TitledPane();
        categoryPane.setText("");
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
            categoryGroups.get(groupIndex).getCategories().remove(categoryIndex);
            updateUI();
        });

        categoryHeader.getChildren().addAll(categoryTitle, categoryImage, deleteCategoryButton);
        categoryBox.getChildren().addAll(categoryHeader, categoryPane, addCardButton);

        return categoryBox;
    }

    private HBox createCardBox(Card card, int groupIndex, int categoryIndex, int cardIndex) {
        HBox cardBox = new HBox(5);
        cardBox.setStyle("-fx-padding: 0 0 0 20;");

        TextField cardTitle = new TextField(card.getTitle());
        TextField cardText = new TextField(card.getText());
        TextField cardImage = new TextField(card.getImageFile());

        cardTitle.setPrefWidth(150);
        cardText.setPrefWidth(150);
        cardImage.setPrefWidth(150);

        cardTitle.textProperty().addListener((obs, old, newValue) -> {
            card.setTitle(newValue);
        });
        cardText.textProperty().addListener((obs, old, newValue) -> {
            card.setText(newValue);
        });
        cardImage.textProperty().addListener((obs, old, newValue) -> {
            card.setImageFile(newValue);
        });

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            categoryGroups.get(groupIndex).getCategories().get(categoryIndex).getCards().remove(cardIndex);
            updateUI();
        });

        cardBox.getChildren().addAll(cardTitle, cardText, cardImage, deleteButton);
        return cardBox;
    }

    private void saveChanges(ActionEvent event) {
        try {
            String jsonString = mapper.writeValueAsString(categoryGroups);
            File file = getCategoryFile();
            try(FileWriter writer = new FileWriter(file)) {
                writer.write(jsonString);
                writer.flush();
            }
            showAlert("Changes saved successfully!");
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

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
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
