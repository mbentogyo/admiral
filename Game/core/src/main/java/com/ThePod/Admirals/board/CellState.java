package com.ThePod.Admirals.board;

 import lombok.Getter;

public enum CellState {
    CARRIER         (7),
    CORVETTE        (6),
    DESTROYER       (5),
    FRIGATE         (4),
    SUBMARINE       (3),
    PATROL_BOAT     (2),

    NONE            (1),
    MISS            (0),
    HIT_UNKNOWN     (-1),

    HIT_PATROL_BOAT (-2),
    HIT_SUBMARINE   (-3),
    HIT_FRIGATE     (-4),
    HIT_DESTROYER   (-5),
    HIT_CORVETTE    (-6),
    HIT_CARRIER     (-7);


    @Getter private final int value;

    CellState(int value) {
        this.value = value;
    }

    public static CellState fromName(String name) {
        try {
            return CellState.valueOf(name);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static int fromNameToValue(String name) {
        CellState state = fromName(name);
        return (state != null) ? state.value : 0;
    }
}