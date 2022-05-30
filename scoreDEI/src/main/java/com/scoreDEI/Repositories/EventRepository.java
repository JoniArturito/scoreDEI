package com.scoreDEI.Repositories;

import com.scoreDEI.Entities.Game;
import com.scoreDEI.Entities.GameEvent;
import com.scoreDEI.Entities.GameStatus;
import com.scoreDEI.Entities.Player;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.sql.Time;
import java.util.List;

public interface EventRepository extends CrudRepository<GameEvent, Integer> {
    /**
     * Return the number of GameStatus objects that have a type of 0 and a game of the given game.
     *
     * @param game The game that the status is for
     * @return The number of GameStatus objects that have a type of 0 and a game that matches the game passed in.
     */
    @Query("SELECT COUNT(t) FROM GameStatus t WHERE t.game = ?1 AND t.type = 0")
    public int beginningOfGameExists(Game game);

    /**
     * This function returns a list of GameStatus objects that are associated with a specific game and have a type of 0
     *
     * @param game The game that the status is for
     * @return A list of GameStatus objects that are the beginning of a game.
     */
    @Query("SELECT t FROM GameStatus t WHERE t.game = ?1 AND t.type = 0")
    public List<GameStatus> getBeginningOfGame(Game game);

    /**
     * Returns the number of rows in the GameStatus table where the game is equal to the game passed in and the type is
     * equal to 1.
     *
     * @param game The game that the status is for
     * @return The number of GameStatus objects that have a type of 1 and a game of the given game.
     */
    @Query("SELECT COUNT(t) FROM GameStatus t WHERE t.game = ?1 AND t.type = 1")
    public int endingOfGameExists(Game game);

    /**
     * Returns the number of Interruption events in a game
     *
     * @param game The game object that is being checked for an interruption
     * @param eventMin The minimum time of the event
     * @param eventMax The time of the event that is being checked for.
     * @return The number of interruptions that occurred in a game.
     */
    @Query("SELECT COUNT(t) FROM GameStatus t WHERE t.game = ?1 AND t.type = 2 AND t.eventDate BETWEEN ?2 AND ?3")
    public int interruptionExists(Game game, Time eventMin, Time eventMax);

    /**
     * "I want to know if there is a GameStatus record for a given game, with a type of 3, and an eventDate between two
     * times."
     *
     * @param game The game object that is being queried
     * @param eventMin The minimum time of the event
     * @param eventMax The time of the last event in the game.
     * @return The number of GameStatus objects that have a type of 3 and an eventDate between the eventMin and eventMax
     * times.
     */
    @Query("SELECT COUNT(t) FROM GameStatus t WHERE t.game = ?1 AND t.type = 3 AND t.eventDate BETWEEN ?2 AND ?3")
    public int resumeGameExists(Game game, Time eventMin, Time eventMax);

    /**
     * Returns the number of yellow cards a player has in a game.
     *
     * @param game The game that the card is associated with
     * @param player The player who has the card
     * @return The number of yellow cards a player has in a game.
     */
    @Query("SELECT COUNT(t) FROM Card t WHERE t.game = ?1 AND t.player = ?2 AND t.isYellow IS true")
    public int yellowCardExists(Game game, Player player);

    /**
     * Returns the number of red cards that a player has in a game.
     *
     * @param game The game that the card is in
     * @param player The player who is being checked for a card
     * @return The number of red cards that exist for a given game and player.
     */
    @Query("SELECT COUNT(t) FROM Card t WHERE t.game = ?1 AND t.player = ?2 AND t.isYellow IS false")
    public int redCardExists(Game game, Player player);

    /**
     * Get the most recent event for a given game.
     *
     * @param game The game to get the most recent event for.
     * @return A list of GameEvents
     */
    @Query("SELECT t FROM GameEvent t WHERE t.game = ?1 ORDER BY t.eventDate DESC")
    public List<GameEvent> getMostRecentEvent(Game game);

    /**
     * Get all events for a game that occur after a given time.
     *
     * @param game The game object that the events are associated with.
     * @param begin The time to start the query from.
     * @return A list of GameEvents that are associated with the game and have an eventDate that is before the begin time.
     */
    @Query("SELECT t FROM GameEvent t WHERE t.game = ?1 AND t.eventDate < ?2 ORDER BY t.eventDate ASC")
    public List<GameEvent> getNextDayEvents(Game game, Time begin);

    /**
     * Get all events for a game that occur after a certain time.
     *
     * @param game The game object that we want to get events for.
     * @param begin The time to start the query from.
     * @return A list of GameEvents that are associated with the game and have an eventDate greater than or equal to the
     * begin time.
     */
    @Query("SELECT t FROM GameEvent t WHERE t.game = ?1 AND t.eventDate >= ?2 ORDER BY t.eventDate ASC")
    public List<GameEvent> getTodayEvents(Game game, Time begin);

    /**
     * Return the number of yellow cards a player has.
     *
     * @param player The player to get the yellow cards for
     * @return The number of yellow cards a player has.
     */
    @Query("SELECT COUNT(t) FROM Card t WHERE t.player = ?1 AND t.isYellow IS true")
    public int playerYellowCards(Player player);

    /**
     * Return the number of red cards a player has.
     *
     * @param player The player to get the cards for
     * @return The number of red cards a player has.
     */
    @Query("SELECT COUNT(t) FROM Card t WHERE t.player = ?1 AND t.isYellow IS false")
    public int playerRedCards(Player player);

    /**
     * Return the number of goals scored by a player.
     *
     * @param player The player whose goals we want to count.
     * @return The number of goals scored by a player.
     */
    @Query("SELECT COUNT(t) FROM Goal t WHERE t.player = ?1")
    public int playerGoals(Player player);
}
