package com.victorlengler.wordsearch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    int cellSize = 70;
    int max = 10;
    int letterSize = 24;
    int wordsRows = 2;

    List<String> wordsToFind = new ArrayList(Arrays.asList("SWIFT", "KOTLIN", "OBJECTIVEC", "VARIABLE", "JAVA", "MOBILE"));

    TextView selectingTextView;

    final String STATE_FOUND = "found";
    final String STATE_GRID = "grid";

    Grid mGrid;
    ArrayList mFoundWords = new ArrayList();
    TableLayout mWordsLayout;
    ConstraintLayout mainLayout;
    Letter[][] lettersArray = new Letter[max][max];

    ArrayList<View> alreadyAnimated = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState != null){
            mFoundWords = savedInstanceState.getStringArrayList(STATE_FOUND);
            lettersArray = (Letter[][])savedInstanceState.getSerializable(STATE_GRID);

            if(lettersArray == null){
                lettersArray = new Letter[max][max];
            }
            if(mFoundWords == null){
                mFoundWords = new ArrayList();
            }
        }

        this.mGrid = findViewById(R.id.grid);
        this.selectingTextView = findViewById(R.id.selectingTextView);
        this.selectingTextView.setText("");

        this.mWordsLayout = findViewById(R.id.wordsTable);
//        mWordsLayout.setStretchAllColumns(true);

        this.mainLayout = (ConstraintLayout) findViewById(R.id.mainLayout);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            this.cellSize = width / 11;
            wordsRows = 2;

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(mainLayout);

            constraintSet.connect(R.id.selectingTextView,ConstraintSet.TOP,R.id.mainLayout,ConstraintSet.TOP,16);
            constraintSet.connect(R.id.selectingTextView,ConstraintSet.BOTTOM,R.id.grid,ConstraintSet.TOP,16);
            constraintSet.connect(R.id.selectingTextView,ConstraintSet.LEFT,R.id.wordsTable,ConstraintSet.LEFT,0);
            constraintSet.connect(R.id.selectingTextView,ConstraintSet.RIGHT,R.id.wordsTable,ConstraintSet.RIGHT,0);

            constraintSet.connect(R.id.grid,ConstraintSet.LEFT,R.id.mainLayout,ConstraintSet.LEFT,0);
            constraintSet.connect(R.id.grid,ConstraintSet.RIGHT,R.id.mainLayout,ConstraintSet.RIGHT,0);

            constraintSet.connect(R.id.wordsTable,ConstraintSet.LEFT,R.id.mainLayout,ConstraintSet.LEFT,0);
            constraintSet.connect(R.id.wordsTable,ConstraintSet.RIGHT,R.id.mainLayout,ConstraintSet.RIGHT,0);
            constraintSet.connect(R.id.wordsTable,ConstraintSet.TOP,R.id.grid,ConstraintSet.BOTTOM,0);
            constraintSet.connect(R.id.wordsTable,ConstraintSet.BOTTOM,R.id.mainLayout,ConstraintSet.BOTTOM,0);

            constraintSet.applyTo(mainLayout);
        }else{
            this.cellSize = height / 13;
            this.letterSize = cellSize / 3;
            wordsRows = 6;

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(mainLayout);

            constraintSet.connect(R.id.selectingTextView,ConstraintSet.TOP,R.id.mainLayout,ConstraintSet.TOP,0);
            constraintSet.connect(R.id.selectingTextView,ConstraintSet.BOTTOM,R.id.wordsTable,ConstraintSet.TOP,0);
            constraintSet.connect(R.id.selectingTextView,ConstraintSet.RIGHT,R.id.wordsTable,ConstraintSet.RIGHT,0);
            constraintSet.connect(R.id.selectingTextView,ConstraintSet.LEFT,R.id.wordsTable,ConstraintSet.LEFT,0);

            constraintSet.connect(R.id.wordsTable,ConstraintSet.LEFT,R.id.mainLayout,ConstraintSet.LEFT,0);
            constraintSet.connect(R.id.wordsTable,ConstraintSet.BOTTOM,R.id.grid,ConstraintSet.BOTTOM,0);
            constraintSet.connect(R.id.wordsTable,ConstraintSet.RIGHT,R.id.grid,ConstraintSet.LEFT,0);

            constraintSet.connect(R.id.grid,ConstraintSet.LEFT,R.id.wordsTable,ConstraintSet.RIGHT,0);
            constraintSet.connect(R.id.grid,ConstraintSet.RIGHT,R.id.mainLayout,ConstraintSet.RIGHT,0);

            constraintSet.applyTo(mainLayout);
        }

        mGrid.generateGrid(wordsToFind, max, cellSize, letterSize, lettersArray);

        for(int i = 0; i < wordsRows; i++){
            mWordsLayout.addView(new TableRow(this));
            TableRow tr = (TableRow) mWordsLayout.getChildAt(i % wordsRows);
            for(int j = 0; j < wordsToFind.size() / wordsRows; j++){
                int index = i * (wordsToFind.size() / wordsRows) + j;
                String word = wordsToFind.get(index);
                TextView t = new TextView(this);
                t.setPadding(10, 10, 10, 10);
                t.setText(word);
                t.setTextSize(letterSize);
                t.setTextColor(getResources().getColor(R.color.colorToFind));
                t.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
                t.setTypeface(Typeface.create("casual", Typeface.BOLD));
                if(mFoundWords.contains(word)){
                    t.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    t.setTextColor(getResources().getColor(R.color.colorFoundWord));
                    blink(t);
                }
                tr.addView(t);
            }
        }

        update();
    }

    public void update(){
        List<Letter> selectedLetters = mGrid.getSelectedLetters();

        String s = "";
        for (int i = 0; i < selectedLetters.size(); i++) {
            selectedLetters.get(i).getView().setBackgroundResource(R.drawable.letter_selected);
            s += selectedLetters.get(i).getValue();
        }
        selectingTextView.setTextColor(Color.BLACK);
        selectingTextView.setText(s);
        selectingTextView.setTextSize(letterSize);

        for (int i = 0; i < max; i++) {
            for (int j = 0; j < max; j++) {
                Letter l = lettersArray[i][j];
                if (!selectedLetters.contains(l)) {
                    l.getView().setBackgroundResource(l.isFound() ? R.drawable.letter_found : R.drawable.letter_border);
                    l.getView().setTextColor(l.isFound() ? Color.WHITE : getResources().getColor(R.color.colorLetter));
                    if(l.isFound()){
                        blink(l.getView());
                    }
                }
            }
        }

        for (int i = 0; i < wordsToFind.size(); i++){
            TableRow tr = (TableRow) mWordsLayout.getChildAt(i % wordsRows);
            for(int j = 0; j < wordsToFind.size() / wordsRows; j++){
                TextView t = (TextView) tr.getChildAt(j);
                if(mFoundWords.contains(t.getText())){
                    t.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    t.setTextColor(getResources().getColor(R.color.colorFoundWord));
                    blink(t);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        List<Letter> selectedLetters = mGrid.getSelectedLetters();

        if (event.getAction() == MotionEvent.ACTION_UP) {
//            if (verifySelected()) {
//                for (int i = 0; i < selectedLetters.size(); i++) {
//                    selectedLetters.get(i).setFound(true);
//                }
//            }
//
//            mGrid.setSelectedLetters(new ArrayList());
//
//            mGrid.setFirstSelected(null);

            mGrid.onTouchEvent(event);
        }

        this.update();

        return super.onTouchEvent(event);
    }

    public boolean verifySelected() {
        List<Letter> selectedLetters = mGrid.getSelectedLetters();
        String word = "";
        for (int i = 0; i < selectedLetters.size(); i++) {
            word += selectedLetters.get(i).getValue();
        }

        if (wordsToFind.contains(word)) {
            mFoundWords.add(word);
            return true;
        } else if (wordsToFind.contains(new StringBuilder(word).reverse().toString())) {
            mFoundWords.add(new StringBuilder(word).reverse().toString());
            return true;
        }

        return false;
    }

    public void blink(final View view){
        if(alreadyAnimated.contains(view)){
            return;
        }

        alreadyAnimated.add(view);

        final Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
        view.startAnimation(animation);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    view.clearAnimation();
                } catch (Exception e) {
                    System.err.println(e);
                }
            }
        }).start();
    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putSerializable(STATE_GRID, lettersArray);
//        outState.putStringArrayList(STATE_FOUND, mFoundWords);
//    }

//    @Override
//    public void onConfigurationChanged(@NonNull Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        int height = displayMetrics.heightPixels;
//        int width = displayMetrics.widthPixels;
//        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
//            this.cellSize = width / 10;
//            this.letterSize = cellSize / 2;
//            mWordsLayout.setOrientation(LinearLayout.HORIZONTAL);
//        }else{
//            this.cellSize = height / 10;
//            this.letterSize = cellSize / 2;
//            mWordsLayout.setOrientation(LinearLayout.VERTICAL);
//        }
//    }
}
