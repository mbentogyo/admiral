package com.ThePod.Admirals.board;

import java.util.ArrayList;
import java.util.List;

public class MyBoard {
    private int[][] board;

    public MyBoard(int[][] board) {
        this.board = board;
    }

    public AttackResult attacked(Coordinates crd) {
        if (board[crd.getRow()][crd.getColumn()] < 0) return AttackResult.ALREADY_CHECKED;

        if (board[crd.getRow()][crd.getColumn()] == CellState.NONE.getValue()) {
            board[crd.getRow()][crd.getColumn()] = CellState.MISS.getValue();
            return AttackResult.MISS;
        }

        int state = board[crd.getRow()][crd.getColumn()];
        board[crd.getRow()][crd.getColumn()] *= -1;

        if (isSunk(state)) {
            if (isDefeated()) return AttackResult.GAME_OVER;
            else return AttackResult.getFromState(state);
        }
        else return AttackResult.HIT;
    }

    public String getShip(CellState state) {
        List<Coordinates> coordinates = new ArrayList<>();
        int boat = state.getValue();

        for (int i = 0; i < board.length; i++) {
            for (int  j = 0; j < board[i].length; j++) {
                if (Math.abs(board[i][j]) == boat) coordinates.add(new Coordinates(j, i));
            }
        }

        return coordinates.toString().replace("[", "").replace("]", "").replace(" ", "");
    }

    private boolean isSunk(int boat){
        for (int[] ints : board) {
            for (int anInt : ints) {
                if (anInt == boat) return false;
            }
        }

        return true;
    }

    private boolean isDefeated(){
        for (int[] ints : board) {
            for (int anInt : ints) {
                if (anInt > 1) return false;
            }
        }

        return true;
    }
}
