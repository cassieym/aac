//package com.aac.device;
//
//import com.aac.device.model.CategoryGroup;
//import com.aac.device.model.Category;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Node;
//import javafx.scene.Scene;
//import javafx.scene.control.Button;
//import javafx.scene.control.ListView;
//import javafx.scene.control.TextField;
//import javafx.scene.image.ImageView;
//import javafx.scene.input.MouseEvent;
//import javafx.scene.layout.HBox;
//import javafx.scene.control.Label;
//import javafx.scene.layout.Priority;
//import javafx.stage.Stage;
//
//import java.io.IOException;
//import java.util.List;
//
//public class SettingsController {
//    private MainComponent mainComponent;
//    @FXML
//    private ListView cardsListView;
//    @FXML
//    private ListView categoryListView;
//
//
//    public void setMainComponent(MainComponent mainComponent) {
//        this.mainComponent = mainComponent;
//    }
//
//    @FXML
//    private void initialize() {
//        cardsListView = new ListView<>();
//        categoryListView = new ListView<>();
//
////        loadCategories();
//    }
//
//    private void loadCategories() {
//        List<CategoryGroup> categoryGroups = this.mainComponent.getCategoryGroups();
//
//        for (CategoryGroup group : categoryGroups) {
//            for (Category category : group.getCategories()) {
//                HBox categoryDisplay = categoryBox(category);
//                categoryListView.getItems().add(categoryBox(category));
//            }
//        }
//    }
//
//    private HBox categoryBox(Category category) {
//        TextField title = new TextField(category.getTitle());
//        Button button = new Button();
//
//        ImageView view = new ImageView(category.getCellImage());
//        button.setGraphic(view);
//
//        HBox categoryBox = new HBox(button, title);
//        return categoryBox;
//    }
//
////    public void clickSaveButton(MouseEvent f){
////        AacApplication.;
////        ((Node)(f.getSource())).getScene().getWindow().hide();
////    }
//
//
//}
//
//// load all components (private ...)
//// start method never used
