/**
 * It's a form that contains a game and a boolean that indicates if the card is yellow or not
 */
package com.scoreDEI.Forms;

import com.scoreDEI.Entities.Game;

public class CardForm {
    private Game game;
    private boolean isYellow;

    private String playerName;
    private String beginDate;

    private String gameIdString;
    private String isYellowString;

    public CardForm() {
    }

    public CardForm(Game game, boolean isYellow) {
        this.game = game;
        this.isYellow = isYellow;
        gameIdString = Integer.toString(game.getGameId());
        isYellowString = Boolean.toString(isYellow);
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

    public String getGameIdString() {
        return gameIdString;
    }

    public void setGameIdString(String gameIdString) {
        this.gameIdString = gameIdString;
    }

    public String getIsYellowString() {
        return isYellowString;
    }

    public void setIsYellowString(String isYellowString) {
        this.isYellowString = isYellowString;
    }
}
