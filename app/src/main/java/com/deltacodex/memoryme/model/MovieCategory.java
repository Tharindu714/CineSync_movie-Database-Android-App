package com.deltacodex.memoryme.model;

public class MovieCategory {
    private String name;
    private int imageResId; // Resource ID for image

    public MovieCategory(String name, int imageResId) {
        this.name = name;
        this.imageResId = imageResId;
    }

    public String getName() { return name; }
    public int getImageResId() { return imageResId; }
}

