package com.scoreDEI.Database;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlRootElement;
import java.sql.Timestamp;

@Entity
@JsonIgnoreProperties({"goals"})
@XmlRootElement
public class Goal extends GameEvent{
    @ManyToOne
    private Player player;

    public Goal() {
    }

    public Goal(Timestamp eventDate, Player player) {
        super(eventDate);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public String toString() {
        return "Goal{" +
                "player=" + player +
                '}';
    }
}
