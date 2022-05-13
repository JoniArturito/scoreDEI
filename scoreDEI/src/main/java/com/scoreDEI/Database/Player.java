package com.scoreDEI.Database;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.sql.Date;
import java.util.ArrayList;

@Entity
@JsonIgnoreProperties({"players"})
@XmlRootElement
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private int id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "position", nullable = false)
    private String position;
    @Column(name = "birthday", nullable = false)
    private Date birthday;
    @ManyToOne
    private Team team;
    @OneToMany(mappedBy = "id")
    private ArrayList<Card> cards;
    @OneToMany(mappedBy = "id")
    private ArrayList<Goal> goals;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public ArrayList<Card> getCards() {
        return cards;
    }

    public void setCards(ArrayList<Card> cards) {
        this.cards = cards;
    }

    public ArrayList<Goal> getGoals() {
        return goals;
    }

    public void setGoals(ArrayList<Goal> goals) {
        this.goals = goals;
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
