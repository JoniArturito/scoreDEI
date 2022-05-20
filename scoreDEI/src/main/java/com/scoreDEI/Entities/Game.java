package com.scoreDEI.Entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.sql.Timestamp;
import java.util.ArrayList;

@Entity
@JsonIgnoreProperties({"games"})
@XmlRootElement
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private int id;
    @Column(name = "begindate", nullable = false)
    private Timestamp beginDate;
    @Column(name = "location", nullable = false)
    private String location;

    @ManyToMany(mappedBy = "id")
    private ArrayList<Team> teams;
    @OneToMany
    private ArrayList<GameEvent> events;

    public Game() {
    }

    public Game(Timestamp beginDate, String location) {
        this.beginDate = beginDate;
        this.location = location;
        teams = new ArrayList<>();
        events = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public ArrayList<Team> getTeams() {
        return teams;
    }

    public void setTeams(ArrayList<Team> teams) {
        this.teams = teams;
    }

    public ArrayList<GameEvent> getEvents() {
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
