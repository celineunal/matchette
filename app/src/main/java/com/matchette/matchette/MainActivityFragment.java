package com.matchette.matchette;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.devs.vectorchildfinder.VectorChildFinder;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends android.app.Fragment {
    View fragment;
    ImageView shirt;
    ImageView pant;
    LinearLayout shirtLayout;
    LinearLayout pantLayout;
    LinearLayout paddingLayout;

    public MainActivityFragment() {

    }

    protected void changeColor(String color, String type, String style){
        switch (type){
            case "shirt":
                switch(style){
                    case "t-shirt":
                        changeColorUtil(color, "t-shirt", type, 4);
                        break;
                    case "polo":
                        changeColorUtil(color, "polo", type, 1);
                        break;
                    case "dress-shirt":
                        changeColorUtil(color, "dress-shirt", type, 5);
                        break;
                    case "hoodie":
                        changeColorUtil(color, "hoodie", type, 1);
                        break;
                    case "woman-sleeveless-shirt":
                        changeColorUtil(color, "woman-sleeveless-shirt", type, 4);
                        break;
                    case "blouse":
                        changeColorUtil(color, "blouse", type, 3);
                        break;
                    case "man-coat":
                        changeColorUtil(color, "man-coat", type, 4);
                        break;
                    case "man-suit":
                        changeColorUtil(color, "man-suit", type, 1);
                        break;
                    case "woman-jacket":
                        changeColorUtil(color,"woman-jacket", type, 4);
                        break;
                    case "woman-suit":
                        changeColorUtil(color,"woman-suit", type, 5);
                        break;
                }
                break;
            case "pant":
                switch(style){
                    case "pants":
                        changeColorUtil(color, "pants",type,  1);
                        break;
                    case "shorts":
                        changeColorUtil(color, "shorts",type, 2);
                        break;
                    case "formal-pants":
                        changeColorUtil(color, "formal-pants",type, 1);
                        break;
                    case "woman-pants":
                        changeColorUtil(color, "woman-pants", type, 1);
                        break;
                    case "skirt":
                        changeColorUtil(color, "skirt", type, 1);
                        break;
                    case "long-skirt":
                        changeColorUtil(color, "long-skirt", type, 4);
                        break;
                    case "formal-skirt":
                        changeColorUtil(color, "formal-skirt", type, 2);
                        break;
                }
                break;
        }
    }

    protected void changeColorUtil(String color, String style, String type, int num){
        int id = 0;
        switch(style){
            case "t-shirt":
                id = R.drawable.ic_t_shirt;
                break;
            case "polo":
                id = R.drawable.ic_polo;
                break;
            case "dress-shirt":
                id = R.drawable.ic_dress_shirt;
                break;
            case "hoodie":
                id = R.drawable.ic_hoodie;
                break;
            case "woman-sleeveless-shirt":
                id = R.drawable.ic_woman_sleeveless_shirt;
                break;
            case "blouse":
                id = R.drawable.ic_blouse;
                break;
            case "man-coat":
                id = R.drawable.ic_man_coat;
                break;
            case "man-suit":
                id = R.drawable.ic_man_suit;
                break;
            case "woman-jacket":
                id = R.drawable.ic_woman_jacket;
                break;
            case "pants":
                id = R.drawable.ic_pant;
                break;
            case "shorts":
                id = R.drawable.ic_shorts;
                break;
            case "formal-pants":
                id = R.drawable.ic_formal_pants;
                break;
            case "woman-pants":
                id = R.drawable.ic_woman_pants;
                break;
            case "skirt":
                id = R.drawable.ic_skirt;
                break;
            case "long-skirt":
                id = R.drawable.ic_long_skirt;
                break;
            case "formal-skirt":
                id = R.drawable.ic_formal_skirt;
                break;
            case "woman-suit":
                id = R.drawable.ic_woman_suit;
                break;

        }
        ImageView view = (type.equals("shirt")) ? shirt:pant;
        VectorChildFinder vector = new VectorChildFinder(getActivity(), id, view);
        for (int i = 1; i <= num; i++){
            vector.findPathByName("yolo" + i).setFillColor(Color.parseColor("#"+color));
        }
        view.invalidate();
    }

    protected void changeStyle(String type, String style){
        Log.d("change 2", type + " | " + style);
        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        lp2.weight = 0;
        paddingLayout.setLayoutParams(lp2);

        switch(type){
            case "shirt":
                switch(style){
                    case "dress-shirt":
                        shirt.setImageResource(R.drawable.ic_dress_shirt);
                        lp1.weight=1.0f;
                        shirtLayout.setLayoutParams(lp1);
                        break;
                    case "polo":
                        shirt.setImageResource(R.drawable.ic_polo);
                        lp1.weight=1.2f;
                        shirtLayout.setLayoutParams(lp1);
                        break;
                    case "t-shirt":
                        shirt.setImageResource(R.drawable.ic_t_shirt);
                        lp1.weight=1.0f;
                        shirtLayout.setLayoutParams(lp1);
                        break;
                    case "hoodie":
                        shirt.setImageResource(R.drawable.ic_hoodie);
                        lp1.weight=1.2f;
                        shirtLayout.setLayoutParams(lp1);
                        break;
                    case "woman-sleeveless-shirt":
                        shirt.setImageResource(R.drawable.ic_woman_sleeveless_shirt);
                        lp1.weight=0.95f;
                        shirtLayout.setLayoutParams(lp1);
                        break;
                    case "blouse":
                        shirt.setImageResource(R.drawable.ic_blouse);
                        lp1.weight=1.2f;
                        shirtLayout.setLayoutParams(lp1);
                        break;
                    case "man-coat":
                        shirt.setImageResource(R.drawable.ic_man_coat);
                        lp1.weight=1.2f;
                        shirtLayout.setLayoutParams(lp1);
                        break;
                    case "man-suit":
                        shirt.setImageResource(R.drawable.ic_man_suit);
                        lp1.weight=1.25f;
                        shirtLayout.setLayoutParams(lp1);
                        break;
                    case "woman-jacket":
                        shirt.setImageResource(R.drawable.ic_woman_jacket);
                        lp1.weight=1.3f;
                        shirtLayout.setLayoutParams(lp1);
                        break;
                    case "woman-suit":
                        shirt.setImageResource(R.drawable.ic_woman_suit);
                        lp1.weight=1.4f;
                        shirtLayout.setLayoutParams(lp1);
                        break;
                }
                break;

            case "pant":
                switch(style){
                    case "pants":
                        pant.setImageResource(R.drawable.ic_pant);
                        lp1.weight=1.25f;
                        pantLayout.setLayoutParams(lp1);
                        break;
                    case "shorts":
                        pant.setImageResource(R.drawable.ic_shorts);
                        lp1.weight=0.7f;
                        pantLayout.setLayoutParams(lp1);
                        break;
                    case "formal-pants":
                        pant.setImageResource(R.drawable.ic_formal_pants);
                        lp1.weight=1.25f;
                        pantLayout.setLayoutParams(lp1);
                        break;
                    case "woman-pants":
                        pant.setImageResource(R.drawable.ic_woman_pants);
                        lp1.weight=1.2f;
                        pantLayout.setLayoutParams(lp1);
                        break;
                    case "skirt":
                        pant.setImageResource(R.drawable.ic_skirt);
                        lp1.weight=0.9f;
                        pantLayout.setLayoutParams(lp1);
                        break;
                    case "long-skirt":
                        pant.setImageResource(R.drawable.ic_long_skirt);
                        lp1.weight=1.25f;
                        pantLayout.setLayoutParams(lp1);
                        break;
                    case "formal-skirt":
                        pant.setImageResource(R.drawable.ic_formal_skirt);
                        lp1.weight=1.1f;
                        pantLayout.setLayoutParams(lp1);
                        break;
                }
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        fragment = inflater.inflate(R.layout.fragment_main, container, false);

        //Tried to get the swiping work in the fragment, but no dice.
/*
        LinearLayout wholeFragment = fragment.findViewById(R.id.fragmentLayout);
        wholeFragment.setOnTouchListener(new OnSwipeTouchListener(this.getContext()) {
            @Override
            public void onSwipeDown() {
                ((MainActivity)getActivity()).hideSnackbar();
            }

        });
*/

        pant = (ImageView) fragment.findViewById(R.id.pant);
        shirt = (ImageView) fragment.findViewById(R.id.shirt);
        shirtLayout = (LinearLayout) fragment.findViewById(R.id.shirtLayout);
        pantLayout = (LinearLayout) fragment.findViewById(R.id.pantLayout);
        paddingLayout = (LinearLayout) fragment.findViewById(R.id.paddingLayout);
        shirt.setClickable(true);
        shirt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity act = (MainActivity) getActivity();
                act.animationLogicShirt();
            }
        });
        pant.setClickable(true);
        pant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity act = (MainActivity) getActivity();
                act.animationLogicPant();
            }
        });

        return fragment;
    }
}
