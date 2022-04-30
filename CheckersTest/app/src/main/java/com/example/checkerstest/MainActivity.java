package com.example.checkerstest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private boolean gameMode=false;
    private CheckersBoard checkersBoard;
    private TextView playTurn;
    private TextView originPosition;
    private TextView destinationPositions;
    private TextView errorMessage;
    private ArrayList<TextView> messages;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        gameMode = intent.getBooleanExtra(InitialActivity.GAMETYPE,false);
        playTurn=findViewById(R.id.playerTurn);
        playTurn.setText("White pieces play");
        originPosition=findViewById(R.id.piecePositionSelected);
        originPosition.setText("");
        destinationPositions=findViewById(R.id.piecePositionToMove);
        destinationPositions.setText("");
        errorMessage=findViewById(R.id.errorMessage);
        errorMessage.setText("");
        messages=new ArrayList<TextView>();
        messages.add(playTurn);
        messages.add(originPosition);
        messages.add(destinationPositions);
        messages.add(errorMessage);

        CheckersGame checkersGame=new CheckersGame();
        checkersGame.initialize(this,messages);
        checkersGame.play();
    }
}