/**
 * It's a form that contains a game and a player name
 */
package com.scoreDEI.Forms;

import com.scoreDEI.Entities.Game;

public class GoalForm {
    private Game game;
    private String gameIdString;

    private String beginDate;
    private String playerName;

    public GoalForm() {
    }

    public GoalForm(Game game) {
        this.game = game;
        gameIdString = Integer.toString(game.getGameId());
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getGameIdString() {
        return gameIdString;
    }

    public void setGameIdString(String gameIdString) {
        this.gameIdString = gameIdString;
    }
}
