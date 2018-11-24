package com.matchette.matchette;

import android.Manifest;
import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorChangedListener;
import com.flask.colorpicker.OnColorSelectedListener;

import java.io.File;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;


public class MainActivity extends Activity {
    private FrameLayout frame;

    public RelativeLayout snackBar;
    String currSnackbarSelection = "shirt";
    String currColor = "CCD1D9";

    private MainActivityFragment mainFragment;

    // sharing
    private String shareFileName = "sharableImage.png";

    // taking photo
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_TAKE_PHOTO = 1;

    // first-time tutorial
    final String WHOLE_LAYOUT_SHOWCASE_ID = "12345",
                SNACKBAR_LAYOUT_SHOWCASE_ID = "12346"; // Unique ID's to show tutorialS only once7

    @Override
    protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        snackBar = findViewById(R.id.custom_snackbar);
        frame = findViewById(R.id.temporary_frame);

        mainFragmentTransaction();

        snackbarAnimation(snackBar, frame);
        setCloseSnackbar();

        createColorPickerView();

        createGalleryButton();
        createShareButton();
        createSaveButton();
        createCameraButton();

        showWholeLayoutTutorial();
    }

    private void createCameraButton() {
        final ImageButton cameraButton = findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getBitmapFromCamera();
            }
        });
    }

    private void mainFragmentTransaction() {
        mainFragment = new MainActivityFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.fragment, mainFragment);
        ft.commit();
    }

    private void createGalleryButton() {
        final Button galleryButton = findViewById(R.id.gallery_button);
        galleryButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Coming soon!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createShareButton() {
        final ImageButton shareButton = findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Bitmap sharableBitmap = Save.getBitmapFromView(frame);
                File dir = getApplicationContext().getCacheDir();
                deleteFile(dir, shareFileName);
                Save.saveImageToCache(sharableBitmap, shareFileName, getApplicationContext());

                File imagePath = new File(getApplicationContext().getCacheDir(), "images");
                File newFile = new File(imagePath, shareFileName);
                Uri contentUri = FileProvider.getUriForFile(getApplicationContext(), "com.matchette.matchette.fileprovider", newFile);
                shareFromUri(contentUri);
            }
        });
    }

    private void createSaveButton() {
        final ImageButton saveButton = findViewById(R.id.saveButton);
        saveButtonListener(saveButton);
    }

    /**
     * For animation when showing/hiding the snackbar so that it doesn't show white space.
     */
    private void snackbarAnimation(RelativeLayout snackBar, FrameLayout frame){
        final LinearLayout wholeLayout = findViewById(R.id.whole_layout);
        setStartDelayToZero(wholeLayout);
        setStartDelayToZero(frame);
        setStartDelayToZero(snackBar);

        // For swiping snackbar down
        wholeLayout.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeDown() {
                hideSnackbar();
            }
        });
    }

    private void setCloseSnackbar() {
        final ImageButton closeSnackbar = findViewById(R.id.closeSnackbar);
        closeSnackbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (snackBar.getVisibility() != LinearLayout.GONE) {
                    hideSnackbar();
                }
            }
        });
    }

    private void createColorPickerView() {
        final ColorPickerView colorPicker = findViewById(R.id.colorPickerView);
        colorPicker.addOnColorSelectedListener(new OnColorSelectedListener() {
            @Override
            public void onColorSelected(int i) {
                changeStyleColor(i);
            }
        });

        colorPicker.addOnColorChangedListener(new OnColorChangedListener() {
            @Override
            public void onColorChanged(int i) {
                changeStyleColor(i);
            }
        });
    }

    private void changeStyleColor(int i){
            currColor = Integer.toHexString(i).toUpperCase();
            mainFragment.changeStyleColor(currColor);
    }

    public void saveButtonListener(ImageButton btn){
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                        showAlertDialog();

                    } else requestStoragePermission(); // No explanation needed; request the permission

                } else showSavedMessage();
            }
        });
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        // User clicked OK button
        builder.setPositiveButton(R.string.alertDialogOkay, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                requestStoragePermission();
            }
        });

        // User cancelled the dialog
        builder.setNegativeButton(R.string.alertDialogCancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(getApplicationContext(), R.string.cantSave, Toast.LENGTH_LONG).show();
            }
        });

        builder.setMessage(R.string.alertDialogMessage);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showSavedMessage() {
        Bitmap bmp = Save.getBitmapFromView(frame);
        Save.saveImage(bmp, getApplicationContext());
        Toast.makeText(getApplicationContext(), R.string.saved, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE : {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    showSavedMessage();
                else
                    // permission denied
                    Toast.makeText(getApplicationContext(), R.string.cantSave, Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            Bitmap colorPicture = (Bitmap) data.getExtras().get("data");//this is your bitmap image and now you can do whatever you want with this
            createPaletteAsync(colorPicture);
        }
    }

    public int getSnackBarVisibility () {
        return snackBar.getVisibility();
    }

    public void showSnackBar() {
        snackBar.setVisibility(LinearLayout.VISIBLE);
    }

    /**
     * Sets the various start delays to 0 for automatic animation in layout changes.
     * @param viewGroup is the viewgroup you want the transition of to be set to zero.
     */
    private void setStartDelayToZero(ViewGroup viewGroup){
        LayoutTransition transitioner = viewGroup.getLayoutTransition();
        transitioner.setStartDelay(LayoutTransition.CHANGE_DISAPPEARING, 0);
        transitioner.setStartDelay(LayoutTransition.APPEARING, 0);
        transitioner.setStartDelay(LayoutTransition.DISAPPEARING, 0);
        transitioner.setStartDelay(LayoutTransition.CHANGE_APPEARING, 0);
        transitioner.setStartDelay(LayoutTransition.CHANGING, 0);
    }

    /**
     * Hides the snackbar. Public because we might have to call it from the fragment class as well.
     */
    public void hideSnackbar(){
        snackBar.setVisibility(View.GONE);
    }

    /**
     * Shares whatever the uri points to.
     * @param uri The uri of the sharable. In our case that is the bitmap grabbed from the frame.
     */
    private void shareFromUri(Uri uri){
        if (uri != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
            shareIntent.setDataAndType(uri, getContentResolver().getType(uri));
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(shareIntent, "Choose an app"));
        }
    }

    /**
     * Deletes a file.
     * @param file The directory of the file to be deleted.
     * @param filename The name of the file to be deleted.
     */
    private void deleteFile(File file, String filename){
        if (file.isDirectory()) {
            getApplicationContext().deleteFile(filename);
        }
    }

    /**
     * Requests the WRITE_EXTERNAL_STORAGE permission.
     */
    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
        );
    }

    private void getBitmapFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
           startActivityForResult(intent, REQUEST_TAKE_PHOTO);
        } else {
            Toast.makeText(getApplicationContext(), R.string.photoError, Toast.LENGTH_SHORT).show();
        }
    }

    // Generate palette asynchronously and use it on a different
// thread using onGenerated()
    public void createPaletteAsync(Bitmap bitmap) {
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette p) {
                int domColor = p.getDominantColor(Color.parseColor("#CCD1D9"));
                String hexColor = Integer.toHexString(domColor).toUpperCase();
                mainFragment.changeStyleColor(hexColor);
            }
        });
    }

    private void showWholeLayoutTutorial() {
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view

        MaterialShowcaseSequence wholeLayoutSequence = new MaterialShowcaseSequence(this, WHOLE_LAYOUT_SHOWCASE_ID);
        wholeLayoutSequence.setConfig(config);

        wholeLayoutSequence.addSequenceItem(findViewById(R.id.gallery_button),
                "Collection of saved outfits, can be ordered based on your preference",
                "GOT IT");

        wholeLayoutSequence.addSequenceItem(findViewById(R.id.shareButton),
                "Share the current outfit",
                "GOT IT");

        wholeLayoutSequence.addSequenceItem(findViewById(R.id.saveButton),
                "Save the current outfit to your phone",
                "GOT IT");

        wholeLayoutSequence.start();
    }

    private void showSnackbarTutorial() {
            ShowcaseConfig config = new ShowcaseConfig();
            config.setDelay(500); // half second between each showcase view

            MaterialShowcaseSequence snackbarSequence = new MaterialShowcaseSequence(this, SNACKBAR_LAYOUT_SHOWCASE_ID);
            snackbarSequence.setConfig(config);

            snackbarSequence.addSequenceItem(findViewById(R.id.colorPickerView),
                    "Tap to pick a color",
                    "GOT IT");
            snackbarSequence.addSequenceItem(findViewById(R.id.v_lightness_slider),
                    "Slide to change color lightness",
                    "GOT IT");
            snackbarSequence.addSequenceItem(findViewById(R.id.cameraButton),
                    "Take picture of a color to apply on outfit",
                    "GOT IT");
            snackbarSequence.addSequenceItem(findViewById(R.id.closeSnackbar),
                    "Click here or slide down to close bottom sheet",
                    "GOT IT");

            snackbarSequence.start();
    }

    public RelativeLayout getSnackbar() {
        return snackBar;
    }
}

