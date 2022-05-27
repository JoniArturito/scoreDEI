package com.scoreDEI.Repositories;

import com.scoreDEI.Entities.Player;
import com.scoreDEI.Entities.Team;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PlayerRepository extends CrudRepository<Player, Integer> {
    @Query("SELECT t FROM Player t WHERE t.name = ?1")
    public List<Player> findPlayerByName(String chars);
}
