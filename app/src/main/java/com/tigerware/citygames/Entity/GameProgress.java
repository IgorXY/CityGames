package com.tigerware.citygames.Entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Tonko_I on 17/04/2017.
 */

public class GameProgress implements Serializable{
    private Game game;
    private int stage;
    private String destination;


    public String getDestination() {
        return destination;
    }

    public void setDestination(int destination) {
        if(destination < 10) {
            this.destination = "достигнута";
            return;
        }
        if(destination < 100) {
            this.destination = "очень близко";
            return;
        }
        if(destination < 1000) {
            this.destination = "близко";
            return;
        }
        if(destination < 5000) {
            this.destination = "далеко";
            return;
        }
        this.destination = "очень далеко";

    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public double calculateDistance(double startLat, double startLon, double endLat, double endLon){
        double dLat = (startLat - endLat) * 0.0175;
        double dLon = (startLon - endLon) * 0.0175;
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(startLat) * Math.cos(endLat) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = 6372795  * c;
        return d;
    }


}
