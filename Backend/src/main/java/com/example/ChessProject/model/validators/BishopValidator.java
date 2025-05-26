package com.example.ChessProject.model.validators;

import com.example.ChessProject.model.*;

public class BishopValidator extends MoveValidator{
    public BishopValidator(GameState s){
        super(s);
    }
    @Override
    public boolean basicMove(Move move, GameState state){
        Position from = move.getFrom();
        Position to = move.getTo();
        Board board = state.getBoard();
        int rd = Math.abs(to.row-from.row);
        int cd = Math.abs(to.column-from.column);
        if(!board.isInside(from) || !board.isInside(to)){return false;}
        if(board.getPiece(from)==null){return false;}
        if(state.getSideToMove()!=board.getPiece(from).getColor()){return false;}
        if(board.getPiece(to)!=null && state.getSideToMove()==board.getPiece(to).getColor()){return false;}
        if(rd!=cd) return false;
        if(!isPathClear(move, board)) return false;
        return true;
    }

    @Override
    public boolean canAttack(Position from, Position target, Board b) {
        int dr = Math.abs(target.row - from.row);
        int dc = Math.abs(target.column - from.column);
        if (dr != dc) return false;
        return isPathClear(new Move(from, target), b);
    }


    private boolean isPathClear(Move mv, Board board){
        int stepR = (mv.getTo().row    > mv.getFrom().row)    ? 1 : -1;
        int stepC = (mv.getTo().column > mv.getFrom().column) ? 1 : -1;
    
        int r = mv.getFrom().row    + stepR;
        int c = mv.getFrom().column + stepC;
        while(r != mv.getTo().row || c != mv.getTo().column){
            if(!board.isEmpty(new Position(r,c))) return false;
            r += stepR;
            c += stepC;
        }
        return true;
    }
    
}
