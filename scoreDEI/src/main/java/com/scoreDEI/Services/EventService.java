package com.scoreDEI.Services;

import com.scoreDEI.Entities.*;
import com.scoreDEI.Repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
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

    public boolean beginningGameExists(Game game){
        int counter = eventRepository.beginningOfGameExists(game);
        return counter != 0;
    }

    public boolean endingGameExists(Game game){
        int counter = eventRepository.endingOfGameExists(game);
        return counter != 0;
    }

    public Optional<Time> getBeginningOfGame(Game game) {
        List<GameStatus> query = eventRepository.getBeginningOfGame(game);
        if (query.size() == 0) {
            return Optional.empty();
        }
        return Optional.ofNullable(query.get(0).getEventDate());
    }

    public Optional<Time> getMostRecentEventOfGame(Game game, Time firstEvent) {
        List<GameEvent> query = eventRepository.getMostRecentEvent(game);
        if (query.size() == 0) {
            return Optional.empty();
        }
        List<GameEvent> nextDays = eventRepository.getNextDayEvents(game, firstEvent);
        if (nextDays.size() == 0) return Optional.ofNullable(query.get(0).getEventDate());
        else return Optional.ofNullable(nextDays.get(0).getEventDate());
    }
}
