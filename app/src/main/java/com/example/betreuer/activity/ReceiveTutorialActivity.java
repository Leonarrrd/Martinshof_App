package com.example.betreuer.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.widget.TextView;

import com.example.betreuer.R;
import com.example.betreuer.helper.IOHelper;
import com.example.betreuer.helper.ShareHelper;
import com.example.betreuer.helper.UIHelper;

import org.zeroturnaround.zip.ZipUtil;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ReceiveTutorialActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_tutorial);

        // Get the intent that started this activity
        Intent intent = getIntent();
        Uri data = intent.getData();

        ContentResolver cr = getApplicationContext().getContentResolver();
        try {
            InputStream in = cr.openInputStream(data);
            ZipInputStream zis = new ZipInputStream(in);

            byte[] buffer = new byte[2048];

            File file = new File (Environment.getExternalStorageDirectory() + "/Martinshof", "received");
            if (!file.exists()){
                file.mkdir();
            }
            Path outDir = Paths.get(file.toString());

            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path filePath = outDir.resolve(entry.getName());

                try (FileOutputStream fos = new FileOutputStream(filePath.toFile());
                     BufferedOutputStream bos = new BufferedOutputStream(fos, buffer.length)) {
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        bos.write(buffer, 0, len);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
