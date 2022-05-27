package com.scoreDEI.Repositories;

import com.scoreDEI.Entities.Team;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TeamRepository extends CrudRepository<Team, Integer> {
    @Query("SELECT t FROM Team t WHERE t.name = ?1")
    public List<Team> findTeamByName(String chars);
}
