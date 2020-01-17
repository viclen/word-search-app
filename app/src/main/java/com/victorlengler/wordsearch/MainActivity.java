package com.victorlengler.wordsearch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
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

    final List<String> wordsToFind = new ArrayList(Arrays.asList("SWIFT", "KOTLIN", "OBJECTIVEC", "VARIABLE", "JAVA", "MOBILE"));

    TextView selectingTextView;

    final String STATE_FOUND = "found";
    final String STATE_GRID = "grid";
    final String STATE_LETTERS = "letters";

    Grid mGrid;
    ArrayList mFoundWords = new ArrayList();
    TableLayout mWordsLayout;
    ConstraintLayout mainLayout;
    Letter[][] lettersArray = new Letter[max][max];
    TextView winningText;
    Button btnNewGame;

    ArrayList<View> alreadyAnimated = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.mGrid = findViewById(R.id.grid);
        this.selectingTextView = findViewById(R.id.selectingTextView);

        this.mWordsLayout = findViewById(R.id.wordsTable);

        this.mainLayout = (ConstraintLayout) findViewById(R.id.mainLayout);

        this.btnNewGame = (Button)findViewById(R.id.buttonNewGame);

        btnNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lettersArray = new Letter[max][max];
                mFoundWords = new ArrayList();
                mGrid.removeAllViews();
                hideYouWin();
                loadGame();
            }
        });

        this.winningText = (TextView) findViewById(R.id.winningText);
        hideYouWin();

        if (savedInstanceState != null) {
            lettersArray = new Letter[10][10];

            for(int i = 0; i<max; i++){
                for(int j = 0; j<max; j++){
                    Letter l = (Letter)savedInstanceState.getSerializable("Letter" + i * max + j);
                    if(l != null){
                        lettersArray[i][j] = l;
                    }else{
                        break;
                    }
                }
            }

            mFoundWords = savedInstanceState.getStringArrayList(STATE_FOUND);

            if (lettersArray == null) {
                lettersArray = new Letter[max][max];
            }else {
                mGrid.setLettersArray(lettersArray);
            }
            if (mFoundWords == null) {
                mFoundWords = new ArrayList();
            }

            if(mFoundWords.size() == wordsToFind.size()){
                showYouWin();
            }
        }

        measure();

        loadGame();
    }

    public void measure(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;

            this.cellSize = width / 11;
            wordsRows = 2;

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(mainLayout);

            constraintSet.connect(R.id.selectingTextView, ConstraintSet.BOTTOM, R.id.grid, ConstraintSet.TOP, 8);
            constraintSet.connect(R.id.selectingTextView, ConstraintSet.LEFT, R.id.wordsTable, ConstraintSet.LEFT, 0);
            constraintSet.connect(R.id.selectingTextView, ConstraintSet.RIGHT, R.id.wordsTable, ConstraintSet.RIGHT, 0);

            constraintSet.connect(R.id.grid, ConstraintSet.LEFT, R.id.mainLayout, ConstraintSet.LEFT, 0);
            constraintSet.connect(R.id.grid, ConstraintSet.RIGHT, R.id.mainLayout, ConstraintSet.RIGHT, 0);

            constraintSet.connect(R.id.wordsTable, ConstraintSet.LEFT, R.id.mainLayout, ConstraintSet.LEFT, 0);
            constraintSet.connect(R.id.wordsTable, ConstraintSet.RIGHT, R.id.mainLayout, ConstraintSet.RIGHT, 0);
            constraintSet.connect(R.id.wordsTable, ConstraintSet.TOP, R.id.grid, ConstraintSet.BOTTOM, 0);
            constraintSet.connect(R.id.wordsTable, ConstraintSet.BOTTOM, R.id.mainLayout, ConstraintSet.BOTTOM, 0);

            constraintSet.applyTo(mainLayout);
        } else {
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;

            getSupportActionBar().hide();

            this.cellSize = height / 11;
            this.letterSize = cellSize / 3;
            wordsRows = 6;

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(mainLayout);

            constraintSet.connect(R.id.selectingTextView, ConstraintSet.TOP, R.id.mainLayout, ConstraintSet.TOP, 0);
            constraintSet.connect(R.id.selectingTextView, ConstraintSet.BOTTOM, R.id.wordsTable, ConstraintSet.TOP, 0);
            constraintSet.connect(R.id.selectingTextView, ConstraintSet.RIGHT, R.id.wordsTable, ConstraintSet.RIGHT, 0);
            constraintSet.connect(R.id.selectingTextView, ConstraintSet.LEFT, R.id.wordsTable, ConstraintSet.LEFT, 0);

            constraintSet.connect(R.id.wordsTable, ConstraintSet.LEFT, R.id.mainLayout, ConstraintSet.LEFT, 0);
            constraintSet.connect(R.id.wordsTable, ConstraintSet.BOTTOM, R.id.grid, ConstraintSet.BOTTOM, 0);
            constraintSet.connect(R.id.wordsTable, ConstraintSet.RIGHT, R.id.grid, ConstraintSet.LEFT, 0);

            constraintSet.connect(R.id.grid, ConstraintSet.RIGHT, R.id.buttonNewGame, ConstraintSet.LEFT, 8);

            constraintSet.applyTo(mainLayout);
        }
    }

    public void loadGame() {
        this.selectingTextView.setText("");

        mGrid.generateGrid(wordsToFind, max, cellSize, letterSize, lettersArray);

        mWordsLayout.removeAllViews();
        for (int i = 0; i < wordsRows; i++) {
            mWordsLayout.addView(new TableRow(this));
            TableRow tr = (TableRow) mWordsLayout.getChildAt(i % wordsRows);
            for (int j = 0; j < wordsToFind.size() / wordsRows; j++) {
                int index = i * (wordsToFind.size() / wordsRows) + j;
                String word = wordsToFind.get(index);
                TextView t = new TextView(this);
                t.setPadding(10, 10, 10, 10);
                t.setText(word);
                t.setTextSize(letterSize);
                t.setTextColor(getResources().getColor(R.color.colorToFind));
                t.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
                t.setTypeface(Typeface.create("casual", Typeface.BOLD));
                if (mFoundWords.contains(word)) {
                    t.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    t.setTextColor(getResources().getColor(R.color.colorFoundWord));
                    blink(t);
                }
                tr.addView(t);
            }
        }

        update();
    }

    public void update() {
        List<Letter> selectedLetters = mGrid.getSelectedLetters();

        String s = "";
        for (int i = 0; i < selectedLetters.size(); i++) {
            selectedLetters.get(i).getView().setBackgroundResource(R.drawable.letter_selected);
            s += selectedLetters.get(i).getValue();
        }
        selectingTextView.setText(Html.fromHtml("<u>" + s + "</u>"));
        selectingTextView.setTextSize(letterSize);

        for (int i = 0; i < max; i++) {
            for (int j = 0; j < max; j++) {
                Letter l = lettersArray[i][j];
                if (!selectedLetters.contains(l)) {
                    l.getView().setBackgroundResource(l.isFound() ? R.drawable.letter_found : R.drawable.letter_border);
                    l.getView().setTextColor(l.isFound() ? Color.WHITE : getResources().getColor(R.color.colorLetter));
                    if (l.isFound()) {
                        blink(l.getView());
                    }
                }
            }
        }

        for (int i = 0; i < wordsToFind.size(); i++) {
            TableRow tr = (TableRow) mWordsLayout.getChildAt(i % wordsRows);
            for (int j = 0; j < wordsToFind.size() / wordsRows; j++) {
                TextView t = (TextView) tr.getChildAt(j);
                if (mFoundWords.contains(t.getText())) {
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
            if (verifySelected()) {
                for (int i = 0; i < selectedLetters.size(); i++) {
                    selectedLetters.get(i).setFound(true);
                }
            }

            mGrid.setSelectedLetters(new ArrayList());

            mGrid.setFirstSelected(null);
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

        boolean found = false;

        if (wordsToFind.contains(word)) {
            if(!mFoundWords.contains(word)){
                mFoundWords.add(word);
            }
            found = true;
        } else if (wordsToFind.contains(new StringBuilder(word).reverse().toString())) {
            String w = new StringBuilder(word).reverse().toString();
            if(!mFoundWords.contains(w)) {
                mFoundWords.add(w);
            }
            found = true;
        }

        if(mFoundWords.size() == wordsToFind.size()){
            showYouWin();
        }

        return found;
    }

    public void blink(final View view) {
        if (alreadyAnimated.contains(view)) {
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

    public void showYouWin() {
        final Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.you_win_anim);
        winningText.startAnimation(animation);
        winningText.setAlpha(1);

        btnNewGame.getBackground().setTint(Color.WHITE);
        btnNewGame.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
    }

    public void hideYouWin() {
        winningText.clearAnimation();
        winningText.setAlpha(0);

        btnNewGame.getBackground().setTint(getResources().getColor(R.color.colorPrimary));
        btnNewGame.setTextColor(Color.WHITE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        System.out.println("onSaveInstanceState");

        for(int i = 0; i<max; i++){
            for(int j = 0; j<max; j++){
                outState.putSerializable("Letter" + i * max + j, lettersArray[i][j]);
            }
        }

        outState.putStringArrayList(STATE_FOUND, mFoundWords);
    }

}
