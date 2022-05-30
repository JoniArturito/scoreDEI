package com.scoreDEI.Repositories;

import com.scoreDEI.Entities.Player;
import com.scoreDEI.Entities.Team;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.sql.Date;
import java.util.List;

public interface PlayerRepository extends CrudRepository<Player, Integer> {
    /**
     * Find all players whose name equals the given string.
     *
     * @param chars The name of the player you want to find.
     * @return A list of players with the name that matches the parameter.
     */
    @Query("SELECT t FROM Player t WHERE t.name = ?1")
    public List<Player> findPlayerByName(String chars);

    /**
     * This function returns a list of players that have the same name, position, birthday, and team as the parameters.
     *
     * @param chars the name of the player
     * @param position String
     * @param birthday java.util.Date
     * @param t the entity class
     * @return A list of players that match the given parameters.
     */
    @Query("SELECT t FROM Player t WHERE t.name = ?1 AND t.position = ?2 AND t.birthday = ?3 AND t.team = ?4")
    public List<Player> playerExist(String chars, String position, Date birthday, Team t);

    /**
     * Delete all goals for a given player.
     *
     * @param p The player whose goals we want to delete.
     */
    @Modifying
    @Query("DELETE FROM Goal g WHERE g.player = ?1")
    public void deleteGoals(Player p);

    /**
     * Delete all cards that belong to a player.
     *
     * @param p The player whose cards we want to delete.
     */
    @Modifying
    @Query("DELETE FROM Card c WHERE c.player = ?1")
    public void deleteCards(Player p);

    /**
     * Get the name of the team that the player with the given id plays for.
     *
     * @param id The id of the player
     * @return A list of strings
     */
    @Query("SELECT p.team.name FROM Player p WHERE p.playerId = ?1")
    public List<String> getTeam(int id);
}
