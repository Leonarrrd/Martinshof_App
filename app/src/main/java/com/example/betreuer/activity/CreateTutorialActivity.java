package com.example.betreuer.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.betreuer.R;
import com.example.betreuer.helper.ShareHelper;
import com.example.betreuer.helper.UIHelper;
import com.example.betreuer.helper.IOHelper;
import com.example.betreuer.model.Tutorial;
import com.example.betreuer.service.ControllerService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateTutorialActivity extends AppCompatActivity {

    public Tutorial tutorial; // see problem below
    private ListView listView;
    private int totalSteps = 0;
    private boolean created = false;
    private ControllerService cs;
    private ListAdapter adapter;


    // TODO: this is some hot garbage,
    // should use startActivityForResult() for this i guess
    public static CreateTutorialActivity ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO: There might have to be some consideration here that onResume() won't always fire
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_creation);
        listView = findViewById(R.id.listview);

        cs = ControllerService.getInstance();

        if (getIntent().getStringExtra("tutorialName") == null){
            openStringInputDialog("Nicht zu lang, nicht zu kurz, darf nicht schon vorhanden sein");
        } else {
            tutorial = cs.getTutorial(getIntent().getStringExtra("tutorialName"));
            tutorial.cacheImages();
            created = true;
        }
/*
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openEditStepActivity(view);
            }
        });*/

        ctx = this;
    }

    @Override
    public void onResume(){
        super.onResume();
        if (created) {
            ((TextView)findViewById(R.id.title)).setText(tutorial.getTitle());

            String[] titles = tutorial.getSubheadings().toArray(new String[tutorial.getTotalSteps()]);
            Bitmap[] images = tutorial.getThumbnails().toArray(new Bitmap[tutorial.getTotalSteps()]);
            adapter = new ListAdapter(this, titles, images);
            listView = findViewById(R.id.listview);
            listView.setAdapter(adapter);
        } else {
            created = true;
        }
    }

    @Override
    public void onBackPressed() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        finish();
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Die Änderungen werden nicht gespeichert. " +
                "Klicke auf \"Fertig\" wenn du die Änderungen speichern möchtest")
                .setPositiveButton("Weiter bearbeiten", dialogClickListener)
                .setNegativeButton("Zum Hauptmenü", dialogClickListener)
                .show();
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
                boolean alreadyExists = ControllerService.getInstance().getTitles().contains(s);
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
                    String tutorialName = editText.getText().toString();
                    ((TextView)findViewById(R.id.title)).setText(tutorialName);
//                    createSubDirectory();
                    tutorial = new Tutorial(tutorialName);
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
        intent.putExtra("title", tutorial.getTitle());
        intent.putExtra("stepNr", ctx.getTotalSteps()+1);
        intent.putExtra("new", true);
        startActivity(intent);
    }

    public void openEditStepActivity(View view){
        int step = (Integer) view.getTag();
        Intent intent = new Intent(this, CreateNewStepActivity.class);
        intent.putExtra("title", tutorial.getTitle());
        intent.putExtra("stepNr", step);
        intent.putExtra("new", false);
        startActivity(intent);
    }

    public int getTotalSteps() {
        return totalSteps;
    }

    public void increaseTotalSteps() {
        this.totalSteps++;
    }

    public void finish(View view){
        if (listView.getChildCount() > 0) {
            IOHelper.writeTutorialToStorage(tutorial);
            cs.update();
        }
        finish();
    }

    public void openTutorial(View view){
        if (listView.getChildCount() < 1){
            UIHelper.showErrorDialog(this,"Bitte erstelle mindestens einen Schritt");
            return;
        }
        Intent intent = new Intent(this, ViewTutorialActivity.class);
        intent.putExtra("tutorial", tutorial.getTitle());
        intent.putExtra("title", tutorial.getTitle());
        startActivity(intent);
    }

    class ListAdapter extends ArrayAdapter<String> {
        Context context;
        String[] titles;
        Bitmap[] images;

        ListAdapter(Context c, String[] titles, Bitmap[] images) {
            super(c, R.layout.row, R.id.textView1, titles);
            this.context = c;
            this.titles = titles;
            this.images = images;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View row = layoutInflater.inflate(R.layout.row, parent, false);
            row.setTag(position);
            ImageView myImage = row.findViewById(R.id.imageView);
            final TextView myTitle = row.findViewById(R.id.textView1);
            final Button edButton = row.findViewById(R.id.edButton);
            myImage.setImageBitmap(images[position]);
            myTitle.setText(titles[position]);

            edButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(context, edButton);
                    popup.inflate(R.menu.tutorial_listview_menu);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.edit:
                                    openEditStepActivity(row);
                                    //Intent intent = new Intent(context, CreateTutorialActivity.class);
                                  //  intent.putExtra("tutorialName", titles[position]);
                                   // startActivity(intent);
                                    break;
                                case R.id.delete:
                                   // cs.deleteTutorial(titles[position]);
                                    // TODO: this doesn't work:
                                    // adapter.notifyDataSetChanged();
                                    // TODO: and this is the shitty workaround for it
                                    ((Activity)context).recreate();
                                    break;
                                case R.id.share:
                                 //   ShareHelper.shareTutorial(getContext(), titles[position]);
                                    break;
                            }
                            return false;
                        }
                    });
                    //displaying the popup
                    popup.show();
                }
            });

            return row;
        }
    }
}