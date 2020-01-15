package com.victorlengler.wordsearch;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    TableLayout table;
    int cellSize = 70;
    int max = 10;
    int letterSize = 24;

    List<String> wordsToFind = new ArrayList(Arrays.asList("Swift", "Kotlin", "ObjectiveC", "Variable", "Java", "Mobile"));
    List foundWords = new ArrayList();

    Letter[][] grid = new Letter[max][max];

    List<Letter> selectedLetters = new ArrayList();

    TextView foundTextView;


    final int DIRECTION_HORIZONTAL = 1;
    final int DIRECTION_VERTICAL = 2;
    final int DIRECTION_DIAGONAL = 3;
    final int DIRECTION_DIAGONAL2 = 4;

    int current_direction = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.table = findViewById(R.id.table);
        this.foundTextView = findViewById(R.id.foundTextView);

        generateGrid();
    }

    public void generateGrid() {
        for (int i = 0; i < wordsToFind.size(); i++) {
            String word = wordsToFind.get(i).toLowerCase();
            Random rand = new Random();
            int direction = 3;// rand.nextInt(4) + 1;
            int placeX, placeY;
            switch (direction) {
                case DIRECTION_DIAGONAL:
                    placeX = rand.nextInt(11 - word.length());
                    placeY = rand.nextInt(11 - word.length());
                    for (int j = 0; j < word.length(); j++) {
                        int x = (placeX + j) * cellSize;
                        int y = (placeY + j) * cellSize;
                        if (grid[placeX + j][placeY + j] == null) {
                            grid[placeX + j][placeY + j] = new Letter(word.charAt(j), x, y, cellSize, letterSize, this);
                        } else {
                            for (int k = 0; k <= j; k++) {
                                grid[placeX + k][placeY + k] = null;
                            }
//                            i--;
                            break;
                        }
                    }
                    break;
                case DIRECTION_DIAGONAL2:
                    placeX = rand.nextInt(11 - word.length());
                    placeY = rand.nextInt(11 - word.length());
                    for (int j = 0; j < word.length(); j++) {
                        int x = (placeX + j) * cellSize;
                        int y = (placeY - j) * cellSize;
                        if (grid[placeX + j][placeY + j] == null) {
                            grid[placeX + j][placeY + j] = new Letter(word.charAt(j), x, y, cellSize, letterSize, this);
                        } else {
                            for (int k = 0; k <= j; k++) {
                                grid[placeX + k][placeY + k] = null;
                            }
//                            i--;
                            break;
                        }
                    }
                    break;
                case DIRECTION_HORIZONTAL:

                    break;
                case DIRECTION_VERTICAL:

                    break;
            }
        }

        for (int i = 0; i < max; i++) {
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT));

            for (int j = 0; j < max; j++) {
                char letter = randomLetter();

                if (grid[i][j] == null) {
                    int x = i * cellSize;
                    int y = j * cellSize;
                    grid[i][j] = new Letter(letter, x, y, cellSize, letterSize, this);
                }

                row.addView(grid[i][j].getView());
            }
            table.addView(row);
        }

        table.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                return touchEvent(event);
            }
        });
    }

    public char randomLetter() {
        String allowed = "QWERTYUIOPASDFGHJKLZXCVBNM";

        Random rand = new Random();

        return allowed.charAt(rand.nextInt(26));
    }

    public boolean touchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        Letter letter = getLetter(x, y);
        if (letter == null) {
            return false;
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            letter.getView().setBackgroundColor(Color.GREEN);
            selectedLetters.add(letter);
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            int verification = verifyDirection(letter.getX(), letter.getY());
            if (verification == 1) {
                selectedLetters.add(letter);
                letter.getView().setBackgroundColor(Color.GREEN);
            } else if (verification == -1) {
                selectedLetters.get(selectedLetters.size() - 1).getView().setBackgroundColor(Color.TRANSPARENT);
                selectedLetters.remove(selectedLetters.size() - 1);
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (verifySelected()) {
                for (int i = 0; i < selectedLetters.size(); i++) {
                    selectedLetters.get(i).getView().setBackgroundColor(Color.CYAN);
                }
            } else {
                for (int i = 0; i < selectedLetters.size(); i++) {
                    selectedLetters.get(i).getView().setBackgroundColor(Color.TRANSPARENT);
                }
            }

            foundTextView.setText(foundWords.toString());

            selectedLetters = new ArrayList();

            current_direction = 0;
        }

        return true;
    }

    public Letter getLetter(float x, float y) {
        for (int i = 0; i < max; i++) {
            for (int j = 0; j < max; j++) {
                Letter letter = grid[i][j];
                if (letter.getX() < y - 1 && letter.getY() < x - 1 && letter.getX() + cellSize > y + 1 && letter.getY() + cellSize > x + 1) {
                    return letter;
                }
            }
        }
        return null;
    }

    public int verifyDirection(int x, int y) {
        int previousX = selectedLetters.get(selectedLetters.size() - 1).getX();
        int previousY = selectedLetters.get(selectedLetters.size() - 1).getY();
        int firstX = selectedLetters.get(0).getX();
        int firstY = selectedLetters.get(0).getY();

        // go back
        if (selectedLetters.size() > 1 && selectedLetters.get(selectedLetters.size() - 2).getX() == x && selectedLetters.get(selectedLetters.size() - 2).getY() == y) {
            return -1;
        }

        // horizontal
        if ((current_direction == DIRECTION_HORIZONTAL || current_direction == 0) &&
                (firstX == x && (previousY == y + cellSize && firstY > y || previousY == y - cellSize && firstY < y))) {
            current_direction = DIRECTION_HORIZONTAL;
            return 1;
        }

        // vertical
        if ((current_direction == DIRECTION_VERTICAL || current_direction == 0) &&
                (firstY == y && (previousX == x + cellSize && firstX > x || previousX == x - cellSize && firstX < x))) {
            current_direction = DIRECTION_VERTICAL;
            return 1;
        }

        // diagonal
        if ((current_direction == DIRECTION_DIAGONAL || current_direction == 0) && (
                previousX == x - cellSize && previousY == y - cellSize && firstX < x && firstY < y ||
                        previousX == x + cellSize && previousY == y + cellSize && firstX > x && firstY > y ||
                        previousX == y - cellSize && previousY == x - cellSize && firstX < y && firstY < x ||
                        previousX == y + cellSize && previousY == x + cellSize && firstX > y && firstY > x ||
                        previousX == x + cellSize && previousY == y - cellSize && firstX > x && firstY < y ||
                        previousX == x - cellSize && previousY == y + cellSize && firstX < x && firstY > y ||
                        previousX == y + cellSize && previousY == x - cellSize && firstX > y && firstY < x ||
                        previousX == y - cellSize && previousY == x + cellSize && firstX < y && firstY > x
        )) {
            current_direction = DIRECTION_DIAGONAL;
            return 1;
        }

        return 0;
    }

    public boolean verifySelected() {
        String word = "";
        for (int i = 0; i < selectedLetters.size(); i++) {
            word += selectedLetters.get(i).getValue();
        }

        if (wordsToFind.contains(word)) {
            foundWords.add(word);
            return true;
        }

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_UP){
            selectedLetters = new ArrayList();
            current_direction = 0;
        }

        return super.onTouchEvent(event);
    }
}
