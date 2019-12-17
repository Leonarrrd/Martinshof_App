package com.example.betreuer.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import org.zeroturnaround.zip.NameMapper;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;

import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;

/**
 * Class that holds sharing functionality
 * Must not necessarily be a Helper class, can be refactored if wanted
 */
public class ShareHelper {
    private static File sdcard = Environment.getExternalStorageDirectory();
    private static File app_root = new File (sdcard + "/Martinshof");

    public static void shareTutorial(Context ctx, String tutorialName) {
        zipDirectory(tutorialName);
        File file = new File(app_root, tutorialName + ".tut");
        Uri sharedFileUri = FileProvider.getUriForFile(ctx, "com.betreuer.fileprovider", file);
        Intent intent = ShareCompat.IntentBuilder.from((Activity) ctx)
                .setStream(sharedFileUri) // uri from FileProvider
                .setType("*/*")
                .getIntent()
                .setAction(Intent.ACTION_SEND) //Change if needed
                .setDataAndType(sharedFileUri, "*/*")
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        ctx.startActivity(intent);
    }

    public static void zipDirectory(final String directory) {
        ZipUtil.pack(new File(app_root, directory), new File(app_root, directory + ".tut"),new NameMapper() {
            public String map(String name) {
                return directory + "/" + name;
            }
        });
    }
}
