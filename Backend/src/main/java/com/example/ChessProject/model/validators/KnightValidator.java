package com.example.ChessProject.model.validators;
import com.example.ChessProject.model.*;
import com.example.ChessProject.model.MoveValidator;

public class KnightValidator extends MoveValidator {
    public KnightValidator(GameState s){ super(s); }

    @Override
    public boolean basicMove(Move move, GameState state){
        Position from = move.getFrom();
        Position to   = move.getTo();
        Board b = state.getBoard();

        if(!b.isInside(from) || !b.isInside(to)) return false;
        if(b.getPiece(from)==null)               return false;
        if(state.getSideToMove()!=b.getPiece(from).getColor()) return false;
        if(b.getPiece(to)!=null && b.getPiece(to).getColor()==state.getSideToMove()) return false;

        int dr = Math.abs(to.row    - from.row);
        int dc = Math.abs(to.column - from.column);
        return (dr==2 && dc==1) || (dr==1 && dc==2);
    }

    @Override
    public boolean canAttack(Position from, Position target, Board b){
        int dr = Math.abs(target.row    - from.row);
        int dc = Math.abs(target.column - from.column);
        return (dr==2 && dc==1) || (dr==1 && dc==2);
    }
}

