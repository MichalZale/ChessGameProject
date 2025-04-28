package com.example.ChessProject.model;


public class Timer {
    private int whiteTime;
    private int blackTime;

    Timer(int whiteTime, int blackTime){
        this.whiteTime=whiteTime;
        this.blackTime=blackTime;
    }

    public int getTime(Color color){
        return (color==Color.WHITE) ? whiteTime : blackTime;
    }

}
