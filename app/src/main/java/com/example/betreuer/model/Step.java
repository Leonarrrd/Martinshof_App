package com.example.betreuer.model;

import android.graphics.Bitmap;

public class Step {
    private String subheading;
    private String description;
    private String pathToImage;
    private Bitmap image;

    public Step(String subheading, String description, String pathToImage) {
        this.subheading = subheading;
        this.description = description;
        this.pathToImage = pathToImage;
    }

    public Step(String subheading, String description, Bitmap image) {
        this.subheading = subheading;
        this.description = description;
        this.image = image;
    }

    public String getSubheading() {
        return subheading;
    }

    public void setSubheading(String subheading) {
        this.subheading = subheading;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPathToImage() {
        return pathToImage;
    }

    public void setPathToImage(String pathToImage) {
        this.pathToImage = pathToImage;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
