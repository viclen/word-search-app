package com.victorlengler.wordsearch;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Grid extends TableLayout {
    public static final int DIRECTION_HORIZONTAL = 1;
    public static final int DIRECTION_VERTICAL = 2;
    public static final int DIRECTION_DIAGONAL = 3;
    public static final int DIRECTION_DIAGONAL2 = 4;

    private List<String> wordsToFind;
    private List<Letter> selectedLetters = new ArrayList();
    private Letter[][] lettersArray;
    private Letter firstSelected = null;
    private int max;
    private int cellSize;

    public Grid(Context context) {
        super(context);
    }

    public Grid(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void generateGrid(List<String> wordsToFind, int max, int cellSize, int letterSize, Letter[][] letters) {
        this.wordsToFind = wordsToFind;
        this.lettersArray = letters;
        this.max = max;
        this.cellSize = cellSize;

        for (int i = 0; i < wordsToFind.size(); i++) {
            Random rand = new Random();
            String word = rand.nextInt(2) == 1 ? wordsToFind.get(i).toUpperCase() : new StringBuilder(wordsToFind.get(i).toUpperCase()).reverse().toString();
            int direction = rand.nextInt(4) + 1;
            int placeX, placeY;

            Letter l = null;

            switch (direction) {
                case DIRECTION_DIAGONAL:
                    placeX = rand.nextInt(11 - word.length());
                    placeY = rand.nextInt(11 - word.length());
                    for (int j = 0; j < word.length(); j++) {
                        int x = (placeX + j) * cellSize;
                        int y = (placeY + j) * cellSize;
                        if (letters[placeX + j][placeY + j] == null) {
                            l = new Letter(word.charAt(j), x, y, cellSize, letterSize, this.getContext());
                            l.getView().setBackgroundColor(Color.YELLOW);
                            letters[placeX + j][placeY + j] = l;
                        } else {
                            for (int k = 0; k < j; k++) {
                                letters[placeX + k][placeY + k] = null;
                            }
                            i--;
                            break;
                        }
                    }
                    break;
                case DIRECTION_DIAGONAL2:
                    placeX = rand.nextInt(11 - word.length());
                    placeY = word.length() + rand.nextInt(11 - word.length()) - 1;
                    for (int j = 0; j < word.length(); j++) {
                        int x = (placeX + j) * cellSize;
                        int y = (placeY - j) * cellSize;
                        if (letters[placeX + j][placeY - j] == null) {
                            l = new Letter(word.charAt(j), x, y, cellSize, letterSize, this.getContext());
                            l.getView().setBackgroundColor(Color.YELLOW);
                            letters[placeX + j][placeY - j] = l;
                        } else {
                            for (int k = 0; k < j; k++) {
                                letters[placeX + k][placeY - k] = null;
                            }
                            i--;
                            break;
                        }
                    }
                    break;
                case DIRECTION_HORIZONTAL:
                    placeX = rand.nextInt(10);
                    placeY = rand.nextInt(11 - word.length());
                    for (int j = 0; j < word.length(); j++) {
                        int x = (placeX) * cellSize;
                        int y = (placeY + j) * cellSize;
                        l = new Letter(word.charAt(j), x, y, cellSize, letterSize, this.getContext());
                        if (letters[placeX][placeY + j] == null) {
                            l.getView().setBackgroundColor(Color.YELLOW);
                            letters[placeX][placeY + j] = l;
                        } else {
                            for (int k = 0; k < j; k++) {
                                letters[placeX][placeY + k] = null;
                            }
                            i--;
                            break;
                        }
                    }
                    break;
                case DIRECTION_VERTICAL:
                    placeY = rand.nextInt(10);
                    placeX = rand.nextInt(11 - word.length());
                    for (int j = 0; j < word.length(); j++) {
                        int x = (placeX + j) * cellSize;
                        int y = (placeY) * cellSize;
                        l = new Letter(word.charAt(j), x, y, cellSize, letterSize, this.getContext());
                        if (letters[placeX + j][placeY] == null) {
                            l.getView().setBackgroundColor(Color.YELLOW);
                            letters[placeX + j][placeY] = l;
                        } else {
                            for (int k = 0; k < j; k++) {
                                letters[placeX + k][placeY] = null;
                            }
                            i--;
                            break;
                        }
                    }
                    break;
            }
        }

        for (int i = 0; i < max; i++) {
            TableRow row = new TableRow(this.getContext());
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT));

            for (int j = 0; j < max; j++) {
                char letter = randomLetter();

                if (letters[i][j] == null) {
                    int x = i * cellSize;
                    int y = j * cellSize;
                    letters[i][j] = new Letter(letter, x, y, cellSize, letterSize, this.getContext());
                }

                row.addView(letters[i][j].getView());
            }
            this.addView(row);
        }

        this.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                return touchEvent(event);
            }
        });
    }

    public void verifyDirection(int x, int y) {
        if (firstSelected == null) {
            return;
        }
        int firstX = firstSelected.getX() / cellSize;
        int firstY = firstSelected.getY() / cellSize;
        x = x / cellSize;
        y = y / cellSize;

        selectedLetters = new ArrayList(Arrays.asList(firstSelected));

        // horizontal
        if (firstX == x) {
            if(y > firstY){
                for (int i = 1; i <= y - firstY; i++) {
                    selectedLetters.add(lettersArray[firstX][firstY + i]);
                }
            }else{
                for (int i = 1; i <= firstY - y; i++) {
                    selectedLetters.add(lettersArray[firstX][firstY - i]);
                }
            }
            return;
        }

        // vertical
        if (firstY == y) {
            if(x > firstX){
                for (int i = 1; i <= x - firstX; i++) {
                    selectedLetters.add(lettersArray[firstX + i][firstY]);
                }
            }else{
                for (int i = 1; i <= firstX - x; i++) {
                    selectedLetters.add(lettersArray[firstX - i][firstY]);
                }
            }
            return;
        }

        // diagonal
        if (firstX != x & firstY != y && Math.pow(firstX - x, 2) == Math.pow(firstY - y, 2)) {
            // down right
            if (firstX > x && firstY > y) {
                for (int i = 1; i <= firstX - x; i++) {
                    selectedLetters.add(lettersArray[firstX - i][firstY - i]);
                }
                return;
                // up left
            } else if (firstX < x && firstY < y) {
                for (int i = 1; i <= x - firstX; i++) {
                    selectedLetters.add(lettersArray[firstX + i][firstY + i]);
                }
                return;
                // down left
            } else if (firstX > x && firstY < y) {
                for (int i = 1; i <= firstX - x; i++) {
                    selectedLetters.add(lettersArray[firstX - i][firstY + i]);
                }
                return;
                // up right
            } else if (firstX < x && firstY > y) {
                for (int i = 1; i <= x - firstX; i++) {
                    selectedLetters.add(lettersArray[firstX + i][firstY - i]);
                }
                return;
            }
        }
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
            firstSelected = letter;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            verifyDirection(letter.getX(), letter.getY());
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (verifySelected()) {
                for (int i = 0; i < selectedLetters.size(); i++) {
                    selectedLetters.get(i).setFound(true);
                }
            }

            selectedLetters = new ArrayList();

            firstSelected = null;
        }

        ((MainActivity)this.getContext()).update();

        return true;
    }

    public Letter getLetter(float x, float y) {
        for (int i = 0; i < max; i++) {
            for (int j = 0; j < max; j++) {
                Letter letter = lettersArray[i][j];
                if (letter.getX() < y - 1 && letter.getY() < x - 1 && letter.getX() + cellSize > y + 1 && letter.getY() + cellSize > x + 1) {
                    return letter;
                }
            }
        }
        return null;
    }

    public boolean verifySelected() {
        return ((MainActivity)this.getContext()).verifySelected();
    }

    public List<Letter> getSelectedLetters() {
        return selectedLetters;
    }

    public void setSelectedLetters(List<Letter> selectedLetters) {
        this.selectedLetters = selectedLetters;
    }

    public Letter getFirstSelected() {
        return firstSelected;
    }

    public void setFirstSelected(Letter firstSelected) {
        this.firstSelected = firstSelected;
    }
}
