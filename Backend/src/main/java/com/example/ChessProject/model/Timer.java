package com.example.ChessProject.model;


public class Timer {
    private int whiteTime;
    private int blackTime;
    private int whiteTimeIncrease;
    private int blackTimeIncrease;

    public Timer(int whiteTime, int blackTime, int whiteTimeIncrease, int blackTimeIncrease){
        this.whiteTime=whiteTime;
        this.blackTime=blackTime;
        this.whiteTimeIncrease=whiteTimeIncrease;
        this.blackTimeIncrease=blackTimeIncrease;
    }

    public void setTime(Color color, int time){
        if(color==Color.WHITE)
            whiteTime=time;
        else
            blackTime=time;
    }

    public int getTime(Color color){
        return (color==Color.WHITE) ? whiteTime : blackTime;
    }

    public int getWhiteTimeIncrease(){
        return this.whiteTimeIncrease;
    }

    public int getBlackTimeIncrease(){
        return this.blackTimeIncrease;
    }

}
