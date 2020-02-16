package com.example.instagramclone.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

public class ImageManager {

    private static final String TAG = "ImageManager";

    /**
     * Converts image to Bitmap
     * @param imgURL
     * @return
     */
    public static Bitmap getBitamp(String imgURL){
        File imageFile = new File(imgURL);
        FileInputStream fis = null;
        Bitmap bitmap = null;
        try{
            fis = new FileInputStream(imageFile);
            bitmap = BitmapFactory.decodeStream(fis);
        } catch (Exception e){
            Log.e(TAG, "getBitamp: error " + e.getMessage() );
        }
        finally {
            try{
                fis.close();
            } catch (Exception e){
                Log.e(TAG, "getBitamp: FileNotFOundException "+ e.getMessage() );
            }
        }
        return  bitmap;
    }

    /**
     * return byte array from a bitmap
     * quality is greater than 0 and less than 100
     * @param bm
     * @param quality
     * @return
     */
    public static byte[] getBytesFromBitmap(Bitmap bm , int quality){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG , quality , stream);

        return stream.toByteArray();
    }
}










