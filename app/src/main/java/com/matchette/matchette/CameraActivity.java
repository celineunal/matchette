package com.matchette.matchette;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.madrapps.eyedropper.EyeDropper;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

/**
 * This is the Camera Activity for taking a picture and picking a color from a spot on that picture.
 * When finished, the activity adds the chosen color to an intent to be passed back to the MainActivity.
 */

public class CameraActivity extends AppCompatActivity {

    private static final int REQUEST_TAKE_PHOTO = 1;
    final String SHOWCASE_ID = "125"; // Unique ID to show tutorial only once
    final String GOT_IT = "GOT IT";

    private ImageView imageView;
    private ImageView checkButton;
    private Bitmap colorPicture;

    private int currentSelectedColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        imageView = findViewById(R.id.captured_photo);

        createCheckButton();
        showTutorial();
        openCamera();
    }

    /**
     * Open up the camera to take a picture.
     */

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_TAKE_PHOTO);
        } else {
            Toast.makeText(getApplicationContext(), R.string.photoError, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Respond to the request to take a picture.
     * If allowed to take a picture, get a bitmap from the camera and set it on an ImageView
     * and pick a color from there.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            //this is your bitmap image and now you can do whatever you want with this
            colorPicture = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(colorPicture);

            pickColor(imageView);
        }
    }

    /**
     * Create a check button and its listener. This button reflects the color being tapped on.
     * When the check button is clicked, the selected color is added to an intent to be sent back
     * to the MainActivity, and CameraActivity is finished.
     */

    private void createCheckButton() {
        checkButton = findViewById(R.id.checkButton);
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent colorSelected = new Intent();
                colorSelected.putExtra("Selected color", currentSelectedColor);
                setResult(RESULT_OK, colorSelected);
                finish();
            }
        });
    }

    /**
     * Pick a color from an ImageView by tapping on a point of the ImageView. Reflect the selected
     * color on the check button.
     * Using EyeDropper by Madrapps
     * @param imageView
     */

    private void pickColor(ImageView imageView) {
        new EyeDropper(imageView, new EyeDropper.ColorSelectionListener() {
            @Override
            public void onColorSelected(@ColorInt int color) {
                currentSelectedColor = color;
                checkButton.setColorFilter(color);
            }
        });
    }

    /**
     * Show tutorial for this activity, when it is first opened.
     */

    private void showTutorial(){
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view
        config.setRenderOverNavigationBar(true);

        MaterialShowcaseSequence showcaseSequence = new MaterialShowcaseSequence(this, SHOWCASE_ID);
        showcaseSequence.setConfig(config);

        showcaseSequence.addSequenceItem(checkButton,
                "Tap on a point in the picture to pick color",
                GOT_IT);
        showcaseSequence.addSequenceItem(checkButton,
                "Color is reflected on check button. Click check to finish",
                GOT_IT);

        showcaseSequence.start();
    }
}
