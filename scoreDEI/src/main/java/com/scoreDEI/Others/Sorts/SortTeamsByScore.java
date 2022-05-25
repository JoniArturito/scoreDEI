package com.scoreDEI.Others.Sorts;

import com.scoreDEI.Entities.Team;

import java.util.Comparator;

public class SortTeamsByScore implements Comparator<Team> {
    @Override
    public int compare(Team o1, Team o2) {
        int[] statsO1 = o1.getGamesInfo();
        int[] statsO2 = o2.getGamesInfo();
        int o1TotalScore = statsO1[1] * 3 + statsO1[2];
        int o2TotalScore = statsO2[1] * 3 + statsO2[2];
        if (o1TotalScore > o2TotalScore) return 1;
        else if (o1TotalScore < o2TotalScore) return -1;
        else{
            if (statsO1[1] > statsO2[1]) return 1;
            else if (statsO1[1] < statsO2[1]) return -1;
            else{
                if (statsO1[2] > statsO2[2]) return 1;
                else if (statsO1[2] < statsO2[2]) return -1;
                else return Integer.compare(statsO1[0], statsO2[0]);
            }
        }
    }
}
