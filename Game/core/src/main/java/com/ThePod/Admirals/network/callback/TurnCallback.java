package com.ThePod.Admirals.network.callback;

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
     * @param message message to display
     */
    void enemyAttack(String message);

    /**
     * Method called after game ends
     * @param message message to display
     */
    void onGameOver(String message);
}
