package com.ThePod.Admirals.network.callback;

import com.ThePod.Admirals.board.AttackResult;
import com.ThePod.Admirals.board.Coordinates;

public interface TurnCallback {
    /**
     * Method called for when the two players can start set up
     */
    void setUp();

    /**
     * Method called for when it is the player's turn
     */
    void onMyTurn();

    /**
     * Method called for when it is the enemy's turn
     */
    void onEnemyTurn();

    /**
     * Method called after player attacks
     * @param message message to display
     */
    void myAttack(String message);

    /**
     * Method called after enemy attacks
     * @param coordinates the coordinates of the attack
     * @param message message to display
     */
    void enemyAttack(Coordinates coordinates, AttackResult result, String message);

    /**
     * Method called after game ends
     * @param message message to display
     */
    void onGameOver(String message);

    /**
     * Method called after enemy is ready AND player is not
     */
    void onEnemyReady();
}
