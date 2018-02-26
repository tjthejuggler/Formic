package com.example.malabarista.formic;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

/**
 * Created by malabarista on 11/23/2016.
 */

public class NumberPicker extends android.widget.NumberPicker {



    public NumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void addView(View child) {
        super.addView(child);
        updateView(child);
    }

    @Override
    public void addView(View child, int index, android.view.ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        updateView(child);
    }

    @Override
    public void addView(View child, android.view.ViewGroup.LayoutParams params) {
        super.addView(child, params);
        updateView(child);
    }

    @Override
    protected float getTopFadingEdgeStrength(){
        return 1.0f;
    }
    @Override
    protected float getBottomFadingEdgeStrength(){
        return 1.0f;
    }

    private void updateView(View view) {
        if(view instanceof EditText){
            ((EditText) view).setTextSize(50);
            ((EditText) view).setTextColor(Color.parseColor("#FFFFFF"));
        }
    }

}
