package com.scoreDEI.Others.Sorts;

import com.scoreDEI.Entities.GameEvent;

import java.util.Comparator;

public class SortEventByDate implements Comparator<GameEvent> {

    @Override
    public int compare(GameEvent o1, GameEvent o2) {
        return o1.getEventDate().compareTo(o2.getEventDate());
    }
}
