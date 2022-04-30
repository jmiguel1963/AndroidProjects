package com.example.checkerstest;

import java.util.ArrayList;

public abstract class Piece {

    public final static boolean WHITE = true;
    public final static boolean BLACK = false;
    protected boolean color;
    private int drawableId;

    public Piece(boolean color) {
        SetColor(color);
    }

    public void SetColor(boolean color) {
        this.color = color;
    }

    int getDrawableId(){
        return this.drawableId;
    }

    public boolean getColor() {
        return this.color;
    }

    public static String convertPosToString(int row, int col) {
        String position = "";
        position += (char)('a' + col);
        position += (char)('1' + row);
        return position;
    }

    public static boolean IsInsideBoard(int row, int col) {
        if (col >= 0 && col < CheckersBoard.COLS &&
                row >= 0 && row < CheckersBoard.ROWS) {
            return true;
        }
        return false;
    }

    public abstract ArrayList<String> GetValidMoves (CheckersBoard board, int row, int col);
}
