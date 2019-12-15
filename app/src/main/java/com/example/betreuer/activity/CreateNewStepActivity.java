package com.example.betreuer.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.betreuer.R;
import com.example.betreuer.helper.IOHelper;
import com.example.betreuer.helper.UIHelper;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CreateNewStepActivity extends AppCompatActivity {
    private int stepNr;
    private boolean isNew;
    private String title;
    private EditText subheader;
    private EditText description;

    // EXPERIMENTAL
    String mCameraFileName = Environment.getExternalStorageDirectory() + "/Martinshof";
    Uri image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_step);
        title = getIntent().getStringExtra("title");
        subheader = (EditText) findViewById(R.id.slide_subheader);
        subheader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });
        description = (EditText) findViewById(R.id.slide_desc);
        description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });
        ((TextView)findViewById(R.id.title)).setText(title);
        stepNr = getIntent().getIntExtra("stepNr", 50);
        isNew = getIntent().getBooleanExtra("new", true);
        if(!isNew){
            List<String> subheader = IOHelper.getSubheadingsFromDirectory(title);
            List<String> desc = IOHelper.getDescsFromDirectory(title);
            List<Bitmap> imgs = IOHelper.getImagesFromDirectory(title);
            ((EditText)findViewById(R.id.slide_subheader)).setText(subheader.get(stepNr));
            ((EditText)findViewById(R.id.slide_desc)).setText(desc.get(stepNr));

            ImageView img = findViewById(R.id.image_view);
            img.setImageBitmap(imgs.get(stepNr));

            //stepNr greift vorher auf den Index zu, muss also jetzt um 1 erhöht werden
            stepNr++;
        }
    }

    public void finish(View view){
        Environment.getExternalStorageState();
        if(((EditText) findViewById(R.id.slide_subheader)).getText().toString().isEmpty()
        || ((EditText) findViewById(R.id.slide_desc)).getText().toString().isEmpty()){
            System.out.println("hm");
            UIHelper.showErrorDialog(this, "Bitte gebe eine Überschrift und eine Erklärung ein.");
            return;
        }
        // TODO: wipe this out
        CreateTutorialActivity.ctx.increaseTotalSteps();
        writeTextToStorage();
        writeImageToStorage();
        finish();
    }

    public void selectImageDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Wähle ein Bild für den Schritt aus.");

        builder.setPositiveButton("Neues Bild mit Kamera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent takePicture = getCameraIntent();
                startActivityForResult(takePicture , 1);
            }
        });
        builder.setNegativeButton("Bild aus der Galerie", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent pickImage = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickImage , 0);
            }
        });
        builder.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        ImageView imageView = findViewById(R.id.image_view);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 0){
                image = imageReturnedIntent.getData();
                imageView.setImageURI(image);
            }
            if (requestCode == 1) {
                if (imageReturnedIntent != null) {
                    image = imageReturnedIntent.getData();
                    imageView.setImageURI(image);
                    imageView.setVisibility(View.VISIBLE);
                }
                if (image == null && mCameraFileName != null) {
                    image = Uri.fromFile(new File(mCameraFileName));
                    imageView.setImageURI(image);
                    imageView.setVisibility(View.VISIBLE);
                }
                File file = new File(mCameraFileName);
                if (!file.exists()) {
                    file.mkdir();
                }
            }
        }
    }

    protected Intent getCameraIntent(){
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

        Date date = new Date();
        DateFormat df = new SimpleDateFormat("-mm-ss");

        String newPicFile = df.format(date) + ".jpg";
        String outPath = "/sdcard/" + newPicFile;
        File outFile = new File(outPath);

        mCameraFileName = outFile.toString();
        Uri outuri = Uri.fromFile(outFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outuri);
        return intent;
    }

    protected void writeTextToStorage() {
        File directory = new File(Environment.getExternalStorageDirectory() + "/Martinshof", getIntent().getStringExtra("title"));
        String subheader = ((EditText) findViewById(R.id.slide_subheader)).getText().toString();
        String desc = ((EditText) findViewById(R.id.slide_desc)).getText().toString();
        if (isNew) {
            IOHelper.writeTextToStorage(directory, subheader, desc);
        } else {
            IOHelper.writeTextToStorage(directory, subheader, desc, stepNr);
        }
    }

    protected void writeImageToStorage(){
        String dir = getIntent().getStringExtra("title");
        ImageView iv = findViewById(R.id.image_view);
        BitmapDrawable drawable = (BitmapDrawable) iv.getDrawable();
        Bitmap bitmapImg = drawable.getBitmap();
        int step;
        if (!isNew){
            step = stepNr;
        } else {
            step = IOHelper.getSubheadingsFromDirectory(dir).size();
        }
        IOHelper.writeImageToStorage(dir, step, bitmapImg);
    }
}
