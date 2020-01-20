package com.victorlengler.wordsearch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.Date;
import java.util.List;

public class GameActivity extends AppCompatActivity {
    // strings used to save data when orientation changes
    // found words
    final String STATE_FOUND = "found";
    // time when the game started
    final String STATE_TIMER = "timer";
    // timer current value
    final String STATE_CURRENT_TIMER = "currenttimer";
    // the size of the grid
    final String STATE_GRID_SIZE = "maxGridSize";

    // size of the cell containing the letter
    int cellSize = 70;
    // size of the grid (10x10)
    int maxGridSize = 10;
    // size of the letter
    int letterSize = 24;
    // number of rows for the words table
    int wordsRows = 2;

    // list of words to find in the grid
    List<String> wordsToFind = new ArrayList(Arrays.asList("SWIFT", "KOTLIN", "OBJECTIVEC", "VARIABLE", "JAVA", "MOBILE"));
    // view with the current selected letters from the grid
    TextView selectingTextView;
    // grid of letters
    Grid mGrid;
    // record of found words
    ArrayList mFoundWords = new ArrayList();
    // words found and o find shown in the UI
    TableLayout mWordsLayout;
    // root layout of the activity
    ConstraintLayout mainLayout;
    // array with the letters for the grid
    Letter[][] lettersArray = new Letter[maxGridSize][maxGridSize];
    // text shown when the user wins
    TextView winningText;
    // button to create a new game
    Button btnNewGame;
    // timer on top of the screen
    TextView timerText;
    // timer shown when the user wins
    TextView totalTime;
    // list of the view that have already blinked
    ArrayList<View> alreadyAnimated = new ArrayList();
    // time when the game started, used for the timer
    Date gameStartedDate;
    // if the timer is stopped, it doesnt count anymore
    boolean timerStopped;
    // the difficulty of the game
    int difficulty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // set the views to the variables
        this.mGrid = findViewById(R.id.grid);
        this.selectingTextView = findViewById(R.id.selectingTextView);
        this.mWordsLayout = findViewById(R.id.wordsTable);
        this.mainLayout = (ConstraintLayout) findViewById(R.id.mainLayout);
        this.timerText = (TextView) findViewById(R.id.timerText);
        this.totalTime = (TextView) findViewById(R.id.totalTimerText);
        this.btnNewGame = (Button) findViewById(R.id.buttonNewGame);
        this.winningText = (TextView) findViewById(R.id.winningText);

        // when the new game button is clicked
        btnNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeNewGame();
            }
        });

        // hide the 'You Win' message
        hideYouWin();

        // if the orientation changed
        if (savedInstanceState != null) {
            // get saved grid size
            maxGridSize = savedInstanceState.getInt(STATE_GRID_SIZE, 10);

            // if the dificulty is normal
            if (difficulty == 1) {
                wordsToFind = new ArrayList(Arrays.asList("SWIFT", "KOTLIN", "OBJECTIVEC", "VARIABLE", "JAVA", "MOBILE", "FLUTTER", ""));

                // if the dificulty is hard
            } else if (difficulty == 2) {
                wordsToFind = new ArrayList(Arrays.asList("SWIFT", "KOTLIN", "OBJECTIVEC", "VARIABLE", "JAVA", "MOBILE", "FLUTTER", "REACT"));

                // if the difficulty is easy
            } else {
                wordsToFind = new ArrayList(Arrays.asList("SWIFT", "KOTLIN", "OBJECTIVEC", "VARIABLE", "JAVA", "MOBILE"));
            }

            // create the letters array with the size of the grid
            lettersArray = new Letter[maxGridSize][maxGridSize];

            // iterates getting the saved letters and putting to the letters array
            for (int i = 0; i < maxGridSize; i++) {
                for (int j = 0; j < maxGridSize; j++) {
                    Letter l = (Letter) savedInstanceState.getSerializable("Letter" + i * maxGridSize + j);
                    if (l != null) {
                        lettersArray[i][j] = l;
                    } else {
                        break;
                    }
                }
            }

            // the list of words that have been found
            mFoundWords = savedInstanceState.getStringArrayList(STATE_FOUND);

            // if the letters array turns into null, creates a new empty
            if (lettersArray == null) {
                lettersArray = new Letter[maxGridSize][maxGridSize];

                // otherwise sets it to the grid
            } else {
                mGrid.setLettersArray(lettersArray);
            }

            // if the found words were not saved creates an empty list
            if (mFoundWords == null) {
                mFoundWords = new ArrayList();
            }

            // if the number of found words is the same as all words to find, it means the user won
            if (mFoundWords.size() == wordsToFind.size() || (mFoundWords.size() == wordsToFind.size() - 1 && wordsToFind.contains(""))) {
                // shows the You Win message
                showYouWin();

                // otherwise allows the timer to count
            } else {
                timerStopped = false;
            }

            // set the started date so it's not null
            gameStartedDate = new Date();
            // gets the started game time
            long timer = savedInstanceState.getLong(STATE_TIMER, 0);
            // if it has been saved
            if (timer > 0) {
                // set the game started time
                gameStartedDate.setTime(timer);

                // if the timer cannot count and there is a current timer, set to the text view
                if (timerStopped && savedInstanceState.getString(STATE_CURRENT_TIMER) != null) {
                    totalTime.setText(savedInstanceState.getString(STATE_CURRENT_TIMER));
                    timerText.setText(savedInstanceState.getString(STATE_CURRENT_TIMER));
                }
            }

            // start the timer
            startTimer();

            // measure the screen to adapt
            measure();

            // starts the game with saved values
            loadGame();
        } else {
            // measure the screen to adapt
            measure();
            // create new game
            makeNewGame();
        }
    }

    /**
     * function to create a new game getting the difficulty
     */
    public void makeNewGame() {
        Intent i = getIntent();
        difficulty = (int) i.getLongExtra(MenuActivity.DIFFICULTY_EXTRA, 0);

        // when the user selects some difficulty
        switch (difficulty) {
            // easy
            case 0:
                // changes the size and the words to find
                maxGridSize = 10;
                wordsToFind = new ArrayList(Arrays.asList("SWIFT", "KOTLIN", "OBJECTIVEC", "VARIABLE", "JAVA", "MOBILE"));
                break;
            // normal
            case 1:
                // changes the size and the words to find
                maxGridSize = 12;
                wordsToFind = new ArrayList(Arrays.asList("SWIFT", "KOTLIN", "OBJECTIVEC", "VARIABLE", "JAVA", "MOBILE", "FLUTTER", ""));
                break;
            // hard
            case 2:
                // changes the size and the words to find
                maxGridSize = 15;
                wordsToFind = new ArrayList(Arrays.asList("SWIFT", "KOTLIN", "OBJECTIVEC", "VARIABLE", "JAVA", "MOBILE", "FLUTTER", "REACT"));
        }

        // creates the empty array of the asked size
        lettersArray = new Letter[maxGridSize][maxGridSize];
        // empty the list of found words
        mFoundWords = new ArrayList();
        // clear the grid on the screen
        mGrid.removeAllViews();
        // hide the you win message
        hideYouWin();
        // clear the timers
        gameStartedDate = null;
        timerStopped = false;
        // measure the screen to adapt
        measure();
        // load game
        loadGame();
        // start timer
        startTimer();
    }

    /**
     * this function measures the screen size and gets the orientation
     * based on these values, it adapts the sizes and bounds to fit the screen
     */
    public void measure() {
        // get the measures of the screen
        DisplayMetrics displayMetrics = new DisplayMetrics();

        // orientation portrait
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // get the measures
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

            // width of the screen
            int width = displayMetrics.widthPixels;

            // sets the cells size
            this.cellSize = width / (maxGridSize + 1);

            // number of rows in the
            wordsRows = 2;

            // gets the current values
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(mainLayout);

            // set the bounds of the timer text
            constraintSet.connect(R.id.timerText, ConstraintSet.TOP, R.id.buttonNewGame, ConstraintSet.TOP, 0);
            constraintSet.connect(R.id.timerText, ConstraintSet.BOTTOM, R.id.buttonNewGame, ConstraintSet.BOTTOM, 0);
            constraintSet.connect(R.id.timerText, ConstraintSet.RIGHT, R.id.buttonNewGame, ConstraintSet.LEFT, 8);

            // set the bounds of the text view of current selection
            constraintSet.connect(R.id.selectingTextView, ConstraintSet.BOTTOM, R.id.grid, ConstraintSet.TOP, 0);
            constraintSet.connect(R.id.selectingTextView, ConstraintSet.TOP, R.id.buttonNewGame, ConstraintSet.BOTTOM, 0);
            constraintSet.connect(R.id.selectingTextView, ConstraintSet.LEFT, R.id.wordsTable, ConstraintSet.LEFT, 0);
            constraintSet.connect(R.id.selectingTextView, ConstraintSet.RIGHT, R.id.wordsTable, ConstraintSet.RIGHT, 0);

            // set the bounds of the grid of letters
            constraintSet.connect(R.id.grid, ConstraintSet.LEFT, R.id.mainLayout, ConstraintSet.LEFT, 0);
            constraintSet.connect(R.id.grid, ConstraintSet.RIGHT, R.id.mainLayout, ConstraintSet.RIGHT, 0);

            // set the bounds of the table with the words
            constraintSet.connect(R.id.wordsTable, ConstraintSet.LEFT, R.id.mainLayout, ConstraintSet.LEFT, 0);
            constraintSet.connect(R.id.wordsTable, ConstraintSet.RIGHT, R.id.mainLayout, ConstraintSet.RIGHT, 0);
            constraintSet.connect(R.id.wordsTable, ConstraintSet.TOP, R.id.grid, ConstraintSet.BOTTOM, 0);
            constraintSet.connect(R.id.wordsTable, ConstraintSet.BOTTOM, R.id.mainLayout, ConstraintSet.BOTTOM, 0);

            // apply the values
            constraintSet.applyTo(mainLayout);

            // orientation landscape
        } else {
            // get the measures
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

            // height of the screen
            int height = displayMetrics.heightPixels;

            // hides the action bar
            getSupportActionBar().hide();

            // sets the cells size
            this.cellSize = height / (maxGridSize + 2);

            // number of rows in the
            wordsRows = wordsToFind.contains("") ? wordsToFind.size() - 1 : wordsToFind.size();

            // gets the current values
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(mainLayout);

            // set the bounds of the timer text
            constraintSet.connect(R.id.timerText, ConstraintSet.TOP, R.id.buttonNewGame, ConstraintSet.BOTTOM, 8);
            constraintSet.connect(R.id.timerText, ConstraintSet.LEFT, R.id.buttonNewGame, ConstraintSet.LEFT, 0);
            constraintSet.connect(R.id.timerText, ConstraintSet.RIGHT, R.id.buttonNewGame, ConstraintSet.RIGHT, 0);

            // set the bounds of the text view of current selection
            constraintSet.connect(R.id.selectingTextView, ConstraintSet.TOP, R.id.mainLayout, ConstraintSet.TOP, 0);
            constraintSet.connect(R.id.selectingTextView, ConstraintSet.BOTTOM, R.id.wordsTable, ConstraintSet.TOP, 0);
            constraintSet.connect(R.id.selectingTextView, ConstraintSet.RIGHT, R.id.wordsTable, ConstraintSet.RIGHT, 0);
            constraintSet.connect(R.id.selectingTextView, ConstraintSet.LEFT, R.id.wordsTable, ConstraintSet.LEFT, 0);

            // set the bounds of the table with the words
            constraintSet.connect(R.id.wordsTable, ConstraintSet.LEFT, R.id.mainLayout, ConstraintSet.LEFT, 0);
            constraintSet.connect(R.id.wordsTable, ConstraintSet.BOTTOM, R.id.grid, ConstraintSet.BOTTOM, 0);
            constraintSet.connect(R.id.wordsTable, ConstraintSet.RIGHT, R.id.grid, ConstraintSet.LEFT, 0);

            // set the bounds of the grid of letters
            constraintSet.connect(R.id.grid, ConstraintSet.RIGHT, R.id.buttonNewGame, ConstraintSet.LEFT, 8);

            // apply the values
            constraintSet.applyTo(mainLayout);
        }

        // set the letter size
        this.letterSize = (int) (cellSize / 3.2f);
    }

    /**
     * this function creates the ui for the game using the previously set values
     */
    public void loadGame() {
        // empty the current selection text
        this.selectingTextView.setText("");

        // creates the grid
        mGrid.generateGrid(wordsToFind, maxGridSize, cellSize, letterSize, lettersArray);

        // clears the words table
        mWordsLayout.removeAllViews();

        // iterates for each row
        for (int i = 0; i < wordsRows; i++) {
            // add a new row to the table
            mWordsLayout.addView(new TableRow(this));
            TableRow tr = (TableRow) mWordsLayout.getChildAt(i % wordsRows);

            // iterates for each word in a row
            for (int j = 0; j < wordsToFind.size() / wordsRows; j++) {
                // the index of the word multiplying i and j
                int index = i * (wordsToFind.size() / wordsRows) + j;
                // gets the word from the list
                String word = wordsToFind.get(index);
                // create the text view for the word
                TextView t = new TextView(this);
                t.setPadding(10, 10, 10, 10);
                t.setText(word);
                t.setTextSize((int)letterSize * 0.9f);
                t.setTextColor(getResources().getColor(R.color.colorToFind));
                t.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
                t.setTypeface(Typeface.create("casual", Typeface.BOLD));
                // if the word was found
                if (mFoundWords.contains(word)) {
                    // put the stroke and set the color to gray
                    t.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    t.setTextColor(getResources().getColor(R.color.colorFoundWord));
                    alreadyAnimated.add(t);
                }
                // put the text view to the ui
                tr.addView(t);
            }
        }

        // start timer
        startTimer();
        // start game work
        update();
    }

    /**
     * this function runs on each change so it adapts the ui, like colors and animations to users actions
     */
    public void update() {
        // the list of the selected letters
        List<Letter> selectedLetters = mGrid.getSelectedLetters();

        // makes the word joining the letters
        String s = "";
        for (int i = 0; i < selectedLetters.size(); i++) {
            selectedLetters.get(i).getView().setBackgroundResource(R.drawable.letter_selected);
            s += selectedLetters.get(i).getValue();
        }

        // sets the text of the current selection text view
        selectingTextView.setText(Html.fromHtml("<u>" + s + "</u>"));
        selectingTextView.setTextSize(letterSize);

        // updates the grid UI
        for (int i = 0; i < maxGridSize; i++) {
            for (int j = 0; j < maxGridSize; j++) {
                Letter l = lettersArray[i][j];
                // if the letter is not selected
                if (!selectedLetters.contains(l)) {
                    // if the letter was found in a word, set the found color and blinks the letter
                    if (l.isFound()) {
                        l.getView().setBackgroundResource(R.drawable.letter_found);
                        l.getView().setTextColor(Color.WHITE);
                        blink(l.getView());
                        // if the letter was not found in a word, set the not found color
                    } else {
                        l.getView().setBackgroundResource(R.drawable.letter_border);
                        l.getView().setTextColor(getResources().getColor(R.color.colorLetter));
                    }
                }
            }
        }

        // iterates through the table of words to find
        for (int i = 0; i < wordsToFind.size(); i++) {
            TableRow tr = (TableRow) mWordsLayout.getChildAt(i % wordsRows);
            for (int j = 0; j < wordsToFind.size() / wordsRows; j++) {
                TextView t = (TextView) tr.getChildAt(j);
                // if the word was found change the color to gray, put the stroke and blink
                if (mFoundWords.contains(t.getText())) {
                    t.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    t.setTextColor(getResources().getColor(R.color.colorFoundWord));
                    blink(t);
                }
            }
        }
    }

    /**
     * handler for the event of touching this activity's view
     *
     * @param event the MotionEvent object
     * @return the super's method
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        List<Letter> selectedLetters = mGrid.getSelectedLetters();

        // if the finger is up
        if (event.getAction() == MotionEvent.ACTION_UP) {
            // if the the word is selected in the grid
            if (verifySelected()) {
                // sets the letters to found
                for (int i = 0; i < selectedLetters.size(); i++) {
                    selectedLetters.get(i).setFound(true);
                }
            }

            // clears the list of selected letters
            mGrid.setSelectedLetters(new ArrayList());

            // clears the first selected letter
            mGrid.setFirstSelected(null);
        }

        // updated the game
        this.update();

        // returns the event handling
        return super.onTouchEvent(event);
    }

    /**
     * this function verifies if the current selected letters form a word in the list to find
     *
     * @return wheter the selected letters form a word in the word list
     */
    public boolean verifySelected() {
        List<Letter> selectedLetters = mGrid.getSelectedLetters();

        // creates the word based on the selected letters
        String word = "";
        for (int i = 0; i < selectedLetters.size(); i++) {
            word += selectedLetters.get(i).getValue();
        }

        // initialize the found with false
        boolean found = false;

        // if the formed word is in the list to find
        if (wordsToFind.contains(word)) {
            // if the word is not already found
            if (!mFoundWords.contains(word) && !word.isEmpty()) {
                // add the word to the found list
                mFoundWords.add(word);
            }

            // sets found to true
            found = true;
            // if the list to find constains the words, but reversed, do the same thing as above
        } else if (wordsToFind.contains(new StringBuilder(word).reverse().toString())) {
            String w = new StringBuilder(word).reverse().toString();
            if (!mFoundWords.contains(w) && !w.isEmpty()) {
                mFoundWords.add(w);
            }
            found = true;
        }

        // if the user found every word, show you win
        if (mFoundWords.size() == wordsToFind.size() || (mFoundWords.size() == wordsToFind.size() - 1 && wordsToFind.contains(""))) {
            showYouWin();
        }

        // return the result
        return found;
    }

    /**
     * this functions does the animation of blinking a view
     *
     * @param view the view to animate
     */
    public void blink(final View view) {
        // if the view has already been animated, cancel
        if (alreadyAnimated.contains(view)) {
            return;
        }

        // otherwise add it to the already animated views
        alreadyAnimated.add(view);

        // the animation
        final Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);

        // add the animation to the view
        view.startAnimation(animation);

        // thread to cancel the animation
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // after 1 second
                    Thread.sleep(1000);
                    // stops animation
                    view.clearAnimation();
                } catch (Exception e) {
                    System.err.println(e);
                }
            }
        }).start();
    }

    /**
     * this function show the You Win message on the screen
     */
    public void showYouWin() {
        // the animation for the views
        final Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.you_win_anim);

        // set the text to visible
        winningText.setAlpha(1);
        // add the animation to the you win text
        winningText.startAnimation(animation);

        // changes the colors of the new game button
        btnNewGame.getBackground().setTint(getResources().getColor(R.color.colorBtnBackground));
        btnNewGame.setTextColor(Color.WHITE);

        // sets the total timer text with the current timer
        totalTime.setText(timerText.getText());
        // set the text to visible
        totalTime.setAlpha(1);
        // add the animation to the you win text
        totalTime.startAnimation(animation);

        // stop the timer
        timerStopped = true;

        // save the score
        saveHighScore();
    }

    /**
     * this function hides the views of the function above
     */
    public void hideYouWin() {
        // clear the animation and set to invisible
        winningText.clearAnimation();
        winningText.setAlpha(0);

        // changes the colors of the new game button
        btnNewGame.getBackground().setTint(getResources().getColor(R.color.colorBtnBackground));
        btnNewGame.setTextColor(Color.WHITE);

        // clear the animation and set to invisible
        totalTime.clearAnimation();
        totalTime.setAlpha(0);
        // clear the text
        totalTime.setText("");
    }

    /**
     * this function runs recursively to count the timer
     */
    public void startTimer() {
        // if the timer is stopped, cancel
        if (timerStopped) {
            return;
        }

        // if the game started date doesnt exist, creates it with now time
        if (gameStartedDate == null) {
            gameStartedDate = new Date();
        }

        // the date now to use in the math
        Date now = new Date();

        // calculates how many minutes and seconds passed since game started
        long timeDiff = now.getTime() - gameStartedDate.getTime();
        long diffSeconds = timeDiff / 1000 % 60;
        long diffMinutes = timeDiff / (60 * 1000) % 60;

        // put the timer text to a string to show on the screen
        String timeString = diffMinutes + ":" + (diffSeconds < 10 ? "0" + diffSeconds : diffSeconds);
        timerText.setText(timeString);

        // thread to make a recursive call
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        // after 1 second
                        wait(1000);

                        // calls the timer function agains
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                startTimer();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            ;
        };
        thread.start();
    }

    /**
     * function to save data when the orientation changes
     *
     * @param outState the bundle to save the data inside
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // saves the letters array as individual values
        for (int i = 0; i < maxGridSize; i++) {
            for (int j = 0; j < maxGridSize; j++) {
                outState.putSerializable("Letter" + i * maxGridSize + j, lettersArray[i][j]);
            }
        }

        // save the list of found words
        outState.putStringArrayList(STATE_FOUND, mFoundWords);

        // save the size of the grid
        outState.putInt(STATE_GRID_SIZE, maxGridSize);

        // save the current time
        outState.putString(STATE_CURRENT_TIMER, timerText.getText().toString());

        // if the game started date exists, save it too
        if (gameStartedDate != null) {
            outState.putLong(STATE_TIMER, gameStartedDate.getTime());
        }
    }

    /**
     * this function saves the score if the time is less than the current highscore
     *
     * @return whether it is the highscore
     */
    public boolean saveHighScore() {
        return MenuActivity.instance.saveHighScore(totalTime.getText().toString(), difficulty);
    }
}
