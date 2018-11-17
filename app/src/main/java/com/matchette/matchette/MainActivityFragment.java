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

    protected void changeColorShirt(String color, Style style){
        ImageView view = shirt;
        int num = style.getNum();
        int rid = style.getRid();
        changeVectorColor(rid, view, num, color);
        view.invalidate();
    }

    protected void changeColorPant(String color, Style style){
        ImageView view = pant;
        int num = style.getNum();
        int rid = style.getRid();
        changeVectorColor(rid, view, num, color);
        view.invalidate();
    }

    protected void changeVectorColor(int rid, ImageView view, int num, String color) {
        VectorChildFinder vector = new VectorChildFinder(getActivity(), rid, view);
        for (int i = 1; i <= num; i++){
            vector.findPathByName("yolo" + i).setFillColor(Color.parseColor("#"+color));
        }
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
