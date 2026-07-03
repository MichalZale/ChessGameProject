package com.example.ChessProject.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Timer {
    private int whiteTime;
    private int blackTime;
    private int whiteTimeIncrease;
    private int blackTimeIncrease;


    public void setTime(Color color, int time) {
        if (color == Color.WHITE) {
            this.whiteTime = time;
        } else {
            this.blackTime = time;
        }
    }

    public int getTime(Color color) {
        return (color == Color.WHITE) ? this.whiteTime : this.blackTime;
    }

  
    public boolean decrementTime(Color color) {
        if (color == Color.WHITE) {
            if (this.whiteTime > 0) {
                this.whiteTime--;
                return true;
            }
            return false;
        } else {
            if (this.blackTime > 0) {
                this.blackTime--;
                return true;
            }
            return false;
        }
    }

    
    public void applyIncrement(Color color) {
        if (color == Color.WHITE) {
            this.whiteTime += this.whiteTimeIncrease;
        } else { 
            this.blackTime += this.blackTimeIncrease;
        }
    }
}

