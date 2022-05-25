package com.scoreDEI.Services;

import com.scoreDEI.Entities.Card;
import com.scoreDEI.Entities.GameEvent;
import com.scoreDEI.Entities.GameStatus;
import com.scoreDEI.Entities.Goal;
import com.scoreDEI.Repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    public List<GameEvent> getAllGameEvents(){
        List<GameEvent> allGameEvents = new ArrayList<>();
        eventRepository.findAll().forEach(allGameEvents::add);
        return allGameEvents;
    }

    public void addGameEvent(GameEvent event){
        eventRepository.save(event);
    }

    public Optional<GameEvent> getGameEvent(int id){
        return eventRepository.findById(id);
    }

    public void addCardEvent(Card event)
    {
        eventRepository.save(event);
    }

    public void addStatusEvent(GameStatus event)
    {
        eventRepository.save(event);
    }

    public void addGoalEvent(Goal event)
    {
        eventRepository.save(event);
    }

    public void clearAllEvents(){
        eventRepository.deleteAll();
    }
}
