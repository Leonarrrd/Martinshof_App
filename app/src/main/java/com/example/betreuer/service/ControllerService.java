package com.example.betreuer.service;

import android.graphics.Bitmap;

import com.example.betreuer.helper.FormatHelper;
import com.example.betreuer.helper.IOHelper;
import com.example.betreuer.model.Tutorial;

import java.util.ArrayList;
import java.util.List;

public class ControllerService {
    private static ControllerService instance;
    private List<Tutorial> tutorials;
    private List<String> titles;
    private List<Bitmap> thumbnails;

    public static ControllerService getInstance(){
        if (instance == null){
            instance = new ControllerService();
            instance.update();
        }
        return instance;
    }

    public void update(){
        tutorials = IOHelper.getTutorials();
        titles = new ArrayList<>();
        thumbnails = new ArrayList<>();
        for (Tutorial tut : tutorials) {
            titles.add(tut.getTitle());
            Bitmap bm = IOHelper.getImageFromPath(tut.getSteps().get(0).getPathToImage());
            thumbnails.add(FormatHelper.getThumbnail(bm));
        }
    }

    public List<Tutorial> getTutorials() {
        return tutorials;
    }

    public List<String> getTitles() {
        return titles;
    }

    public List<Bitmap> getThumbnails() {
        return thumbnails;
    }

    public Tutorial getTutorial(String tutorialName){
        for (Tutorial tut : tutorials){
            if (tut.getTitle().equals(tutorialName)){
                return tut;
            }
        }
        // TODO: TutorialNotFoundException?
        return null;
    }

    public void deleteTutorial(String tutorialTitle) {
        if (!titles.contains(tutorialTitle)) {
            // TODO: TutorialNotFoundException?
        } else {
            IOHelper.deleteTutorial(tutorialTitle);
        }
        update();
    }
}
