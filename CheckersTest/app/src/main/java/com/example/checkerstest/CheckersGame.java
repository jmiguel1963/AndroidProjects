package com.example.checkerstest;

import android.app.Activity;
import android.widget.TextView;

import java.util.ArrayList;

public class CheckersGame {

    private CheckersBoard checkersBoard;
    public static boolean Player=true;
    CheckersGame(){

    }

    public void initialize(Activity activity,ArrayList<TextView> messages){
        CheckersGame.Player=true;
        checkersBoard=activity.findViewById(R.id.checkersBoard);
        checkersBoard.initLayout(activity,messages);
        checkersBoard.initializeBoard();
        checkersBoard.initializePieces();
        //checkersBoard.initializeSpecialPieces();
    }

    public void play(){

        checkersBoard.move();
    }

}
