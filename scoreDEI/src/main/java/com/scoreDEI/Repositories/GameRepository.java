package com.scoreDEI.Repositories;

import com.scoreDEI.Entities.Game;

import com.scoreDEI.Entities.Team;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.sql.Timestamp;
import java.util.List;

public interface GameRepository extends CrudRepository<Game, Integer> {
    @Query("SELECT t FROM Game t WHERE t.name = ?1")
    public List<Game> findGameByName(String chars);

    @Query("SELECT t FROM Game t WHERE t.location = ?1 AND t.beginDate = ?2")
    public List<Game> stadiumOccupied(String location, Timestamp t);

    @Modifying
    @Query("DELETE FROM GameEvent e WHERE e.game = ?1")
    public void deleteEvents(Game game);

    @Query("SELECT COUNT(t) FROM Goal t WHERE t.game = ?1 AND t.player.team = ?2")
    public int getTeamScore(Game game, Team team);
}
