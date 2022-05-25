package com.scoreDEI.Entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlRootElement;
import java.sql.Timestamp;

@Entity
@JsonIgnoreProperties({"cards"})
@XmlRootElement
public class Card extends GameEvent{
    @Column(name = "yellow", nullable = false)
    private boolean isYellow = true;
    @ManyToOne
    @JoinColumn(name="playerId", nullable=false)
    private Player player;

    public Card() {
    }

    public Card(Timestamp eventDate, boolean isYellow, Player player) {
        super(eventDate);
        this.isYellow = isYellow;
        this.player = player;
    }

    public boolean isYellow() {
        return isYellow;
    }

    public void setYellow(boolean yellow) {
        isYellow = yellow;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public int getTypeEvent() {
        return 1;
    }

    @Override
    public String toString() {
        return "Card{" +
                "isYellow=" + isYellow +
                ", player=" + player +
                '}';
    }
}
