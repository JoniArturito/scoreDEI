/**
 * This class is responsible for all the operations related to the GameEvent entity
 */
package com.scoreDEI.Services;

import com.scoreDEI.Entities.*;
import com.scoreDEI.Repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    /**
     * It returns a list of all game events.
     *
     * @return A list of all the game events in the database.
     */
    public List<GameEvent> getAllGameEvents(){
        List<GameEvent> allGameEvents = new ArrayList<>();
        eventRepository.findAll().forEach(allGameEvents::add);
        return allGameEvents;
    }

    /**
     * This function saves a game event to the database.
     *
     * @param event The event to be added to the database.
     */
    public void addGameEvent(GameEvent event){
        eventRepository.save(event);
    }

    /**
     * If the event exists, return it, otherwise return null.
     *
     * @param id The id of the event you want to get.
     * @return Optional<GameEvent>
     */
    public Optional<GameEvent> getGameEvent(int id){
        return eventRepository.findById(id);
    }

    /**
     * Add a card event to the database.
     *
     * @param event The event object that you want to save.
     */
    public void addCardEvent(Card event)
    {
        eventRepository.save(event);
    }

    /**
     * > This function saves a GameStatus object to the database
     *
     * @param event The event to be added to the database.
     */
    public void addStatusEvent(GameStatus event)
    {
        eventRepository.save(event);
    }

    /**
     * Add a goal event to the database.
     *
     * @param event The event object that is being passed in.
     */
    public void addGoalEvent(Goal event)
    {
        eventRepository.save(event);
    }

    /**
     * Delete all events from the database.
     */
    public void clearAllEvents(){
        eventRepository.deleteAll();
    }

    /**
     * This function checks if the beginning of a game exists in the database
     *
     * @param game The game object that you want to check if the beginning of the game exists for.
     * @return A boolean value.
     */
    public boolean beginningGameExists(Game game){
        int counter = eventRepository.beginningOfGameExists(game);
        return counter != 0;
    }

    /**
     * > This function checks if the ending of a game exists in the database
     *
     * @param game the game that we want to check if it has an ending
     * @return The number of rows in the database that match the game.
     */
    public boolean endingGameExists(Game game){
        int counter = eventRepository.endingOfGameExists(game);
        return counter != 0;
    }

    /**
     * "Get the beginning of the game, if it exists."
     *
     * The function is a query to the database, and the query returns a list of GameStatus objects.  If the list is empty,
     * then the game has not started yet.  If the list is not empty, then the game has started, and the first element of
     * the list is the beginning of the game
     *
     * @param game The game you want to get the beginning of.
     * @return Optional<Time>
     */
    public Optional<Time> getBeginningOfGame(Game game) {
        List<GameStatus> query = eventRepository.getBeginningOfGame(game);
        if (query.size() == 0) {
            return Optional.empty();
        }
        return Optional.ofNullable(query.get(0).getEventDate());
    }

    /**
     * > Given a game and a time, return the most recent event time of the game, or the first event time of the next day if
     * it exists
     *
     * @param game The game you want to get the most recent event time of.
     * @param firstEvent The first event of the game.
     * @return The most recent event time of a game.
     */
    public Optional<Time> getMostRecentEventTimeOfGame(Game game, Time firstEvent) {
        List<GameEvent> query = eventRepository.getMostRecentEvent(game);
        if (query.size() == 0) {
            return Optional.empty();
        }
        List<GameEvent> nextDays = eventRepository.getNextDayEvents(game, firstEvent);
        if (nextDays.size() == 0) return Optional.ofNullable(query.get(0).getEventDate());
        else return Optional.ofNullable(nextDays.get(0).getEventDate());
    }

    /**
     * > Get the most recent event of a game, or if there are no events, get the first event of the next day
     *
     * @param game the game you want to get the most recent event of
     * @return The most recent event of a game.
     */
    public Optional<GameEvent> getMostRecentEventOfGame(Game game) {
        List<GameEvent> query = eventRepository.getMostRecentEvent(game);
        if (query.size() == 0) {
            return Optional.empty();
        }
        List<GameEvent> nextDays = eventRepository.getNextDayEvents(game, eventRepository.getBeginningOfGame(game).get(0).getEventDate());
        if (nextDays.size() == 0) return Optional.ofNullable(query.get(0));
        else return Optional.ofNullable(nextDays.get(0));
    }

    /**
     * "Get the events and return them in chronological order."
     *
     * The first thing we do is get the beginning of the game. If we can't find it, we return an empty list
     *
     * @param game the game you want to get the events for
     * @return A list of events that are happening today and tomorrow.
     */
    public List<GameEvent> getChronologicEvents(Game game){
        Optional<Time> opTime = getBeginningOfGame(game);
        if (opTime.isPresent()){
            Time time = opTime.get();
            List<GameEvent> today = eventRepository.getTodayEvents(game, time);
            List<GameEvent> tomorrow = eventRepository.getNextDayEvents(game, time);
            today.addAll(tomorrow);
            return today;
        }
        return new ArrayList<>();
    }

    /**
     * > If the event exists, delete it
     *
     * @param id the id of the event to be deleted
     * @return A boolean value.
     */
    @Transactional
    public boolean deleteEvent(int id) {
        Optional<GameEvent> event = eventRepository.findById(id);
        if (event.isPresent()) {
            eventRepository.delete(event.get());
            return true;
        }
        return false;
    }

    /**
     * "Return true if the player has a red card in the game."
     *
     * The function is annotated with @Transactional, which means that the function is executed in a transaction
     *
     * @param game The game in which the player has a red card
     * @param player the player who is being checked for a red card
     * @return The number of red cards a player has in a game.
     */
    @Transactional
    public boolean hasRedCard(Game game, Player player) {
        return eventRepository.redCardExists(game, player) != 0;
    }

    /**
     * If the player has a red card, return false. If the player has a yellow card and the new card is yellow, add a red
     * card. If the player has less than two yellow cards, return true
     *
     * @param dateAndTime The time of the event
     * @param game The game in which the card was given
     * @param isYellow true if the card is yellow, false if it's red
     * @param player The player who received the card
     * @return A boolean value.
     */
    @Transactional
    public boolean validateCard(Time dateAndTime, Game game, boolean isYellow, Player player) {
        int yellow_count = eventRepository.yellowCardExists(game, player);
        int red_count = eventRepository.redCardExists(game, player);

        if(red_count > 0) return false;
        if(isYellow && yellow_count == 1) {
            Card redCard = new Card(dateAndTime, game, false, player);
            this.addCardEvent(redCard);
            return true;
        }

        if(yellow_count < 2) return true;
        return false;
    }

    /**
     * Get the number of goals, yellow cards and red cards for a given player.
     *
     * @param p the player whose statistics are to be retrieved
     * @return An array of integers.
     */
    @Transactional
    public int[] getPlayerStatistic(Player p) {
        return new int[]{eventRepository.playerGoals(p), eventRepository.playerYellowCards(p), eventRepository.playerRedCards(p)};
    }
}
