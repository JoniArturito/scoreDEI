package com.scoreDEI.Repositories;

import com.scoreDEI.Entities.Player;
import com.scoreDEI.Entities.Team;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.sql.Date;
import java.util.List;

public interface PlayerRepository extends CrudRepository<Player, Integer> {
    @Query("SELECT t FROM Player t WHERE t.name = ?1")
    public List<Player> findPlayerByName(String chars);

    @Query("SELECT t FROM Player t WHERE t.name = ?1 AND t.position = ?2 AND t.birthday = ?3 AND t.team = ?4")
    public List<Player> playerExist(String chars, String position, Date birthday, Team t);
}
