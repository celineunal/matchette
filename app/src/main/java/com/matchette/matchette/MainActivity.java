package com.matchette.matchette;

import android.animation.ObjectAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    //A variable that says if the snackbar is down (invisible) or not.
    boolean isDown = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Temporary buttons 1 and 2, the one in the middle and the one at the bottom. Should show/hide the custom snackbar.
        final Button tempButton1 = findViewById(R.id.temporary_button);
        final Button tempButton2 = findViewById(R.id.temporary_button_2);


        //This is the custom snackbar that is set to be GONE at creation.
        final LinearLayout snackBar = (LinearLayout)this.findViewById(R.id.custom_snackbar);


        /*Set button 1 behavior, not satisfied by this but it's a start. It forces me to have a View parameter
        in the onClick method override.
         */
        tempButton1.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick (View v){
                if (isDown) {
                    show_layout(snackBar);
                    isDown=false;
                } else {
                    hide_layout(snackBar);
                    isDown=true;
                }
            }
        });

        //Set the Button 2 behavior.
        tempButton2.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick (View v){

                hide_layout(snackBar);
                isDown=true;
            }
        });



    }

    /**
     * Shows the custom snackbar with animation.
     * @param l is the LinearLayout you want to be visible.
     */
    public void show_layout(LinearLayout l) {
        ObjectAnimator animation = ObjectAnimator.ofFloat(l, "translationY", l.getHeight());
        animation.setDuration(1000);
        animation.start();
        l.setVisibility(LinearLayout.VISIBLE);
    }

    /**
     * Hides the custom snackbar with animation.
     * @param l is the LinearLayout you want to be hidden.
     */
    public void hide_layout(LinearLayout l) {
        ObjectAnimator animation = ObjectAnimator.ofFloat(l, "translationY", -l.getHeight());
        animation.setDuration(1000);
        animation.start();
        l.setVisibility(LinearLayout.GONE);
    }

}

