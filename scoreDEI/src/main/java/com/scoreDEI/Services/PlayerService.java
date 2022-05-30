/**
 * This class is responsible for all the operations that can be done on a player
 */
package com.scoreDEI.Services;

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

    /**
     * It returns a list of all players in the database.
     *
     * @return A list of all players in the database.
     */
    @Transactional
    public List<Player> getAllPlayers(){
        List<Player> allPlayers = new ArrayList<>();
        playerRepository.findAll().forEach(allPlayers::add);
        return allPlayers;
    }

    /**
     * > The function takes a player object, and saves it to the database
     *
     * @param player The player object that we want to save.
     */
    @Transactional
    public void addPlayer(Player player){
        playerRepository.save(player);
    }

    /**
     * > This function gets a player by id
     *
     * @param id The id of the player you want to get.
     * @return Optional<Player>
     */
    @Transactional
    public Optional<Player> getPlayer(int id){
        return playerRepository.findById(id);
    }

    /**
     * Get all players, sort them by score, and return the first one.
     *
     * @return Optional<Player>
     */
    @Transactional
    public Optional<Player> getBestScorer()
    {
        List<Player> allPlayers = new ArrayList<>();
        playerRepository.findAll().forEach(allPlayers::add);
        if (allPlayers.size() == 0) return Optional.empty();
        allPlayers.sort(new SortPlayersByScore());
        return Optional.ofNullable(allPlayers.get(0));
    }

    /**
     * Delete all players from the database.
     */
    public void clearAllPlayers(){
        playerRepository.deleteAll();
    }

    /**
     * If the player exists, update the name and return true, otherwise return false.
     *
     * @param id The id of the player you want to update
     * @param name The name of the method.
     * @return A boolean value.
     */
    @Transactional
    public boolean updateName(int id, String name) {
        Optional<Player> player = this.getPlayer(id);
        if(player.isPresent()) {
            player.get().setName(name);
            return true;
        }

        return false;
    }

    /**
     * If the player exists, update the position and return true, otherwise return false.
     *
     * @param id The id of the player you want to update.
     * @param position The position of the player.
     * @return A boolean value.
     */
    @Transactional
    public boolean updatePosition(int id, String position) {
        Optional<Player> player = this.getPlayer(id);

        if(player.isPresent()) {
            player.get().setPosition(position);
            return true;
        }

        return false;
    }

    /**
     * If the player exists, update the birthday and return true, otherwise return false.
     *
     * @param id The id of the player you want to update
     * @param birthday The new birthday of the player
     * @return A boolean value.
     */
    @Transactional
    public boolean updateBirthday(int id, Date birthday) {
        Optional<Player> player = this.getPlayer(id);

        if(player.isPresent()) {
            player.get().setBirthday(birthday);
            return true;
        }

        return false;
    }

    /**
     * If the player exists, update the team and return true, otherwise return false.
     *
     * @param id The id of the player you want to update
     * @param team The team to update the player with.
     * @return A boolean value.
     */
    @Transactional
    public boolean updateTeam(int id, Team team) {
        Optional<Player> player = this.getPlayer(id);

        if(player.isPresent()) {
            player.get().setTeam(team);
            return true;
        }

        return false;
    }

    /**
     * Find all players whose name contains the given string.
     *
     * @param chars The characters that the player's name must start with.
     * @return A list of players with the name that matches the chars parameter.
     */
    public List<Player> findPlayerByName(String chars) {
        return playerRepository.findPlayerByName(chars);
    }

    /**
     * Get the player with the given name, or return null if no such player exists.
     *
     * @param name The name of the player you want to get.
     * @return A list of players with the name given.
     */
    @Transactional
    public Optional<Player> getPlayer(String name) {
        List<Player> query = playerRepository.findPlayerByName(name);
        return Optional.ofNullable(query.get(0));
    }

    /**
     * > This function checks if a player exists in the database
     *
     * @param p the player object that we want to check if it exists in the database
     * @return A list of players that match the parameters.
     */
    @Transactional
    public boolean isPlayerExist(Player p) {
        List<Player> list = playerRepository.playerExist(p.getName(), p.getPosition(), p.getBirthday(), p.getTeam());
        return list.size() != 0;
    }

    /**
     * Delete a player by id, and delete all goals and cards associated with that player
     *
     * @param id the id of the player to be deleted
     * @return A boolean value.
     */
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

    /**
     * > This function returns the team of a player with a given id
     *
     * @param id the id of the player
     * @return A list of strings
     */
    @Transactional
    public String getTeam(int id){
        List<String> query = playerRepository.getTeam(id);
        if (query.size() == 0) return null;
        return query.get(0);
    }
}
