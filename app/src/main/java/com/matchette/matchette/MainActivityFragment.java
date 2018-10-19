package com.matchette.matchette;

import android.animation.LayoutTransition;
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
                }
                break;
        }
    }

    protected void changeColorUtil(String color, String style, String type, int num){
        int id = 0;
        switch(style){
            case "t-shirt":
                id = R.drawable.ic_shirt;
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
            case "pants":
                id = R.drawable.ic_pant;
                break;
            case "shorts":
                id = R.drawable.ic_shorts;
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
        switch(type){
            case "shirt":
                switch(style){
                    case "dress-shirt":
                        shirt.setImageResource(R.drawable.ic_dress_shirt);
                        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
                        lp1.weight=1.0f;
                        shirtLayout.setLayoutParams(lp1);
                        LinearLayout.LayoutParams lp5 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
                        lp5.weight=0f;
                        paddingLayout.setLayoutParams(lp5);
                        break;
                    case "polo":
                        shirt.setImageResource(R.drawable.ic_polo);
                        LinearLayout.LayoutParams lp4 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0);
                        lp4.weight=1.2f;
                        shirtLayout.setLayoutParams(lp4);
                        LinearLayout.LayoutParams lp6 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
                        lp6.weight=0f;
                        paddingLayout.setLayoutParams(lp6);
                        break;
                    case "t-shirt":
                        shirt.setImageResource(R.drawable.ic_shirt);
                        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
                        lp2.weight=1.1f;
                        shirtLayout.setLayoutParams(lp2);
                        LinearLayout.LayoutParams lp7 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
                        lp7.weight=0f;
                        paddingLayout.setLayoutParams(lp7);
                        break;
                    case "hoodie":
                        //change weight here
                        shirt.setImageResource(R.drawable.ic_hoodie);
                        LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
                        lp3.weight=1.2f;
                        shirtLayout.setLayoutParams(lp3);
                        LinearLayout.LayoutParams lp8 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
                        lp8.weight=0f;
                        paddingLayout.setLayoutParams(lp8);
                        break;
                }
                break;
            case "pant":
                switch(style){
                    case "pants":
                        pant.setImageResource(R.drawable.ic_pant);
                        LinearLayout.LayoutParams lpp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
                        lpp.weight=1.25f;
                        pantLayout.setLayoutParams(lpp);
                        LinearLayout.LayoutParams lp5 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
                        lp5.weight=0f;
                        paddingLayout.setLayoutParams(lp5);
                        break;
                    case "shorts":
                        pant.setImageResource(R.drawable.ic_shorts);
                        LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
                        lp3.weight=0.7f;
                        pantLayout.setLayoutParams(lp3);
                        LinearLayout.LayoutParams lp4 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
                        lp4.weight=0f;
                        paddingLayout.setLayoutParams(lp4);
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

/*        LinearLayout wholeFragment = fragment.findViewById(R.id.fragmentLayout);
        final LinearLayout snackBar = getActivity().findViewById(R.id.custom_snackbar);
        wholeFragment.setOnTouchListener(new OnSwipeTouchListener(getActivity().getApplicationContext()) {
            @Override
            public void onSwipeDown() {
                snackBar.setVisibility(LinearLayout.GONE);
            }

            @Override
            public void onSwipeUp() {
//                snackBar.setVisibility(LinearLayout.VISIBLE);
            }
        });*/

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
