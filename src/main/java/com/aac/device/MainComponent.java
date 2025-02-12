package com.aac.device;

import com.aac.device.model.Card;
import com.aac.device.model.Category;
import com.aac.device.model.CategoryGroup;
import com.aac.device.model.GridCell;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Timer;

public class MainComponent {
    private AacController aacController;
    private Stage stage;

    private int categoryGroupGridRows;
    private int categoryGroupGridColumns;
    private int categoryGridRows;
    private int categoryGridColumns;
    private int cardGridRows;
    private int cardGridColumns;

    private static final String CELL_STYLE = "-fx-background-color: white; -fx-border-color: grey; -fx-border-width: 2; -fx-alignment: center;";
    private static final long MULTI_CLICK_MAX_INTERVAL = 800; // double click timer in ms

    private Node activeNode = null;

    private String activeNodeOriginalStyle = null;

    private int currentCategoryGroupIndex = 0;
    private int currentCategoryIndex = 0;
    private int currentCardIndex = 0;

    private NavigationLevel navigationLevel = NavigationLevel.CATEGORY_GROUP;

    private List<CategoryGroup> categoryGroups;
    private boolean enableVoice = true;
    private int clickCount = 0;
    private long lastClickTime = 0;

    private Timer timer;
    private Timeline clickTimeline;

    public MainComponent(AacController aacController, Stage stage) {
        this.aacController = aacController;
        this.stage = stage;
    }

    public void onKeyReleased(KeyEvent event) {
        long currentTime = System.currentTimeMillis();

        if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT) {
            if (currentTime - lastClickTime > MULTI_CLICK_MAX_INTERVAL) {  // reset click count if time between clicks exceeds 800ms
                clickCount = 0;
            }

            clickCount++;
            lastClickTime = currentTime;

            // cancel any previously scheduled timeline to avoid premature single click execution
            if (clickTimeline != null) {
                clickTimeline.stop();
            }

            // delay processing for full click sequence (single, double, or triple)
            clickTimeline = new Timeline(new KeyFrame(Duration.millis(MULTI_CLICK_MAX_INTERVAL), e -> {
                if (clickCount == 1) {
                    if (event.getCode() == KeyCode.RIGHT) {
                        moveRight();  // Single right click
                    } else {
                        moveLeft();  // Single left click
                    }
                } else if (clickCount == 2) {
                    if (event.getCode() == KeyCode.RIGHT) {
                        selectCurrentCell();  // Double right click - select
                    }
                    else if (event.getCode() == KeyCode.LEFT) {
                        moveUp(); // Double left click - move up hierarchy
                    }
                } else if (clickCount == 3) {
                    clearText();  // Triple click
                }
                clickCount = 0; // Reset after handling the action
            }));
            clickTimeline.setCycleCount(1);
            clickTimeline.play(); // executes action after waiting 800ms
        }
//        cancelTimer();
//        if(event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT) {
//            // check for a double click event (same key pressed twice within a specific time frame)
//            if(this.previousKeyCode == event.getCode() && (System.currentTimeMillis() - this.previousReleaseKeyTime) < DOUBLE_CLICK_MAX_INTERVAL) {
//                //reset previous key event
//                this.previousKeyCode = KeyCode.ESCAPE;
//                if (event.getCode() == KeyCode.RIGHT)  // right key double click
//                    selectCurrentCell();
//                else
//                    moveUp();   // left key double click
//
//            }
//            else {
//                this.previousKeyCode = event.getCode();
//                timer = new Timer();
//                timer.scheduleAtFixedRate(new TimerTask() {
//                    @Override
//                    public void run() {
//                        Platform.runLater(() -> {
//                            if(event.getCode() == KeyCode.RIGHT ) {  // right key single click
//                                moveRight();
//                            }
//                            else {
//                                moveLeft();   // left key single click
//                            }
//                        });
//                    }
//                }, DOUBLE_CLICK_MAX_INTERVAL, 300000);
//            }
//        }
//        else {
//            this.previousKeyCode = event.getCode();
//        }
//
//        this.previousReleaseKeyTime = System.currentTimeMillis();
    }

    private void clearText() {
        this.aacController.setDisplayText();
    }

    public void load() throws Exception {
        aacController.setDisplayText("");
        //setup voices https://stackoverflow.com/questions/12684627/freetts-unable-to-find-any-voice
        System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");

        GridPane categoryGroupGridPane = this.aacController.getLeftGridPane();
        this.categoryGroupGridRows = categoryGroupGridPane.getRowCount();
        this.categoryGroupGridColumns = categoryGroupGridPane.getColumnCount();

        GridPane categoryGridPane =(GridPane) categoryGroupGridPane.getChildren().get(0);
        this.categoryGridRows = categoryGridPane.getRowCount();
        this.categoryGridColumns = categoryGridPane.getColumnCount();


        Pane rightPane = this.aacController.getRightPane();
        GridPane cardGridPane =(GridPane) rightPane.getChildren().get(0);
        this.cardGridRows = cardGridPane.getRowCount();
        this.cardGridColumns = cardGridPane.getColumnCount();

        categoryGroups = loadCategories();

        // populate grid
        for(int i = 0; i < categoryGroupGridRows; i++) {
            for(int j = 0; j < categoryGroupGridColumns; j++) {
                int idx = i * categoryGroupGridRows + j;   // get index of node
                Node node = getChildNode(categoryGroupGridPane, i, j);
                if(idx < categoryGroups.size()) {
                    CategoryGroup categoryGroup = categoryGroups.get(idx);
                    categoryGroup.setRowIndex(i);
                    categoryGroup.setColumnIndex(j);
                    loadCategory((GridPane)node, categoryGroup);
                    node.setVisible(true);
                }
                else {
                    node.setVisible(false);
                }
            }
        }
        refresh(true);
    }

    public void close() {
        // alert box
    }

    private void moveRight() {
        cancelTimer();
        if(this.navigationLevel == NavigationLevel.CATEGORY_GROUP) {  // navigationLevel == NavigationLevel.CATEGORY_GROUP
            if(this.categoryGroups.size() > 1) {
                if(this.currentCategoryGroupIndex < this.categoryGroups.size() - 1) { // index starts at 0
                    this.currentCategoryGroupIndex++;
                }
                else {
                    this.currentCategoryGroupIndex = 0;  // last category_group returns to first (wrap)
                }
                // Reset
                this.currentCategoryIndex = 0;
                this.currentCardIndex = 0;
                this.refresh(true); // refresh UI
            }
        }
        else { // navigationLevel == NavigationLevel.CATEGORY
            CategoryGroup categoryGroup = this.categoryGroups.get(this.currentCategoryGroupIndex);
            List<Category> categories = categoryGroup.getCategories();
            if(this.navigationLevel == NavigationLevel.CATEGORY) {
                if(categories.size() > 1) {
                    if(this.currentCategoryIndex < categories.size() -1) {
                        this.currentCategoryIndex++; // move to next category
                    }
                    else {
                        this.currentCategoryIndex = 0;  // wrap to start
                    }
                    // Reset
                    this.currentCardIndex = 0;
                    this.refresh(true); // refresh UI
                }
            }
            else { // navigationLevel == NavigationLevel.CARD
                Category category = categories.get(this.currentCategoryIndex);
                List<Card> cards = category.getCards();
                if(cards.size() > 1) {
                    if(this.currentCardIndex < cards.size() -1) {
                        this.currentCardIndex++; // move to next card
                    }
                    else {
                        this.currentCardIndex = 0; // wrap to start
                    }
                    this.refresh(false); // refresh
                }
            }
        }
    }

    private void moveLeft() {
        cancelTimer();
        if(this.navigationLevel == NavigationLevel.CATEGORY_GROUP) {
            if(this.categoryGroups.size() > 1) {
                if(this.currentCategoryGroupIndex > 0) {
                    this.currentCategoryGroupIndex--;
                }
                else {
                    this.currentCategoryGroupIndex = categoryGroups.size() -1;
                }
                // Reset
                this.currentCategoryIndex = 0;
                this.currentCardIndex = 0;
                this.refresh(true);
            }
        }
        else {
            CategoryGroup categoryGroup = this.categoryGroups.get(this.currentCategoryGroupIndex);
            List<Category> categories = categoryGroup.getCategories();
            if (this.navigationLevel == NavigationLevel.CATEGORY) {
                if (categories.size() > 1) {
                    if (this.currentCategoryIndex > 0) {
                        this.currentCategoryIndex--;
                    } else {
                        this.currentCategoryIndex = categories.size() - 1;
                    }
                    // Reset
                    this.currentCardIndex = 0;
                    this.refresh(true);
                }
            } else { // navigationLevel == NavigationLevel.CARD
                Category category = categories.get(this.currentCategoryIndex);
                List<Card> cards = category.getCards();
                if (cards.size() > 1) {
                    if (this.currentCardIndex > 0) {
                        this.currentCardIndex--;
                    } else {
                        this.currentCardIndex = cards.size() - 1;
                    }
                    this.refresh(false);
                }
            }
        }
    }

    private void selectCurrentCell() {
        if(this.navigationLevel == NavigationLevel.CATEGORY_GROUP) {
            this.navigationLevel = NavigationLevel.CATEGORY;

            this.currentCategoryIndex = 0;
            this.currentCardIndex = 0;
            this.refresh(true);
        }
        else if(this.navigationLevel == NavigationLevel.CATEGORY) {
            this.navigationLevel = NavigationLevel.CARD;

            this.currentCardIndex = 0;
            this.refresh(true);
        }
        else {
            CategoryGroup categoryGroup = this.categoryGroups.get(this.currentCategoryGroupIndex);
            List<Category> categories = categoryGroup.getCategories();
            Category category = categories.get(this.currentCategoryIndex);
            List<Card> cards = category.getCards();
            Card card = cards.get(this.currentCardIndex);
            playCard(card.getText());
        }
    }

    private void moveUp() {
        if(this.navigationLevel == NavigationLevel.CARD) {
            this.navigationLevel = NavigationLevel.CATEGORY;
            this.currentCardIndex = 0;
            this.refresh(false);
        }
        else if(this.navigationLevel == NavigationLevel.CATEGORY) {
            this.navigationLevel = NavigationLevel.CATEGORY_GROUP;
            this.currentCategoryIndex = 0;
            this.currentCardIndex = 0;
            this.refresh(true);
        }
    }

    private void playCard(String text) {
        if(text == null || text.isBlank())
            return;
        this.aacController.setDisplayText(text);
        this.playVoice(text);
    }

    private void playVoice(String text) {
        if(enableVoice) {
            try {
                VoiceManager voiceManager = VoiceManager.getInstance();
                Voice voice = voiceManager.getVoice("kevin16");
                if (voice != null) {
                    voice.allocate();
                    voice.speak(text);
                    voice.deallocate();
                }
            } catch (Exception ex) {
                //Ignore
            }
        }
    }

    private void cancelTimer() {
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void loadCategory(GridPane categoryGroupPane, CategoryGroup categoryGroup) {
        List<Category> categories = categoryGroup.getCategories();
        double cellWidth =(categoryGroupPane.getWidth() - categoryGroupPane.getPadding().getLeft() - categoryGroupPane.getPadding().getRight())/this.categoryGridColumns;
        double cellHeight =(categoryGroupPane.getHeight() - categoryGroupPane.getPadding().getTop() - categoryGroupPane.getPadding().getBottom())/this.categoryGridColumns;

        for(int i = 0; i < categoryGridRows; i++) {
            for(int j = 0; j < categoryGridColumns; j++) {
                int idx = i * categoryGroupGridRows + j;
                if(idx < categories.size()) {
                    Category category = categories.get(idx);
                    category.setRowIndex(i);
                    category.setColumnIndex(j);
                    setGridCell(categoryGroupPane, cellWidth, cellHeight, category, category.getTitle());
                }
                else {
                    break;
                }
            }
        }
    }

    private void refresh(boolean reloadCards) {
        if(reloadCards) {
            this.loadCards();
        }
        this.updateHighlight();
//        this.aacController.setDisplayText("");

    }

    private void updateHighlight() {
        Node node;
        String title;
        CategoryGroup categoryGroup = this.categoryGroups.get(currentCategoryGroupIndex);
        if(this.navigationLevel == NavigationLevel.CARD) {
            Pane rightPane = aacController.getRightPane();
            GridPane cellGridPane =(GridPane)rightPane.getChildren().get(0);

            Category category = categoryGroup.getCategories().get(currentCategoryIndex);
            List<Card> cards = category.getCards();
            Card card = cards.get(this.currentCardIndex);
            node = getChildNode(cellGridPane, card.getRowIndex(), card.getColumnIndex());
            title = card.getTitle();
        }
        else {

            GridPane categoryGroupGridPane = this.aacController.getLeftGridPane();
            if(this.navigationLevel == NavigationLevel.CATEGORY_GROUP) {
                node = getChildNode(categoryGroupGridPane, categoryGroup.getRowIndex(), categoryGroup.getColumnIndex());
                title = categoryGroup.getTitle();
            }
            else {
                List<Category> categories = categoryGroup.getCategories();
                Category category = categories.get(this.currentCategoryIndex);
                GridPane categoryGridPane =(GridPane)getChildNode(categoryGroupGridPane, categoryGroup.getRowIndex(), categoryGroup.getColumnIndex());
                node = getChildNode(categoryGridPane, category.getRowIndex(), category.getColumnIndex());
                title = category.getTitle();
            }
        }
        if(node != null) {
            this.updateBorderColor(node);
            node.requestFocus();
        }
        this.playVoice(title);
    }

    private void updateBorderColor(Node node) {
        // Restore the previous active node
        if(this.activeNode != null && this.activeNodeOriginalStyle  != null) {
            this.activeNode.setStyle(this.activeNodeOriginalStyle);
        }
        this.activeNode = node;
        this.activeNodeOriginalStyle = node.getStyle();

        String[] styles = node.getStyle().split(";");
        for(int i = 0; i < styles.length; i++) {
            String style = styles[i];
            if(style.contains("-fx-border-width")) {
                styles[i] = "-fx-border-width: 5";
            }
            if(style.contains("-fx-border-color")) {
                styles[i] = "-fx-border-color: red";
            }
        }
        String highlightStyle = String.join(";", styles) + ";";
        // In case the original style does not define -fx-border-width or -fx-border-color
        if(!highlightStyle.contains("-fx-border-width")) {
            highlightStyle += "-fx-border-width: 5;";
        }
        if(!highlightStyle.contains("-fx-border-color")) {
            highlightStyle += "-fx-border-color: red;";
        }
        node.setStyle(highlightStyle);
    }

    private void loadCards() {
        CategoryGroup categoryGroup = this.categoryGroups.get(currentCategoryGroupIndex);
        Category category = categoryGroup.getCategories().get(currentCategoryIndex);
        List<Card> cards = category.getCards();
        Pane rightPane = aacController.getRightPane();
        double cellWidth =(rightPane.getWidth() - rightPane.getPadding().getLeft() - rightPane.getPadding().getRight())/this.cardGridRows;
        double cellHeight =(rightPane.getHeight() - rightPane.getPadding().getTop() - rightPane.getPadding().getBottom())/this.cardGridColumns;
        GridPane cellGridPane =(GridPane)rightPane.getChildren().get(0);
        // Clear previous settings
        cellGridPane.getChildren().clear();
        for(int i = 0; i < cardGridRows; i++) {
            for(int j = 0; j < cardGridColumns; j++) {
                int idx = i * cardGridColumns + j;
                if(idx < cards.size()) {
                    Card card = cards.get(idx);
                    card.setRowIndex(i);
                    card.setColumnIndex(j);
                    setGridCell(cellGridPane, cellWidth, cellHeight, card, card.getText());
                }
                else {
                    break;
                }
            }
        }
    }

    private Node getChildNode(GridPane gridPane, int rowIndex, int columnIndex) {
        for(Node node : gridPane.getChildren()) {
            if(GridPane.getRowIndex(node) == rowIndex && GridPane.getColumnIndex(node) == columnIndex) {
                return node;
            }
        }
        return null;
    }

    private void setGridCell(GridPane gridPane, double cellWidth, double cellHeight, GridCell gridCell, String tooltip) {
        Image image = gridCell.getCellImage();
        if(image != null) {
            double imageWidth = image.getWidth();
            double imageHeight = image.getHeight();
            double factor = Math.min(cellWidth/imageWidth, cellHeight/ imageHeight) * 0.85;
            if (Double.isFinite(factor) && factor > 0) {
                StackPane stackPane = new StackPane();
                stackPane.setStyle(CELL_STYLE);
                ImageView imageView = new ImageView(image);
                imageView.setFitHeight(factor * imageHeight);
                imageView.setFitWidth(factor * imageWidth);
                stackPane.getChildren().add(imageView);
                if(tooltip != null && !tooltip.isBlank()) {
                    Tooltip.install(imageView, new Tooltip(tooltip.trim()));
                }
                gridPane.add(stackPane, gridCell.getColumnIndex(), gridCell.getRowIndex());
            }
        }
    }

    private List<CategoryGroup> loadCategories() throws Exception {
        String categoryJson = getJsonOfCategories(); // stores JSON content in String
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // ignores extraneous JSON fields
        return objectMapper.readValue(categoryJson, new TypeReference<List<CategoryGroup>>(){}); // converts string into list of CategoryGroup objects
    }

    private String getJsonOfCategories() throws IOException {
        InputStream inStream = this.getClass().getResourceAsStream("/category_card.json"); // load JSON
        BufferedReader br = new BufferedReader(new InputStreamReader(inStream));
        StringBuilder stringBuilder = new StringBuilder();
        String sCurrentLine;
        while ((sCurrentLine = br.readLine()) != null) // reads file
        {
            stringBuilder.append(sCurrentLine).append("\n"); // new line
        }
        return stringBuilder.toString(); // returns JSON's content as string
    }

}
