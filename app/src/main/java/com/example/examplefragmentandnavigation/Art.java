package com.example.examplefragmentandnavigation;

import android.graphics.Bitmap;

public class Art {
    int id;
    String name;
    String year;
    Bitmap image;

    public Art(int id, String name, String year, Bitmap image) {
        this.id = id;
        this.name = name;
        this.year = year;
        this.image = image;
    }
}
