package com.example.betreuer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;


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

    public static Bitmap[] getFirstImageOfEachTutorial() {
        List<Bitmap> imageList = new ArrayList<>();
        for (String dir : getTutorialNamesFromStorage()){
            String pathToImage = sdcard + "/Martinshof/" + dir + "/step1.jpg";
            Bitmap image = BitmapFactory.decodeFile(pathToImage);
            imageList.add(image);
        }
        Bitmap[] imageArray = imageList.toArray(new Bitmap[imageList.size()]);
        return imageArray;
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

    public static void writeTextToStorage(File directory, String subheader, String desc, int stepNumber){
        try {
            File file = new File(directory, "descs.txt");
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            StringBuilder sb = new StringBuilder();
            int i = 0;
            while ((line = br.readLine()) != null) {
                if(i!=stepNumber-1){
                    sb.append(line);
                } else {
                    sb.append(subheader);
                    sb.append("§");
                    sb.append(desc);
                }
                sb.append("\n");
                i++;
            }
            br.close();
            System.out.println(sb.toString());
            FileWriter writer = new FileWriter(file,false);
            writer.write(sb.toString());
            writer.flush();
            writer.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void writeImageToStorage(String directory, int step,  Bitmap bitmap){
        File root = new File(app_root, directory);
        String fileName = String.format("step" + step + ".jpg", System.currentTimeMillis());
        FileOutputStream outStream = null;
        File outFile = new File(root, fileName);
        try {
            outStream = new FileOutputStream(outFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, outStream);
            outStream.flush();
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createSubDirectory(File directory){
        if (!directory.exists()){
            directory.mkdirs();
        }
    }

    public static List<String> getSubheadingsFromDirectory(String directory){
        List<String> subHeadings = new ArrayList<>();
        try {
            File root = new File(app_root, directory);
            BufferedReader br = new BufferedReader(new FileReader(root + "/descs.txt"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splits = line.split("§");
                subHeadings.add(splits[0]);
            }
            br.close();
        }
        catch (IOException e) {
        }
        return subHeadings;
    }

    public static List<String> getDescsFromDirectory(String directory){
        List<String> descs = new ArrayList<>();
        try {
            File root = new File(app_root, directory);
            BufferedReader br = new BufferedReader(new FileReader(root + "/descs.txt"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splits = line.split("§");
                String desc = splits[1].replace("\\n", System.getProperty("line.separator"));
                descs.add(desc);
            }
            br.close();
        }
        catch (IOException e) {
        }
        return descs;
    }

    public static List<Bitmap> getImagesFromDirectory(String directory){
        File root = new File(app_root, directory);

        List<Bitmap> images = new ArrayList<>();

        int i = 1;
        while (true){
            String pathToImage = root + "/step" + i + ".jpg";
            if ((new File (pathToImage)).exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(pathToImage);
                images.add(bitmap);
            } else {
                break;
            }
            i++;
        }
        return images;
    }

    public static void deleteDirectory(String directory){
        Environment.getExternalStorageState();
        File root = new File(app_root + "/" + directory);
        deleteRecursive(root);
    }

    private static void deleteRecursive(File root) {
        if (root.isDirectory())
            for (File child : root.listFiles())
                deleteRecursive(child);
        root.delete();
    }
}
