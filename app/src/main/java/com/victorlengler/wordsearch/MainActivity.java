package com.victorlengler.wordsearch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TableLayout table;
    int cellSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.table = findViewById(R.id.table);
    }

    public void generateGrid(){
        int max = 10;
        for (int i=0; i<max; i++){
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(cellSize * 10, cellSize));

            for (int j=0; i<max; i++) {
                TextView cell = new TextView(this);
                cell.setLayoutParams(new TableRow.LayoutParams(cellSize, cellSize));

                row.addView(cell);
            }
            this.table.addView(row);
        }
    }
}
