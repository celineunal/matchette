package com.matchette.matchette;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Save {

    /**
     * Save image to a folder named Matchette in gallery
     * @param bmp
     */
    public void saveImage(Bitmap bmp, Context context) {
        String folderName = "/Matchette";
        String filename = "matchette" + System.currentTimeMillis() + ".png";
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + folderName;
        File file = new File(filePath);
        if (!file.exists())
            file.mkdirs();
        File dest = new File(file, filename);

        try {
            FileOutputStream out = new FileOutputStream(dest);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            MediaScannerConnection.scanFile(context, new String[]{dest.toString()},
                    null, new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String s, Uri uri) {
                            Log.e("ExternalStorage", "Scanned" + s + ":");
                            Log.e("ExternalStorage", "-> uri=" + uri);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves an image to cache (so it doesn't need storage permission)
     * @param bitmap Bitmap to be saved.
     * @param name Name of the file.
     */
    public void saveImageToCache(Bitmap bitmap, String name, Context context){
        try {
            File cachePath = new File(context.getCacheDir(), "images");
            cachePath.mkdirs(); // don't forget to make the directory
            FileOutputStream stream = new FileOutputStream(cachePath + "/" + name);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();
            //Log.d("FileProvider", "File provider worked.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Turns a view in to a bitmap
     * @param view
     * @return The view as a bitmap
     */
    public static Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null)
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

}

