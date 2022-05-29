package com.scoreDEI.Others.Sorts;

import com.scoreDEI.Entities.Player;

import java.util.Comparator;

public class SortPlayersByScore implements Comparator<Player> {
    @Override
    public int compare(Player o1, Player o2) {
        return Integer.compare(o2.getNumberGoals(), o1.getNumberGoals());
    }
}
