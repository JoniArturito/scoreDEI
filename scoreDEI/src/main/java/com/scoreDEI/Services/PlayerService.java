package com.scoreDEI.Services;

import com.scoreDEI.Entities.Game;
import com.scoreDEI.Entities.Player;
import com.scoreDEI.Entities.Team;
import com.scoreDEI.Others.Sorts.SortPlayersByScore;
import com.scoreDEI.Repositories.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PlayerService {
    @Autowired
    private PlayerRepository playerRepository;

    public List<Player> getAllPlayers(){
        List<Player> allPlayers = new ArrayList<>();
        playerRepository.findAll().forEach(allPlayers::add);
        return allPlayers;
    }

    public void addPlayer(Player player){
        playerRepository.save(player);
    }

    public Optional<Player> getPlayer(int id){
        return playerRepository.findById(id);
    }

    public Player getBestScorer()
    {
        List<Player> allPlayers = new ArrayList<>();
        playerRepository.findAll().forEach(allPlayers::add);
        allPlayers.sort(new SortPlayersByScore());
        for (Player player: allPlayers)
        {
            System.out.printf("%s -> %d\n", player, player.getNumberGoals());
        }
        return allPlayers.get(0);
    }

    public void clearAllPlayers(){
        playerRepository.deleteAll();
    }

    @Transactional
    public void updateName(int id, String name) {
        Optional<Player> p = this.getPlayer(id);
        p.ifPresent(player -> player.setName(name));
    }

    @Transactional
    public void updatePosition(int id, String position) {
        Optional<Player> p = this.getPlayer(id);
        p.ifPresent(player -> player.setPosition(position));
    }

    @Transactional
    public void updateBirthday(int id, Date birthday) {
        Optional<Player> p = this.getPlayer(id);
        p.ifPresent(player -> player.setBirthday(birthday));
    }

    @Transactional
    public void updateTeam(int id, Team team) {
        Optional<Player> p = this.getPlayer(id);
        p.ifPresent(player -> player.setTeam(team));
    }

    public List<Player> findPlayerByName(String chars) {
        return playerRepository.findPlayerByName(chars);
    }

    @Transactional
    public Optional<Player> getPlayer(String name) {
        List<Player> query = playerRepository.findPlayerByName(name);
        System.out.println();
        for(Player q: query)
        {
            System.out.println(q);
        }
        System.out.println();
        return Optional.ofNullable(query.get(0));
    }

    @Transactional
    public boolean isPlayerExist(Player p)
    {
        List<Player> list = playerRepository.playerExist(p.getName(), p.getPosition(), p.getBirthday(), p.getTeam());
        return list.size() != 0;
    }
}
