package com.scoreDEI.Entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.scoreDEI.Others.Sorts.SortEventByDate;

import javax.persistence.*;
import javax.transaction.Transactional;
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
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "game_gen")
    @Column(name = "gameId", nullable = false)
    private int gameId;
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    @Column(name = "begindate", nullable = false)
    private Timestamp beginDate;
    @Column(name = "location", nullable = false)
    private String location;

    //Divide into one Home Team and one Visitor Team
    /*@ManyToMany(mappedBy = "games")
    private List<Team> teams;*/

    @ManyToOne
    @JoinColumn(name = "home_team_id", nullable = false)
    private Team homeTeam;
    @ManyToOne
    @JoinColumn(name = "visitor_team_id", nullable = false, foreignKey = @ForeignKey(name = "visitor_team_id"))
    private Team visitorTeam;

    @OneToMany
    private List<GameEvent> events;

    public Game() {
    }

    public Game(Timestamp beginDate, String location, Team homeTeam, Team visitorTeam) {
        this.beginDate = beginDate;
        this.location = location;
        this.homeTeam = homeTeam;
        this.visitorTeam = visitorTeam;
        events = new ArrayList<>();
        name = homeTeam.getName() + " - " +
                visitorTeam.getName() + " (" +
                beginDate.toString() + ")";
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Transactional
    public Team getHomeTeam() {
        return homeTeam;
    }

    @Transactional
    public void setHomeTeam(Team homeTeam) {
        this.homeTeam = homeTeam;
    }

    @Transactional
    public Team getVisitorTeam() {
        return visitorTeam;
    }

    public void setVisitorTeam(Team visitorTeam) {
        this.visitorTeam = visitorTeam;
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

    public List<GameEvent> getEvents() {
        events.sort(new SortEventByDate());
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
                ", homeTeam=" + homeTeam +
                ", visitorTeam" + visitorTeam +
                '}';
    }
}
