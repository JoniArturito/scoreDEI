package com.scoreDEI.Repositories;

import com.scoreDEI.Entities.Game;
import com.scoreDEI.Entities.Team;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GameRepository extends CrudRepository<Game, Integer> {
    @Query("SELECT t FROM Game t WHERE t.name = ?1")
    public List<Game> findGameByName(String chars);
}
