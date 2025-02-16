package com.aac.device.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.scene.image.Image;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.FileInputStream;
import java.io.InputStream;
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

    @JsonIgnore
    public Image getCellImage() {
        if(image == null) {
            if(imageFile == null || imageFile.trim() == "") {
                return null;
            }
            try {
                if(isResourceFile()) {
                    FileInputStream imageInputStream = new FileInputStream(this.getClass().getResource(imageFile).getPath());
                    image = new Image(imageInputStream);
                }
                else {
                    URL url = new URL(this.imageFile);
                    URLConnection conn = url.openConnection();
                    InputStream in = conn.getInputStream();
                    image = new Image(in);
                }
            }
            catch(Exception ex) {
                image = getDefaultImage();
            }
        }
        return image;
    }
    
    // Check if image file is a resource file
    private boolean isResourceFile() {
        URL url = this.getClass().getResource(imageFile);
        return url != null;
    }

    // Default image for cells with invalid URL/image file
    private Image getDefaultImage() {
        if(defaultImage != null) {
            return defaultImage;
        }

        try {
            FileInputStream imageInputStream = new FileInputStream(this.getClass().getResource("/images/default.png").getPath());
            defaultImage = new Image(imageInputStream);
        }
        catch(Exception e) {
        }
        return defaultImage;
    }
}
