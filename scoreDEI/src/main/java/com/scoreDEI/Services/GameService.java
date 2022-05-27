package com.scoreDEI.Services;

import com.scoreDEI.Entities.Game;
import com.scoreDEI.Entities.Team;
import com.scoreDEI.Others.Sorts.SortTeamsByScore;
import com.scoreDEI.Repositories.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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
    public Optional<Game> getGame(String name)
    {
        List<Game> query = gameRepository.findGameByName(name);
        System.out.println();
        for(Game q: query)
        {
            System.out.println(q);
        }
        System.out.println();
        return Optional.ofNullable(query.get(0));
    }
}
