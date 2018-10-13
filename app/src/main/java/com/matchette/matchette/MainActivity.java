package com.matchette.matchette;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.skydoves.colorpickerpreference.ColorEnvelope;
import com.skydoves.colorpickerpreference.ColorListener;
import com.skydoves.colorpickerpreference.ColorPickerView;

public class MainActivity extends AppCompatActivity {

    //Set the variables
    String [] shirts;
    String [] pants;
    String shirtColor;
    String pantColor;
    String shirtType;
    String pantType;

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



        final FrameLayout frame = findViewById(R.id.temporary_frame);
        LayoutTransition transitioner2 = frame.getLayoutTransition();
        transitioner2.setStartDelay(LayoutTransition.CHANGE_DISAPPEARING, 0);
        transitioner2.setStartDelay(LayoutTransition.APPEARING, 0);
        transitioner2.setStartDelay(LayoutTransition.DISAPPEARING, 0);
        transitioner2.setStartDelay(LayoutTransition.CHANGE_APPEARING, 0);
        transitioner2.setStartDelay(LayoutTransition.CHANGING, 0);

        //This is the custom snackbar that is set to be GONE at creation.
        final LinearLayout snackBar = this.findViewById(R.id.custom_snackbar);

        LayoutTransition transitioner3 = snackBar.getLayoutTransition();
        transitioner3.setStartDelay(LayoutTransition.CHANGE_DISAPPEARING, 0);
        transitioner3.setStartDelay(LayoutTransition.APPEARING, 0);
        transitioner3.setStartDelay(LayoutTransition.DISAPPEARING, 0);
        transitioner3.setStartDelay(LayoutTransition.CHANGE_APPEARING, 0);
        transitioner3.setStartDelay(LayoutTransition.CHANGING, 0);


        //Temporary buttons 1 and 2, the one in the middle and the one at the bottom. Should show/hide the custom snackbar.
        final Button tempButton1 = findViewById(R.id.temporary_button);
        final Button tempButton2 = findViewById(R.id.temporary_button_2);
        final ColorPickerView colorPicker = findViewById(R.id.colorPickerView);



        /*Set button 1 behavior, not satisfied by this but it's a start. It forces me to have a View parameter
        in the onClick method override.
         */
        tempButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (snackBar.getVisibility()==LinearLayout.GONE) {
                    snackBar.setVisibility(LinearLayout.VISIBLE);
                    //show_layout(snackBar);

                } else {
                    //hide_layout(snackBar);
                    snackBar.setVisibility(LinearLayout.GONE);
                }
            }
        });

        //Set the Button 2 behavior.
        tempButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (snackBar.getVisibility() != LinearLayout.GONE) {
                    //hide_layout(snackBar);
                    snackBar.setVisibility(LinearLayout.GONE);
                }

            }
        });



        colorPicker.setColorListener(new ColorListener() {
            @Override
            public void onColorSelected(ColorEnvelope colorEnvelope) {
                frame.setBackgroundColor(colorEnvelope.getColor());
            }
        });


    }

    //Implement custom animation -- tried to fix the bug here
    /**
     * Shows the custom snackbar with animation.
     *
     * @param l is the LinearLayout you want to be visible.
     */
    public void show_layout(LinearLayout l) {
        final int screenHeight = getScreenHeight();
        ObjectAnimator animator = ObjectAnimator.ofFloat(l, "y", screenHeight, (screenHeight * 0.619F));
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setStartDelay(0);
        animator.start();
    }

    /**
     * Hides the custom snackbar with animation.
     *
     * @param l is the LinearLayout you want to be hidden.
     */
    public void hide_layout(LinearLayout l) {
        final int screenHeight = getScreenHeight();
        ObjectAnimator animator = ObjectAnimator.ofFloat(l, "y", screenHeight);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setStartDelay(0);
        animator.start();
    }

    /**
     * Gets the screen height.
     * @return The screen height as an int.
     */
    public int getScreenHeight() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.heightPixels;
    }

    /**
     * Sets the shirtColor or pantColor variable.
     * @param colorHexValue
     * @param type Shirt or pants.
     */
    public void setColor (String colorHexValue, String type) {
        if (type == "shirt"){
            shirtColor = colorHexValue;
        } else if (type == "pants") {
            pantColor = colorHexValue;
        }
    }

    /**
     * Sets the shirtType or pantType variable.
     * @param style
     * @param type Shirt or pants.
     */
    public void setStyle (String style, String type) {
        if (type == "shirt"){
            shirtType= style;
        } else if (type == "pants") {
            pantType = style;
        }
    }
}
