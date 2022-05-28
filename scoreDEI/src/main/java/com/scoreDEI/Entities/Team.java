package com.scoreDEI.Entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.scoreDEI.Others.Sorts.SortPlayersByScore;

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

    public Team() {
    }

    public Team(String name, byte[] logo) {
        this.name = name;
        this.logo = logo;
        homeGames = new ArrayList<>();
        visitorGames = new ArrayList<>();
        players = new ArrayList<>();
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

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

    public int[] getGamesInfo()
    {
        /*
            0 -> numero de jogos
            1 -> vitorias
            2 -> empates
            3 -> derrotas
         */
        int[] info = new int[4];
        info[0] = homeGames.size() + visitorGames.size();
        int error = 0;
        for (Game game: homeGames) {
            switch (game.isWinner(this)) {
                case 1 -> info[1]++;
                case 0 -> info[2]++;
                case -1 -> info[3]++;
                default -> error++;
            }
        }
        for (Game game: visitorGames) {
            switch (game.isWinner(this)) {
                case 1 -> info[1]++;
                case 0 -> info[2]++;
                case -1 -> info[3]++;
                default -> error++;
            }
        }
        return info;
    }

    public Player getBestScorer()
    {
        players.sort(new SortPlayersByScore());
        for (Player player: players)
        {
            System.out.printf("%s -> %d\n", player, player.getNumberGoals());
        }
        return players.get(0);
    }

    @Override
    public String toString() {
        return "Team{" +
                "name='" + name + '\'' +
                '}';
    }
}
