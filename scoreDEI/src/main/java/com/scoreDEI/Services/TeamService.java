package com.scoreDEI.Services;

import com.scoreDEI.Entities.Team;
import com.scoreDEI.Others.Sorts.SortTeamsByScore;
import com.scoreDEI.Repositories.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TeamService {
    @Autowired
    private TeamRepository teamRepository;

    public List<Team> getAllTeams(){
        List<Team> allTeams = new ArrayList<>();
        teamRepository.findAll().forEach(allTeams::add);
        return allTeams;
    }

    public void addTeam(Team team){
        teamRepository.save(team);
    }

    public Optional<Team> getTeam(int id){
        return teamRepository.findById(id);
    }

    public List<Team> getOrderedTeams(){
        List<Team> allTeams = new ArrayList<>();
        teamRepository.findAll().forEach(allTeams::add);
        allTeams.sort(new SortTeamsByScore());
        return allTeams;
    }
}
