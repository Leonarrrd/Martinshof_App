package com.example.betreuer.model;

import java.util.List;

public class Tutorial {
    String title;
    int steps;
    List<String> subheadings;
    List<String> stepDescriptions;
    List<String> stepImagePaths;

    public Tutorial(String title) {
        this.title = title;
        init();
    }

    private void init(){

    }
}
