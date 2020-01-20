package com.victorlengler.wordsearch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class MenuActivity extends AppCompatActivity {

    Spinner difficultySpinner;
    public static final String DIFFICULTY_EXTRA = "difficulty";
    public static final String SAVE_HIGHSCORE = "highscore";
    TextView highscoreText;

    public static MenuActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // set the instance
        instance = this;

        // hides the action bar
        getSupportActionBar().hide();

        // get the difficulties from the array value
        ArrayList difficulties = new ArrayList(Arrays.asList(getResources().getStringArray(R.array.difficulties)));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, difficulties);

        // get the highscore text element
        highscoreText = findViewById(R.id.textHighscore);

        // get the spinner
        difficultySpinner = (Spinner) findViewById(R.id.difficulty_spinner);
        // everytime the spinner changes its value
        difficultySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                highscoreText.setText(getSavedHighscore());
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        // set the options
        difficultySpinner.setAdapter(adapter);

        // set the listener for the help button
        Button btnHelp = (Button) findViewById(R.id.btnHelp);
        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openHelp();
            }
        });

        // set the listener for the game button
        Button btnPlay = (Button) findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGame();
            }
        });
    }

    /**
     * this function opens the help activity
     */
    public void openHelp() {
        Intent i = new Intent(this, HelpActivity.class);
        startActivity(i);
    }

    /**
     * this function opens the game activity
     */
    public void openGame(){
        Intent i = new Intent(this, GameActivity.class);
        i.putExtra(DIFFICULTY_EXTRA, difficultySpinner.getSelectedItemId());
        startActivity(i);
    }

    /**
     * this function gets the current record based on the current selected difficulty
     * @return the current record
     */
    public String getSavedHighscore(){
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        String highScore = sharedPref.getString(SAVE_HIGHSCORE + difficultySpinner.getSelectedItemId(), "0:00");

        return highScore;
    }

    /**
     * this function saves the score if the time is less than the current highscore
     *
     * @return whether it is the highscore
     */
    public boolean saveHighScore(String score, int difficulty) {
        // get the shared preferences
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        // get the current highscore
        String highScore = sharedPref.getString(MenuActivity.SAVE_HIGHSCORE + difficulty, "0:00");
        // initialize the new highscore
        String newHighScore = "0:00";

        try {
            // sum the time value of the current highscore
            int highScoreValue = Integer.parseInt(highScore.split(":")[0]) * 60 + Integer.parseInt(highScore.split(":")[1]);
            // sum the time value of the current timer
            int scoreValue = Integer.parseInt(score.split(":")[0]) * 60 + Integer.parseInt(score.split(":")[1]);

            // if the current timer is less than the current highscore
            if (highScoreValue == 0 || scoreValue < highScoreValue) {
                // set the current timer as the new highscore
                newHighScore = score;
            } else {
                // its not the highscore
                return false;
            }
        } catch (Exception e) {
            // set the current timer as the new highscore
            newHighScore = score;
        }

        // save to the shared preferences
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(MenuActivity.SAVE_HIGHSCORE + difficulty, newHighScore);
        editor.commit();

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // refresh the highscore text
        highscoreText.setText(getSavedHighscore());
    }
}
