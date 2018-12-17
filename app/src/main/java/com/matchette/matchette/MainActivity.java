package com.matchette.matchette;

import android.Manifest;
import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorChangedListener;
import com.flask.colorpicker.OnColorSelectedListener;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;


/**
 * This is the Main Activity that handles most UI elements.
 */

public class MainActivity extends Activity {
    private FrameLayout frame;

    // for changing colors in the bottom sheet
    private LinearLayout bottomSheet;
    String currClothingPiece = "shirt";
    Style currShirt = new Style("t-shirt", R.drawable.t_shirt, 1.0f);
    Style currPant = new Style("pants", R.drawable.pant,  1.25f);
    int currShirtColor = Integer.parseInt("FFFFFF",16);
    int currPantColor =  Integer.parseInt("FFFFFF",16);

    // for recyclerView of styles
    public List<Style> shirtStyleList = new ArrayList<Style>();
    public List<Style> pantsStyleList = new ArrayList<Style>();
    private List<Style> currentItemList = new ArrayList<Style>();
    private RecyclerView recyclerView;
    private StyleAdapter sAdapter;
    private MainActivityFragment mainFragment;

    // for sharing
    private String shareFileName = "sharableImage.png";

    // for picking colors from photo
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private static final int PICK_COLOR = 3;

    // going back and forth between selected colors
    private Stack<Integer> shirtColors = new Stack<>(),
            pantsColors = new Stack<>(),
            shirtColors1 = new Stack<>(),
            pantsColors1 = new Stack<>();
    private ImageButton redoColor, undoColor;

    // for tutorial
    final String WHOLE_LAYOUT_SHOWCASE_ID = "123",
                BOTTOM_SHEET_SHOWCASE_ID = "124"; // Unique ID's to show tutorials only once
    final String GOT_IT = "GOT IT";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomSheetAnimation();
        setCloseBottomSheet();
        createRecyclerView(getApplicationContext());
        recyclerViewListener();

        createColorPickerView();
        createShareButton();
        createSaveButton();
        createCameraButton();

        createUndoColorButton();
        createRedoColorButton();

        mainFragmentTransaction();
        showWholeLayoutTutorial();
    }

    /**
     * For the transaction of the main fragment.
     */

    private void mainFragmentTransaction() {
        mainFragment = new MainActivityFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.fragment, mainFragment);
        ft.commit();
    }

    /**
     * Create the camera button and its listener.
     */

    private void createCameraButton() {
        final ImageButton cameraButton = findViewById(R.id.camera_button);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera();
            }
        });
    }

    /**
     * Open the camera for color picking.
     */

    private void openCamera() {
        Intent cameraIntent = new Intent(this, CameraActivity.class);
        startActivityForResult(cameraIntent, PICK_COLOR);
    }

    /**
     * Create the share button and its listener.
     */

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

    /**
     * Create the save button and its listener.
     */

    private void createSaveButton() {
        final ImageButton saveButton = findViewById(R.id.saveButton);
        saveButtonListener(saveButton);
    }

    /**
     * Listener for the save button.
     * @param btn
     */
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

    /**
     * Show alert dialog asking the user for save permission.
     */

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

    /**
     * Show a message that the outfit has been saved.
     */

    private void showSavedMessage() {
        Bitmap bmp = Save.getBitmapFromView(frame);
        Save.saveImage(bmp, getApplicationContext());
        Toast.makeText(getApplicationContext(), R.string.saved, Toast.LENGTH_SHORT).show();
    }

    /**
     * For animation when showing/hiding the bottom sheet so that it doesn't show white space.
     */

    private void bottomSheetAnimation(){
        final LinearLayout wholeLayout = findViewById(R.id.whole_layout);
        setStartDelayToZero(wholeLayout);
        frame = this.findViewById(R.id.temporary_frame);
        setStartDelayToZero(frame);
        bottomSheet = this.findViewById(R.id.custom_bottom_sheet);
        setStartDelayToZero(bottomSheet);

        // For swiping the bottom sheet down
        wholeLayout.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeDown() {
                hideBottomSheet();
            }
        });
    }

    /**
     * Button for closing bottom sheet.
     */

    private void setCloseBottomSheet() {
        final ImageButton closeBottomSheet = findViewById(R.id.closeBottomSheet);
        closeBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheet.getVisibility() != LinearLayout.GONE) {
                    Thread th = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            hideBottomSheet();
                        }
                    });
                    th.run();
                }
            }
        });
    }

    /**
     * Create the recycler view with shirt and pants lists.
     * @param context
     */

    private void createRecyclerView(Context context){
        recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        recyclerView.setScrollbarFadingEnabled(false); // always visible
        shirtStyleList = loadStyleListFromXml("shirt_styles", context);
        pantsStyleList = loadStyleListFromXml("pant_styles", context);
    }

    /**
     * Create a listener for the recycler view.
     */

    private void recyclerViewListener(){
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Style currentStyle = currentItemList.get(position);
                if (currClothingPiece.equals("shirt")){
                    currShirt = currentStyle;
                    mainFragment.changeStyleShirt(currentStyle);
                    mainFragment.changeColorShirt(currShirtColor);
                } else if (currClothingPiece.equals("pant")){
                    currPant = currentStyle;
                    mainFragment.changeStylePant(currentStyle);
                    mainFragment.changeColorPant(currPantColor);
                }
            }

            @Override
            public void onLongClick(View view, int position) {}
        }));
    }

    /**
     * Create the color picker view and its listener.
     */

    private void createColorPickerView() {
        final ColorPickerView colorPicker = findViewById(R.id.colorPickerView);
        colorPicker.addOnColorSelectedListener(new OnColorSelectedListener() {
            @Override
            public void onColorSelected(int i) {
                changeStyleColor(i);
                checkColorShiftingEligibility();
            }
        });

        // color changes when lightness changes
        colorPicker.addOnColorChangedListener(new OnColorChangedListener() {
            @Override
            public void onColorChanged(int i) {
                changeStyleColorUnrecorded(i);
            }
        });
    }

    /**
     * Change style color when selected.
     * @param i the color being selected
     */

    private void changeStyleColor(int i){
        if (currClothingPiece.equals("shirt")) {
            currShirtColor = i;
            shirtColors.push(currShirtColor);
            mainFragment.changeColorShirt(i);
        } else {
            currPantColor = i;
            pantsColors.push(currPantColor);
            mainFragment.changeColorPant(i);
        }
    }

    /**
     * Change style color when selected, but not recording the color. This is for when you change
     * lightness and don't want to record every shade of the current color as it changes.
     * @param i the color being selected
     */

    private void changeStyleColorUnrecorded(int i){
        if (currClothingPiece.equals("shirt")){
            currShirtColor = i;
            mainFragment.changeColorShirt(i);
        } else {
            currPantColor = i;
            mainFragment.changeColorPant(i);
        }
    }

    /**
     * Create the undo color button and its listener.
     */

    private void createUndoColorButton() {
        // initial colors
        shirtColors.push(currShirtColor);
        pantsColors.push(currPantColor);

        undoColor = findViewById(R.id.undoColor);
        undoColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                undoColor();
                checkColorShiftingEligibility();
            }
        });
        // initial color of button, showing we cannot undo color yet
        undoColor.setColorFilter(Color.argb(100,119,136,153));
    }

    /**
     * Create the redo color button and its listener.
     */

    private void createRedoColorButton(){
        redoColor = findViewById(R.id.redoColor);
        redoColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redoColor();
                checkColorShiftingEligibility();
            }
        });
        // initial color of button, showing we cannot redo color yet
        redoColor.setColorFilter(Color.argb(100,119,136,153));
    }

    /**
     * Go to the previously chosen color.
     */

    private void undoColor(){
        if (canUndoColor()){
            if (currClothingPiece.equals("shirt")){
                shirtColors1.push(shirtColors.pop());
                colorShiftingUtil();
            } else {
                pantsColors1.push(pantsColors.pop());
                colorShiftingUtil();
            }
        }
    }

    /**
     * Go forward to the previously undone color.
     */

    private void redoColor(){
        if (canRedoColor()){
            if (currClothingPiece.equals("shirt")){
                shirtColors.push(shirtColors1.pop());
                colorShiftingUtil();
            } else {
                pantsColors.push(pantsColors1.pop());
                colorShiftingUtil();
            }
        }
    }

    /**
     * Update the clothing color when undoing/redoing colors. To be used in these aforementioned methods.
     */

    private void colorShiftingUtil(){
        if (currClothingPiece.equals("shirt")){
            currShirtColor = shirtColors.peek();
            mainFragment.changeColorShirt(currShirtColor);
        } else {
            currPantColor = pantsColors.peek();
            mainFragment.changeColorPant(currPantColor);
        }
    }

    /**
     * Disable a button and color it grey.
     * @param btn
     */

    private void disableButton(ImageButton btn){
        btn.setColorFilter(Color.argb(100,119,136,153));
        btn.setEnabled(false);
    }

    /**
     * Enable a button and color it white.
     * @param btn
     */

    private void enableButton(ImageButton btn){
        btn.setColorFilter(getResources().getColor(R.color.primary_light));
        btn.setEnabled(true);
    }

    /**
     * Check if we can currently going back and forth between colors.
     * Enable/disable color buttons accordingly.
     */

    public void checkColorShiftingEligibility(){
        if  (canUndoColor() && !undoColor.isEnabled()) {
            enableButton(undoColor);
        }
        else if (!canUndoColor() && undoColor.isEnabled())
            disableButton(undoColor);

        if  (canRedoColor() && !redoColor.isEnabled()){
            enableButton(redoColor);
        }
        else if (!canRedoColor() && redoColor.isEnabled())
            disableButton(redoColor);
    }

    /**
     * Check if we can go back one color.
     * @return
     */

    private boolean canUndoColor(){
        if (currClothingPiece.equals("shirt"))
            return shirtColors.size() > 1;
        else return pantsColors.size() > 1;
    }

    /**
     * Check if we can go forward one color.
     * @return
     */
    private boolean canRedoColor(){
        if (currClothingPiece.equals("shirt"))
            return !shirtColors1.isEmpty();
        else return !pantsColors1.isEmpty();
    }

    /**
     * Respond to the result of a save permission request. Use toasts to announce this result.
     * @param requestCode
     * @param permissions
     * @param grantResults
     */

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

    /**
     * Respond to the result of CameraActivity. Apply the chosen color to the current clothing item.
     * @param requestCode
     * @param resultCode
     * @param data
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == PICK_COLOR && resultCode == RESULT_OK) {
            changeStyleColor(data.getIntExtra("Selected color", currShirtColor));
            checkColorShiftingEligibility();
        }
    }

    /**
     * Establish the animation logic of shirts and pants in the recycler view.
     */

    protected void animationLogicShirt(){
        animationLogicForTypes("shirt",shirtStyleList);
    }

    protected void animationLogicPant(){
        animationLogicForTypes("pant",pantsStyleList);
    }

    /**
     * Animation logic for a generic type in the recycler view
     * @param type
     * @param styleList
     */

    protected void animationLogicForTypes(String type, List styleList){
        showBottomSheetTutorial(); // tutorial when bottom sheet is opened for the first time.

        currClothingPiece = type;
        if (currentItemList == styleList && bottomSheet.getVisibility()==LinearLayout.GONE) {
            makeBottomSheetVisible();
        }
        else if (currentItemList != styleList && bottomSheet.getVisibility()==LinearLayout.VISIBLE){
            currentItemList = styleList;
            updateRecyclerView(styleList);
        }
        else if (currentItemList != styleList && bottomSheet.getVisibility()==LinearLayout.GONE){
            currentItemList = styleList;
            updateRecyclerView(styleList);
            makeBottomSheetVisible();
        }
    }

    /**
     * Make the bottom sheet visible in a new thread. To be used in animation logic for clothing types.
     */

    private void makeBottomSheetVisible() {
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                bottomSheet.setVisibility(LinearLayout.VISIBLE);
            }
        });
        th.run();
    }

    /**
     * Update the recycler view to switch between shirts and pants.
     * @param styleList
     */

    private void updateRecyclerView(List styleList){
        sAdapter = new StyleAdapter(styleList);
        recyclerView.setAdapter(sAdapter);
    }

    /**
     * Load the style list from xml, so that we only have to add new styles to the xml. Use toasts to
     * announce if there is an error.
     * @param filename
     * @param context
     * @return a list of styles
     */
    private List<Style> loadStyleListFromXml(String filename, Context context){
        InputStream stream = null;
        // Instantiate the parser
        StyleParser styleParser = new StyleParser();
        List<Style> styles = null;

        try {
            AssetManager manager = getAssets();
            stream = manager.open(filename + ".xml");
            styles = styleParser.parse(stream, context);
        } catch (FileNotFoundException fE){
            Toast.makeText(context, "Error in loading styles.", Toast.LENGTH_LONG);
        } catch (XmlPullParserException xE) {
            Toast.makeText(context, "Error in parsing the styles file.", Toast.LENGTH_LONG);
        } catch (IOException iE) {
            Toast.makeText(context, "Error", Toast.LENGTH_LONG);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException iE) {
                    Toast.makeText(context, "Error", Toast.LENGTH_LONG);
                }
            }
        }

        return styles;
    }

    /**
     * Set the various start delays to 0 for automatic animation in layout changes.
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
     * Hide the bottom sheet.
     */
    public void hideBottomSheet(){
        bottomSheet.setVisibility(View.GONE);
    }

    /**
     * Share what the uri currently points to.
     * @param uri The uri of the sharable. In this case, it is the bitmap grabbed from the frame.
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
     * Delete a file.
     * @param file The directory of the file to be deleted.
     * @param filename The name of the file to be deleted.
     */
    private void deleteFile(File file, String filename){
        if (file.isDirectory()) {
            getApplicationContext().deleteFile(filename);
        }
    }

    /**
     * Request the WRITE_EXTERNAL_STORAGE permission.
     */
    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
        );
    }

    /**
     * Tutorial for the whole layout, at first launch.
     */

    private void showWholeLayoutTutorial() {
        MaterialShowcaseSequence wholeLayoutSequence = createShowcaseSequence(WHOLE_LAYOUT_SHOWCASE_ID);

        addToShowcaseSequence(findViewById(R.id.colorPickerView),
                "Tap on shirt or pants to change style and color",
                wholeLayoutSequence);
        addToShowcaseSequence(findViewById(R.id.shareButton),
                "Share current outfit",
                wholeLayoutSequence);
        addToShowcaseSequence(findViewById(R.id.saveButton),
                "Save current outfit to phone",
                wholeLayoutSequence);

        wholeLayoutSequence.start();
    }

    /**
     * Tutorial for the bottom sheet, at first launch.
     */

    private void showBottomSheetTutorial() {
        MaterialShowcaseSequence bottomSheetSequence = createShowcaseSequence(BOTTOM_SHEET_SHOWCASE_ID);

        addToShowcaseSequence(findViewById(R.id.recycler_view),
                "Scroll down to choose style",
                bottomSheetSequence);
        addToShowcaseSequence(findViewById(R.id.colorPickerView),
                "Tap to pick a color",
                bottomSheetSequence);
        addToShowcaseSequence(findViewById(R.id.v_lightness_slider),
                "Slide to change color lightness",
                bottomSheetSequence);
        addToShowcaseSequence(findViewById(R.id.undoColor),
                "Go back one color",
                bottomSheetSequence);
        addToShowcaseSequence(findViewById(R.id.redoColor),
                "Go forward one color",
                bottomSheetSequence);
        addToShowcaseSequence(findViewById(R.id.camera_button),
                "Take picture to pick color",
                bottomSheetSequence);
        addToShowcaseSequence(findViewById(R.id.closeBottomSheet),
                "Close bottom sheet",
                bottomSheetSequence);

        bottomSheetSequence.start();
    }

    /**
     * Create a showcase sequence for the tutorial at first launch. There will be a sequence for
     * the whole layout and one for the bottom sheet.
     * @param uniqueID
     * @return
     */
    private MaterialShowcaseSequence createShowcaseSequence(String uniqueID){
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view
        config.setRenderOverNavigationBar(true);

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, uniqueID);
        sequence.setConfig(config);

        return sequence;
    }

    /**
     * Add a showcase item to a specific showcase sequence for the tutorial.
     * @param view
     * @param message
     * @param sequence
     */
    private void addToShowcaseSequence(View view, String message, MaterialShowcaseSequence sequence){
        sequence.addSequenceItem(view, message, GOT_IT);
    }
}