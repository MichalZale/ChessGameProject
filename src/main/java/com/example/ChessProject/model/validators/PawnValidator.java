package com.example.ChessProject.model.validators;

import com.example.ChessProject.model.*;

public class PawnValidator extends MoveValidator {
    public PawnValidator(GameState s){ super(s); }

    @Override
    public boolean basicMove(Move move, GameState st){
        Position from=move.getFrom(), to=move.getTo();
        Board b=st.getBoard();

        if(!b.isInside(from)||!b.isInside(to)) return false;
        Piece p=b.getPiece(from);
        if(p==null) return false;
        if(p.getColor()!=st.getSideToMove()) return false;
        if(b.getPiece(to)!=null && b.getPiece(to).getColor()==p.getColor()) return false;

        int dir=(p.getColor()==Color.WHITE)?-1:1;
        int startRow=(p.getColor()==Color.WHITE)?6:1;
        int dr=to.row-from.row;
        int dc=Math.abs(to.column-from.column);

        if(dc==0){
            if(dr==dir && b.isEmpty(to)) return true;
            if(dr==2*dir && from.row==startRow && b.isEmpty(to) && b.isEmpty(new Position(from.row+dir,from.column))) return true;
        }
        if(dc==1 && dr==dir){
            if(b.getPiece(to)!=null && b.getPiece(to).getColor()!=p.getColor()) return true;
            if(st.getEnPassant()!=null && st.getEnPassant().equals(to)) return true;
        }
        return false;
    }

    @Override
    public boolean canAttack(Position from, Position target, Board b){
        Color col=b.getPiece(from).getColor();
        int dir=(col==Color.WHITE)?-1:1;
        int dr=target.row-from.row;
        int dc=Math.abs(target.column-from.column);
        return dr==dir && dc==1;
    }
}
