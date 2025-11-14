package com.ThePod.Admirals.board;

import lombok.Getter;

import java.util.List;

public class EnemyBoard {
    @Getter private int[][] board;

    public EnemyBoard(){
        this.board = new int[15][10];
    }

    public void attacked(Coordinates crd, AttackResult result){
        //TODO
    }

    public void boatSunk(List<Coordinates> list, AttackResult result){
        //TODO
    }

}
