package com.victorlengler.wordsearch;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Letter implements Serializable {
    // the letter itself as a character
    private char value;
    // the position of the letter in the grid
    private int x, y;

    // the view to show in the grid view
    private TextView view;

    // whether this letter was found in a word
    private boolean found;

    // constructor
    public Letter(char value, int x, int y, int cellSize, int letterSize, Context context) {
        // set attributes
        this.value = value;
        this.x = x;
        this.y = y;
        this.found = false;

        // create the text view for the letter
        view = new TextView(context);
        // set the size
        view.setWidth(cellSize);
        view.setHeight(cellSize);
        view.setTextSize(letterSize);
        // set the background
        view.setBackgroundResource(R.drawable.letter_border);
        // set the text
        view.setText(value + "");
        // set the font style
        view.setTypeface(Typeface.create("casual", Typeface.BOLD));
        // remove any padding
        view.setPadding(0,0,0,0);
        // set the gravity to center
        view.setGravity(Gravity.CENTER);
    }

    // getters and setters
    public boolean isFound() {
        return found;
    }

    public void setFound(boolean found) {
        this.found = found;
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

    // to string
    @Override
    public String toString() {
        return "Letter{" +
                "value=" + value +
                ", x=" + x +
                ", y=" + y +
                '}';
    }

    // clone
    @NonNull
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
