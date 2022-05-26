package com.scoreDEI.Entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.scoreDEI.Others.Sorts.SortPlayersByScore;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "teams")
@JsonIgnoreProperties({"teams"})
@XmlRootElement
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "teamId", nullable = false)
    private int teamId;
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    @Column(name = "imageName", nullable = false)
    private String imageName;
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "logo")
    private byte[] logo;
    @ManyToMany(cascade = CascadeType.MERGE)
    private List<Game> games;
    @OneToMany(mappedBy = "playerId")
    private List<Player> players;

    public Team() {
    }

    public Team(String name, byte[] logo) {
        this.name = name;
        this.logo = logo;
        games = new ArrayList<>();
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

    public List<Game> getGames() {
        return games;
    }

    public void setGames(ArrayList<Game> games) {
        this.games = games;
    }

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
        info[0] = games.size();
        int error = 0;
        for (Game game: games)
        {
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
