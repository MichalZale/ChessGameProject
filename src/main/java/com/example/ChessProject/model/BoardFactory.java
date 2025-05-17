package com.example.ChessProject.model;
import com.example.ChessProject.model.*;
import com.example.ChessProject.model.pieces.*;

public class BoardFactory {
    public static Board createBoard(){
        Board board = new Board();
        board.setPiece(new Position(0, 0), new Rook(Color.WHITE, new Position(0, 0)));
        board.setPiece(new Position(0, 1), new Knight(Color.WHITE, new Position(0, 1)));
        board.setPiece(new Position(0, 2), new Bishop(Color.WHITE, new Position(0, 2)));
        board.setPiece(new Position(0, 3), new Queen(Color.WHITE, new Position(0, 3)));
        board.setPiece(new Position(0, 4), new King(Color.WHITE, new Position(0, 4)));
        board.setPiece(new Position(0, 5), new Bishop(Color.WHITE, new Position(0, 5)));
        board.setPiece(new Position(0, 6), new Knight(Color.WHITE, new Position(0, 6)));
        board.setPiece(new Position(0, 7), new Rook(Color.WHITE, new Position(0, 7)));

        board.setPiece(new Position(1, 0), new Pawn(Color.WHITE, new Position(1, 0)));
        board.setPiece(new Position(1, 1), new Pawn(Color.WHITE, new Position(1, 1)));
        board.setPiece(new Position(1, 2), new Pawn(Color.WHITE, new Position(1, 2)));
        board.setPiece(new Position(1, 3), new Pawn(Color.WHITE, new Position(1, 3)));
        board.setPiece(new Position(1, 4), new Pawn(Color.WHITE, new Position(1, 4)));
        board.setPiece(new Position(1, 5), new Pawn(Color.WHITE, new Position(1, 5)));
        board.setPiece(new Position(1, 6), new Pawn(Color.WHITE, new Position(1, 6)));
        board.setPiece(new Position(1, 7), new Pawn(Color.WHITE, new Position(1, 7)));

        board.setPiece(new Position(7, 0), new Rook(Color.BLACK, new Position(7, 0)));
        board.setPiece(new Position(7, 1), new Knight(Color.BLACK, new Position(7, 1)));
        board.setPiece(new Position(7, 2), new Bishop(Color.BLACK, new Position(7, 2)));
        board.setPiece(new Position(7, 3), new Queen(Color.BLACK, new Position(7, 3)));
        board.setPiece(new Position(7, 4), new King(Color.BLACK, new Position(7, 4)));
        board.setPiece(new Position(7, 5), new Bishop(Color.BLACK, new Position(7, 5)));
        board.setPiece(new Position(7, 6), new Knight(Color.BLACK, new Position(7, 6)));
        board.setPiece(new Position(7, 7), new Rook(Color.BLACK, new Position(7, 7)));

        board.setPiece(new Position(6, 0), new Pawn(Color.BLACK, new Position(6, 0)));
        board.setPiece(new Position(6, 1), new Pawn(Color.BLACK, new Position(6, 1)));
        board.setPiece(new Position(6, 2), new Pawn(Color.BLACK, new Position(6, 2)));
        board.setPiece(new Position(6, 3), new Pawn(Color.BLACK, new Position(6, 3)));
        board.setPiece(new Position(6, 4), new Pawn(Color.BLACK, new Position(6, 4)));
        board.setPiece(new Position(6, 5), new Pawn(Color.BLACK, new Position(6, 5)));
        board.setPiece(new Position(6, 6), new Pawn(Color.BLACK, new Position(6, 6)));
        board.setPiece(new Position(6, 7), new Pawn(Color.BLACK, new Position(6, 7)));


        return board;
    }
}
