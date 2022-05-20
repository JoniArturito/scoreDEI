package com.scoreDEI.Repositories;

import com.scoreDEI.Entities.GameEvent;
import org.springframework.data.repository.CrudRepository;

public interface EventRepository extends CrudRepository<GameEvent, Integer> {
}
