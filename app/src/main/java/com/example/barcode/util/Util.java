package com.example.barcode.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class Util {
    static final String TAG = "TOAST";
    public static void toast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        logD(message);
    }

    public static void logD(String message){
        Log.d(TAG, message);
    }

    public static void logE(String message){
        Log.e(TAG, message);
    }

    public static void logE(String message, Exception err){
        Log.e(TAG, message, err);
    }
}
