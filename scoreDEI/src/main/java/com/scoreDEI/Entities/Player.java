package com.scoreDEI.Entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "players")
@JsonIgnoreProperties({"players"})
@XmlRootElement
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "playerId", nullable = false)
    private int playerId;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "position", nullable = false)
    private String position;
    @Column(name = "birthday", nullable = false)
    private Date birthday;
    @ManyToOne(fetch = FetchType.LAZY)
    private Team team;
    @OneToMany(mappedBy = "gameEventId")
    private List<GameEvent> events;

    public Player() {
    }

    public Player(String name, String position, Date birthday) {
        this.name = name;
        this.position = position;
        this.birthday = birthday;
        events = new ArrayList<>();
    }

    public Player(String name, String position, Date birthday, Team team) {
        this.name = name;
        this.position = position;
        this.birthday = birthday;
        this.team = team;
        events = new ArrayList<>();
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public List<GameEvent> getEvents() {
        return events;
    }

    public void setEvents(List<GameEvent> events) {
        this.events = events;
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", position='" + position + '\'' +
                ", birthday=" + birthday +
                ", team=" + team +
                '}';
    }
}
