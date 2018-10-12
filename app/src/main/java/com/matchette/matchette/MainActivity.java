package com.matchette.matchette;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // For animation when showing/hiding the snackbar
        final ViewGroup wholeLayout = findViewById(R.id.whole_layout);
        LayoutTransition transitioner1 = wholeLayout.getLayoutTransition();
        transitioner1.setStartDelay(LayoutTransition.CHANGE_DISAPPEARING, 0);
        transitioner1.setStartDelay(LayoutTransition.APPEARING, 0);
        transitioner1.setStartDelay(LayoutTransition.DISAPPEARING, 0);

        final ViewGroup frame = findViewById(R.id.temporary_frame);
        LayoutTransition transitioner2 = frame.getLayoutTransition();
        transitioner2.setStartDelay(LayoutTransition.CHANGE_DISAPPEARING, 0);
        transitioner2.setStartDelay(LayoutTransition.APPEARING, 0);
        transitioner2.setStartDelay(LayoutTransition.DISAPPEARING, 0);

        final ViewGroup snackbar = findViewById(R.id.custom_snackbar);
        LayoutTransition transitioner3 = snackbar.getLayoutTransition();
        transitioner3.setStartDelay(LayoutTransition.CHANGE_DISAPPEARING, 0);
        transitioner3.setStartDelay(LayoutTransition.APPEARING, 0);
        transitioner3.setStartDelay(LayoutTransition.DISAPPEARING, 0);

        //Temporary buttons 1 and 2, the one in the middle and the one at the bottom. Should show/hide the custom snackbar.
        final Button tempButton1 = findViewById(R.id.temporary_button);
        final Button tempButton2 = findViewById(R.id.temporary_button_2);


        //This is the custom snackbar that is set to be GONE at creation.
        final LinearLayout snackBar = this.findViewById(R.id.custom_snackbar);

        /*Set button 1 behavior, not satisfied by this but it's a start. It forces me to have a View parameter
        in the onClick method override.
         */
        tempButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (snackBar.getVisibility()==LinearLayout.GONE) {
                    snackBar.setVisibility(LinearLayout.VISIBLE);
                    show_layout(snackBar);

                } else {
                    hide_layout(snackBar);
                    snackBar.setVisibility(LinearLayout.GONE);
                }
            }
        });

        //Set the Button 2 behavior.
        tempButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (snackBar.getVisibility() != LinearLayout.GONE) {
                    hide_layout(snackBar);
                    snackBar.setVisibility(LinearLayout.GONE);
                }

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
        animator.start();
    }

    /*
     * Hides the custom snackbar with animation.
     *
     * @param l is the LinearLayout you want to be hidden.
     */
    public void hide_layout(LinearLayout l) {
        final int screenHeight = getScreenHeight();
        ObjectAnimator animator = ObjectAnimator.ofFloat(l, "y", screenHeight);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }

    /**
     * Gets the screen height.
     * @return The screen height as an int.
     */
    int getScreenHeight() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.heightPixels;
    }
}

