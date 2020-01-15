package com.victorlengler.wordsearch.old;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WordSearchGrid extends TiledGrid {

    public static final int DIRECTION_UNKNOWN = 0;
    public static final int DIRECTION_LEFT_TO_RIGHT = 1;
    public static final int DIRECTION_TOP_TO_BOTTOM = 2;
    public static final int DIRECTION_TOP_BOTTOM_LEFT_RIGHT = 3;

    public interface OnWordSelectedListener {
        /**
         * Listener for the clients of this board to tell it whether a selected word is valid or not.
         * If a selected word is valid, the board will highlight it, otherwise it will be discarded.
         * @param selectedWord the selected word
         * @return True if the word is valid and should be kept selected
         */
        boolean onWordSelected(Word selectedWord);
    }

    public interface OnWordHighlightedListener {
      /**
       * Listener for the clients of this board to take action after a word has been successfully highlighted.
       * @param highlightedWord the highlighted word
       */
      void onWordHighlighted(Word highlightedWord);
    }

    private SelectedWord currentSelectedWord;
    private List<SelectedWord> selectedWords;

    private OnWordSelectedListener listener;
    private OnWordHighlightedListener highlightedListener;

    public WordSearchGrid(Context context) {
        super(context);
        init();
    }

    public WordSearchGrid(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WordSearchGrid(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @SuppressWarnings("unused")
    public WordSearchGrid(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        selectedWords = new ArrayList<>();
    }

    @Override
    protected void initBoardTileViews(Context context) {
        for (int i = 0; i < getTileCount(); i++) {
            LetterTile tileView = new LetterTile(context);
            tileView.setBackgroundResource(getTileBackgroundDrawableResId());
            addView(tileView);
        }
    }

    public void clearBoard() {
        List<SelectedWord> selectedWordsToClear = new ArrayList<>(this.selectedWords);
        this.selectedWords.clear();

        for (SelectedWord word : selectedWordsToClear) {
            updateTiles(word.selectedTiles, false, false);
        }
    }

    /**
     * Sets the given letters on the grid
     * @param letterBoard A 2-d (row-by-col) array containing the letters to set
     */
    public void setLetterBoard(String[][] letterBoard) {
        if (letterBoard.length != getNumRows()
                || letterBoard[0].length != getNumCols()) {
            setBoardSize(letterBoard.length, letterBoard[0].length);
        }

        int row, col;
        for (int i=0; i < getChildCount(); i++) {
            row = i / getNumCols();
            col = i % getNumCols();

            LetterTile child = (LetterTile) getChildAt(i);
            child.setLetter(letterBoard[row][col]);
        }
    }

    private boolean canInsertWordOnBoard(String word, Cell startPoint, int selectionType, String[][] letterBoard) {
        Cell currPoint = startPoint;
        for (char wordLetter : word.toCharArray()) {
            String boardLetter = letterBoard[currPoint.row][currPoint.col];
            if (!TextUtils.isEmpty(boardLetter) && !boardLetter.equals(String.valueOf(wordLetter))) {
                return false;
            }

            currPoint = shift(currPoint, selectionType, 1);
        }
        return true;
    }

    public List<Word> generateRandomLetterBoard(List<String> validWords) {
        int boardSize = 4;
        for (String word : validWords) {
            if (word.length() > boardSize) {
                boardSize = word.length();
            }
        }
        return generateRandomLetterBoard(validWords, Math.min(10, boardSize + 1));
    }

    public List<Word> generateRandomLetterBoard(List<String> validWords, int boardSize) {
        String[][] letterBoard = new String[boardSize][boardSize];
        List<Word> wordLocations = new ArrayList<>(validWords.size());

        for (String word : validWords) {
            if (word.length() > boardSize) {
                throw new IllegalArgumentException("Word '" + word + "' is longer than the specified board size");
            }

            int selectionType;
            int row, col;
            int tries = 0;
            do {
                Random r = new Random();
                //noinspection ResourceType
                selectionType = r.nextInt(3) + 1;

                row = selectionType == DIRECTION_LEFT_TO_RIGHT ? r.nextInt(boardSize)
                    : (boardSize - word.length() == 0 ? 0 : r.nextInt(boardSize - word.length()));
                col = selectionType == DIRECTION_TOP_TO_BOTTOM ? r.nextInt(boardSize)
                    : (boardSize - word.length() == 0 ? 0 : r.nextInt(boardSize - word.length()));
            } while (tries++ < 100 && !canInsertWordOnBoard(word, new Cell(row, col), selectionType, letterBoard));

            if (tries == 100) break;

            Cell startPoint = new Cell(row, col);
            Cell currPoint = startPoint;
            for (char c : word.toCharArray()) {
                letterBoard[currPoint.row][currPoint.col] = String.valueOf(c);
                currPoint = shift(currPoint, selectionType, 1);
            }
            wordLocations.add(new Word(word, selectionType, startPoint));
        }

        for (int row = 0; row < letterBoard.length; row++) {
            for (int col = 0; col < letterBoard[row].length; col++) {
                if (TextUtils.isEmpty(letterBoard[row][col])) {
                    Random r = new Random();
                    letterBoard[row][col] = String.valueOf((char) (r.nextInt(26) + 'a'));
                }
            }
        }
        setLetterBoard(letterBoard);
        return wordLocations;
    }

    public void setOnWordSelectedListener(OnWordSelectedListener listener) {
        this.listener = listener;
    }

    public void setOnWordHighlightedListener(OnWordHighlightedListener listener) {
      this.highlightedListener = listener;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        List<LetterCell> tiles = new ArrayList<>(getChildCount());
        int row, col;
        for (int i = 0; i < getChildCount(); i++) {
            row = i / getNumCols();
            col = i % getNumCols();
            View child = getChildAt(i);
            tiles.add(new LetterCell(row, col, child));
        }

        Parcelable p = super.onSaveInstanceState();
        SavedState savedState = new SavedState(p);
        savedState.boardRows = getNumRows();
        savedState.boardCols = getNumCols();
        savedState.boardTiles = tiles;
        savedState.selectedWords = this.selectedWords;
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        this.selectedWords = savedState.selectedWords;

        String[][] letterBoard = new String[savedState.boardRows][savedState.boardCols];
        for (LetterCell tile : savedState.boardTiles) {
            letterBoard[tile.row][tile.col] = tile.letter;
        }
        setLetterBoard(letterBoard);

        for (SelectedWord word : selectedWords) {
            for (Tile tile : word.selectedTiles) {
                tile.view = getChildAt(tile.row, tile.col);
                tile.view.setPressed(false);
                tile.view.setSelected(true);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float X = event.getX();
        float Y = event.getY();
        int row = (int) (Y / getTileSize());
        int col = (int) (X / getTileSize());

        View child = getChildAt(row, col);

        //Exit on invalid touches
        if (event.getActionMasked() != MotionEvent.ACTION_UP
                && (row >= getNumRows()
                || col >= getNumCols()
                || child == null)) {
            return true;
        }

        super.onTouchEvent(event);

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                Tile currentTile = new Tile(row, col, child);
                if (currentSelectedWord == null) {
                    currentSelectedWord = new SelectedWord(currentTile);
                } else if (!currentTile.equals(currentSelectedWord.lastTile)
                        && currentSelectedWord.isTileValid(currentTile)) {
                    if (!currentSelectedWord.isTileAllowed(currentTile)) {
                        //Clear the status of the old selection
                        updateTiles(currentSelectedWord.selectedTiles, false, false);
                        //If the current tile is valid but not allowed for the current word selection,
                        //start a new selection that matches the tile
                        currentSelectedWord = new SelectedWord(currentSelectedWord.getInitialTile());
                    }
                    List<Tile> tiles = getTilesBetween(currentSelectedWord.lastTile, currentTile);
                    if (tiles.size() > 0) {
                        currentSelectedWord.addTiles(tiles);
                    }
                }
                updateTiles(currentSelectedWord.selectedTiles, true, false);
                break;
            case MotionEvent.ACTION_UP:
                if (currentSelectedWord != null) {
                    boolean isValidSelection = (listener != null && listener.onWordSelected(currentSelectedWord.toBoardWord()));
                    updateTiles(currentSelectedWord.selectedTiles, false, isValidSelection);
                    if (isValidSelection) {
                        selectedWords.add(currentSelectedWord);
                      if(highlightedListener != null) {
                        highlightedListener.onWordHighlighted(currentSelectedWord.toBoardWord());
                      }
                    }
                    currentSelectedWord = null;
                }
                break;
            default:
                return false;
        }
        return true;
    }

    private boolean isTileSelected(Tile tile, int direction) {
        for (SelectedWord word : selectedWords) {
            //A selected tile cannot be selected again for the same selection type
            if (direction == DIRECTION_UNKNOWN || word.direction == direction) {
                for (Tile wordTile : word.selectedTiles) {
                    //Check also the previous tile in the same direction to prevent connected
                    //selected tiles for different words from happening
                    if (wordTile.equals(tile) || wordTile.equals(shift(tile, direction, -1))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private View getChildAt(int row, int col) {
        return getChildAt(col + row * getNumCols());
    }

    private void updateTiles(List<Tile> tiles, boolean pressed, boolean selected) {
        for (Tile tile : tiles) {
            tile.view.setPressed(pressed);
            //Keep the tile selected if it belongs to a previously selected word
            tile.view.setSelected(selected || isTileSelected(tile, DIRECTION_UNKNOWN));
        }
    }

    /**
     * Get all the tiles between the start and end, excluding the
     * start tile but including the end one
     */
    private List<Tile> getTilesBetween(Tile startTile, Tile endTile) {
        List<Tile> tiles = new ArrayList<>();
        int direction = startTile.getDirectionFrom(endTile);
        Cell currPoint = startTile;
        if (direction != DIRECTION_UNKNOWN) {
            while (!currPoint.equals(endTile)) {
                currPoint = shift(currPoint, direction, 1);

                View child = getChildAt(currPoint.row, currPoint.col);
                Tile t = new Tile(currPoint.row, currPoint.col, child);
                if (isTileSelected(t, direction)) {
                    break;
                } else {
                    tiles.add(t);
                }
            }
        }
        return tiles;
    }

    private Cell shift(Cell point, int direction, int n) {
        if (direction == DIRECTION_TOP_TO_BOTTOM) {
            return new Cell(point.row + n, point.col);
        } else if (direction == DIRECTION_LEFT_TO_RIGHT) {
            return new Cell(point.row, point.col + n);
        } else if (direction == DIRECTION_TOP_BOTTOM_LEFT_RIGHT) {
            return new Cell(point.row + n, point.col + n);
        }
        return point;
    }

    private static class SelectedWord implements Parcelable {
        private int direction = DIRECTION_UNKNOWN;

        private Tile lastTile;
        private List<Tile> selectedTiles;

        public SelectedWord(Tile initialTile) {
            lastTile = initialTile;
            selectedTiles = new ArrayList<>();
            selectedTiles.add(initialTile);
        }

        protected SelectedWord(Parcel in) {
            //noinspection ResourceType
            direction = in.readInt();
            lastTile = in.readParcelable(Tile.class.getClassLoader());
            selectedTiles = in.createTypedArrayList(Tile.CREATOR);
        }

        public static final Creator<SelectedWord> CREATOR = new Creator<SelectedWord>() {
            @Override
            public SelectedWord createFromParcel(Parcel in) {
                return new SelectedWord(in);
            }

            @Override
            public SelectedWord[] newArray(int size) {
                return new SelectedWord[size];
            }
        };

        public boolean isTileValid(Tile tile) {
            return getInitialTile().getDirectionFrom(tile) != DIRECTION_UNKNOWN;
        }

        public boolean isTileAllowed(Tile tile) {
            int currType = lastTile.getDirectionFrom(tile);
            return currType != DIRECTION_UNKNOWN
                    && (direction == DIRECTION_UNKNOWN || direction == currType);
        }

        public Tile getInitialTile() {
            return selectedTiles.get(0);
        }

        public void addTiles(List<Tile> tiles) {
            if (direction == DIRECTION_UNKNOWN) {
                direction = lastTile.getDirectionFrom(tiles.get(0));
            }
            selectedTiles.addAll(tiles);
            lastTile = selectedTiles.get(selectedTiles.size() - 1);
        }

        @Override
        public String toString() {
            StringBuilder letters = new StringBuilder(selectedTiles.size());
            for (Tile LetterCell : selectedTiles) {
                letters.append(((LetterTile) LetterCell.view).getLetter());
            }
            return letters.toString();
        }

        public Word toBoardWord() {
            return new Word(toString(), direction, getInitialTile());
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(direction);
            dest.writeParcelable(lastTile, flags);
            dest.writeTypedList(selectedTiles);
        }
    }

    protected static class LetterCell extends Tile {

        private final String letter;

        public LetterCell(int row, int col, View view) {
            super(row, col, view);
            letter = ((LetterTile)view).getLetter();
        }

        protected LetterCell(Parcel in) {
            super(in);
            letter = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeString(letter);
        }

        public static final Creator<LetterCell> CREATOR = new Creator<LetterCell>() {
            @Override
            public LetterCell createFromParcel(Parcel in) {
                return new LetterCell(in);
            }

            @Override
            public LetterCell[] newArray(int size) {
                return new LetterCell[size];
            }
        };
    }

    private static class SavedState extends BaseSavedState {

        private int boardRows, boardCols;
        private List<LetterCell> boardTiles;
        private List<SelectedWord> selectedWords;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            boardRows = in.readInt();
            boardCols = in.readInt();
            boardTiles = in.createTypedArrayList(LetterCell.CREATOR);
            selectedWords = in.createTypedArrayList(SelectedWord.CREATOR);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(boardRows);
            out.writeInt(boardCols);
            out.writeTypedList(boardTiles);
            out.writeTypedList(selectedWords);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
