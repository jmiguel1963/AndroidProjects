package com.example.checkerstest;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

public class Cell extends androidx.appcompat.widget.AppCompatImageButton {

    public final static boolean WHITE=true;
    public final static boolean BLACK=false;
    private boolean color;
    private Piece piece;

    public Cell(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
    }

    public void initialize(Context context,boolean color, Piece piece) {

        if (color){
            this.setBackgroundColor(Color.WHITE);
        }
        else{
            this.setBackgroundColor(Color.BLACK);
        }
        if (piece==null && color)
            this.setBackground(context.getDrawable(R.drawable.border));
        this.color=color;
        this.piece=piece;
        if (piece!=null)
            this.setImageResource(piece.getDrawableId());
    }

    public boolean getColor(){
        return this.color;
    }

    public void setColor(boolean color){
        this.color=color;
    }

    public Piece getPiece() {
        return this.piece;
    }

    public void setPiece(Piece piece) {

        this.piece = piece;
        if (this.piece!=null)
            this.setImageResource(piece.getDrawableId());
    }

    public boolean hasPiece() {
        if (getPiece()!=null)
            return true;
        return false;
    }

    public void empty() {
        this.piece=null;
    }

    public void move(String position){

    }


}
