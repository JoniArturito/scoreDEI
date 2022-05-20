package com.scoreDEI.Repositories;

import com.scoreDEI.Entities.Game;
import org.springframework.data.repository.CrudRepository;

public interface GameRepository extends CrudRepository<Game, Integer> {
}
