package com.example.checkerstest;

import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;

public class Queen extends Piece {

    private int drawableId;
    public Queen(boolean color) {
        super(color);
        this.drawableId=(this.color)?R.drawable.white_queen:R.drawable.black_queen;
    }

    public int getDrawableId(){
        return this.drawableId;
    }

    @Override
    public ArrayList<String> GetValidMoves(CheckersBoard board, int row, int col) {

        ArrayList<String> moves = new ArrayList<String>();
        ArrayList<String> intermediate_moves = new ArrayList<String>();
        ArrayList<String> validPos;
        ArrayList<String> addSel=new ArrayList<String>();
        //ArrayList<String> addSecSel=new ArrayList<String>();
        //ArrayList<String> addThirdSel=new ArrayList<String>();
        //ArrayList<String> addFourthSel=new ArrayList<String>();

        String initial_position=Piece.convertPosToString(row,col);

        boolean moves_available=true;
        int count=0;
        while(moves_available) {
            if (moves.size()!=0){
                count=0;

                for (String move:moves) {

                    String last_2_characters=move.substring(move.length()-2,move.length());
                    String moveWoDirection=move.substring(0,move.length()-2);
                    String prevMove=move.substring(0,move.length()-4);
                    if (!last_2_characters.equals("ko") && !last_2_characters.equals("ok")) {

                        if (last_2_characters.equals("01") || last_2_characters.equals("10")){
                            validPos = GetValidDiagonalMove(board,moveWoDirection,1,1);
                            for (String validMove:validPos){
                                addSel.add(prevMove+validMove);
                                intermediate_moves.add(prevMove+validMove);
                                count++;
                            }
                        }

                        if (last_2_characters.equals("00") || last_2_characters.equals("11")){
                            validPos = GetValidDiagonalMove(board,moveWoDirection,-1,1);
                            for (String validMove:validPos){
                                addSel.add(prevMove+validMove);
                                intermediate_moves.add(prevMove+validMove);
                                count++;
                            }
                        }

                        if (last_2_characters.equals("11") || last_2_characters.equals("00")){
                            validPos = GetValidDiagonalMove(board,moveWoDirection,1,-1);
                            for (String validMove:validPos){
                                addSel.add(prevMove+validMove);
                                intermediate_moves.add(prevMove+validMove);
                                count++;
                            }
                        }

                        if (last_2_characters.equals("10") || last_2_characters.equals("01")){
                            validPos = GetValidDiagonalMove(board,moveWoDirection,-1,-1);
                            for (String validMove:validPos){
                                addSel.add(prevMove+validMove);
                                intermediate_moves.add(prevMove+validMove);
                                count++;
                            }
                        }
                    }

                    if (addSel.size()==0){
                        if (!last_2_characters.equals("ko") && !last_2_characters.equals("ok")){
                        //if (!last_2_characters.equals("ko") && prevMove.length()>1){
                            intermediate_moves.add(prevMove+"ok");
                        }else{
                            intermediate_moves.add(move);
                        }
                        //addSel=new ArrayList<String>();
                    }else{
                        addSel=new ArrayList<String>();
                    }
                }

                moves=new ArrayList<String>();
                for (String mov:intermediate_moves) {
                    moves.add(mov);
                }

                intermediate_moves=new ArrayList<String>();

            }else {
                validPos = GetValidDiagonalMove(board,initial_position,1,1);
                for (String validMove:validPos){
                    moves.add(validMove);
                    count++;
                }

                validPos = GetValidDiagonalMove(board,initial_position,-1,1);
                for (String validMove:validPos){
                    moves.add(validMove);
                    count++;
                }

                validPos = GetValidDiagonalMove(board,initial_position,1,-1);

                for (String validMove:validPos){
                    moves.add(validMove);
                    count++;
                }

                validPos = GetValidDiagonalMove(board,initial_position,-1,-1);

                for (String validMove:validPos){
                    moves.add(validMove);
                    count++;
                }
            }

            if (count==0)
                moves_available=false;
        }
        intermediate_moves=new ArrayList<String>();

        for (String mov1:moves) {
            for (String mov2:moves) {
                if (!mov2.equals(mov1)) {
                    if (mov2.substring(mov2.length()-4,mov2.length()-2).equals(mov1.substring(mov1.length()-4,mov1.length()-2)) && (mov2.length()<mov1.length())){
                        if (!intermediate_moves.contains(mov2)) {
                            intermediate_moves.add(mov2);
                        }
                    }
                }
            }
        }

        Iterator<String> itr = moves.iterator();
        for (String movement:intermediate_moves) {
            while (itr.hasNext()) {
                String movsub=itr.next();
                if (movsub.equals(movement)) {
                    itr.remove();
                }
            }
        }

        return moves;
    }

    public ArrayList<String> GetValidDiagonalMove(CheckersBoard board,String position,int vdir,int hdir) {

        boolean beforeKilled=false;
        boolean beyondKilledPiecePlaces=false;
        boolean loopNotFinished=true;
        ArrayList<String> moves=new ArrayList<String>();
        Cell cell;
        int counterCellsAfterKilled=0;
        String new_position,positionSaved;
        int col_move,row_move;
        int i,j;

        String last_2_characters=position.substring(position.length()-2,position.length());

        if (last_2_characters.equals("pw")) {
            beforeKilled=true;
            position=position.substring(0,position.length()-2);
        }else if (position.length()>2){
            position=position.substring(0,position.length()-2);
        }

        if (position.length()>2){
            position=position.substring(position.length()-2,position.length());
        }

        int col_original = board.getCol(position);
        int row_original = board.getRow(position);

        if (vdir==1 && hdir==1) {

            for (i=row_original+1;i<CheckersBoard.ROWS && loopNotFinished;i++) {
                for (j=col_original+1;j<CheckersBoard.COLS;j++) {
                    if ((i+j)%2==0 && ((i-j)==(row_original-col_original))) {
                        cell=board.getCell(i,j);
                        new_position=Piece.convertPosToString(i,j);
                        if (!beforeKilled) {
                            if (!cell.hasPiece() && !beyondKilledPiecePlaces) {
                                moves.add(new_position+"ko");
                                break;
                            }
                            else if (!cell.hasPiece() && beyondKilledPiecePlaces) {
                                counterCellsAfterKilled++;
                                moves.add(new_position+"pw11");
                                break;
                            }
                            else if (cell.getPiece().getColor() != this.color && !beyondKilledPiecePlaces){
                                row_move =i+vdir;
                                col_move =j+hdir;
                                if (!IsInsideBoard(row_move, col_move)){
                                    loopNotFinished=false;
                                    break;
                                }
                                new_position = convertPosToString(row_move, col_move);
                                cell = board.getCell(row_move,col_move);
                                if (!cell.hasPiece()) {
                                    moves=new ArrayList<String>();
                                    moves.add(new_position+"pw11");
                                    beyondKilledPiecePlaces=true;
                                    counterCellsAfterKilled++;
                                    i=i+vdir;
                                    break;
                                }else{
                                    loopNotFinished=false;
                                    break;
                                }
                            }
                            else if(cell.getPiece().getColor() != this.color && beyondKilledPiecePlaces){
                                row_move=i+vdir;
                                col_move=j+hdir;
                                if (!IsInsideBoard(row_move,col_move)){
                                    loopNotFinished=false;
                                    break;
                                }
                                new_position = convertPosToString(row_move, col_move);
                                cell = board.getCell(row_move,col_move);
                                if (!cell.hasPiece()) {
                                    while(counterCellsAfterKilled>1){
                                        moves.remove(moves.size()-1);
                                        counterCellsAfterKilled--;
                                    }
                                    String lastPosition=moves.get(moves.size()-1);
                                    moves.set(moves.size()-1,lastPosition.substring(0,lastPosition.length()-4)+new_position+"pw11");
                                    beyondKilledPiecePlaces=true;
                                    counterCellsAfterKilled=0;
                                    i=i+vdir;
                                    break;
                                }else{
                                    loopNotFinished=false;
                                    break;
                                }
                            }
                            else if (cell.getPiece().getColor() == this.color) {
                                loopNotFinished=false;
                                break;
                            }
                        }else {
                            if (!cell.hasPiece() && !beyondKilledPiecePlaces){
                                break;
                            }
                            else if (!cell.hasPiece() && beyondKilledPiecePlaces) {
                                moves.add(new_position+"pw11");
                                counterCellsAfterKilled++;
                                break;
                            }
                            else if (cell.getPiece().getColor() != this.color && !beyondKilledPiecePlaces){
                                row_move =i+vdir;
                                col_move =j+hdir;
                                if (!IsInsideBoard(row_move, col_move)){
                                    loopNotFinished=false;
                                    break;
                                }
                                new_position = convertPosToString(row_move, col_move);
                                cell = board.getCell(row_move,col_move);
                                if (!cell.hasPiece()) {
                                    moves.add(new_position+"pw11");
                                    beyondKilledPiecePlaces=true;
                                    i=i+vdir;
                                    break;
                                }else{
                                    loopNotFinished=false;
                                    break;
                                }
                            }
                            else if (cell.getPiece().getColor() != this.color && beyondKilledPiecePlaces){
                                row_move =i+vdir;
                                col_move =j+hdir;
                                if (!IsInsideBoard(row_move, col_move)){
                                    loopNotFinished=false;
                                    break;
                                }
                                new_position = convertPosToString(row_move, col_move);
                                cell = board.getCell(row_move,col_move);
                                if (!cell.hasPiece()) {
                                    while(counterCellsAfterKilled>1){
                                        moves.remove(moves.size()-1);
                                        counterCellsAfterKilled--;
                                    }
                                    String lastPosition=moves.get(moves.size()-1);
                                    moves.set(moves.size()-1,lastPosition.substring(0,lastPosition.length()-4)+new_position+"pw11");
                                    beyondKilledPiecePlaces=true;
                                    counterCellsAfterKilled=0;
                                    i=i+vdir;
                                    break;
                                }else{
                                    loopNotFinished=false;
                                    break;
                                }
                            }
                            else if (cell.getPiece().getColor() == this.color) {
                                loopNotFinished=false;
                                break;
                            }
                        }
                    }
                }
            }

        }

        if (vdir==-1 && hdir==1) {

            for (i=row_original-1;i>=0 && loopNotFinished;i--) {
                for (j=col_original+1;j<CheckersBoard.COLS;j++) {
                    if ((i+j)%2==0 && ((i+j)==(row_original+col_original))) {
                        cell=board.getCell(i,j);
                        new_position=Piece.convertPosToString(i,j);
                        if (!beforeKilled) {
                            if (!cell.hasPiece() && !beyondKilledPiecePlaces) {
                                moves.add(new_position+"ko");
                                break;
                            }
                            else if (!cell.hasPiece() && beyondKilledPiecePlaces) {
                                counterCellsAfterKilled++;
                                moves.add(new_position+"pw01");
                                break;
                            }
                            else if (cell.getPiece().getColor() != this.color && !beyondKilledPiecePlaces){
                                row_move =i+vdir;
                                col_move =j+hdir;
                                if (!IsInsideBoard(row_move, col_move)){
                                    loopNotFinished=false;
                                    break;
                                }
                                new_position = convertPosToString(row_move, col_move);
                                cell = board.getCell(row_move,col_move);
                                if (!cell.hasPiece()) {
                                    moves=new ArrayList<String>();
                                    moves.add(new_position+"pw01");
                                    beyondKilledPiecePlaces=true;
                                    counterCellsAfterKilled++;
                                    i=i+vdir;
                                    break;
                                }else{
                                    loopNotFinished=false;
                                    break;
                                }
                            }
                            else if(cell.getPiece().getColor() != this.color && beyondKilledPiecePlaces){
                                row_move=i+vdir;
                                col_move=j+hdir;
                                if (!IsInsideBoard(row_move,col_move)){
                                    loopNotFinished=false;
                                    break;
                                }
                                new_position = convertPosToString(row_move, col_move);
                                cell = board.getCell(row_move,col_move);
                                if (!cell.hasPiece()) {
                                    while(counterCellsAfterKilled>1){
                                        moves.remove(moves.size()-1);
                                        counterCellsAfterKilled--;
                                    }
                                    String lastPosition=moves.get(moves.size()-1);
                                    moves.set(moves.size()-1,lastPosition.substring(0,lastPosition.length()-4)+new_position+"pw01");
                                    beyondKilledPiecePlaces=true;
                                    counterCellsAfterKilled=0;
                                    i=i+vdir;
                                    break;
                                }else{
                                    loopNotFinished=false;
                                    break;
                                }
                            }
                            else if (cell.getPiece().getColor() == this.color) {
                                loopNotFinished=false;
                                break;
                            }
                        }else {
                            if (!cell.hasPiece() && !beyondKilledPiecePlaces){
                                break;
                            }
                            else if (!cell.hasPiece() && beyondKilledPiecePlaces) {
                                moves.add(new_position+"pw01");
                                counterCellsAfterKilled++;
                                break;
                            }
                            else if (cell.getPiece().getColor() != this.color && !beyondKilledPiecePlaces){
                                row_move =i+vdir;
                                col_move =j+hdir;
                                if (!IsInsideBoard(row_move, col_move)){
                                    loopNotFinished=false;
                                    break;
                                }
                                new_position = convertPosToString(row_move, col_move);
                                cell = board.getCell(row_move,col_move);
                                if (!cell.hasPiece()) {
                                    moves.add(new_position+"pw01");
                                    beyondKilledPiecePlaces=true;
                                    i=i+vdir;
                                    break;
                                }else{
                                    loopNotFinished=false;
                                    break;
                                }
                            }
                            else if (cell.getPiece().getColor() != this.color && beyondKilledPiecePlaces){
                                row_move =i+vdir;
                                col_move =j+hdir;
                                if (!IsInsideBoard(row_move, col_move)){
                                    loopNotFinished=false;
                                    break;
                                }
                                new_position = convertPosToString(row_move, col_move);
                                cell = board.getCell(row_move,col_move);
                                if (!cell.hasPiece()) {
                                    while(counterCellsAfterKilled>1){
                                        moves.remove(moves.size()-1);
                                        counterCellsAfterKilled--;
                                    }
                                    String lastPosition=moves.get(moves.size()-1);
                                    moves.set(moves.size()-1,lastPosition.substring(0,lastPosition.length()-4)+new_position+"pw01");
                                    beyondKilledPiecePlaces=true;
                                    counterCellsAfterKilled=0;
                                    i=i+vdir;
                                    break;
                                }else{
                                    loopNotFinished=false;
                                    break;
                                }

                            }
                            else if (cell.getPiece().getColor() == this.color) {
                                loopNotFinished=false;
                                break;
                            }
                        }
                    }
                }
            }

        }

        if (vdir==1 && hdir==-1) {

            for (i=row_original+1;i<CheckersBoard.ROWS && loopNotFinished;i++) {
                for (j=col_original-1;j>=0;j--) {
                    if ((i+j)%2==0 && ((i+j)==(row_original+col_original))) {
                        cell=board.getCell(i,j);
                        new_position=Piece.convertPosToString(i,j);
                        if (!beforeKilled) {
                            if (!cell.hasPiece() && !beyondKilledPiecePlaces) {
                                moves.add(new_position+"ko");
                                break;
                            }
                            else if (!cell.hasPiece() && beyondKilledPiecePlaces) {
                                counterCellsAfterKilled++;
                                moves.add(new_position+"pw10");
                                break;
                            }
                            else if (cell.getPiece().getColor() != this.color && !beyondKilledPiecePlaces){
                                row_move =i+vdir;
                                col_move =j+hdir;
                                if (!IsInsideBoard(row_move, col_move)){
                                    loopNotFinished=false;
                                    break;
                                }
                                new_position = convertPosToString(row_move, col_move);
                                cell = board.getCell(row_move,col_move);
                                if (!cell.hasPiece()) {
                                    moves=new ArrayList<String>();
                                    moves.add(new_position+"pw10");
                                    beyondKilledPiecePlaces=true;
                                    counterCellsAfterKilled++;
                                    i=i+vdir;
                                    break;
                                }else{
                                    loopNotFinished=false;
                                    break;
                                }
                            }
                            else if(cell.getPiece().getColor() != this.color && beyondKilledPiecePlaces){
                                row_move=i+vdir;
                                col_move=j+hdir;
                                if (!IsInsideBoard(row_move,col_move)){
                                    loopNotFinished=false;
                                    break;
                                }
                                new_position = convertPosToString(row_move, col_move);
                                cell = board.getCell(row_move,col_move);
                                if (!cell.hasPiece()) {
                                    while(counterCellsAfterKilled>1){
                                        moves.remove(moves.size()-1);
                                        counterCellsAfterKilled--;
                                    }
                                    String lastPosition=moves.get(moves.size()-1);
                                    moves.set(moves.size()-1,lastPosition.substring(0,lastPosition.length()-4)+new_position+"pw10");
                                    beyondKilledPiecePlaces=true;
                                    counterCellsAfterKilled=0;
                                    i=i+vdir;
                                    break;
                                }else{
                                    loopNotFinished=false;
                                    break;
                                }
                            }
                            else if (cell.getPiece().getColor() == this.color) {
                                loopNotFinished=false;
                                break;
                            }
                        }else {
                            if (!cell.hasPiece() && !beyondKilledPiecePlaces){
                                break;
                            }
                            else if (!cell.hasPiece() && beyondKilledPiecePlaces) {
                                moves.add(new_position+"pw10");
                                counterCellsAfterKilled++;
                                break;
                            }
                            else if (cell.getPiece().getColor() != this.color && !beyondKilledPiecePlaces){
                                row_move =i+vdir;
                                col_move =j+hdir;
                                if (!IsInsideBoard(row_move, col_move)){
                                    loopNotFinished=false;
                                    break;
                                }
                                new_position = convertPosToString(row_move, col_move);
                                cell = board.getCell(row_move,col_move);
                                if (!cell.hasPiece()) {
                                    moves.add(new_position+"pw10");
                                    beyondKilledPiecePlaces=true;
                                    i=i+vdir;
                                    break;
                                }else{
                                    loopNotFinished=false;
                                    break;
                                }
                            }
                            else if (cell.getPiece().getColor() != this.color && beyondKilledPiecePlaces){
                                row_move =i+vdir;
                                col_move =j+hdir;
                                if (!IsInsideBoard(row_move, col_move)){
                                    loopNotFinished=false;
                                    break;
                                }
                                new_position = convertPosToString(row_move, col_move);
                                cell = board.getCell(row_move,col_move);
                                if (!cell.hasPiece()) {
                                    while(counterCellsAfterKilled>1){
                                        moves.remove(moves.size()-1);
                                        counterCellsAfterKilled--;
                                    }
                                    String lastPosition=moves.get(moves.size()-1);
                                    moves.set(moves.size()-1,lastPosition.substring(0,lastPosition.length()-4)+new_position+"pw10");
                                    beyondKilledPiecePlaces=true;
                                    counterCellsAfterKilled=0;
                                    i=i+vdir;
                                    break;
                                }else{
                                    loopNotFinished=false;
                                    break;
                                }
                            }
                            else if (cell.getPiece().getColor() == this.color) {
                                loopNotFinished=false;
                                break;
                            }
                        }
                    }
                }
            }
        }

        if (vdir==-1 && hdir==-1) {

            for (i=row_original-1;i>=0 && loopNotFinished;i--) {
                for (j=col_original-1;j>=0;j--) {
                    if ((i+j)%2==0 && ((i-j)==(row_original-col_original))) {
                        //Log.i("distance","00 "+i+" "+j);
                        cell=board.getCell(i,j);
                        new_position=Piece.convertPosToString(i,j);
                        if (!beforeKilled) {
                            if (!cell.hasPiece() && !beyondKilledPiecePlaces) {
                                moves.add(new_position+"ko");
                                break;
                            }
                            else if (!cell.hasPiece() && beyondKilledPiecePlaces) {
                                counterCellsAfterKilled++;
                                moves.add(new_position+"pw00");
                                break;
                            }
                            else if (cell.getPiece().getColor() != this.color && !beyondKilledPiecePlaces){
                                row_move =i+vdir;
                                col_move =j+hdir;
                                if (!IsInsideBoard(row_move, col_move)){
                                    loopNotFinished=false;
                                    break;
                                }
                                new_position = convertPosToString(row_move, col_move);
                                cell = board.getCell(row_move,col_move);
                                if (!cell.hasPiece()) {
                                    moves=new ArrayList<String>();
                                    moves.add(new_position+"pw00");
                                    beyondKilledPiecePlaces=true;
                                    counterCellsAfterKilled++;
                                    i=i+vdir;
                                    break;
                                }else{
                                    loopNotFinished=false;
                                    break;
                                }
                            }
                            else if(cell.getPiece().getColor() != this.color && beyondKilledPiecePlaces){
                                row_move=i+vdir;
                                col_move=j+hdir;
                                if (!IsInsideBoard(row_move,col_move)){
                                    loopNotFinished=false;
                                    break;
                                }
                                new_position = convertPosToString(row_move, col_move);
                                cell = board.getCell(row_move,col_move);
                                if (!cell.hasPiece()) {
                                    while(counterCellsAfterKilled>1){
                                        moves.remove(moves.size()-1);
                                        counterCellsAfterKilled--;
                                    }
                                    String lastPosition=moves.get(moves.size()-1);
                                    moves.set(moves.size()-1,lastPosition.substring(0,lastPosition.length()-4)+new_position+"pw00");
                                    beyondKilledPiecePlaces=true;
                                    counterCellsAfterKilled=0;
                                    i=i+vdir;
                                    break;
                                }else{
                                    loopNotFinished=false;
                                    break;
                                }
                            }
                            else if (cell.getPiece().getColor() == this.color) {
                                loopNotFinished=false;
                                break;
                            }
                        }else {
                            if (!cell.hasPiece() && !beyondKilledPiecePlaces){
                                break;
                            }
                            else if (!cell.hasPiece() && beyondKilledPiecePlaces) {
                                moves.add(new_position+"pw00");
                                counterCellsAfterKilled++;
                            }
                            else if (cell.getPiece().getColor() != this.color && !beyondKilledPiecePlaces){
                                row_move =i+vdir;
                                col_move =j+hdir;
                                if (!IsInsideBoard(row_move, col_move)){
                                    loopNotFinished=false;
                                    break;
                                }
                                new_position = convertPosToString(row_move, col_move);
                                cell = board.getCell(row_move,col_move);
                                if (!cell.hasPiece()) {
                                    moves.add(new_position+"pw00");
                                    beyondKilledPiecePlaces=true;
                                    i=i+vdir;
                                    break;
                                }else{
                                    loopNotFinished=false;
                                    break;
                                }
                            }
                            else if (cell.getPiece().getColor() != this.color && beyondKilledPiecePlaces){
                                row_move =i+vdir;
                                col_move =j+hdir;
                                if (!IsInsideBoard(row_move, col_move)){
                                    loopNotFinished=false;
                                    break;
                                }
                                new_position = convertPosToString(row_move, col_move);
                                cell = board.getCell(row_move,col_move);
                                if (!cell.hasPiece()) {
                                    while(counterCellsAfterKilled>1){
                                        moves.remove(moves.size()-1);
                                        counterCellsAfterKilled--;
                                    }
                                    String lastPosition=moves.get(moves.size()-1);
                                    moves.set(moves.size()-1,lastPosition.substring(0,lastPosition.length()-4)+new_position+"pw00");
                                    beyondKilledPiecePlaces=true;
                                    counterCellsAfterKilled=0;
                                    i=i+vdir;
                                    break;
                                }else{
                                    loopNotFinished=false;
                                    break;
                                }
                            }
                            else if (cell.getPiece().getColor() == this.color) {
                                loopNotFinished=false;
                                break;
                            }
                        }
                    }
                }
            }
        }
        return moves;
    }
}
