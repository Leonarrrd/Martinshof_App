package com.example.betreuer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

public class SliderAdapter extends PagerAdapter {

    private String tutorialName;
    private int pages;
    private List<String> slide_subHeadings = new ArrayList<>();
    private List<String> slide_descs = new ArrayList<>();
    private List<Bitmap> slide_images = new ArrayList<>();
    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context, String tutorialName){
        this.context = context;
        this.tutorialName = tutorialName;
        init();
    }

    private void init(){
        slide_subHeadings = IOHelper.getSubheadingsFromDirectory(tutorialName);
        slide_descs = IOHelper.getDescsFromDirectory(tutorialName);
        slide_images = IOHelper.getImagesFromDirectory(tutorialName);
        pages = slide_subHeadings.size();
    }

    @Override
    public int getCount() {
        return slide_subHeadings.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view ==  object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout, container, false);

        ImageView slideImageView = (ImageView) view.findViewById(R.id.slide_image);
        TextView slideSubeading = (TextView) view.findViewById(R.id.slide_subheader);
        TextView slideDescription = (TextView) view.findViewById(R.id.slide_descs);

        slideImageView.setImageBitmap(slide_images.get(position));
        slideSubeading.setText(slide_subHeadings.get(position));
        slideDescription.setText(slide_descs.get(position));

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout)object);
    }

    public int getPages(){
        return pages;
    }
}
