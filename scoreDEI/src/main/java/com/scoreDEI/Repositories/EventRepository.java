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
    @Query("SELECT COUNT(t) FROM GameStatus t WHERE t.game = ?1 AND t.type = 0")
    public int beginningOfGameExists(Game game);

    @Query("SELECT t FROM GameStatus t WHERE t.game = ?1 AND t.type = 0")
    public List<GameStatus> getBeginningOfGame(Game game);

    @Query("SELECT COUNT(t) FROM GameStatus t WHERE t.game = ?1 AND t.type = 1")
    public int endingOfGameExists(Game game);

    @Query("SELECT COUNT(t) FROM GameStatus t WHERE t.game = ?1 AND t.type = 2 AND t.eventDate BETWEEN ?2 AND ?3")
    public int interruptionExists(Game game, Time eventMin, Time eventMax);

    @Query("SELECT COUNT(t) FROM GameStatus t WHERE t.game = ?1 AND t.type = 3 AND t.eventDate BETWEEN ?2 AND ?3")
    public int resumeGameExists(Game game, Time eventMin, Time eventMax);

    @Query("SELECT COUNT(t) FROM Card t WHERE t.game = ?1 AND t.player = ?2 AND t.isYellow IS true AND t.eventDate BETWEEN ?3 AND ?4")
    public int yellowCardExists(Game game, Player player, Time eventMin, Time eventMax);

    @Query("SELECT COUNT(t) FROM Card t WHERE t.game = ?1 AND t.player = ?2 AND t.isYellow IS false AND t.eventDate BETWEEN ?3 AND ?4")
    public int redCardExists(Game game, Player player, Time eventMin, Time eventMax);

    @Query("SELECT COUNT(t) FROM Goal t WHERE t.game = ?1 AND t.player = ?2 AND t.eventDate BETWEEN ?3 AND ?4")
    public int goalExists(Game game, Player player, Time eventMin, Time eventMax);

    @Query("SELECT t FROM GameEvent t WHERE t.game = ?1 ORDER BY t.eventDate DESC")
    public List<GameEvent> getMostRecentEvent(Game game);

    @Query("SELECT t FROM GameEvent t WHERE t.game = ?1 AND t.eventDate < ?2 ORDER BY t.eventDate ASC")
    public List<GameEvent> getNextDayEvents(Game game, Time begin);

    @Query("SELECT t FROM GameEvent t WHERE t.game = ?1 AND t.eventDate >= ?2 ORDER BY t.eventDate ASC")
    public List<GameEvent> getTodayEvents(Game game, Time begin);
}
