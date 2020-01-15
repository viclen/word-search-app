package com.victorlengler.wordsearch;

public class Word {
    private final String word;
    private final int direction;
    private final Cell startPoint;

    public Word(String word, int direction, Cell startPoint) {
        this.word = word;
        this.direction = direction;
        this.startPoint = startPoint;
    }

    public Cell getWordStartPoint() {
        return startPoint;
    }

    public int getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return word;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Word word = (Word) o;

        if (!this.word.equals(word.word)) return false;
        //This check is necessary because we might have overlapping words: e.g., car and card.
        //If the user selected "car" from the "card" tiles, we must not accept it.
        return direction == word.direction && startPoint.equals(word.startPoint);

    }

    @Override
    public int hashCode() {
        int result = word.hashCode();
        result = 31 * result + direction;
        result = 31 * result + startPoint.hashCode();
        return result;
    }
}
