package com.example.betreuer.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.example.betreuer.R;
import com.example.betreuer.helper.IOHelper;
import com.example.betreuer.service.ControllerService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkFilePermissions();
        IOHelper.createDirectory();
        ControllerService cs = ControllerService.getInstance();
        System.out.println(cs.getTutorials());
    }

    @Override
    public void onBackPressed() {
        finish();
        System.exit(0);
    }

    public void openCategory(View view){
        Intent intent;
        switch (view.getTag().toString()){
            case "view_tutorials_btn":
                 intent = new Intent(this, ViewTutorialsActivity.class);
                 break;
            case "create_new_tutorial_btn":
                intent = new Intent(this, CreateTutorialActivity.class);
                break;
            default:
                intent = new Intent(this, MainActivity.class);
        }
        startActivity(intent);
    }

    private void checkFilePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int hasWriteExternalStoragePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (hasWriteExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1001);
            }
        }
    }
}
