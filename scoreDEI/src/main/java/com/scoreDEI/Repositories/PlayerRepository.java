package com.scoreDEI.Repositories;

import com.scoreDEI.Entities.Player;
import org.springframework.data.repository.CrudRepository;

public interface PlayerRepository extends CrudRepository<Player, Integer> {
}
