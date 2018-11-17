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

    private void changeVectorColor(int rid, ImageView view, int num, String color) {
        VectorChildFinder vector = new VectorChildFinder(getActivity(), rid, view);
        for (int i = 1; i <= num; i++){
            vector.findPathByName("yolo" + i).setFillColor(Color.parseColor("#"+color));
        }
    }

    protected void changeStyleShirt(Style style) {
        LinearLayout.LayoutParams lp1 = makeLinearLayoutParam(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        LinearLayout.LayoutParams lp2 = makeLinearLayoutParam(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        lp2.weight = 0;
        paddingLayout.setLayoutParams(lp2);

        int rid = style.getRid();
        float weight = style.getWeight();
        shirt.setImageResource(rid);
        lp1.weight = weight;
        shirtLayout.setLayoutParams(lp1);
    }

    protected void changeStylePant(Style style) {
        LinearLayout.LayoutParams lp1 = makeLinearLayoutParam(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        LinearLayout.LayoutParams lp2 = makeLinearLayoutParam(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        lp2.weight = 0;
        paddingLayout.setLayoutParams(lp2);

        int rid = style.getRid();
        float weight = style.getWeight();
        pant.setImageResource(rid);
        lp1.weight = weight;
        shirtLayout.setLayoutParams(lp1);
    }

    private LinearLayout.LayoutParams makeLinearLayoutParam (int params, int height) {
        return new LinearLayout.LayoutParams(params, height);
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
