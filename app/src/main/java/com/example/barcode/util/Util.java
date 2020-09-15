package com.example.barcode.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class Util {
    static final String TAG = "TOAST";
    public static void toast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        Log.d(TAG, message);
    }
}
