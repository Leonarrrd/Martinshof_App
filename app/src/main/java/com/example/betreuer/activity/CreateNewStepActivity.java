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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.betreuer.R;
import com.example.betreuer.helper.UIHelper;
import com.example.betreuer.model.Step;
import com.example.betreuer.model.Tutorial;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateNewStepActivity extends AppCompatActivity {
    private boolean isNew;
    private int stepNr;
    private Tutorial tutorial;
    private EditText subheader;
    private EditText description;

    // EXPERIMENTAL
    String mCameraFileName = Environment.getExternalStorageDirectory() + "/Martinshof";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_step);
        tutorial = CreateTutorialActivity.ctx.tutorial; // TODO: see explanation in CreateTutorialActivity
        subheader = (EditText) findViewById(R.id.slide_subheader);
        description = (EditText) findViewById(R.id.slide_desc);

        ((TextView)findViewById(R.id.title)).setText(tutorial.getTitle());


        isNew = getIntent().getBooleanExtra("new", true);
        if(!isNew){
            stepNr = getIntent().getIntExtra("stepNr", tutorial.getTotalSteps()+1);
            Step step = tutorial.getSteps().get(stepNr);
            ((EditText)findViewById(R.id.slide_subheader)).setText(step.getSubheading());
            ((EditText)findViewById(R.id.slide_desc)).setText(step.getDescription());
            ((ImageView)findViewById(R.id.image_view)).setImageBitmap(step.getImage());
        }

        //hides the keyboard when clicking outside edittext
        findViewById(R.id.relativeLayout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
//                subheader.clearFocus();
//                description.clearFocus();
                return true;
            }
        });

        subheader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });

        description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });
    }



    public void finish(View view){
        if(((EditText) findViewById(R.id.slide_subheader)).getText().toString().isEmpty()
        || ((EditText) findViewById(R.id.slide_desc)).getText().toString().isEmpty()){
            UIHelper.showErrorDialog(this, "Bitte gebe eine Überschrift und eine Erklärung ein.");
            return;
        }

        String subheader = ((EditText) findViewById(R.id.slide_subheader)).getText().toString();
        String desc = ((EditText) findViewById(R.id.slide_desc)).getText().toString();
        ImageView iv = findViewById(R.id.image_view);
        BitmapDrawable drawable = (BitmapDrawable) iv.getDrawable();
        Bitmap bitmapImg = drawable.getBitmap();
        if(isNew){
            tutorial.addStep(new Step(subheader, desc, bitmapImg));
        } else {
            tutorial.changeStep(new Step(subheader, desc, bitmapImg), stepNr);
        }
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

    // TODO: Clean up requestCode 1
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        ImageView imageView = findViewById(R.id.image_view);
        Uri image = null;

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
        Uri outUri = Uri.fromFile(outFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outUri);
        return intent;
    }
}
