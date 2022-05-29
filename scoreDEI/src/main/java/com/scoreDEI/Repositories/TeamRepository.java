package com.scoreDEI.Repositories;

import com.scoreDEI.Entities.Game;
import com.scoreDEI.Entities.Player;
import com.scoreDEI.Entities.Team;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TeamRepository extends CrudRepository<Team, Integer> {
    @Query("SELECT t FROM Team t WHERE t.name = ?1")
    public List<Team> findTeamByName(String chars);

    @Modifying
    @Query("DELETE FROM GameEvent c WHERE c.game = ?1")
    public void deleteGameEvents(Game game);

    @Modifying
    @Query("DELETE FROM Game g WHERE g.homeTeam = ?1 OR g.visitorTeam = ?1")
    public void deleteGames(Team team);

    @Modifying
    @Query("DELETE FROM Goal g WHERE g.player = ?1")
    public void deleteGoals(Player p);

    @Modifying
    @Query("DELETE FROM Card c WHERE c.player = ?1")
    public void deleteCards(Player p);

    @Modifying
    @Query("DELETE FROM Player p WHERE p.team = ?1")
    public void deletePlayers(Team t);
}
