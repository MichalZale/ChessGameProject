package com.example.ChessProject.model.validators;

import com.example.ChessProject.model.*;

public class QueenValidator extends MoveValidator {
    public QueenValidator(GameState s){ super(s); }

    @Override
    public boolean basicMove(Move move, GameState st){
        Position from=move.getFrom(), to=move.getTo();
        Board b=st.getBoard();

        if(!b.isInside(from)||!b.isInside(to)) return false;
        if(b.getPiece(from)==null)             return false;
        if(st.getSideToMove()!=b.getPiece(from).getColor()) return false;
        if(b.getPiece(to)!=null && b.getPiece(to).getColor()==st.getSideToMove()) return false;

        int dr=Math.abs(to.row-from.row);
        int dc=Math.abs(to.column-from.column);
        boolean diag = dr==dc;
        boolean straight = (dr==0||dc==0);
        if(!(diag||straight)) return false;
        return isPathClear(move,b);
    }

    @Override
    public boolean canAttack(Position from, Position target, Board b){
        int dr=Math.abs(target.row-from.row);
        int dc=Math.abs(target.column-from.column);
        boolean diag = dr==dc;
        boolean straight = (dr==0||dc==0);
        if(!(diag||straight)) return false;
        return isPathClear(new Move(from,target),b);
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

