package com.example.betreuer.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.Environment;


import com.example.betreuer.R;
import com.example.betreuer.model.Step;
import com.example.betreuer.model.Tutorial;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IOHelper {
    private static File sdcard = Environment.getExternalStorageDirectory();
    private static File app_root = new File (sdcard + "/Martinshof");

    public static void createDirectory() {
        File f = new File(sdcard, "Martinshof");
        if (!f.exists()) {
            f.mkdirs();
        }
    }

    public static List<String> getTutorialNamesFromStorage(){
        List<String> names = new ArrayList<>();
        File root = new File(sdcard + "/Martinshof");

        String[] directories = root.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });
        return Arrays.asList(directories);
    }

    public static void writeTextToStorage(File directory, String subheader, String desc){
        try {
            if (!directory.exists()){
                directory.mkdir();
            }
            File newFile = new File(directory, "descs.txt");
            FileWriter writer = new FileWriter(newFile,true);
            writer.append(subheader);
            writer.append("§");
            writer.append(desc);
            writer.append("\n");
            writer.flush();
            writer.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void writeImageToStorage(File directory, int step,  Bitmap bitmap){
        String fileName = String.format("step" + step + ".jpg", System.currentTimeMillis());
        FileOutputStream outStream = null;
        File outFile = new File(directory, fileName);
        try {
            outStream = new FileOutputStream(outFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, outStream);
            outStream.flush();
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteRecursive(File root) {
        if (root.isDirectory())
            for (File child : root.listFiles())
                deleteRecursive(child);
        root.delete();
    }

    public static void deleteTutorial(String tutorialTitle){
        deleteRecursive(new File(app_root, tutorialTitle));
    }

    public static List<Tutorial> getTutorials() {
        List<Tutorial> tuts = new ArrayList<>();
        for (String title : getTutorialNamesFromStorage()){
            Tutorial tut = new Tutorial(title);
            try {
                File root = new File(app_root, title);
                BufferedReader br = new BufferedReader(new FileReader(root + "/descs.txt"));
                int i = 1;
                String line;
                while ((line = br.readLine()) != null) {
                    String[] splits = line.split("§");
                    String subheading = splits[0];
                    String desc = splits[1].replace("\\n", System.getProperty("line.separator"));
                    String pathToImage = root + "/step" + i + ".jpg";
                    tut.addStep(new Step(subheading, desc, pathToImage));
                    i++;
                }
                br.close();
            }
            catch (IOException e) {
                // UIHelper.reset();
            }
            // don't add tutorial if descs.txt wasn't found or if descs.txt is empty
            if (tut.getTotalSteps()>0) {
                tuts.add(tut);
            }
        }
        return tuts;
    }

    public static Bitmap getImageFromPath(String pathToImage) {
        File f = new File (pathToImage);
        if (f.exists()) {
            return BitmapFactory.decodeFile(pathToImage);
        } else {
            return BitmapFactory.decodeResource(GlobalContext.getAppContext().getResources(),R.drawable.image_not_found);
        }
    }

    public static void writeTutorialToStorage(Tutorial tutorial) {
        File directory = new File(app_root, tutorial.getTitle());
        if (directory.exists()) {
            deleteRecursive(directory);
        }
        directory.mkdirs();
        int i = 1;
        for (Step step : tutorial.getSteps()){
            writeTextToStorage(directory, step.getSubheading(), step.getDescription());
            writeImageToStorage(directory, i, step.getImage());
            i++;
        }
    }
}
