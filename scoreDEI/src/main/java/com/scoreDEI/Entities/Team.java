package com.scoreDEI.Entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.scoreDEI.Others.Sorts.SortPlayersByScore;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.transaction.Transactional;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "teams")
@JsonIgnoreProperties({"teams"})
@XmlRootElement
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "team_gen")
    @Column(name = "teamId", nullable = false)
    private int teamId;
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "logo")
    private byte[] logo;
    @OneToMany(mappedBy = "homeTeam")
    private List<Game> homeGames;
    @OneToMany(mappedBy = "visitorTeam")
    private List<Game> visitorGames;
    @OneToMany(mappedBy = "team")
    private List<Player> players;

    @Column(name="number_games")
    private int numberGames;
    @Column(name="wins")
    private int numberWins;
    @Column(name="draws")
    private int numberDraws;
    @Column(name="losses")
    private int numberLosses;

    public Team() {
    }

    public Team(String name, byte[] logo) {
        this.name = name;
        this.logo = logo;
        homeGames = new ArrayList<>();
        visitorGames = new ArrayList<>();
        players = new ArrayList<>();
        numberGames = 0;
        numberWins = 0;
        numberDraws = 0;
        numberLosses = 0;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    @Transactional
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getLogo() {
        return logo;
    }

    public void setLogo(byte[] logo) {
        this.logo = logo;
    }

    @Transactional
    public List<Game> getHomeGames() {
        return homeGames;
    }

    @Transactional
    public void setHomeGames(List<Game> homeGames) {
        this.homeGames = homeGames;
    }

    @Transactional
    public List<Game> getVisitorGames() {
        return visitorGames;
    }

    @Transactional
    public void setVisitorGames(List<Game> visitorGames) {
        this.visitorGames = visitorGames;
    }

    @Transactional
    public void addHomeGame(Game home) {
        homeGames.add(home);
    }

    @Transactional
    public void addVisitorGame(Game visitor) {
        visitorGames.add(visitor);
    }

    @Transactional
    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    public int getNumberGames() {
        return numberGames;
    }

    public void setNumberGames(int numberGames) {
        this.numberGames = numberGames;
    }

    public int getNumberWins() {
        return numberWins;
    }

    public void setNumberWins(int numberWins) {
        this.numberWins = numberWins;
    }

    public int getNumberDraws() {
        return numberDraws;
    }

    public void setNumberDraws(int numberDraws) {
        this.numberDraws = numberDraws;
    }

    public int getNumberLosses() {
        return numberLosses;
    }

    public void setNumberLosses(int numberLosses) {
        this.numberLosses = numberLosses;
    }

    public void addNewResult(int type){
        if (type > 0) numberWins++;
        else if (type < 0) numberLosses++;
        else numberDraws++;
        numberGames++;
    }

    @Override
    public String toString() {
        return "Team{" +
                "name='" + name + '\'' +
                '}';
    }
}
