package com.scoreDEI.Forms;

import com.scoreDEI.Entities.Game;

public class CardForm {
    private Game game;
    private boolean isYellow;

    private String playerName;
    private String beginDate;

    public CardForm(Game game, boolean isYellow) {
        this.game = game;
        this.isYellow = isYellow;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public boolean isYellow() {
        return isYellow;
    }

    public void setYellow(boolean yellow) {
        isYellow = yellow;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }
}
