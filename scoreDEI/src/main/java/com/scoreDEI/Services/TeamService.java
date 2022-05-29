package com.scoreDEI.Services;

import com.scoreDEI.Entities.Game;
import com.scoreDEI.Entities.Player;
import com.scoreDEI.Entities.Team;
import com.scoreDEI.Others.Sorts.SortTeamsByScore;
import com.scoreDEI.Repositories.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TeamService {
    @Autowired
    private TeamRepository teamRepository;

    @Transactional
    public List<Team> getAllTeams(){
        List<Team> allTeams = new ArrayList<>();
        teamRepository.findAll().forEach(allTeams::add);
        return allTeams;
    }

    public void addTeam(Team team){
        teamRepository.save(team);
    }

    @Transactional
    public Optional<Team> getTeam(int id){
        return teamRepository.findById(id);
    }

    @Transactional
    public Optional<Team> getTeam(String name) {
        List<Team> query = teamRepository.findTeamByName(name);
        for(Team t: query) {
            System.out.println("-> " + t.getName());
        }
        return Optional.ofNullable(query.get(0));
    }

    @Transactional
    public List<Team> getOrderedTeams(){
        List<Team> allTeams = new ArrayList<>();
        teamRepository.findAll().forEach(allTeams::add);
        allTeams.sort(new SortTeamsByScore());
        return allTeams;
    }

    public void clearAllTeams(){
        teamRepository.deleteAll();
    }

    @Transactional
    public List<Team> findTeamByName(String chars) {
        return teamRepository.findTeamByName(chars);
    }

    @Transactional
    public void addGameToTeam(Team team) {
        teamRepository.findById(team.getTeamId());
    }

    @Transactional
    public void updateTeamLogo(int id, byte[] new_logo) {
        Optional<Team> t = this.getTeam(id);
        t.ifPresent(team -> team.setLogo(new_logo));
    }

    @Transactional
    public void updateTeamName(int id, String name) {
        Optional<Team> t = this.getTeam(id);
        t.ifPresent(team -> team.setName(name));
    }

    @Transactional
    public boolean deleteTeam(int id){
        Optional<Team> opTeam = teamRepository.findById(id);
        if (opTeam.isPresent()) {
            Team team = opTeam.get();
            for (Game g: team.getHomeGames()) {
                teamRepository.deleteGameEvents(g);
            }
            for (Game g: team.getVisitorGames()) {
                teamRepository.deleteGameEvents(g);
            }
            teamRepository.deleteGames(team);

            for (Player p: team.getPlayers()) {
                teamRepository.deleteGoals(p);
                teamRepository.deleteCards(p);
            }
            teamRepository.deletePlayers(team);

            teamRepository.delete(team);
            return true;
        }
        return false;
    }

}
