package com.example.checkerstest;

import android.util.Log;

import java.util.ArrayList;

public class Pawn extends Piece {

    private int drawableId;
    public Pawn(boolean color) {
        super(color);
        this.drawableId=(this.color)?R.drawable.white_piece:R.drawable.black_piece;
    }

    public int getDrawableId(){
        return this.drawableId;
    }

    @Override
    public ArrayList<String> GetValidMoves(CheckersBoard board, int row, int col) {

        ArrayList<String> moves = new ArrayList<String>();
        ArrayList<String> intermediate_moves = new ArrayList<String>();
        String validPos1;
        String validPos2;
        String last_2_characters;
        String moveWoSuffix;
        int VerticalDir=(this.color == Piece.BLACK)?-1:1;
        String addFirstSel="";
        String addSecSel="";

        String initial_position = Piece.convertPosToString(row, col);
        boolean moves_available = true;
        int count = 0;
        while (moves_available) {

            if (moves.size() != 0) {
                count = 0;

                for (String move : moves) {

                    last_2_characters = move.substring(move.length() - 2,move.length());


                    if (!last_2_characters.equals("ko")) {

                        moveWoSuffix = move.substring(0, move.length()-2);
                        validPos1 = GetValidIndividualMove(board,move, VerticalDir, 1);

                        if (!(validPos1.substring(move.length() - 2, move.length())).equals("ko")) {
                            addFirstSel=moveWoSuffix + validPos1;
                            intermediate_moves.add(addFirstSel);
                            count++;
                        }else{
                            addFirstSel="";
                        }

                        validPos2 = GetValidIndividualMove(board,move, VerticalDir, -1);

                        if (!(validPos2.substring(move.length() - 2, move.length())).equals("ko")) {
                            addSecSel=moveWoSuffix + validPos2;
                            intermediate_moves.add(addSecSel);
                            count++;
                        }else{
                            addSecSel="";
                        }
                    }

                    if (!move.equals("ko")){
                        if (addFirstSel.length()==0 && addSecSel.length()==0){
                            intermediate_moves.add(move);
                        }else{
                            addFirstSel="";
                            addSecSel="";
                        }
                    }
                }

                moves = new ArrayList<String>();
                for (String mov : intermediate_moves) {
                    moves.add(mov);
                }
                intermediate_moves = new ArrayList<String>();

            }else{
                validPos1 = GetValidIndividualMove(board,initial_position,VerticalDir, 1);

                if (!validPos1.equals("ko")) {
                    moves.add(validPos1);
                    count++;
                }

                validPos2 = GetValidIndividualMove(board, initial_position, VerticalDir, -1);

                if (!validPos2.equals("ko")) {
                    moves.add(validPos2);
                    count++;
                }
            }
            if (count == 0){
                moves_available = false;
            }

        }
        /*if (moves.size() > 1) {
            if (moves.get(0).length() > moves.get(1).length()) {
                moves.remove(1);
            } else if (moves.get(1).length() > moves.get(0).length()) {
                moves.remove(0);
            } else if (moves.get(0).substring(0, moves.get(0).length() - 2).equals("pw") && moves.get(1).substring(0, moves.get(1).length() - 2).equals("qu")) {
                moves.remove(0);
            } else if (moves.get(1).substring(0, moves.get(1).length() - 2).equals("pw") && moves.get(0).substring(0, moves.get(0).length() - 2).equals("qu")) {
                moves.remove(1);
            }
        }*/
        for (int i = 0; i < moves.size(); i++) {

            moves.set(i, moves.get(i).substring(0, moves.get(i).length() - 2));
        }
        return moves;
    }

    public String GetValidIndividualMove(CheckersBoard board, String position, int vDir, int hDir) {

        boolean before_killed = false;
        String positionSaved=position.substring(0,position.length()-2);
        String last_2_characters = position.substring(position.length() - 2, position.length());
        if (last_2_characters.equals("pw")) {
            before_killed = true;
            position = position.substring(position.length() - 4, position.length() - 2);
        }
        int col_original = board.getCol(position);
        int row_original = board.getRow(position);

        String res = "ko";
        int col_move, row_move;

        col_move = col_original + hDir;
        row_move = row_original + vDir;

        if (!IsInsideBoard(row_move, col_move)) {
            if (!before_killed)
                return "ko";
            else{
                res = positionSaved + "ko";
                return res;
            }
        }

        Cell cell = board.getCell(row_move, col_move);

        String newPos = convertPosToString(row_move, col_move);

        if (!cell.hasPiece()) {

            if (!before_killed)
                res = newPos + "ko";
            else
                res = positionSaved + "ko";
        } else if (cell.getPiece().getColor() != this.color) {

            row_move += vDir;
            col_move += hDir;
            if (!IsInsideBoard(row_move, col_move)) {

                if (!before_killed)
                    return "ko";
                else{
                    res = positionSaved + "ko";
                    return res;
                }
            } else {

                newPos = convertPosToString(row_move, col_move);
                cell = board.getCell(row_move, col_move);
                if (!cell.hasPiece()) {

                    res = newPos + "pw";
                }else{
                    if (!before_killed)
                        res="ko";
                    else
                        res=positionSaved+"ko";
                }
            }
        }else{
            if (before_killed)
                res=positionSaved+"ko";
        }
        return res;
    }
}
