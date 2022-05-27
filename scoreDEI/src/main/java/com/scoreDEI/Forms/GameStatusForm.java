package com.scoreDEI.Forms;

import com.scoreDEI.Entities.Game;

public class GameStatusForm {
    private Game game;
    private int type;

    private String eventDate;

    public GameStatusForm(Game game, int type) {
        this.game = game;
        this.type = type;
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
}
