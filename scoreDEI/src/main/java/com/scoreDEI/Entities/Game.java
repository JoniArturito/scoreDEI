package com.scoreDEI.Entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.scoreDEI.Others.Sorts.SortEventByDate;

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
        events.sort(new SortEventByDate());
        return events;
    }

    public void setEvents(ArrayList<GameEvent> events) {
        this.events = events;
    }

    public int[] getScore() {
        int[] score = new int[2];
        for (GameEvent event: events) {
            if (event.getTypeEvent() == 3) {
                Goal goalEvent = (Goal) event;
                String teamName = goalEvent.getPlayer().getTeam().getName();
                for (int i = 0; i < teams.size(); i++) {
                    if (teamName.equals(teams.get(i).getName())) {
                        score[i]++;
                        break;
                    }
                }
            }
        }
        return score;
    }

    public int isWinner(Team team) {
        int[] score = getScore();
        int index = -1;
        for (int i = 0; i < teams.size(); i++)
        {
            if (team.getName().equals(teams.get(i).getName()))
            {
                index = i;
                break;
            }
        }
        if (index == -1) return -2;
        if (score[index] > score[1-index]) return 1;
        else if (score[index] < score[1-index]) return -1;
        else return 0;
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
