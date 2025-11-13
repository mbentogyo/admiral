package com.ThePod.Admirals.board;

import lombok.Getter;

public enum AttackResult {
    HIT(CellState.HIT_UNKNOWN),

    SINK_CARRIER(CellState.CARRIER),
    SINK_CORVETTE(CellState.CORVETTE),
    SINK_DESTROYER(CellState.DESTROYER),
    SINK_FRIGATE(CellState.FRIGATE),
    SINK_SUBMARINE(CellState.SUBMARINE),
    SINK_PATROL_BOAT(CellState.PATROL_BOAT),

    MISS(CellState.MISS),
    GAME_OVER(CellState.NONE),
    ALREADY_CHECKED(CellState.NONE);

    @Getter
    private final CellState cellState;

    AttackResult (CellState cellState) {
        this.cellState = cellState;
    }

    public static AttackResult getFromState(int i) {
        for (AttackResult attackResult : values()) {
            if (attackResult.getCellState().getValue() == i) return attackResult;
        }
        
        return null;
    }
}
