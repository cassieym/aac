package com.aac.device.model;

import javafx.scene.image.Image;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.FileInputStream;


@Data
public class GridCell {
    private String title;
    private String imageFile;
    private int rowIndex;
    private int columnIndex;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Image image;

    public Image getCellImage() {
        Image image = null;
        if(image == null) {
            if(imageFile == null || imageFile.trim() == "") {
                return null;
            }
            try {
                FileInputStream imageInputStream = new FileInputStream(this.getClass().getResource(imageFile).getPath());
                image = new Image(imageInputStream);
            }
            catch(Exception ex) {
            }
        }

        return image;
    }
}
