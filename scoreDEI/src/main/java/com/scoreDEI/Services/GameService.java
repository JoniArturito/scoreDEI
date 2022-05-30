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

    public List<Game> getAllGames(){
        List<Game> allGames = new ArrayList<>();
        gameRepository.findAll().forEach(allGames::add);
        return allGames;
    }

    public List<Game> getOrderedGames(){
        List<Game> allGames = new ArrayList<>();
        gameRepository.findAll().forEach(allGames::add);
        allGames.sort(new SortGamesByDate());
        return allGames;
    }

    public void addGame(Game game){
        gameRepository.save(game);
    }

    public Optional<Game> getGame(int id){
        return gameRepository.findById(id);
    }

    public void clearAllGames(){
        gameRepository.deleteAll();
    }

    @Transactional
    public List<Game> findGameByName(String chars) {
        return gameRepository.findGameByName(chars);
    }

    @Transactional
    public Optional<Game> getGame(String name) {
        List<Game> query = gameRepository.findGameByName(name);
        System.out.println();
        for(Game q: query)
        {
            System.out.println(q);
        }
        System.out.println();
        return Optional.ofNullable(query.get(0));
    }

    @Transactional
    public boolean isStadiumOccupied(String location, Timestamp t){
        List<Game> query = gameRepository.stadiumOccupied(location, t);
        return query.size() != 0;
    }

    public String[] getTimeInterval(Timestamp event){
        long threeHours = ((3*60)*60)*1000;
        Timestamp maxDuration = new Timestamp(event.getTime() + threeHours);

        String minTime = event.toString().split(" ")[1];
        String maxTime = maxDuration.toString().split(" ")[1];

        String minHour = minTime.split("\\.")[0];
        String maxHour = maxTime.split("\\.")[0];;

        return new String[]{minHour, maxHour};
    }

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

    @Transactional
    public boolean updateLocation(int gameId, String location) {
        Optional<Game> game = this.getGame(gameId);
        if(game.isPresent()) {
            game.get().setLocation(location);
            return true;
        }

        return false;
    }

    @Transactional
    public boolean updateDate(int gameId, Timestamp beginDate) {
        Optional<Game> game = this.getGame(gameId);
        if (game.isPresent()) {
            game.get().setBeginDate(beginDate);
            return true;
        }
        return false;
    }

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

    public int[] getScore(Game game){
        int homeScore = gameRepository.getTeamScore(game, game.getHomeTeam());
        int visitorScore = gameRepository.getTeamScore(game, game.getVisitorTeam());
        return new int[]{homeScore, visitorScore};
    }
}
