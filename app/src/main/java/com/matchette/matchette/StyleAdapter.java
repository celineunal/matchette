package com.matchette.matchette;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.devs.vectorchildfinder.VectorChildFinder;

import java.util.ArrayList;
import java.util.List;

public class StyleAdapter extends RecyclerView.Adapter<StyleAdapter.MyViewHolder>{


    private List<Style> styleList;
    private Context context;
    public String color = "CCD1D9";

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class MyViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public ImageView style_image;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public MyViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            style_image = itemView.findViewById(R.id.style_image);

        }
    }

    public StyleAdapter(List<Style> styleList) {
        this.styleList = styleList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_style, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        //Next line is like a hack for endless recyclerview.
        position = position % styleList.size();
        Style style = styleList.get(position);
        int rid = style.getRid();
        int num = style.getNum();
        holder.style_image.setImageResource(rid);
        context = holder.style_image.getContext();
        changeVectorColor(rid, holder.style_image, num, color);
    }

    public int getActualItemCount() {
        if (styleList == null) {
            styleList = new ArrayList<>();
        }
        return styleList.size();
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    private void changeVectorColor(int rid, ImageView view, int num, String color) {
        VectorChildFinder vector = new VectorChildFinder(context, rid, view);
        for (int i = 1; i <= num; i++){
            vector.findPathByName("yolo" + i).setFillColor(Color.parseColor("#"+color));
        }
    }

    public void changeRVcolor(String color){
        this.color = color;
    }
}
