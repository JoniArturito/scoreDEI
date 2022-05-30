/**
 * It's a form that contains a game and a type
 */
package com.scoreDEI.Forms;

import com.scoreDEI.Entities.Game;

public class GameStatusForm {
    private Game game;
    private int type;
    private int gameId;

    private String eventDate;

    private String typeString;
    private String gameIdString;

    public GameStatusForm() {
    }

    public GameStatusForm(Game game, int type) {
        this.game = game;
        this.type = type;
        this.gameId = game.getGameId();
        typeString = Integer.toString(type);
        gameIdString = Integer.toString(gameId);
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public String getTypeString() {
        return typeString;
    }

    public void setTypeString(String typeString) {
        this.typeString = typeString;
    }

    public String getGameIdString() {
        return gameIdString;
    }

    public void setGameIdString(String gameIdString) {
        this.gameIdString = gameIdString;
    }
}
