package com.example.betreuer.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class UIHelper {
    public static void showErrorDialog(Context ctx, String title){
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
}
