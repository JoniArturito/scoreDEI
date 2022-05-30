/**
 * This class is used to represent the status of a game
 */
package com.scoreDEI.Entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;
import java.sql.Time;

@Entity
@JsonIgnoreProperties({"game_status"})
@XmlRootElement
public class GameStatus extends GameEvent{
    @Column(name = "type", nullable = false)
    private int type;

    public GameStatus() {
    }

    public GameStatus(Time eventDate, Game game, int type) {
        super(eventDate, game);
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int getTypeEvent() {
        return 2;
    }

    @Override
    public String toString() {
        return "GameStatus{" +
                "type=" + type +
                '}';
    }
}
