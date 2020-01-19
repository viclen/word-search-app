package com.victorlengler.wordsearch;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * the grid view as a table layout to contain the letters as table cells
 */
public class Grid extends TableLayout implements Serializable {
    // direction constants
    public static final int DIRECTION_HORIZONTAL = 1;
    public static final int DIRECTION_VERTICAL = 2;
    public static final int DIRECTION_DIAGONAL = 3;
    public static final int DIRECTION_DIAGONAL2 = 4;

    // list of letter that are being selected by the user in the grid
    private List<Letter> selectedLetters = new ArrayList();
    // 2 dimensional array with the letters as a grid
    private Letter[][] lettersArray;
    // the first letter of the selection
    private Letter firstSelected = null;
    // the maximum width and height of the grid
    private int maxGridSize;
    // the size of each cell
    private int cellSize;

    // constructors
    public Grid(Context context) {
        super(context);
    }
    public Grid(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * function to generate the grid view with the letters based on the information given
     * @param wordsToFind the list of words to find
     * @param max the size of the grid in number of letters
     * @param cellSize the size of the cell
     * @param letterSize the size of each letter
     * @param letters the 2 dimensional array of letters
     */
    public void generateGrid(List<String> wordsToFind, int max, int cellSize, int letterSize, Letter[][] letters) {
        // the 2 dimensional array of letters
        this.lettersArray = letters;
        // the width and height of the grid
        this.maxGridSize = max;
        // the size of the cell
        this.cellSize = cellSize;

        // if this hasnt been generated yet
        if(!isGenerated()) {
            // loop through all words to put them in the grid
            for (int i = 0; i < wordsToFind.size(); i++) {
                // create a random generator
                Random rand = new Random();
                // randomize if word is reversed of not
                String word = rand.nextInt(2) == 1 ? wordsToFind.get(i).toUpperCase() : new StringBuilder(wordsToFind.get(i).toUpperCase()).reverse().toString();

                // if the word is "", stop the loop
                if(word.isEmpty()) break;

                // randomize word direction
                int direction = rand.nextInt(4) + 1;
                // the place where the word will start
                int placeX, placeY;

                // object of the type Letter to be used as auxiliary
                Letter l;

                // select the random direction
                switch (direction) {
                    // if its diagonal to the right
                    case DIRECTION_DIAGONAL:
                        // the place of the first letter cant be more than the maximum size minus the size of the word
                        placeX = rand.nextInt(max + 1 - word.length());
                        placeY = rand.nextInt(max + 1 - word.length());
                        // loop for each letter
                        for (int j = 0; j < word.length(); j++) {
                            // the position (x and y) of the letter on the screen inside the view
                            int x = (placeX + j) * cellSize;
                            int y = (placeY + j) * cellSize;

                            // if the letters can continue to be put in the grid
                            if (letters[placeX + j][placeY + j] == null) {
                                // instantiate the letter
                                l = new Letter(word.charAt(j), x, y, cellSize, letterSize, this.getContext());
                                letters[placeX + j][placeY + j] = l;
                            // otherwise take the letters of this word out of the grid
                            } else {
                                // loop for each letter that has been put
                                for (int k = 0; k < j; k++) {
                                    // make it null
                                    letters[placeX + k][placeY + k] = null;
                                }
                                // start the process again with the same word
                                i--;
                                break;
                            }
                        }
                        break;
                    // if its diagonal to the left
                    case DIRECTION_DIAGONAL2:
                        // the place of the first letter cant be more than the maximum size minus the size of the word and less than the words size
                        placeX = rand.nextInt(max + 1 - word.length());
                        placeY = word.length() + rand.nextInt(11 - word.length()) - 1;
                        // loop for each letter
                        for (int j = 0; j < word.length(); j++) {
                            // the position (x and y) of the letter on the screen inside the view
                            int x = (placeX + j) * cellSize;
                            int y = (placeY - j) * cellSize;

                            // if the letters can continue to be put in the grid
                            if (letters[placeX + j][placeY - j] == null) {
                                // instantiate the letter
                                l = new Letter(word.charAt(j), x, y, cellSize, letterSize, this.getContext());
                                letters[placeX + j][placeY - j] = l;
                                // otherwise take the letters of this word out of the grid
                            } else {
                                // loop for each letter that has been put
                                for (int k = 0; k < j; k++) {
                                    // make it null
                                    letters[placeX + k][placeY - k] = null;
                                }
                                // start the process again with the same word
                                i--;
                                break;
                            }
                        }
                        break;
                    // if the direction is horizontal
                    case DIRECTION_HORIZONTAL:
                        // the X can be anyware, but the Y can only be less the the maximum size minus the words size
                        placeX = rand.nextInt(max);
                        placeY = rand.nextInt(max + 1 - word.length());
                        // loop for each letter
                        for (int j = 0; j < word.length(); j++) {
                            // the position (x and y) of the letter on the screen inside the view
                            int x = (placeX) * cellSize;
                            int y = (placeY + j) * cellSize;

                            // instantiate the letter
                            l = new Letter(word.charAt(j), x, y, cellSize, letterSize, this.getContext());

                            // if the letters can continue to be put in the grid
                            if (letters[placeX][placeY + j] == null) {
                                letters[placeX][placeY + j] = l;
                            // otherwise take the letters of this word out of the grid
                            } else {
                                // loop for each letter that has been put
                                for (int k = 0; k < j; k++) {
                                    // make it null
                                    letters[placeX][placeY + k] = null;
                                }
                                // start the process again with the same word
                                i--;
                                break;
                            }
                        }
                        break;
                    case DIRECTION_VERTICAL:
                        // the Y can be anyware, but the X can only be less the the maximum size minus the words size
                        placeY = rand.nextInt(max);
                        placeX = rand.nextInt(max + 1 - word.length());
                        // loop for each letter
                        for (int j = 0; j < word.length(); j++) {
                            // the position (x and y) of the letter on the screen inside the view
                            int x = (placeX + j) * cellSize;
                            int y = (placeY) * cellSize;

                            // instantiate the letter
                            l = new Letter(word.charAt(j), x, y, cellSize, letterSize, this.getContext());

                            // if the letters can continue to be put in the grid
                            if (letters[placeX + j][placeY] == null) {
                                letters[placeX + j][placeY] = l;
                            // otherwise take the letters of this word out of the grid
                            } else {
                                // loop for each letter that has been put
                                for (int k = 0; k < j; k++) {
                                    // make it null
                                    letters[placeX + k][placeY] = null;
                                }
                                // start the process again with the same word
                                i--;
                                break;
                            }
                        }
                        break;
                }
            }
        }

        // clean the grid before putting the letters
        this.removeAllViews();

        // loop through the rows of letters array
        for (int i = 0; i < max; i++) {
            // instantiate a new table row view
            TableRow row = new TableRow(this.getContext());
            // set it to wrap the content
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT));

            // loop through the columns of this row of the letters array
            for (int j = 0; j < max; j++) {
                // get a random letter
                char letter = randomLetter();

                // get the letter from the 2d array
                Letter l = letters[i][j];

                // if there is no letter in this position
                if (l == null) {
                    // the x and y position on the screen of the letters view
                    int x = i * cellSize;
                    int y = j * cellSize;
                    // instatantiate a new letter with the random char and the x and y
                    l = new Letter(letter, x, y, cellSize, letterSize, this.getContext());
                // if the letter exists
                }else{
                    // instantiate a new letter using the letter from the array, in case there was a problem or the orientation changed
                    l = new Letter(letters[i][j].getValue(), letters[i][j].getX(), letters[i][j].getY(), cellSize, letterSize, this.getContext());
                    l.setFound(letters[i][j].isFound());
                }
                // set the letter to the position
                letters[i][j] = l;

                // add the letter view to the row view
                row.addView(letters[i][j].getView());
            }

            // add the row view to the table view
            this.addView(row);
        }

        // set the listener for user touches
        this.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                return touchEvent(event);
            }
        });
    }

    /**
     * this function is to verify which direction the touch is being dragged
     * @param x the x position of the touch on the screen
     * @param y the y position of the touch on the screen
     */
    public void verifyDirection(int x, int y) {
        // if there is no letter selected
        if (firstSelected == null) {
            return;
        }

        // get the position of the first letter
        int firstX = firstSelected.getX() / cellSize;
        int firstY = firstSelected.getY() / cellSize;
        x = x / cellSize;
        y = y / cellSize;

        // create the selected letters with the first selected letter only
        selectedLetters = new ArrayList(Arrays.asList(firstSelected));

        // horizontal
        if (firstX == x) {
            // the user is dragging to the right
            if(y > firstY){
                for (int i = 1; i <= y - firstY; i++) {
                    selectedLetters.add(lettersArray[firstX][firstY + i]);
                }
            // the user is dragging to the left
            }else{
                for (int i = 1; i <= firstY - y; i++) {
                    selectedLetters.add(lettersArray[firstX][firstY - i]);
                }
            }
            return;
        }

        // vertical
        if (firstY == y) {
            // is the user is dragging up
            if(x > firstX){
                for (int i = 1; i <= x - firstX; i++) {
                    selectedLetters.add(lettersArray[firstX + i][firstY]);
                }
            // if the user is dragging down
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

    /**
     * get a random letter
     * @return the random letter
     */
    public char randomLetter() {
        // all characters allowed
        String allowed = "QWERTYUIOPASDFGHJKLZXCVBNM";

        // random generator
        Random rand = new Random();

        return allowed.charAt(rand.nextInt(allowed.length()));
    }

    /**
     * the manager for the touch event
     * @param event the MotionEvent object
     * @return whether any letter was found
     */
    public boolean touchEvent(MotionEvent event) {
        // get the screen position of the touch
        float x = event.getX();
        float y = event.getY();

        // get the letter at this position
        Letter letter = getLetter(x, y);
        if (letter == null) {
            return false;
        }

        // if the user just started the touch, this is the first letter
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            firstSelected = letter;
        // if the user is moving the finger on the screen
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            // send it to the verify direction function
            verifyDirection(letter.getX(), letter.getY());
        // if the user just took the finger from the screen
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            // if the selected word is verified from the word list
            if (verifySelected()) {
                // set the selected letters to found
                for (int i = 0; i < selectedLetters.size(); i++) {
                    selectedLetters.get(i).setFound(true);
                }
            }

            // clean the selected letters array
            selectedLetters = new ArrayList();

            // clean the first selected letter
            firstSelected = null;
        }

        // update the interface
        ((MainActivity)this.getContext()).update();

        return true;
    }

    /**
     * this function finds the letter based on its position on the screen
     * @param x the x position on the screen
     * @param y the y position on the screen
     * @return the letter if its found or null if its not found
     */
    public Letter getLetter(float x, float y) {
        for (int i = 0; i < maxGridSize; i++) {
            for (int j = 0; j < maxGridSize; j++) {
                Letter letter = lettersArray[i][j];
                if (letter.getX() < y - 1 && letter.getY() < x - 1 && letter.getX() + cellSize > y + 1 && letter.getY() + cellSize > x + 1) {
                    return letter;
                }
            }
        }
        return null;
    }

    /**
     * alias for the main activity's verify selected function
     * @return the result of the main activity's verifySelected
     */
    public boolean verifySelected() {
        return ((MainActivity)this.getContext()).verifySelected();
    }

    /**
     * this function verifies if the grid has already been generated
     * @return whether it was generated
     */
    public boolean isGenerated() {
        // if the letters array is not null
        return lettersArray != null
                && lettersArray.length > 0
                && lettersArray[0][0] != null
                && lettersArray[0][0].getValue() + "" != "";
    }

    // getters and setters
    public List<Letter> getSelectedLetters() {
        return selectedLetters;
    }

    public void setSelectedLetters(List<Letter> selectedLetters) {
        this.selectedLetters = selectedLetters;
    }

    public void setFirstSelected(Letter firstSelected) {
        this.firstSelected = firstSelected;
    }

    public void setLettersArray(Letter[][] lettersArray) {
        this.lettersArray = lettersArray;
    }
}
