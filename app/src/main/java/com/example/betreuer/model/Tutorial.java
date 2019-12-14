package com.example.betreuer.model;

import android.graphics.Bitmap;

import com.example.betreuer.helper.IOHelper;

import java.util.ArrayList;
import java.util.List;

public class Tutorial {
    private String title;
    private List<Step> steps = new ArrayList<>();

    public Tutorial(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Step> getSteps(){
        return steps;
    }

    public int getTotalSteps(){
        return steps.size();
    }

    public void addStep(Step step){
        steps.add(step);
    }

    public void removeStep(Step step){
        steps.remove(step);
    }

    public List<String> getSubheadings(){
        List<String> subHeadings = new ArrayList<>();
        for (Step step : steps){
            subHeadings.add(step.getSubheading());
        }
        return  subHeadings;
    }

    public List<String> getDescriptions(){
        List<String> descriptions = new ArrayList<>();
        for (Step step : steps){
            descriptions.add(step.getDescription());
        }
        return descriptions;
    }

    public List<Bitmap> getImages(){
        List<Bitmap> bitmaps = new ArrayList<>();
        for (Step step : steps){
            if (step.getImage() == null) {
                bitmaps.add(IOHelper.getImageFromPath(step.getPathToImage()));
            } else {
                bitmaps.add(step.getImage());
            }
        }
        return  bitmaps;
    }

    public List<Bitmap> getThumbnails(){
        List<Bitmap> bitmaps = getImages();
        List<Bitmap> thumbnails = new ArrayList<>();
        for (Bitmap bm : bitmaps){
            thumbnails.add(Bitmap.createScaledBitmap(bm, 100, 100, true));
        }
        return thumbnails;
    }

    @Override
    public String toString(){
        return title + ": "  + getTotalSteps() + " steps";
    }

    public void cacheImages() {
        for (Step step : steps){
            if (step.getImage() == null) {
                step.setImage(IOHelper.getImageFromPath(step.getPathToImage()));
            }
        }
    }
}
