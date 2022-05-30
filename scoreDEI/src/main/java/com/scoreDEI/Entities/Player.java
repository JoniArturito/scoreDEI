package com.scoreDEI.Entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.transaction.Transactional;
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
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "player_gen")
    @Column(name = "playerId", nullable = false)
    private int playerId;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "position", nullable = false)
    private String position;
    @Column(name = "birthday", nullable = false)
    private Date birthday;

    @Column(name = "urlPhoto")
    private String urlPhoto;

    @ManyToOne(fetch = FetchType.LAZY)
    private Team team;
    @OneToMany(mappedBy = "player")
    private List<Card> cards;
    @OneToMany(mappedBy = "player")
    private List<Goal> goals;

    public Player() {
    }

    public Player(String name, String position, Date birthday) {
        this.name = name;
        this.position = position;
        this.birthday = birthday;
        cards = new ArrayList<>();
        goals = new ArrayList<>();
    }

    public Player(String name, String position, Date birthday, Team team) {
        this.name = name;
        this.position = position;
        this.birthday = birthday;
        this.team = team;
        cards = new ArrayList<>();
        goals = new ArrayList<>();
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

    public String getUrlPhoto() {
        return urlPhoto;
    }

    public void setUrlPhoto(String urlPhoto) {
        this.urlPhoto = urlPhoto;
    }

    @Transactional
    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public List<Goal> getGoals() {
        return goals;
    }

    public void setGoals(List<Goal> goals) {
        this.goals = goals;
    }

    @Transactional
    public int getNumberGoals()
    {
        return goals.size();
    }

    @Transactional
    public int[] getNumberCards(){
        int yellow = 0;
        int red = 0;
        for (Card c: cards) {
            if (c.isYellow()){
                yellow++;
            }
            else{
                red++;
            }
        }
        return new int[]{yellow, red};
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
