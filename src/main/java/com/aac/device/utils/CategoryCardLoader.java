package com.aac.device.utils;

import com.aac.device.model.CategoryGroup;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


public class CategoryCardLoader {
    private static final String CATEGORY_FILE = "category_card.json";
    
    public static List<CategoryGroup> loadCategories() throws Exception {
        String categoryJson = getJsonOfCategories(); // stores JSON content in String
        if(categoryJson == null) {
            categoryJson = getDefaultJsonOfCategories();
        }
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // ignores extraneous JSON fields
        return objectMapper.readValue(categoryJson, new TypeReference<List<CategoryGroup>>(){}); // converts string into list of CategoryGroup objects
    }

    public static void saveCategories(List<CategoryGroup> categoryGroups) throws IOException, URISyntaxException {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = getCategoryFile();
        objectMapper.writeValue(file, categoryGroups);
    }

    private static String getJsonOfCategories() throws IOException, URISyntaxException {
        File file = getCategoryFile();
        if(!file.exists()) {
            System.out.printf("File %s does not exists%n", file.getPath());
            return null;
        }
        InputStream inStream = new FileInputStream(file);
        return getTextFromJsonFile(inStream);
    }

    private static File getCategoryFile() throws URISyntaxException {
        String directory = System.getProperty("user.dir");;
        String filePath = directory + "/" + CATEGORY_FILE;
        return new File(filePath);
    }

    private static String getDefaultJsonOfCategories() throws IOException {
        InputStream inputStream = CategoryCardLoader.class.getResourceAsStream("/category_card.json");
        return getTextFromJsonFile(inputStream);
    }

    private static String getTextFromJsonFile(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String sCurrentLine;
        while ((sCurrentLine = br.readLine()) != null) // reads file
        {
            stringBuilder.append(sCurrentLine).append("\n"); // new line
        }
        return stringBuilder.toString(); // returns JSON's content as string
    }
}
