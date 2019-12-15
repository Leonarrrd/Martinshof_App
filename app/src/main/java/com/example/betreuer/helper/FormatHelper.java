package com.example.betreuer.helper;

import android.graphics.Bitmap;

public class FormatHelper {
    public static Bitmap getThumbnail(Bitmap bm){
//        if (bm.getHeight()>bm.getWidth()){
////            return Bitmap.createScaledBitmap(bm, 80, 100, true);
////        } else {
////            return Bitmap.createScaledBitmap(bm, 135, 80, true);
////        }
        return Bitmap.createScaledBitmap(bm, bm.getWidth()/8, bm.getHeight()/8, true);
    }
}