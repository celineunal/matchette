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

public class CameraActivity extends AppCompatActivity {

    private static final int REQUEST_TAKE_PHOTO = 1;
    final String SHOWCASE_ID = "123"; // Unique ID to show tutorial only once
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

//        showTutorial();
        getBitmapFromCamera();
    }

    private void getBitmapFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_TAKE_PHOTO);
        } else {
            Toast.makeText(getApplicationContext(), R.string.photoError, Toast.LENGTH_SHORT).show();
        }
    }

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

    private void pickColor(ImageView imageView) {
        new EyeDropper(imageView, new EyeDropper.ColorSelectionListener() {
            @Override
            public void onColorSelected(@ColorInt int color) {
                currentSelectedColor = color;
                checkButton.setColorFilter(color);
            }
        });
    }

    private void showTutorial(){
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view

        MaterialShowcaseSequence showcaseSequence = new MaterialShowcaseSequence(this, SHOWCASE_ID);
        showcaseSequence.setConfig(config);

        showcaseSequence.addSequenceItem(checkButton,
                "Take a picture then tap on a point to pick color",
                GOT_IT);
        showcaseSequence.addSequenceItem(checkButton,
                "Color is reflected on check button. Click check to finish",
                GOT_IT);

        showcaseSequence.start();
    }
}
