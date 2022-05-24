package com.scoreDEI.Entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "games")
@JsonIgnoreProperties({"games"})
@XmlRootElement
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "gameId", nullable = false)
    private int gameId;
    @Column(name = "begindate", nullable = false)
    private Timestamp beginDate;
    @Column(name = "location", nullable = false)
    private String location;

    @ManyToMany(mappedBy = "games")
    private List<Team> teams;
    @OneToMany
    private List<GameEvent> events;

    public Game() {
    }

    public Game(Timestamp beginDate, String location) {
        this.beginDate = beginDate;
        this.location = location;
        teams = new ArrayList<>();
        events = new ArrayList<>();
    }

    public int getId() {
        return gameId;
    }

    public void setId(int id) {
        this.gameId = id;
    }

    public Timestamp getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Timestamp beginDate) {
        this.beginDate = beginDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(ArrayList<Team> teams) {
        this.teams = teams;
    }

    public List<GameEvent> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<GameEvent> events) {
        this.events = events;
    }

    @Override
    public String toString() {
        return "Game{" +
                "beginDate=" + beginDate +
                ", location='" + location + '\'' +
                ", teams=" + teams +
                '}';
    }
}
