package com.matchette.matchette;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
    public String currSelection = "shirt";
    public String currColor = "CCD1D9";

    private Context context;
    private MainActivity activity;

    public MainActivityFragment() {

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

    private void recyclerViewListener(RecyclerView recyclerView, Context context, final String selection){
        Log.d("RVListener", "running");
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(context, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                currSelection = selection;
                if (activity.getSnackBarVisibility() == LinearLayout.GONE) {
                    activity.showSnackBar();
                }
            }

            @Override
            public void onLongClick(View view, int position) {}
        }));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currSelection = selection;
            }
        });
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

    private void updateRecyclerView(List styleList, RecyclerView recyclerView, String color){
        StyleAdapter sAdapter = new StyleAdapter(styleList);
        sAdapter.changeRVcolor(color);
        recyclerView.setAdapter(sAdapter);
    }

    public void updateShirtColorRV(String color) {
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerViewShirt.getLayoutManager();
        int position = linearLayoutManager.findFirstVisibleItemPosition();
        RecyclerView.LayoutManager nLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        nLayoutManager.scrollToPosition(position);
        recyclerViewShirt.setLayoutManager(nLayoutManager);
        updateRecyclerView(shirtStyleList, recyclerViewShirt, color);
    }

    public void updatePantColorRV(String color) {
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerViewPant.getLayoutManager();
        int position = linearLayoutManager.findFirstVisibleItemPosition();
        RecyclerView.LayoutManager nLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        nLayoutManager.scrollToPosition(position);
        recyclerViewPant.setLayoutManager(nLayoutManager);
        updateRecyclerView(pantsStyleList, recyclerViewPant, color);
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
        updateRecyclerView(shirtStyleList, recyclerViewShirt, activity.currShirtColor);
        updateRecyclerView(pantsStyleList, recyclerViewPant, activity.currPantColor);
        recyclerViewListener(recyclerViewShirt, context, "shirt");
        recyclerViewListener(recyclerViewPant, context, "pant");


        return fragment;
    }

}
