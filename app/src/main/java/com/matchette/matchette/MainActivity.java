package com.matchette.matchette;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.skydoves.colorpickerpreference.ColorEnvelope;
import com.skydoves.colorpickerpreference.ColorListener;
import com.flask.colorpicker.ColorPickerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private LinearLayout snackBar;
    private int count = 0;
    String [] shirts;
    String [] pants;
    String currShirtColor;
    String currPantColor;
    String currSnackbarSelection = "shirt";

    String currShirt = "t-shirt";
    String currPant = "pants";

    //For the recyclerView
    private List<Style> shirtStyleList = new ArrayList<>();
    private List<Style> pantsStyleList = new ArrayList<>();
    private List<Style> currentItemList = new ArrayList<>();
    private RecyclerView recyclerView;
    private StyleAdapter sAdapter;
    private MainActivityFragment mainFragment;

    protected String getCurrShirt(){
        return this.currShirt;
    }

    protected String getCurrPant(){
        return this.currPant;
    }

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
        snackBar = this.findViewById(R.id.custom_snackbar);

        LayoutTransition transitioner3 = snackBar.getLayoutTransition();
        transitioner3.setStartDelay(LayoutTransition.CHANGE_DISAPPEARING, 0);
        transitioner3.setStartDelay(LayoutTransition.APPEARING, 0);
        transitioner3.setStartDelay(LayoutTransition.DISAPPEARING, 0);
        transitioner3.setStartDelay(LayoutTransition.CHANGE_APPEARING, 0);
        transitioner3.setStartDelay(LayoutTransition.CHANGING, 0);


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
                Log.d("change", currSnackbarSelection + " | " + currentStyle.getName());
                if (currSnackbarSelection.equals("shirt")){
                    currShirt = currentStyle.getName();
                    mainFragment.changeStyle("shirt", currentStyle.getName());
                } else {
                    currPant = currentStyle.getName();
                    mainFragment.changeStyle("pant", currentStyle.getName());
                }
            }

            @Override
            public void onLongClick(View view, int position) {}
        }));

        //Set the closeSnackbar behavior.
        closeSnackbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (snackBar.getVisibility() != LinearLayout.GONE) {
                    snackBar.setVisibility(LinearLayout.GONE);
                }

            }
        });

        colorPicker.addOnColorSelectedListener(new OnColorSelectedListener() {
            @Override
            public void onColorSelected(int i) {
                if (currSnackbarSelection.equals("shirt")) {
                    mainFragment.changeColor(Integer.toHexString(i).toUpperCase(), currSnackbarSelection, currShirt);
                } else {
                    mainFragment.changeColor(Integer.toHexString(i).toUpperCase(), currSnackbarSelection, currPant);
                }
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

        mainFragment = new MainActivityFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.fragment, mainFragment);
        ft.commit();
    }


    protected void animationLogicShirt(){
        currSnackbarSelection = "shirt";
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

    protected void animationLogicPant(){
        currSnackbarSelection = "pant";
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

    private void prepareShirtStyles () {
        Style style = new Style("t-shirt", R.drawable.ic_shirt);
        shirtStyleList.add(style);

        style = new Style("dress-shirt", R.drawable.ic_dress_shirt);
        shirtStyleList.add(style);

        style = new Style("hoodie", R.drawable.ic_hoodie);
        shirtStyleList.add(style);

        style = new Style("polo", R.drawable.ic_polo);
        shirtStyleList.add(style);

    }

    private void preparePantsStyles() {
        Style style = new Style("pants", R.drawable.ic_pant);
        pantsStyleList.add(style);

//        style = new Style("shorts", R.drawable.ic_shorts);
//        pantsStyleList.add(style);
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

