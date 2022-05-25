package com.scoreDEI.Services;

import com.scoreDEI.Entities.Game;
import com.scoreDEI.Repositories.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
