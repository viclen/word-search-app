package com.victorlengler.wordsearch;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Letter implements Serializable {
    private char value;
    private int x, y;

    private TextView view;

    private boolean found;

    public boolean isFound() {
        return found;
    }

    public void setFound(boolean found) {
        this.found = found;
    }

    public Letter(char value, int x, int y, int cellSize, int letterSize, Context context) {
        this.value = value;
        this.x = x;
        this.y = y;

        view = new TextView(context);
        view.setWidth(cellSize);
        view.setHeight(cellSize);
        view.setBackgroundResource(R.drawable.letter_border);
        view.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        view.setTextSize(letterSize);
        view.setText(value + "");
        view.setTypeface(Typeface.create("casual", Typeface.BOLD));
        view.setPadding(0,0,0,0);
    }

    public char getValue() {
        return value;
    }

    public void setValue(char value) {
        this.value = value;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public TextView getView() {
        return view;
    }

    public void setView(TextView view) {
        this.view = view;
    }

    @Override
    public String toString() {
        return "Letter{" +
                "value=" + value +
                ", x=" + x +
                ", y=" + y +
                '}';
    }

    @NonNull
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
