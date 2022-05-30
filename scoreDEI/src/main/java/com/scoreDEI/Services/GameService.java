/**
 * This class is responsible for all the operations related to the Game entity
 */
package com.scoreDEI.Services;

import com.scoreDEI.Entities.Game;
import com.scoreDEI.Entities.Team;
import com.scoreDEI.Others.Sorts.SortGamesByDate;
import com.scoreDEI.Repositories.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GameService {
    @Autowired
    private GameRepository gameRepository;

    /**
     * > The function gets all the games from the database and returns them as a list
     *
     * @return A list of all games in the database.
     */
    public List<Game> getAllGames(){
        List<Game> allGames = new ArrayList<>();
        gameRepository.findAll().forEach(allGames::add);
        return allGames;
    }

    /**
     * Get all the games from the database, sort them by date, and return them.
     *
     * @return A list of all games in the database, sorted by date.
     */
    public List<Game> getOrderedGames(){
        List<Game> allGames = new ArrayList<>();
        gameRepository.findAll().forEach(allGames::add);
        allGames.sort(new SortGamesByDate());
        return allGames;
    }

    /**
     * This function takes a game object and saves it to the database.
     *
     * @param game The game object that we want to add to the database.
     */
    public void addGame(Game game){
        gameRepository.save(game);
    }

    /**
     * If the game exists, return it, otherwise return null.
     *
     * @param id The id of the game you want to get.
     * @return Optional<Game>
     */
    public Optional<Game> getGame(int id){
        return gameRepository.findById(id);
    }

    /**
     * Delete all games from the database.
     */
    public void clearAllGames(){
        gameRepository.deleteAll();
    }

    /**
     * It returns a list of games whose name contains the string of characters passed in as a parameter
     *
     * @param chars The characters that the user has entered in the search box.
     * @return A list of games that have the name that is passed in.
     */
    @Transactional
    public List<Game> findGameByName(String chars) {
        return gameRepository.findGameByName(chars);
    }

    /**
     * > This function gets a game by name from the database
     *
     * @param name The name of the game you want to get.
     * @return A list of games with the name of the game that was passed in.
     */
    @Transactional
    public Optional<Game> getGame(String name) {
        List<Game> query = gameRepository.findGameByName(name);
        return Optional.ofNullable(query.get(0));
    }

    /**
     * > This function checks if a stadium is occupied at a given time
     *
     * @param location the location of the stadium
     * @param t the time of the game
     * @return A list of games that are being played at the stadium at the time specified.
     */
    @Transactional
    public boolean isStadiumOccupied(String location, Timestamp t){
        List<Game> query = gameRepository.stadiumOccupied(location, t);
        return query.size() != 0;
    }

    /**
     * It takes a timestamp and returns a string array of the minimum and maximum hours that the event can be scheduled for
     *
     * @param event The timestamp of the event
     * @return The time interval of the event.
     */
    public String[] getTimeInterval(Timestamp event){
        long threeHours = ((3*60)*60)*1000;
        Timestamp maxDuration = new Timestamp(event.getTime() + threeHours);

        String minTime = event.toString().split(" ")[1];
        String maxTime = maxDuration.toString().split(" ")[1];

        String minHour = minTime.split("\\.")[0];
        String maxHour = maxTime.split("\\.")[0];;

        return new String[]{minHour, maxHour};
    }

    /**
     * If the game exists, and the team is not the same as the other team, then update the team
     *
     * @param gameId The id of the game you want to update
     * @param team The team to update
     * @param homeTeam boolean value that determines whether the team is the home team or the visitor team
     * @return A boolean value.
     */
    @Transactional
    public boolean updateTeam(int gameId, Team team, boolean homeTeam) {
        Optional<Game> game = this.getGame(gameId);
        if(game.isPresent()) {
            if (homeTeam) {
                if(game.get().getVisitorTeam().getName().equals(team.getName())) return false;
                game.get().setHomeTeam(team);
            } else {
                if(game.get().getHomeTeam().getName().equals(team.getName())) return false;
                game.get().setVisitorTeam(team);
            }
            return true;
        }

        return false;
    }

    /**
     * If the game exists, update the location and return true, otherwise return false.
     *
     * @param gameId The id of the game you want to update
     * @param location The location of the game.
     * @return A boolean value.
     */
    @Transactional
    public boolean updateLocation(int gameId, String location) {
        Optional<Game> game = this.getGame(gameId);
        if(game.isPresent()) {
            game.get().setLocation(location);
            return true;
        }

        return false;
    }

    /**
     * > This function updates the begin date of a game
     *
     * @param gameId the id of the game you want to update
     * @param beginDate The date and time the game will begin.
     * @return A boolean value.
     */
    @Transactional
    public boolean updateDate(int gameId, Timestamp beginDate) {
        Optional<Game> game = this.getGame(gameId);
        if (game.isPresent()) {
            game.get().setBeginDate(beginDate);
            return true;
        }
        return false;
    }

    /**
     * > Delete a game and all of its events
     *
     * @param id the id of the game to be deleted
     * @return A boolean value.
     */
    @Transactional
    public boolean deleteGame(int id){
        Optional<Game> opGame = gameRepository.findById(id);
        if (opGame.isPresent()) {
            Game game = opGame.get();
            gameRepository.deleteEvents(game);
            gameRepository.delete(game);
            return true;
        }
        return false;
    }

    /**
     * "Get the score of a game."
     *
     * The function is named getScore, and it takes a Game object as a parameter. It returns an array of two integers, the
     * first being the home team's score, and the second being the visitor team's score
     *
     * @param game The game object that is being played.
     * @return An array of two integers.
     */
    public int[] getScore(Game game){
        int homeScore = gameRepository.getTeamScore(game, game.getHomeTeam());
        int visitorScore = gameRepository.getTeamScore(game, game.getVisitorTeam());
        return new int[]{homeScore, visitorScore};
    }

    /**
     * > Given a list of games, return a list of scores for each game
     *
     * @param games a list of Game objects
     * @return A list of int arrays.
     */
    public List<int[]> getAllScores(List<Game> games) {
        List<int[]> toReturn = new ArrayList<>();
        for (Game g: games){
            toReturn.add(getScore(g));
        }
        return toReturn;
    }
}
