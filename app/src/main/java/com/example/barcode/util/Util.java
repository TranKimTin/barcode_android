package com.example.barcode.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

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

    //tao ma barcode
    public static Bitmap createBarcodeBitmap(String data, int width, int height, BarcodeFormat code) throws WriterException {
        MultiFormatWriter writer = new MultiFormatWriter();
        String finalData = Uri.encode(data);

        // Use 1 as the height of the matrix as this is a 1D Barcode.
        BitMatrix bm = writer.encode(finalData, code, width, 1);
        int bmWidth = bm.getWidth();

        Bitmap imageBitmap = Bitmap.createBitmap(bmWidth, height, Bitmap.Config.ARGB_8888);

        for (int i = 0; i < bmWidth; i++) {
            // Paint columns of width 1
            int[] column = new int[height];
            Arrays.fill(column, bm.get(i, 0) ? Color.BLACK : Color.WHITE);
            imageBitmap.setPixels(column, 0, 1, i, 0, 1, height);
        }

        return imageBitmap;
    }


    /**************************************************************
     * getting from com.google.zxing.client.android.encode.QRCodeEncoder
     *
     * See the sites below
     * http://code.google.com/p/zxing/
     * http://code.google.com/p/zxing/source/browse/trunk/android/src/com/google/zxing/client/android/encode/EncodeActivity.java
     * http://code.google.com/p/zxing/source/browse/trunk/android/src/com/google/zxing/client/android/encode/QRCodeEncoder.java
     */

    public static final int WHITE = 0xFFFFFFFF;
    public static final int BLACK = 0xFF000000;

    public static Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int img_width, int img_height) throws WriterException
    {
        if (contents == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contents);
        if (encoding != null) {
            hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try {
            result = writer.encode(contents, format, img_width, img_height, hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    public static String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }
}
