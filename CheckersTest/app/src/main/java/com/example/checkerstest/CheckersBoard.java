package com.example.checkerstest;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;

public class CheckersBoard extends ConstraintLayout {

    public final static int COLS = 8;
    public final static int ROWS = 8;
    private Cell[][] board;
    private ArrayList<String> availableMovements;
    private ArrayList<String> initialPositions;
    private Activity thisActivity;
    private ArrayList<TextView> thisMessages;
    private boolean playFinished=false;
    private boolean playDraws=false;

    public CheckersBoard(Context context,AttributeSet attributeSet) {
        super(context,attributeSet);
    }

    public void initializeBoard(){
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i+j)%2!=0){
                    board[i][j].initialize(getContext(),Cell.WHITE,null);
                }else{
                    board[i][j].initialize(getContext(),Cell.BLACK,null);
                }
            }
        }
    }

    public void initializePieces(){
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i+j)%2==0 && i>4){
                    board[i][j].initialize(getContext(),Cell.BLACK,new Pawn(Piece.BLACK));
                    board[i][j].setTag(R.id.filled,1);
                }
                if ((i+j)%2==0 && i<3){
                    board[i][j].initialize(getContext(),Cell.BLACK,new Pawn(Piece.WHITE));
                    board[i][j].setTag(R.id.filled,2);
                }
            }
        }
    }

    public void initializeSpecialPieces(){
        board[6][0].initialize(getContext(),Cell.BLACK,new Pawn(Piece.BLACK));
        board[6][0].setTag(R.id.filled,1);
        board[6][4].initialize(getContext(),Cell.BLACK,new Pawn(Piece.BLACK));
        board[6][4].setTag(R.id.filled,1);
        board[5][5].initialize(getContext(),Cell.BLACK,new Pawn(Piece.BLACK));
        board[5][5].setTag(R.id.filled,1);
        board[6][6].initialize(getContext(),Cell.BLACK,new Pawn(Piece.BLACK));
        board[6][6].setTag(R.id.filled,1);
        board[4][6].initialize(getContext(),Cell.BLACK,new Pawn(Piece.BLACK));
        board[4][6].setTag(R.id.filled,1);
        board[0][4].initialize(getContext(),Cell.BLACK,new Queen(Piece.BLACK));
        board[0][4].setTag(R.id.filled,1);
        board[5][1].initialize(getContext(),Cell.BLACK,new Pawn(Piece.WHITE));
        board[5][1].setTag(R.id.filled,2);
        board[4][2].initialize(getContext(),Cell.BLACK,new Pawn(Piece.WHITE));
        board[4][2].setTag(R.id.filled,2);
        board[3][3].initialize(getContext(),Cell.BLACK,new Pawn(Piece.WHITE));
        board[3][3].setTag(R.id.filled,2);
        board[1][3].initialize(getContext(),Cell.BLACK,new Pawn(Piece.WHITE));
        board[1][3].setTag(R.id.filled,2);
        board[2][4].initialize(getContext(),Cell.BLACK,new Pawn(Piece.WHITE));
        board[2][4].setTag(R.id.filled,2);
        board[3][7].initialize(getContext(),Cell.BLACK,new Pawn(Piece.WHITE));
        board[3][7].setTag(R.id.filled,2);
    }

    public void move(){
        initialPositions=new ArrayList<String>();
        availableMovements=new ArrayList<String>();

        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                board[i][j].setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (playFinished || playDraws){
                            thisActivity.finish();
                        }

                        Cell cell=(Cell)view;
                        int tag=(int)cell.getTag(R.id.filled);
                        String position=(String)cell.getTag(R.id.position);
                        int row=Integer.parseInt(position.substring(0,1));;
                        int col=Integer.parseInt(position.substring(1));
                        String codifiedPosition=Piece.convertPosToString(row,col);
                        int resultCheck=checkValues(tag,codifiedPosition,CheckersGame.Player,initialPositions.size(),availableMovements);
                        switch(resultCheck){
                            case 2:
                                String currentPlayer=(CheckersGame.Player)?"White":"Black";
                                thisMessages.get(3).setText("It is "+currentPlayer+" pieces turn");
                                break;
                            case 3:
                                thisMessages.get(3).setText(("A piece must be selected"));
                                break;
                            case 4:
                                thisMessages.get(3).setText("Selected position is not available");
                                break;
                            case 1:

                                if (initialPositions.size()!=0){
                                    initialPositions=new ArrayList<String>();
                                    availableMovements= new ArrayList<String>();
                                }
                                initialPositions.add(codifiedPosition);
                                thisMessages.get(3).setText("");
                                thisMessages.get(1).setText("Initial position "+codifiedPosition);
                                availableMovements=cell.getPiece().GetValidMoves(CheckersBoard.this,row,col);
                                coloringAvailableMovements();
                                ArrayList<String> intermediateMovements=new ArrayList<String>();
                                for(String mov:availableMovements){
                                    String last2Characters=mov.substring(mov.length()-2,mov.length());
                                    if (last2Characters.equals("ok") || last2Characters.equals("ko")){
                                        intermediateMovements.add(mov.substring(0,mov.length()-2));
                                    }else{
                                        intermediateMovements.add(mov);
                                    }
                                }
                                availableMovements=new ArrayList<String>();
                                for (String mov:intermediateMovements){
                                    availableMovements.add(mov);
                                }
                                String total="";
                                for (String c:availableMovements)
                                    total +=c+" ";
                                total=total.trim();
                                if (availableMovements.size()!=0){
                                    thisMessages.get(2).setText("available positions "+total);
                                }else{
                                    int draws=moveIsPossible(CheckersGame.Player);
                                    if (draws==0){
                                        thisMessages.get(2).setText("no available positions");
                                    }else if(draws==1){
                                        thisMessages.get(1).setText("");
                                        thisMessages.get(2).setText("");
                                        String oldPlayer=(CheckersGame.Player)?"White":"Black";
                                        thisMessages.get(3).setText(oldPlayer+" pieces are blocked. Change of player");
                                        CheckersGame.Player=!CheckersGame.Player;
                                        String newPlayer=(CheckersGame.Player)?"White pieces play":"Black pieces play";
                                        thisMessages.get(0).setText(newPlayer);
                                    }else if (draws==2){
                                        thisMessages.get(1).setText("");
                                        thisMessages.get(2).setText("");
                                        thisMessages.get(0).setText("");
                                        thisMessages.get(3).setText("No player can move. Click any cell to begin a new Game");
                                    }
                                }
                                break;
                            case 0:
                                for (String availMov:availableMovements){
                                    if (availMov.substring(availMov.length()-2,availMov.length()).compareTo(codifiedPosition)==0){
                                        movePiece(initialPositions.get(0),availMov,CheckersGame.Player);
                                        decoloringAvailableMovements();
                                        break;
                                        }
                                    }
                            }
                        }
                    });
                }
            }
    }

    public int moveIsPossible(boolean player){
        int movePossible=0;
        int possibleFirstPlayer=1;
        int possibleSecondPlayer=1;
        int tag=(player)?2:1;
        boolean loopNotFinished=true;
        for(int i=0;i<ROWS && loopNotFinished;i++){
            for (int j=0;j<COLS;j++){
                if ((i+j)%2==0){
                    if ((int)board[i][j].getTag(R.id.filled)==tag){
                        availableMovements=board[i][j].getPiece().GetValidMoves(CheckersBoard.this,i,j);
                        if (availableMovements.size()>0){
                            possibleFirstPlayer=0;
                            loopNotFinished=false;
                            break;
                        }
                    }
                }
            }
        }
        if (possibleFirstPlayer!=0){
            //CheckersGame.Player=!CheckersGame.Player;
            tag=(!player)?2:1;
            loopNotFinished=true;
            for(int i=0;i<ROWS && loopNotFinished;i++){
                for (int j=0;j<COLS;j++){
                    if ((i+j)%2==0){
                        if ((int)board[i][j].getTag(R.id.filled)==tag){
                            availableMovements=board[i][j].getPiece().GetValidMoves(CheckersBoard.this,i,j);
                            if (availableMovements.size()>0){
                                possibleSecondPlayer=0;
                                loopNotFinished=false;
                                break;
                            }
                        }
                    }
                }
            }
        }
        if (possibleSecondPlayer==0 && possibleFirstPlayer==1){
            /*String newPlayer=(CheckersGame.Player)?"White":"Black";
            String oldPlayer=(CheckersGame.Player)?"Black":"White";
            CheckersGame.Player=!CheckersGame.Player;
            thisMessages.get(1).setText("");
            thisMessages.get(2).setText("");
            thisMessages.get(0).setText(newPlayer+" pieces play");
            thisMessages.get(3).setText(oldPlayer+" pieces are blocked");*/
            movePossible=1;
        }else if (possibleSecondPlayer==1 && possibleFirstPlayer==1){
            movePossible=2;
        }
        if (movePossible==2){
            playDraws = true;
        }

        return movePossible;
    }

    public void coloringAvailableMovements(){
        int row,col;
        int maxLength=0;
        boolean isPawn=false;
        ArrayList<String> maxMovements=new ArrayList<String>();
        ArrayList<String> maxDistances=new ArrayList<String>();
        decoloringAvailableMovements();
        int initialRow=getRow(initialPositions.get(0));
        int initialCol=getCol(initialPositions.get(0));
        int drawableId=board[initialRow][initialCol].getPiece().getDrawableId();
        if (drawableId==R.drawable.white_piece || drawableId==R.drawable.black_piece){
            isPawn=true;
        }
        if (availableMovements.size()>0){

            for (String str:availableMovements){
                if (str.length()>maxLength)
                    maxLength=str.length();
            }
            for (String str:availableMovements){
                if (str.length()==maxLength)
                    maxMovements.add(str);
            }
            if (isPawn){
                for (String str:availableMovements){
                    int distance=Math.abs(getRow(initialPositions.get(0))-getRow(str.substring(str.length()-2)))+Math.abs(getCol(initialPositions.get(0))-getCol(str.substring(str.length()-2)));
                    if (distance>2){
                        maxDistances.add(str);
                    }
                }
            }

            for (String str:availableMovements){
                String last2Characters;
                if (isPawn){
                    last2Characters = str.substring(str.length() - 2);
                }else{
                    last2Characters = str.substring(str.length()-4,str.length() - 2);
                }
                row=getRow(last2Characters);
                col=getCol(last2Characters);
                board[row][col].setBackgroundColor(Color.GREEN);
            }

            if (availableMovements.size()==maxMovements.size() && isPawn){
                if(availableMovements.size()!=maxDistances.size()){
                    for(String str:maxDistances){
                        String last2Characters = str.substring(str.length() - 2);
                        row=getRow(last2Characters);
                        col=getCol(last2Characters);
                        board[row][col].setBackgroundColor(Color.BLUE);
                    }
                }else if(availableMovements.size()==maxDistances.size()){
                   String mylast2Characters=maxDistances.get(0).substring(maxDistances.get(0).length() - 2);
                   row=getRow(mylast2Characters);
                   col=getCol(mylast2Characters);
                   int distance=Math.abs(getRow(initialPositions.get(0))-row)+Math.abs(getCol(initialPositions.get(0))-col);
                   if (distance>2){
                       board[row][col].setBackgroundColor(Color.BLUE);
                   }
                }
            }else if (availableMovements.size()!=maxMovements.size() && isPawn){
                for(String str:maxMovements){
                    String last2Characters = str.substring(str.length() - 2);
                    row=getRow(last2Characters);
                    col=getCol(last2Characters);
                    board[row][col].setBackgroundColor(Color.RED);
                }
            } else if (availableMovements.size()!=maxMovements.size() && !isPawn){
                for(String str:maxMovements){
                    String last2Characters = str.substring(str.length() - 4,str.length() - 2);
                    row=getRow(last2Characters);
                    col=getCol(last2Characters);
                    board[row][col].setBackgroundColor(Color.RED);
                }
                for(String str:availableMovements){
                    String notlast2Characters = str.substring(str.length() - 4,str.length() - 2);
                    String last2Characters=str.substring(str.length() - 2);
                    row=getRow(notlast2Characters);
                    col=getCol(notlast2Characters);
                    if (last2Characters.equals("ok") && ((ColorDrawable)board[row][col].getBackground()).getColor()==Color.GREEN){
                        board[row][col].setBackgroundColor(Color.BLUE);
                    }
                }
            }else if(availableMovements.size()==maxMovements.size() && !isPawn){
                for(String str:availableMovements){
                    String notlast2Characters = str.substring(str.length() - 4,str.length() - 2);
                    String last2Characters=str.substring(str.length() - 2);
                    row=getRow(notlast2Characters);
                    col=getCol(notlast2Characters);
                    if (last2Characters.equals("ok")){
                        board[row][col].setBackgroundColor(Color.BLUE);
                    }
                }
            }
        }
    }
    
    public void decoloringAvailableMovements(){
        int row,col;
        for (int i=0;i<ROWS;i++){
            for(int j=0;j<COLS;j++){
                if ((i+j)%2==0){
                    board[i][j].setBackgroundColor(Color.BLACK);
                }
            }
        }
    }

    public boolean isGameFinished(boolean player){
        int tag=player?1:2;
        int counter=0;
        for(int i=0;i<8;i++){
            for (int j=0;j<8;j++){
                if ((int)board[i][j].getTag(R.id.filled)==tag){
                    counter++;
                }
            }
        }
        if (counter==0)
            return true;
        return false;
    }

    public void movePiece(String initialPosition,String movement,boolean player){
        int playerConvInt=player?1:0;
        int tagPlayer=playerConvInt+1;
        int row,col;
        int initialRow=getRow(initialPosition);
        int initialCol=getCol(initialPosition);
        int drawableID=board[initialRow][initialCol].getPiece().getDrawableId();
        String stepMovement="";
        while (movement.length()>1){
            stepMovement=movement.substring(0,2);
            row=getRow(stepMovement);
            col=getCol(stepMovement);
            int distance=Math.abs(row-initialRow)+Math.abs(col-initialCol);
            int directionRow=(int)((row-initialRow)/Math.abs(row-initialRow));
            int directionCol=(int)((col-initialCol)/Math.abs(col-initialCol));
            board[initialRow][initialCol].setImageResource(0);
            board[initialRow][initialCol].setTag(R.id.filled,0);
            board[initialRow][initialCol].initialize(getContext(),Cell.BLACK,null);
            board[row][col].setTag(R.id.filled,tagPlayer);
            if ((player && row==7) || (!player && row==0) && (drawableID==R.drawable.white_piece || drawableID==R.drawable.black_piece)){
                board[row][col].initialize(getContext(),Cell.BLACK,new Queen(player));
                board[row][col].setImageResource(board[row][col].getPiece().getDrawableId());
            }else{
                if (drawableID==R.drawable.black_queen || drawableID==R.drawable.white_queen){
                    board[row][col].initialize(getContext(),Cell.BLACK,new Queen(player));
                }
                else{
                    board[row][col].initialize(getContext(),Cell.BLACK,new Pawn(player));
                }
                //board[row][col].setImageResource(drawableID);
            }
            if (distance>2){
                int counterRow=initialRow+directionRow;
                int counterCol=initialCol+directionCol;
                while(counterRow!=row){
                    if ((int)board[counterRow][counterCol].getTag(R.id.filled)!=0){
                        board[counterRow][counterCol].setImageResource(0);
                        board[counterRow][counterCol].setTag(R.id.filled,0);
                        board[counterRow][counterCol].initialize(getContext(),Cell.BLACK,null);
                    }
                    counterRow +=directionRow;
                    counterCol +=directionCol;
                }
            }
            if (movement.length()>2){
                movement=movement.substring(2,movement.length());
                initialRow=row;
                initialCol=col;
            }else{
                movement="";
            }
        }
        initialPositions=new ArrayList<String>();
        availableMovements=new ArrayList<String>();
        thisMessages.get(1).setText("");
        thisMessages.get(2).setText("");
        playFinished=isGameFinished(CheckersGame.Player);
        if (playFinished){
            if (CheckersGame.Player)
                thisMessages.get(0).setText("White pieces win");
            else
                thisMessages.get(0).setText("Black pieces win");
            thisMessages.get(3).setText("Click on a cell to begin a new play");
        }else{
            CheckersGame.Player=!CheckersGame.Player;
            if (CheckersGame.Player)
                thisMessages.get(0).setText("White pieces play");
            else
                thisMessages.get(0).setText("Black pieces play");
        }


    }

    public Cell getCell(int i,int j) {
        return this.board[i][j];
    }

    public int getCol(String position) {
        return position.charAt(0) - 'a';
    }

    public int getRow(String position) {
        return position.charAt(1) - '1';
    }

    private int checkValues(int tag,String position,boolean player,int size,ArrayList<String> availablePositions){
        int check_result=0;
        int playerConvInt=player?1:0;
        int tagConv=tag-1;
        if (size<2 && tag!=0){
          if (playerConvInt!=tagConv){
              check_result=2;
          }else{
              check_result=1;
          }
        }else if (size==0 && tag==0){
            check_result=3;
        }else if (size!=0 && tag==0){
            check_result=4;
            for (String pos:availablePositions){
                if (pos.substring(pos.length()-2,pos.length()).compareTo(position)==0){
                    check_result=0;
                    break;
                    }
            }
        }
        return check_result;
    }

    public void initLayout(Activity activity,ArrayList<TextView> messages){
        board = new Cell[ROWS][COLS];
        thisActivity=activity;
        thisMessages=messages;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                String buttonID = "button" + i + j;
                int resID = getResources().getIdentifier(buttonID, "id", activity.getPackageName());
                board[i][j] = findViewById(resID);
                board[i][j].setTag(R.id.position, "" + i + j);
                board[i][j].setTag(R.id.filled, 0);
            }
        }
    }
}
