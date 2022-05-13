package com.scoreDEI.Database;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;
import java.sql.Timestamp;

@Entity
@JsonIgnoreProperties({"game_status"})
@XmlRootElement
public class GameStatus extends GameEvent{
    @Column(name = "type", nullable = false)
    private int type;

    public GameStatus() {
    }

    public GameStatus(Timestamp eventDate, int type) {
        super(eventDate);
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "GameStatus{" +
                "type=" + type +
                '}';
    }
}
