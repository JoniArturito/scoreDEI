/**
 * It compares two teams by their total score, then by their number of wins, then by their number of draws, then by their
 * number of games
 */
package com.scoreDEI.Others.Sorts;

import com.scoreDEI.Entities.Team;

import java.util.Comparator;

public class SortTeamsByScore implements Comparator<Team> {
    @Override
    public int compare(Team o1, Team o2) {
        int o1TotalScore = o1.getNumberWins() * 3 + o1.getNumberDraws();
        int o2TotalScore = o2.getNumberWins() * 3 + o2.getNumberDraws();
        if (o1TotalScore > o2TotalScore) return -1;
        else if (o1TotalScore < o2TotalScore) return 1;
        else{
            if (o1.getNumberWins() > o2.getNumberWins()) return -1;
            else if (o1.getNumberWins() < o2.getNumberWins()) return 1;
            else{
                if (o1.getNumberDraws() > o2.getNumberDraws()) return -1;
                else if (o1.getNumberDraws() < o2.getNumberDraws()) return 1;
                else return Integer.compare(o2.getNumberGames(), o1.getNumberGames());
            }
        }
    }
}
