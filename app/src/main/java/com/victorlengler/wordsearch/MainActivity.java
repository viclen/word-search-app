package com.victorlengler.wordsearch;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    TableLayout table;
    int cellSize = 50;
    String[] wordsToFind = {"Swift", "Kotlin", "ObjectiveC", "Variable", "Java", "Mobile"};
    String[][] grid = new String[10][10];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.table = findViewById(R.id.table);

        generateGrid();
    }

    public void generateGrid(){
        int max = 10;
        for (int i=0; i<max; i++){
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT));

            for (int j=0; j<max; j++) {
                char letter = randomLetter();

                TextView cell = new TextView(this);
                cell.setWidth(cellSize);
                cell.setHeight(cellSize);
                cell.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
//                cell.setBackgroundColor(Color.BLUE);
                cell.setText(letter + "");
                grid[i][j] = letter + "";

                row.addView(cell, j);
            }
            table.addView(row);
        }
    }

    public char randomLetter() {
        String allowed = "QWERTYUIOPASDFGHJKLZXCVBNM";

        Random rand = new Random();

        return allowed.charAt(rand.nextInt(26));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            System.out.println("DOWN");
        }else if(event.getAction() == MotionEvent.ACTION_UP) {
            System.out.println("UP");
        }else if(event.getAction() == MotionEvent.ACTION_MOVE) {
            System.out.println("MOVE");
        }

        return true;
    }
}
