package com.ThePod.Admirals.board;

public class EnemyBoard {
    private int[][] board;

    public EnemyBoard(){
        this.board = new int[10][10];
    }

    public void attacked(Coordinates crd, AttackResult result){
        //TODO
    }

    public void sunk(Coordinates[] crds){
        for (Coordinates crd : crds) {

        }
    }

}
