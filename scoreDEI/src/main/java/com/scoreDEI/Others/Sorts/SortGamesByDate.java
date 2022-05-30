/**
 * It implements the Comparator interface and compares two Game objects by their beginDate attribute
 */
package com.scoreDEI.Others.Sorts;

import com.scoreDEI.Entities.Game;

import java.util.Comparator;

public class SortGamesByDate implements Comparator<Game> {
    @Override
    public int compare(Game o1, Game o2) {
        return o1.getBeginDate().compareTo(o2.getBeginDate());
    }
}
