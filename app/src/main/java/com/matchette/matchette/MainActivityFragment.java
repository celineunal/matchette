package com.matchette.matchette;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.Matrix4f;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicColorMatrix;
import android.renderscript.Type;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends android.app.Fragment {
    private View fragment;
    ImageView shirt;
    ImageView pant;
    LinearLayout shirtLayout;
    LinearLayout pantLayout;
    LinearLayout paddingLayout;
    RenderScript rs;
    ScriptIntrinsicColorMatrix matrix;
    public Bitmap shirtBitmapFinal;
    Bitmap pantBitmapFinal;
    Allocation finalAllocationShirt;
    Allocation finalAllocationPant;
    Allocation shirtAllocation;
    Allocation pantAllocation;

    private AsyncTask<String, Void, Void> task;

    public MainActivityFragment() {

    }

    private void createBitmapAndAllocation(String type, Style style){
        if (type.equals("shirt")){
            Bitmap shirtBitmap = drawableToBitmap(getResources().getDrawable(style.getRid(),getActivity().getTheme()));
            Type.Builder rgbType = new Type.Builder(rs, Element.RGBA_8888(rs)).setX(shirtBitmap.getWidth()).setY(shirtBitmap.getHeight());
            finalAllocationShirt =  Allocation.createTyped(rs, rgbType.create(), Allocation.USAGE_SCRIPT);
            shirtAllocation =  Allocation.createFromBitmap(rs,shirtBitmap);
            shirtBitmapFinal = Bitmap.createBitmap(shirtBitmap.getWidth(), shirtBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        } else {
            Bitmap pantBitmap = drawableToBitmap(getResources().getDrawable(style.getRid(),getActivity().getTheme()));
            Type.Builder rgbType = new Type.Builder(rs, Element.RGBA_8888(rs)).setX(pantBitmap.getWidth()).setY(pantBitmap.getHeight());
            finalAllocationPant =  Allocation.createTyped(rs, rgbType.create(), Allocation.USAGE_SCRIPT);
            pantAllocation = Allocation.createFromBitmap(rs,pantBitmap);
            pantBitmapFinal = Bitmap.createBitmap(pantBitmap.getWidth(), pantBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        }
    }

    protected void changeColorShirt(int color){
        changeColorBitmap(color,"shirt");
    }

    protected void changeColorPant(int color){
        changeColorBitmap(color,"pant");
    }

    protected void changeColorBitmap (int color, final String type) {
        task = new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... params) {
                changeBitmapUtil(Integer.parseInt(params[0]),params[1]);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (type.equals("shirt")){
                    shirt.setImageBitmap(shirtBitmapFinal);
                } else {
                    pant.setImageBitmap(pantBitmapFinal);
                }
                super.onPostExecute(aVoid);
            }
        }.execute(Integer.toString(color),type);
    }


    protected void changeBitmapUtil (int color, String type){
        Log.d("called", "times");
        int red = (color>>16)&0xFF;
        int green = (color>>8)&0xFF;
        int blue = (color)&0xFF;
        //pass these colors to the kernel
        float r = red/255f;
        float g = green/255f;
        float b = blue/255f;
        //float[] newColors = {r,g,b,1.0f};
        Matrix4f m = new Matrix4f(new float[]{
                r, 0f, 0f, 0f,
                0f, g, 0f, 0f,
                0f, 0f, b, 0f,
                0f, 0f, 0f, 1.0f
        });
        if (type.equals("shirt")){
            matrix.setColorMatrix(m);
            matrix.forEach(shirtAllocation, finalAllocationShirt);
            finalAllocationShirt.copyTo(shirtBitmapFinal);
        } else {
            matrix.setColorMatrix(m);
            matrix.forEach(pantAllocation, finalAllocationPant);
            finalAllocationPant.copyTo(pantBitmapFinal);
        }
    }

    public Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
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
        createBitmapAndAllocation("shirt",style);
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
        pantLayout.setLayoutParams(lp1);
        createBitmapAndAllocation("pant",style);
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
                act.checkColorShiftingEligibility();
            }
        });
        pant.setClickable(true);
        pant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity act = (MainActivity) getActivity();
                act.animationLogicPant();
                act.checkColorShiftingEligibility();
            }
        });
        rs = RenderScript.create(getActivity().getApplicationContext());
        matrix = ScriptIntrinsicColorMatrix.create(rs);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        MainActivity act = (MainActivity) getActivity();
        createBitmapAndAllocation("shirt",act.shirtStyleList.get(0));
        createBitmapAndAllocation("pant",act.pantsStyleList.get(0));
    }
}
