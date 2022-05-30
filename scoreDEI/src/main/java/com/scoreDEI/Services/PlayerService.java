package com.scoreDEI.Services;

import com.scoreDEI.Entities.Game;
import com.scoreDEI.Entities.Player;
import com.scoreDEI.Entities.Team;
import com.scoreDEI.Entities.User;
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

    @Transactional
    public List<Player> getAllPlayers(){
        List<Player> allPlayers = new ArrayList<>();
        playerRepository.findAll().forEach(allPlayers::add);
        return allPlayers;
    }

    @Transactional
    public void addPlayer(Player player){
        playerRepository.save(player);
    }

    @Transactional
    public Optional<Player> getPlayer(int id){
        return playerRepository.findById(id);
    }

    @Transactional
    public Optional<Player> getBestScorer()
    {
        List<Player> allPlayers = new ArrayList<>();
        playerRepository.findAll().forEach(allPlayers::add);
        if (allPlayers.size() == 0) return Optional.empty();
        allPlayers.sort(new SortPlayersByScore());
        return Optional.ofNullable(allPlayers.get(0));
    }

    public void clearAllPlayers(){
        playerRepository.deleteAll();
    }

    @Transactional
    public boolean updateName(int id, String name) {
        Optional<Player> player = this.getPlayer(id);
        if(player.isPresent()) {
            player.get().setName(name);
            return true;
        }

        return false;
    }

    @Transactional
    public boolean updatePosition(int id, String position) {
        Optional<Player> player = this.getPlayer(id);

        if(player.isPresent()) {
            player.get().setPosition(position);
            return true;
        }

        return false;
    }

    @Transactional
    public boolean updateBirthday(int id, Date birthday) {
        Optional<Player> player = this.getPlayer(id);

        if(player.isPresent()) {
            player.get().setBirthday(birthday);
            return true;
        }

        return false;
    }

    @Transactional
    public boolean updateTeam(int id, Team team) {
        Optional<Player> player = this.getPlayer(id);

        if(player.isPresent()) {
            player.get().setTeam(team);
            return true;
        }

        return false;
    }

    public List<Player> findPlayerByName(String chars) {
        return playerRepository.findPlayerByName(chars);
    }

    @Transactional
    public Optional<Player> getPlayer(String name) {
        List<Player> query = playerRepository.findPlayerByName(name);
        return Optional.ofNullable(query.get(0));
    }

    @Transactional
    public boolean isPlayerExist(Player p) {
        List<Player> list = playerRepository.playerExist(p.getName(), p.getPosition(), p.getBirthday(), p.getTeam());
        return list.size() != 0;
    }

    @Transactional
    public boolean deletePlayer(int id){
        Optional<Player> opPlayer = playerRepository.findById(id);
        if (opPlayer.isPresent()) {
            Player player = opPlayer.get();
            playerRepository.deleteGoals(player);
            playerRepository.deleteCards(player);
            playerRepository.delete(player);
            return true;
        }
        return false;
    }

    @Transactional
    public String getTeam(int id){
        List<String> query = playerRepository.getTeam(id);
        if (query.size() == 0) return null;
        return query.get(0);
    }
}
