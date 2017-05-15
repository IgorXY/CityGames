package com.tigerware.citygames.Entity;

/**
 * Created by User on 15.05.2017.
 */

public class HintStatus {
    private int userID;
    private int gameID;
    private int hintLeft;

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public int getHintLeft() {
        return hintLeft;
    }

    public void setHintLeft(int hintLeft) {
        this.hintLeft = hintLeft;
    }
}
