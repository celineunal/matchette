package com.matchette.matchette;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.devs.vectorchildfinder.VectorChildFinder;

import org.xmlpull.v1.XmlPullParserException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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

    private RecyclerView recyclerViewShirt;
    private RecyclerView recyclerViewPant;
    private List<Style> shirtStyleList = new ArrayList<>();
    private List<Style> pantsStyleList = new ArrayList<>();

    Context context;
    MainActivity activity;

    public MainActivityFragment() {

    }

    protected void changeStyleColor(String color){

    }

    private void changeVectorColor(int rid, ImageView view, int num, String color) {
        VectorChildFinder vector = new VectorChildFinder(activity, rid, view);
        for (int i = 1; i <= num; i++){
            vector.findPathByName("yolo" + i).setFillColor(Color.parseColor("#"+color));
        }
    }

    private List<Style> loadStyleListFromXml(String filename, Context context){
        InputStream stream = null;
        // Instantiate the parser
        StyleParser styleParser = new StyleParser();
        List<Style> styles = null;

        try {
            AssetManager manager = activity.getAssets();
            //Log.d("manager", manager.toString());
            stream = manager.open(filename + ".xml");
            //Log.d("stream", stream.toString());
            styles = styleParser.parse(stream, context);
        } catch (FileNotFoundException fE){
            Toast.makeText(context, "There was an error loading styles.", Toast.LENGTH_LONG);
        } catch (XmlPullParserException xE) {
            Toast.makeText(context, "There was an error parsing the styles file.", Toast.LENGTH_LONG);
        } catch (IOException iE) {
            Toast.makeText(context, "There was an error, I do not know what.", Toast.LENGTH_LONG);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException iE) {
                    Toast.makeText(context, "What is IOException anyway?", Toast.LENGTH_LONG);
                }
            }
        }

        return styles;
    }

    private void recyclerViewListener(RecyclerView recyclerView, Context context){
        Log.d("RVListener", "running");
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(context, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (activity.getSnackBarVisibility() == LinearLayout.GONE) {
                    activity.showSnackBar();
                } else if (activity.getSnackBarVisibility() == LinearLayout.VISIBLE) {
                    activity.hideSnackbar();
                }
            }

            @Override
            public void onLongClick(View view, int position) {}
        }));
    }

    private void createRecyclerView(Context context, RecyclerView recyclerView){
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        if (recyclerView == recyclerViewShirt) {
            mLayoutManager.scrollToPosition(Integer.MAX_VALUE / 2 - 3);
        } else if (recyclerView == recyclerViewPant) {
            mLayoutManager.scrollToPosition(Integer.MAX_VALUE / 2);
        }
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
    }

    private void updateRecyclerView(List styleList, RecyclerView recyclerView){
        StyleAdapter sAdapter = new StyleAdapter(styleList);
        recyclerView.setAdapter(sAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        fragment = inflater.inflate(R.layout.fragment_main, container, false);
        activity = (MainActivity) getActivity();
        context = activity.getApplicationContext();


        recyclerViewShirt = fragment.findViewById(R.id.recycler_view_shirt);
        recyclerViewPant = fragment.findViewById(R.id.recycler_view_pant);

        shirtStyleList = loadStyleListFromXml("shirt_styles", context);
        pantsStyleList = loadStyleListFromXml("pant_styles", context);
        createRecyclerView(context, recyclerViewShirt);
        createRecyclerView(context, recyclerViewPant);
        updateRecyclerView(shirtStyleList, recyclerViewShirt);
        updateRecyclerView(pantsStyleList, recyclerViewPant);
        recyclerViewListener(recyclerViewShirt, context);
        recyclerViewListener(recyclerViewPant, context);


        return fragment;
    }
}
