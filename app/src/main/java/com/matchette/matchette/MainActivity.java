package com.matchette.matchette;

import android.Manifest;
import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//import com.skydoves.colorpickerpreference.ColorEnvelope;
//import com.skydoves.colorpickerpreference.ColorListener;

public class MainActivity extends Activity {

    private LinearLayout snackBar;
    private FrameLayout frame;
    private int count = 0;
    String [] shirts;
    String [] pants;
    String currShirtColor;
    String currPantColor;
    String currSnackbarSelection = "shirt";

    String currShirt = "t-shirt";
    String currPant = "pants";

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    //For the recyclerView
    private List<Style> shirtStyleList = new ArrayList<>();
    private List<Style> pantsStyleList = new ArrayList<>();
    private List<Style> currentItemList = new ArrayList<>();
    private RecyclerView recyclerView;
    private StyleAdapter sAdapter;
    private MainActivityFragment mainFragment;

    protected String getCurrShirt(){
        return this.currShirt;
    }

    protected String getCurrPant(){
        return this.currPant;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // For animation when showing/hiding the snackbar so that it doesn't show white space.
        final LinearLayout wholeLayout = findViewById(R.id.whole_layout);
        LayoutTransition transitioner1 = wholeLayout.getLayoutTransition();
        transitioner1.setStartDelay(LayoutTransition.CHANGE_DISAPPEARING, 0);
        transitioner1.setStartDelay(LayoutTransition.APPEARING, 0);
        transitioner1.setStartDelay(LayoutTransition.DISAPPEARING, 0);
        transitioner1.setStartDelay(LayoutTransition.CHANGE_APPEARING, 0);
        transitioner1.setStartDelay(LayoutTransition.CHANGING, 0);

        frame = this.findViewById(R.id.temporary_frame);
        LayoutTransition transitioner2 = frame.getLayoutTransition();
        transitioner2.setStartDelay(LayoutTransition.CHANGE_DISAPPEARING, 0);
        transitioner2.setStartDelay(LayoutTransition.APPEARING, 0);
        transitioner2.setStartDelay(LayoutTransition.DISAPPEARING, 0);
        transitioner2.setStartDelay(LayoutTransition.CHANGE_APPEARING, 0);
        transitioner2.setStartDelay(LayoutTransition.CHANGING, 0);


        //This is the custom snackbar that is set to be GONE at creation.
        snackBar = this.findViewById(R.id.custom_snackbar);

        LayoutTransition transitioner3 = snackBar.getLayoutTransition();
        transitioner3.setStartDelay(LayoutTransition.CHANGE_DISAPPEARING, 0);
        transitioner3.setStartDelay(LayoutTransition.APPEARING, 0);
        transitioner3.setStartDelay(LayoutTransition.DISAPPEARING, 0);
        transitioner3.setStartDelay(LayoutTransition.CHANGE_APPEARING, 0);
        transitioner3.setStartDelay(LayoutTransition.CHANGING, 0);


        final ImageButton closeSnackbar = findViewById(R.id.closeSnackbar);
        final ColorPickerView colorPicker = findViewById(R.id.colorPickerView);


        // Set the swipe behavior (check out the OnSwipeTouchListener class).

        /* I commented out the swiping up function. When the app is just opened and neither shirt nor pants are clicked on yet,
         * there would be a blank space in the snackbar where the recycler view should be, if the snackbar was swiped up. */

        wholeLayout.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeDown() {
                snackBar.setVisibility(LinearLayout.GONE);
            }

            @Override
            public void onSwipeUp() {
//                snackBar.setVisibility(LinearLayout.VISIBLE);
            }
        });


        // RecyclerView
        recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        prepareShirtStyles();
        preparePantsStyles();


        // Listener for RecyclerView
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Style currentStyle = currentItemList.get(position);
                Log.d("change", currSnackbarSelection + " | " + currentStyle.getName());
                if (currSnackbarSelection.equals("shirt")){
                    currShirt = currentStyle.getName();
                    mainFragment.changeStyle("shirt", currentStyle.getName());
                } else {
                    currPant = currentStyle.getName();
                    mainFragment.changeStyle("pant", currentStyle.getName());
                }
            }

            @Override
            public void onLongClick(View view, int position) {}
        }));

        //Set the closeSnackbar behavior.
        closeSnackbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (snackBar.getVisibility() != LinearLayout.GONE) {
                    snackBar.setVisibility(LinearLayout.GONE);
                }

            }
        });

        colorPicker.addOnColorSelectedListener(new OnColorSelectedListener() {
            @Override
            public void onColorSelected(int i) {
                if (currSnackbarSelection.equals("shirt")) {
                    mainFragment.changeColor(Integer.toHexString(i).toUpperCase(), currSnackbarSelection, currShirt);
                } else {
                    mainFragment.changeColor(Integer.toHexString(i).toUpperCase(), currSnackbarSelection, currPant);
                }
            }
        });


        // Customize and gallery buttons
        final Button customizeButton = findViewById(R.id.customize_button);
        customizeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Coming soon!", Toast.LENGTH_SHORT).show();
            }
        });

        final Button galleryButton = findViewById(R.id.gallery_button);
        galleryButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Coming soon!", Toast.LENGTH_SHORT).show();
            }
        });

        //Share button:
        final ImageButton shareButton = findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
               Bitmap sharableBitmap = getBitmapFromView(frame);
               //Log.d("BMP", "Bitmap grabbed");
               File dir = getApplicationContext().getCacheDir();
               //Log.d("FileList", dir.listFiles().toString());
               //Log.d("FileList", dir.listFiles().getClass().toString());
                if (dir.isDirectory()) {
                    //Log.d("Delete", "Deleting in progress");
                    getApplicationContext().deleteFile("image.png");
                    //Log.d("Delete", "deletion attempted");
                }
                try {
                    File cachePath = new File(getApplicationContext().getCacheDir(), "images");
                    cachePath.mkdirs(); // don't forget to make the directory
                    FileOutputStream stream = new FileOutputStream(cachePath + "/image.png");
                    sharableBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    stream.close();
                    //Log.d("FileProvider", "File provider worked.");

                } catch (IOException e) {
                    e.printStackTrace();
                }

                File imagePath = new File(getApplicationContext().getCacheDir(), "images");
                //Log.d("File", "got file path.");
                File newFile = new File(imagePath, "image.png");
                //Log.d("File", "Got file.");
                Uri contentUri = FileProvider.getUriForFile(getApplicationContext(), "com.matchette.matchette.fileprovider", newFile);
                //Log.d("File", "Got URI");

                if (contentUri != null) {
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
                    shareIntent.setDataAndType(contentUri, getContentResolver().getType(contentUri));
                    shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                    startActivity(Intent.createChooser(shareIntent, "Choose an app"));
                }

            }
        });

        final ImageButton saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    Log.d("SaveButton", "Permission not granted is active");
                    // Permission is not granted
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                        Log.d("SaveButton", "Should show a dialog");
                        // 1. Instantiate an <code><a href="/reference/android/app/AlertDialog.Builder.html">AlertDialog.Builder</a></code> with its constructor
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                        builder.setPositiveButton(R.string.alertDialogOkay, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User clicked OK button
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
                                );
                            }
                        });
                        builder.setNegativeButton(R.string.alertDialogCancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                                Toast.makeText(getApplicationContext(), R.string.cantSave, Toast.LENGTH_LONG).show();
                            }
                        });

                        // 2. Chain together various setter methods to set the dialog characteristics
                        builder.setMessage(R.string.alertDialogMessage);

                        // 3. Get the <code><a href="/reference/android/app/AlertDialog.html">AlertDialog</a></code> from <code><a href="/reference/android/app/AlertDialog.Builder.html#create()">create()</a></code>
                        AlertDialog dialog = builder.create();
                        dialog.show();

                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
                                );
                        Log.d("SaveButton", "should request permission");
                    }
                } else {
                    Bitmap bmp = getBitmapFromView(frame);
                    saveImage(bmp);
                    Toast.makeText(getApplicationContext(), R.string.saved, Toast.LENGTH_SHORT).show();
                }
            }
        });

        mainFragment = new MainActivityFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.fragment, mainFragment);
        ft.commit();

        // Camera button that opens the camera
        final ImageButton cameraButton = findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivity(intent);
                }

                catch(ActivityNotFoundException anfe){
                    //display an error message
                    String errorMessage = "Capturing image not supported";
                    Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE : {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Bitmap bmp = getBitmapFromView(frame);
                    saveImage(bmp);
                    Toast.makeText(getApplicationContext(), R.string.saved, Toast.LENGTH_SHORT).show();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getApplicationContext(), R.string.cantSave, Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    protected void animationLogicShirt(){
        currSnackbarSelection = "shirt";
        if (currentItemList == shirtStyleList && snackBar.getVisibility()==LinearLayout.GONE)
            snackBar.setVisibility(LinearLayout.VISIBLE);
        else if (currentItemList != shirtStyleList && snackBar.getVisibility()==LinearLayout.VISIBLE){
            currentItemList = shirtStyleList;
            recycShirt();
        }
        else if (currentItemList != shirtStyleList && snackBar.getVisibility()==LinearLayout.GONE){
            currentItemList = shirtStyleList;
            recycShirt();
            snackBar.setVisibility(LinearLayout.VISIBLE);
        }
    }

    protected void animationLogicPant(){
        currSnackbarSelection = "pant";
        if (currentItemList == pantsStyleList && snackBar.getVisibility()==LinearLayout.GONE)
            snackBar.setVisibility(LinearLayout.VISIBLE);
        else if (currentItemList != pantsStyleList && snackBar.getVisibility()==LinearLayout.VISIBLE){
            currentItemList = pantsStyleList;
            recycPants();
        }
        else if (currentItemList != pantsStyleList && snackBar.getVisibility()==LinearLayout.GONE){
            currentItemList = pantsStyleList;
            recycPants();
            snackBar.setVisibility(LinearLayout.VISIBLE);
        }
    }
    /**
     * Set up the RecyclerView for shirts
     */
    private void recycShirt (){
        sAdapter = new StyleAdapter(shirtStyleList);
        recyclerView.setAdapter(sAdapter);
    }

    /**
     * Set up the RecyclerView for pants
     */
    private void recycPants (){
        sAdapter = new StyleAdapter(pantsStyleList);
        recyclerView.setAdapter(sAdapter);
    }

    private void prepareShirtStyles () {
        Style style = new Style("t-shirt", R.drawable.ic_shirt);
        shirtStyleList.add(style);

        style = new Style("dress-shirt", R.drawable.ic_dress_shirt);
        shirtStyleList.add(style);

        style = new Style("hoodie", R.drawable.ic_hoodie);
        shirtStyleList.add(style);

        style = new Style("polo", R.drawable.ic_polo);
        shirtStyleList.add(style);

        style = new Style("woman-sleeveless-shirt", R.drawable.ic_woman_sleeveless_shirt);
        shirtStyleList.add(style);

        style = new Style("blouse", R.drawable.ic_blouse); // 1.2 from here
        shirtStyleList.add(style);

        style = new Style("man-coat", R.drawable.ic_man_coat);
        shirtStyleList.add(style);

        style = new Style("man-suit", R.drawable.ic_man_suit);
        shirtStyleList.add(style);

        style = new Style("woman-jacket", R.drawable.ic_woman_jacket);
        shirtStyleList.add(style);

        style = new Style("woman-suit", R.drawable.ic_woman_suit); //1.3
        shirtStyleList.add(style);
    }

    private void preparePantsStyles() {
        Style style = new Style("pants", R.drawable.ic_pant);
        pantsStyleList.add(style);

        style = new Style("shorts", R.drawable.ic_shorts);
        pantsStyleList.add(style);

        style = new Style("formal-pants", R.drawable.ic_formal_pants);
        pantsStyleList.add(style);

        style = new Style("woman-pants", R.drawable.ic_woman_pants); // trousers + 0.1
        pantsStyleList.add(style);

        style = new Style("skirt", R.drawable.ic_skirt); // shorts + 0.2
        pantsStyleList.add(style);

        style = new Style("long-skirt", R.drawable.ic_long_skirt); // trousers
        pantsStyleList.add(style);

        style = new Style("formal-skirt", R.drawable.ic_formal_skirt); // trousers
        pantsStyleList.add(style);
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

    /**
     * Save image to a folder named Matchette in gallery
     * @param bmp
     */
    public void saveImage(Bitmap bmp) {
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
            MediaScannerConnection.scanFile(this, new String[]{dest.toString()},
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
}

