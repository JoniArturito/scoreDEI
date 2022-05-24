package com.scoreDEI.Entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.sql.Timestamp;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "events")
@JsonIgnoreProperties({"game_events"})
@XmlRootElement
public class GameEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "gameEventId", nullable = false)
    private int gameEventId;
    @Column(name = "eventdate", nullable = false)
    private Timestamp eventDate;
    @ManyToOne
    @JoinColumn(name = "gameId", nullable = false)
    private Game game;

    public GameEvent() {
    }

    public GameEvent(Timestamp eventDate) {
        this.eventDate = eventDate;
    }

    public int getGameEventId() {
        return gameEventId;
    }

    public void setGameEventId(int gameEventId) {
        this.gameEventId = gameEventId;
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
