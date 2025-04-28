package com.example.ChessProject.model.validators;
import com.example.ChessProject.model.*;
import com.example.ChessProject.model.MoveValidator;

public class RookValidator extends MoveValidator {
    public RookValidator(GameState s){ super(s); }

    @Override
    public boolean basicMove(Move move, GameState state){
        Position from=move.getFrom(), to=move.getTo();
        Board b=state.getBoard();

        if(!b.isInside(from)||!b.isInside(to))return false;
        if(b.getPiece(from)==null)return false;
        if(state.getSideToMove()!=b.getPiece(from).getColor())return false;
        if(b.getPiece(to)!=null && b.getPiece(to).getColor()==state.getSideToMove())return false;

        boolean sameRow = from.row==to.row;
        boolean sameCol = from.column==to.column;
        if(!(sameRow||sameCol)) return false;
        return isPathClear(move,b);
    }

    @Override
    public boolean canAttack(Position from, Position target, Board b){
        if(from.row!=target.row && from.column!=target.column) return false;
        return isPathClear(new Move(from,target), b);
    }

    private boolean isPathClear(Move mv, Board b){
        int stepR = Integer.compare(mv.getTo().row   , mv.getFrom().row);
        int stepC = Integer.compare(mv.getTo().column, mv.getFrom().column);
        int r = mv.getFrom().row   + stepR;
        int c = mv.getFrom().column+ stepC;
        while(r!=mv.getTo().row || c!=mv.getTo().column){
            if(!b.isEmpty(new Position(r,c))) return false;
            r+=stepR; c+=stepC;
        }
        return true;
    }
}
