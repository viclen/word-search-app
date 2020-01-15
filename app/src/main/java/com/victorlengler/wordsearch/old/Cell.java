package com.victorlengler.wordsearch.old;

public class Cell {
    public final int row, col;

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getDirectionFrom(Cell other) {
        if (row == other.row
                && col < other.col) {
            return WordSearchGrid.DIRECTION_LEFT_TO_RIGHT;
        } else if (row < other.row
                && col == other.col) {
            return WordSearchGrid.DIRECTION_TOP_TO_BOTTOM;
        } else if (row < other.row
                && col < other.col
                && row - other.row == col - other.col) {
            return WordSearchGrid.DIRECTION_TOP_BOTTOM_LEFT_RIGHT;
        } else {
            return WordSearchGrid.DIRECTION_UNKNOWN;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof Cell)) return false;

        Cell that = (Cell) o;
        return row == that.row && col == that.col;
    }

    @Override
    public int hashCode() {
        int result = row;
        result = 31 * result + col;
        return result;
    }

    @Override
    public String toString() {
        return "[" + row + ", " + col + "]";
    }
}
