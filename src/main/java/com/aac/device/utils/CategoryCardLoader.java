package com.aac.device.utils;

import com.aac.device.model.CategoryGroup;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.URISyntaxException;
import java.util.List;


public class CategoryCardLoader {
    private static String CATEGORY_FILE = "category_card.json";
    public static List<CategoryGroup> loadCategories() throws Exception {
        String categoryJson = getJsonOfCategories(); // stores JSON content in String
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // ignores extraneous JSON fields
        return objectMapper.readValue(categoryJson, new TypeReference<List<CategoryGroup>>(){}); // converts string into list of CategoryGroup objects
    }

    public static void saveCategories(List<CategoryGroup> categoryGroups) throws IOException, URISyntaxException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(categoryGroups);
        File file = getCategoryFile();
        try(FileWriter writer = new FileWriter(file)) {
            writer.write(jsonString);
            writer.flush();
        }
    }

    /*private void loadCategory(GridPane categoryGroupPane, CategoryGroup categoryGroup) {
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
    } */

    private static String getJsonOfCategories() throws IOException, URISyntaxException {
        File file = getCategoryFile();
        if(!file.exists()) {
            System.out.println(String.format("File %s does not exists", file.getPath()));
            return "{}";
        }
        InputStream inStream = new FileInputStream(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(inStream));
        StringBuilder stringBuilder = new StringBuilder();
        String sCurrentLine;
        while ((sCurrentLine = br.readLine()) != null) // reads file
        {
            stringBuilder.append(sCurrentLine).append("\n"); // new line
        }
        return stringBuilder.toString(); // returns JSON's content as string
    }

    private static File getCategoryFile() throws URISyntaxException {
        String directory = CategoryCardLoader.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        String filePath = directory + CATEGORY_FILE;
        return new File(filePath);
    }
}
