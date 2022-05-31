/**
 * This class is responsible for all the operations that can be done on a Team entity
 */
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

    /**
     * It returns a list of all teams in the database.
     *
     * @return A list of all the teams in the database.
     */
    @Transactional
    public List<Team> getAllTeams(){
        List<Team> allTeams = new ArrayList<>();
        teamRepository.findAll().forEach(allTeams::add);
        return allTeams;
    }

    /**
     * > This function gets all the teams from the database, sorts them by score, and returns the sorted list
     *
     * @return A list of teams sorted by score.
     */
    @Transactional
    public List<Team> getOrderedTeams(){
        List<Team> allTeams = new ArrayList<>();
        teamRepository.findAll().forEach(allTeams::add);
        allTeams.sort(new SortTeamsByScore());
        return allTeams;
    }


    /**
     * This function adds a team to the database.
     *
     * @param team The team object that is being passed in.
     */
    public void addTeam(Team team){
        teamRepository.save(team);
    }

    /**
     * > This function gets a team by its id
     *
     * @param id The id of the team you want to get.
     * @return Optional<Team>
     */
    @Transactional
    public Optional<Team> getTeam(int id){
        return teamRepository.findById(id);
    }

    /**
     * Get the team with the given name, or return null if no such team exists.
     *
     * @param name The name of the team you want to get.
     * @return A list of teams with the name of the team that was passed in.
     */
    @Transactional
    public Optional<Team> getTeam(String name) {
        List<Team> query = teamRepository.findTeamByName(name);
        return Optional.ofNullable(query.get(0));
    }

    /**
     * Delete all teams from the database.
     */
    public void clearAllTeams(){
        teamRepository.deleteAll();
    }

    /**
     * > Finds a team by name
     *
     * @param chars The string that you want to search for.
     * @return A list of teams that have the name that is passed in.
     */
    @Transactional
    public List<Team> findTeamByName(String chars) {
        return teamRepository.findTeamByName(chars);
    }

    /**
     * This function is used to add a game to a team
     *
     * @param team The team object that is being added to the database.
     */
    @Transactional
    public void addGameToTeam(Team team) {
        teamRepository.findById(team.getTeamId());
    }

    /**
     * If the team exists, update the logo, otherwise return false.
     *
     * @param id The id of the team you want to update
     * @param new_logo The new logo to be set for the team.
     * @return A boolean value.
     */
    @Transactional
    public boolean updateTeamLogo(int id, byte[] new_logo) {
        Optional<Team> team = this.getTeam(id);

        if(team.isPresent()) {
            team.get().setLogo(new_logo);
            return true;
        }

        return false;
    }

    /**
     * If the team exists, update the team name and return true, otherwise return false.
     *
     * @param id The id of the team you want to update
     * @param name The name of the team
     * @return A boolean value.
     */
    @Transactional
    public boolean updateTeamName(int id, String name) {
        Optional<Team> team = this.getTeam(id);

        if(team.isPresent()) {
            team.get().setName(name);
            return true;
        }

        return false;
    }

    /**
     * Delete all the games, game events, players, goals, and cards for a team, then delete the team
     *
     * @param id the id of the team to be deleted
     * @return A boolean value.
     */
    @Transactional
    public boolean deleteTeam(int id){
        Optional<Team> opTeam = teamRepository.findById(id);
        if (opTeam.isPresent()) {
            Team team = opTeam.get();
            for (Game g: team.getHomeGames()) {
                int homeScore = teamRepository.getTeamScore(g, team);
                int visitorScore = teamRepository.getTeamScore(g, g.getVisitorTeam());
                if (homeScore - visitorScore > 0) {
                    teamRepository.deleteLoss(g.getVisitorTeam().getTeamId());
                }
                else if (homeScore - visitorScore < 0) {
                    teamRepository.deleteWin(g.getVisitorTeam().getTeamId());
                }
                else{
                    teamRepository.deleteDraw(g.getVisitorTeam().getTeamId());
                }
                teamRepository.deleteGameEvents(g);
            }
            for (Game g: team.getVisitorGames()) {
                int homeScore = teamRepository.getTeamScore(g, g.getHomeTeam());
                int visitorScore = teamRepository.getTeamScore(g, team);
                if (homeScore - visitorScore > 0) {
                    teamRepository.deleteWin(g.getHomeTeam().getTeamId());
                }
                else if (homeScore - visitorScore < 0) {
                    teamRepository.deleteLoss(g.getHomeTeam().getTeamId());
                }
                else{
                    teamRepository.deleteDraw(g.getHomeTeam().getTeamId());
                }
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

    /**
     * Update the team's statistics by adding the difference between the team's score and the opponent's score to the
     * team's statistics.
     *
     * @param team the team that is being updated
     * @param difference the difference between the goals scored by the home team and the goals scored by the away team.
     */
    @Transactional
    public void updateStatistic(Team team, int difference) {
        team.addNewResult(difference);
    }
}
