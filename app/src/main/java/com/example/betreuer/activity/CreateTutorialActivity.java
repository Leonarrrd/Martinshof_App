package com.example.betreuer.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.betreuer.helper.IOHelper;
import com.example.betreuer.R;
import com.example.betreuer.helper.UIHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateTutorialActivity extends AppCompatActivity {

    private String m_tutorialName = "";
    private ListView listView;
    private int totalSteps = 0;
    private boolean created = false;

    // TODO: this is some hot garbage,
    // should use startActivityForResult() for this i guess
    public static CreateTutorialActivity ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO: There might have to be some consideration here that onResume() won't always fire
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_creation);
        listView = findViewById(R.id.listview);
        if (getIntent().getStringExtra("tutorialName") == null){
            openStringInputDialog("Nicht zu lang, nicht zu kurz, darf nicht schon vorhanden sein");
        } else {
            m_tutorialName = getIntent().getStringExtra("tutorialName");
            created = true;
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openEditStepActivity(view);
            }
        });

        ctx = this;
    }

    @Override
    public void onResume(){
        super.onResume();
        ((TextView)findViewById(R.id.title)).setText(m_tutorialName);
        if (created) {
            String[] titles = getTitles();
            Bitmap[] images = getImages();
            MyAdapter adapter = new MyAdapter(this, titles, images);
            listView = findViewById(R.id.listview);
            listView.setAdapter(adapter);
        } else {
            created = true;
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        // Potentially problematic since this deletes the directory when we open CreateNewStep
        if (listView.getChildCount() < 1){
            IOHelper.deleteDirectory(m_tutorialName);
        }
    }

    private void openStringInputDialog(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Name der Anleitung:");

        // set the custom layout (layout from xml)
        final View customLayout = getLayoutInflater().inflate(R.layout.custom_layout, null);
        builder.setView(customLayout);
        TextView textMessage = customLayout.findViewById(R.id.text);
        textMessage.setText(message);
        final EditText editText = customLayout.findViewById(R.id.editText);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        // TODO: Improve Layout of EditText (centering, etc.)

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String s = editText.getText().toString();
                boolean tooLong = s.length() > 15;
                boolean tooShort = s.length() < 2;
                boolean alreadyExists = IOHelper.getTutorialNamesFromStorage().contains(s);
                if (tooLong){
                    dialog.cancel();
                    openStringInputDialog("Fehler: Name ist zu lang.");
                } else if (tooShort){
                    dialog.cancel();
                    openStringInputDialog("Fehler: Name ist zu kurz.");
                } else if (alreadyExists){
                    dialog.cancel();
                    openStringInputDialog("Fehler: Name existiert bereits.");
                } else {
                    m_tutorialName = editText.getText().toString();
                    createSubDirectory();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                startActivity(new Intent(getBaseContext(), MainActivity.class));
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void openCreateStepActivity(View view){
        Intent intent = new Intent(this, CreateNewStepActivity.class);
        intent.putExtra("title", m_tutorialName);
        intent.putExtra("stepNr", ctx.getTotalSteps()+1);
        intent.putExtra("new", true);
        startActivity(intent);
    }

    public void openEditStepActivity(View view){
        int step = (Integer) view.getTag();
        Intent intent = new Intent(this, CreateNewStepActivity.class);
        intent.putExtra("title", m_tutorialName);
        intent.putExtra("stepNr", step);
        intent.putExtra("new", false);
        startActivity(intent);
    }

    public void createSubDirectory(){
        File directory = new File(Environment.getExternalStorageDirectory() + "/Martinshof" , m_tutorialName);
        IOHelper.createSubDirectory(directory);
        System.out.println("created");
    }

    public String[] getTitles() {
        List<String> titleList = new ArrayList<>();
        File sdcard = Environment.getExternalStorageDirectory();
        File descs = new File(sdcard + "/Martinshof/" + m_tutorialName, "descs.txt");
        try {
            BufferedReader br = new BufferedReader(new FileReader(descs));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splits = line.split("§");
                titleList.add(splits[0]);
            }
            br.close();
        }
        catch (IOException e) {
        }

        String[] titleArray = titleList.toArray(new String[titleList.size()]);

        return titleArray;
    }

    // TODO: probably want to get rid of this
    private Bitmap[] getImages() {
        List<Bitmap> imageList = IOHelper.getImagesFromDirectory(m_tutorialName);
        Bitmap[] imageArray = imageList.toArray(new Bitmap[imageList.size()]);
        return imageArray;
    }

    public int getTotalSteps() {
        return totalSteps;
    }

    public void increaseTotalSteps() {
        this.totalSteps++;
    }

    public void finish(View view){
        if (listView.getChildCount() < 1){
            IOHelper.deleteDirectory(m_tutorialName);
        }
        finish();
    }

    public void openTutorial(View view){
        if (listView.getChildCount() < 1){
            UIHelper.showErrorDialog(this,"Bitte erstelle mindestens einen Schritt");
            return;
        }
        Intent intent = new Intent(this, ViewTutorialActivity.class);
        String tagString = (String) view.getTag();
        intent.putExtra("tutorial", m_tutorialName);
        intent.putExtra("title", m_tutorialName);
        startActivity(intent);
    }

    class MyAdapter extends ArrayAdapter<String> {
        Context context;
        String[] titles;
        Bitmap[] images;

        MyAdapter(Context c, String[] titles, Bitmap[] images) {
            super(c, R.layout.row, R.id.textView1, titles);
            this.context = c;
            this.titles = titles;
            this.images = images;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.row, parent, false);
            row.setTag(position);
            ImageView myImage = row.findViewById(R.id.imageView);
            TextView myTitle = row.findViewById(R.id.textView1);
            myImage.setImageBitmap(images[position]);
            myTitle.setText(titles[position]);
            return row;
        }
    }
}