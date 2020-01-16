package com.victorlengler.wordsearch;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

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

        TextView cell = new TextView(context);
        cell.setWidth(cellSize);
        cell.setHeight(cellSize);
        cell.setBackgroundResource(R.drawable.letter_border);
        cell.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        cell.setTextSize(letterSize);
        cell.setText(value + "");
        cell.setPadding(0,0,0,0);
        this.view = cell;
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
}
