package com.scoreDEI.Database;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.sql.Timestamp;

@Entity
@JsonIgnoreProperties({"game_events"})
@XmlRootElement
public class GameEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private int id;
    @Column(name = "eventdate", nullable = false)
    private Timestamp eventDate;
    @ManyToOne
    private Game game;

    public GameEvent() {
    }

    public GameEvent(Timestamp eventDate) {
        this.eventDate = eventDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getEventDate() {
        return eventDate;
    }

    public void setEventDate(Timestamp eventDate) {
        this.eventDate = eventDate;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    @Override
    public String toString() {
        return "GameEvent{" +
                "eventDate=" + eventDate +
                ", game=" + game +
                '}';
    }
}
