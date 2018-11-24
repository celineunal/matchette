package com.matchette.matchette;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.devs.vectorchildfinder.VectorChildFinder;

public class StyleImageView extends AppCompatImageView {

    private Context context;

    public StyleImageView(Context context) {
        super(context);
    }

    public StyleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StyleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void changeVectorColor(int rid, ImageView view, int num, String color) {
        VectorChildFinder vector = new VectorChildFinder(context, rid, view);
        for (int i = 1; i <= num; i++){
            vector.findPathByName("yolo" + i).setFillColor(Color.parseColor("#"+color));
        }
    }

}
