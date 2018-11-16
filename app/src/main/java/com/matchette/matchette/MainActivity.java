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
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorChangedListener;
import com.flask.colorpicker.OnColorSelectedListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

//import com.skydoves.colorpickerpreference.ColorEnvelope;
//import com.skydoves.colorpickerpreference.ColorListener;

public class MainActivity extends Activity {
    private FrameLayout frame;

    // custom snackbar GONE at creation
    private LinearLayout snackBar;
    String currSnackbarSelection = "shirt";
    String currShirt = "t-shirt";
    String currPant = "pants";
    String currShirtColor = "CCD1D9";
    String currPantColor = "CCD1D9";

    // recyclerView of styles
    private List<Style> shirtStyleList = new ArrayList<>();
    private List<Style> pantsStyleList = new ArrayList<>();
    private List<Style> currentItemList = new ArrayList<>();
    private RecyclerView recyclerView;
    private StyleAdapter sAdapter;
    private MainActivityFragment mainFragment;

    // sharing
    private String shareFileName = "sharableImage.png";

    // taking photo
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_TAKE_PHOTO = 1;

    // first-time tutorial
    final String WHOLE_LAYOUT_SHOWCASE_ID = "12345",
                SNACKBAR_LAYOUT_SHOWCASE_ID = "12346"; // Unique ID's to show tutorialS only once


    @Override
    protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainFragmentTransaction();

        snackbarAnimation();
        setCloseSnackbar();

        createRecyclerView();
        recyclerViewListener();

        createColorPickerView();

        createCustomButton();
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

    private void createCustomButton() {
        final Button customButton = findViewById(R.id.custom_button);
        customButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Coming soon!", Toast.LENGTH_SHORT).show();
            }
        });
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
    private void snackbarAnimation(){
        final LinearLayout wholeLayout = findViewById(R.id.whole_layout);
        setStartDelayToZero(wholeLayout);
        frame = this.findViewById(R.id.temporary_frame);
        setStartDelayToZero(frame);
        snackBar = this.findViewById(R.id.custom_snackbar);
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

    private void createRecyclerView(){
        recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setScrollbarFadingEnabled(false); // always visible

        prepareShirtStyles();
        preparePantsStyles();
    }

    private void recyclerViewListener(){
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Style currentStyle = currentItemList.get(position);
                if (currSnackbarSelection.equals("shirt")){
                    currShirt = currentStyle.getName();
                    mainFragment.changeStyle("shirt", currentStyle.getName());
                    mainFragment.changeColor(currShirtColor, "shirt", currentStyle.getName());
                } else if (currSnackbarSelection.equals("pant")){
                    currPant = currentStyle.getName();
                    mainFragment.changeStyle("pant", currentStyle.getName());
                    mainFragment.changeColor(currPantColor, "pant", currentStyle.getName());
                }
            }

            @Override
            public void onLongClick(View view, int position) {}
        }));
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
        if (currSnackbarSelection.equals("shirt")) {
            currShirtColor = Integer.toHexString(i).toUpperCase();
            mainFragment.changeColor(currShirtColor, currSnackbarSelection, currShirt);
        } else {
            currPantColor = Integer.toHexString(i).toUpperCase();
            mainFragment.changeColor(currPantColor, currSnackbarSelection, currPant);
        }
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

    protected void animationLogicShirt(){
        animationLogicForTypes("shirt",shirtStyleList);
    }

    protected void animationLogicPant(){
        animationLogicForTypes("pant",pantsStyleList);
    }

    // Animation for a generic type in the recycler view
    protected void animationLogicForTypes(String type, List styleList){
        showSnackbarTutorial();
        currSnackbarSelection = type;
        if (currentItemList == styleList && snackBar.getVisibility()==LinearLayout.GONE)
            snackBar.setVisibility(LinearLayout.VISIBLE);
        else if (currentItemList != styleList && snackBar.getVisibility()==LinearLayout.VISIBLE){
            currentItemList = styleList;
            updateRecyclerView(styleList);
        }
        else if (currentItemList != styleList && snackBar.getVisibility()==LinearLayout.GONE){
            currentItemList = styleList;
            updateRecyclerView(styleList);
            snackBar.setVisibility(LinearLayout.VISIBLE);
        }
    }

    // Updating recycler view to switch between shirts and pants
    private void updateRecyclerView(List styleList){
        sAdapter = new StyleAdapter(styleList);
        recyclerView.setAdapter(sAdapter);
    }

    private void prepareShirtStyles () {
        Style style = new Style("t-shirt", R.drawable.ic_t_shirt);
        shirtStyleList.add(style);

        // would crash
//        style = new Style("dress-shirt", R.drawable.ic_dress_shirt);
//        shirtStyleList.add(style);

        style = new Style("hoodie", R.drawable.ic_hoodie);
        shirtStyleList.add(style);

        style = new Style("polo", R.drawable.ic_polo);
        shirtStyleList.add(style);

        style = new Style("woman-sleeveless-shirt", R.drawable.ic_woman_sleeveless_shirt);
        shirtStyleList.add(style);

        style = new Style("blouse", R.drawable.ic_blouse);
        shirtStyleList.add(style);

        style = new Style("man-coat", R.drawable.ic_man_coat);
        shirtStyleList.add(style);

        style = new Style("man-suit", R.drawable.ic_man_suit);
        shirtStyleList.add(style);

        style = new Style("woman-jacket", R.drawable.ic_woman_jacket);
        shirtStyleList.add(style);

        style = new Style("woman-suit", R.drawable.ic_woman_suit);
        shirtStyleList.add(style);
    }

    private void preparePantsStyles() {
        Style style = new Style("pants", R.drawable.ic_pant);
        pantsStyleList.add(style);

        style = new Style("shorts", R.drawable.ic_shorts);
        pantsStyleList.add(style);

        style = new Style("formal-pants", R.drawable.ic_formal_pants);
        pantsStyleList.add(style);

        style = new Style("woman-pants", R.drawable.ic_woman_pants);
        pantsStyleList.add(style);

        style = new Style("skirt", R.drawable.ic_skirt);
        pantsStyleList.add(style);

        style = new Style("long-skirt", R.drawable.ic_long_skirt);
        pantsStyleList.add(style);

        style = new Style("formal-skirt", R.drawable.ic_formal_skirt);
        pantsStyleList.add(style);
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
                if (currSnackbarSelection.equals("shirt")) {
                    mainFragment.changeColor(hexColor, currSnackbarSelection, currShirt);
                } else {
                    mainFragment.changeColor(hexColor, currSnackbarSelection, currPant);
                }
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

            snackbarSequence.addSequenceItem(findViewById(R.id.recycler_view),
                    "Scroll down to choose style",
                    "GOT IT");
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
}

