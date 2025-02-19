package com.aac.device.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.scene.image.Image;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

@Data
public class GridCell {
    private String title;
    private String imageFile;
    private int rowIndex;
    private int columnIndex;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @JsonIgnore
    private Image image;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @JsonIgnore
    private static Image defaultImage;
    private static final String IMAGE_DIRECTORY = "/local_images";

    @JsonIgnore
    public Image getCellImage(String cellTitle) {
        if(image == null) {
            if(imageFile == null || imageFile.trim() == "") {
                return null;
            }
            try {
                InputStream inputStream = this.getClass().getResourceAsStream(imageFile);
                if(inputStream != null) {  // image file
                    image = new Image(inputStream);
                }
                else {
                    File file = new File(imageFile);
                    if(file.exists()) {
                        // this is image in local file system
                        inputStream = new FileInputStream(file);
                        image = new Image(inputStream);
                    }
                    else { // online image
                        // first check whether there already is a local copy of the online image
                        String imageDirPath = getLocalImageFileDirectory();
                        String cleanedCellTitle = cellTitle.replaceAll(" ", "_").toLowerCase();
                        String localImageFile = imageDirPath + File.separator + cleanedCellTitle + ".png";
                        file = new File(localImageFile);
                        if(file.exists()) {
                            inputStream = new FileInputStream(file);
                            image = new Image(inputStream);
                        }
                        else { // local copy doesn't exist
                            // Download the online image
                            URL url = new URL(this.imageFile);
                            URLConnection conn = url.openConnection();
                            InputStream in = conn.getInputStream();
                            image = new Image(in);

                            // Save the online image to local file
                            saveImage(imageFile, localImageFile);
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                image = getDefaultImage();
            }
        }
        return image;
    }

    private void saveImage(String imageUrl, String destinationFile) throws IOException {
        try (InputStream is = new URL(imageUrl).openStream();
             OutputStream os = new FileOutputStream(destinationFile)) {

            byte[] b = new byte[8192];
            int length;

            while ((length = is.read(b)) != -1) {
                os.write(b, 0, length);
            }
        }
    }

    private String getLocalImageFileDirectory() {
        // Get the project root directory
        String projectRoot = System.getProperty("user.dir");

        // Create full path for the images directory
        String imageDirPath = projectRoot + IMAGE_DIRECTORY;

        // Create directory if it doesn't exist
        File directory = new File(imageDirPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return imageDirPath;
    }


    // Default image for cells with invalid URL/image file
    private Image getDefaultImage() {
        if(defaultImage != null) {
            return defaultImage;
        }

        try {
            InputStream inputStream = this.getClass().getResourceAsStream("/images/default.png");
            if(inputStream != null) {
                defaultImage = new Image(inputStream);
            }
        }
        catch(Exception e) {
        }
        return defaultImage;
    }
}
