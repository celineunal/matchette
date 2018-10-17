package com.matchette.matchette;

import android.animation.LayoutTransition;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.skydoves.colorpickerpreference.ColorEnvelope;
import com.skydoves.colorpickerpreference.ColorListener;
import com.skydoves.colorpickerpreference.ColorPickerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    String [] shirts;
    String [] pants;
    String shirtColor;
    String pantColor;
    String shirtType;
    String pantType;

    //For the recyclerView
    private List<Style> shirtStyleList = new ArrayList<>();
    private List<Style> pantsStyleList = new ArrayList<>();
    private List<Style> currentItemList = new ArrayList<>();
    private RecyclerView recyclerView;
    private StyleAdapter sAdapter;

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


        //Temporary buttons representing shirt and pants. Should show/hide the custom snackbar.
        final Button shirtButton = findViewById(R.id.shirtButton);
        final Button pantsButton = findViewById(R.id.pantsButton);


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
                // fragment.setStyle(curentStyle, type); ??
                Toast.makeText(getApplicationContext(), currentStyle.getName() + " " + currentStyle.getRid(), Toast.LENGTH_SHORT).show(); // temporary
            }

            @Override
            public void onLongClick(View view, int position) {}
        }));


        /*Set the shirtButton and pantsButton behavior, not satisfied by this but it's a start. It forces me to have a View parameter
        in the onClick method override.
         */

        shirtButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });

        pantsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });


        // Set the closeSnackbar behavior.
        closeSnackbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (snackBar.getVisibility() != LinearLayout.GONE) {
                    //hide_layout(snackBar);
                    snackBar.setVisibility(LinearLayout.GONE);
                }

            }
        });

        // Set up the color picker
        colorPicker.setColorListener(new ColorListener() {
            @Override
            public void onColorSelected(ColorEnvelope colorEnvelope) {
                frame.setBackgroundColor(colorEnvelope.getColor());
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

    private void prepareShirtStyles () {
        Style style = new Style("T-shirt", R.drawable.tshirt);
        shirtStyleList.add(style);

        style = new Style("Shirt", R.drawable.shirt);
        shirtStyleList.add(style);

        style = new Style("Polo Shirt", R.drawable.polo);
        shirtStyleList.add(style);

    }

    private void preparePantsStyles() {
        Style style = new Style("Pants", R.drawable.pants);
        pantsStyleList.add(style);

        style = new Style("Shorts", R.drawable.shorts);
        pantsStyleList.add(style);
    }



    //    //Implement custom animation
//    /**
//     * Shows the custom snackbar with animation.
//     *
//     * @param l is the LinearLayout you want to be visible.
//     */
//    public void show_layout(LinearLayout l) {
//        final int screenHeight = getScreenHeight();
//        ObjectAnimator animator = ObjectAnimator.ofFloat(l, "y", screenHeight, (screenHeight * 0.619F));
//        animator.setInterpolator(new DecelerateInterpolator());
//        animator.setStartDelay(0);
//        animator.start();
//    }
//
//    /**
//     * Hides the custom snackbar with animation.
//     *
//     * @param l is the LinearLayout you want to be hidden.
//     */
//    public void hide_layout(LinearLayout l) {
//        final int screenHeight = getScreenHeight();
//        ObjectAnimator animator = ObjectAnimator.ofFloat(l, "y", screenHeight);
//        animator.setInterpolator(new DecelerateInterpolator());
//        animator.setStartDelay(0);
//        animator.start();
//    }
//    /**
//     //     * Gets the screen height.
//     //     * @return The screen height as an int.
//     //     */
//    public int getScreenHeight() {
//        DisplayMetrics displaymetrics = new DisplayMetrics();
//        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
//        return displaymetrics.heightPixels;
//    }
}

