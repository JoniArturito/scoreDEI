package com.scoreDEI.Repositories;

import com.scoreDEI.Entities.Game;

import com.scoreDEI.Entities.Team;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.sql.Timestamp;
import java.util.List;

public interface GameRepository extends CrudRepository<Game, Integer> {
    /**
     * Find all games by name parameter and return them as a list of Game objects.
     *
     * @param chars The name of the game you want to search for.
     * @return A list of games with the name that matches the parameter.
     */
    @Query("SELECT t FROM Game t WHERE t.name = ?1")
    public List<Game> findGameByName(String chars);

    /**
     * This function returns a list of games that are played at a certain stadium on a certain date
     *
     * @param location The location of the stadium
     * @param t the name of the entity
     * @return A list of games that are being played at a certain stadium at a certain time.
     */
    @Query("SELECT t FROM Game t WHERE t.location = ?1 AND t.beginDate = ?2")
    public List<Game> stadiumOccupied(String location, Timestamp t);

    /**
     * Delete all GameEvent objects that have a game attribute equal to the game parameter.
     *
     * @param game The game to delete events for
     */
    @Modifying
    @Query("DELETE FROM GameEvent e WHERE e.game = ?1")
    public void deleteEvents(Game game);

    /**
     * Get the number of goals scored by a team in a game.
     *
     * @param game The game we want to get the score for
     * @param team The team to get the score for
     * @return The number of goals scored by a team in a game.
     */
    @Query("SELECT COUNT(t) FROM Goal t WHERE t.game = ?1 AND t.player.team = ?2")
    public int getTeamScore(Game game, Team team);
}
