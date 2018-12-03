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

public class CameraActivity extends AppCompatActivity {

    private static final int REQUEST_TAKE_PHOTO = 1;

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
}
