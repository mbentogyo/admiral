package com.ThePod.Admirals;

import com.ThePod.Admirals.board.Coordinates;
import com.ThePod.Admirals.board.AttackResult;
import com.ThePod.Admirals.network.callback.TurnCallback;
import com.badlogic.gdx.Gdx;

// This implements Turn Call Back interface and calls UI (PlayScreen)
public class GameManagerCallbackImpl implements TurnCallback {

    private final PlayScreen screen;

    public GameManagerCallbackImpl(PlayScreen screen) {
        this.screen = screen;
    }

    // All these methods are called by the GameManager
    // Using postRunnable to safely call UI code from the main render thread
    @Override
    public void setUp() {
        Gdx.app.postRunnable(() -> {
            screen.enterSetupWaitState();
        });
    }

    @Override
    public void onEnemyReady() {
        // The GameManager will handle the START? call and eventually call onMyTurn/onEnemyTurn
        // just update the HUD
        Gdx.app.postRunnable(() -> {
            screen.setEnemyReady();
        });
    }

    @Override
    public void onMyTurn() {
        Gdx.app.postRunnable(() -> {
            screen.enterMyTurnState();
        });
    }

    @Override
    public void onEnemyTurn() {
        Gdx.app.postRunnable(() -> {
            screen.enterEnemyTurnState();
        });
    }

    // This is the result of OUR attack
    @Override
    public void myAttack(String message) {
        Gdx.app.postRunnable(() -> {
            screen.onMyAttackResult(message);
        });
    }

    // This is the result of the ENEMY's attack
    @Override
    public void enemyAttack(Coordinates coordinates, AttackResult result, String message) {
        Gdx.app.postRunnable(() -> {
            screen.onEnemyAttack(coordinates, result, message);
        });
    }

    @Override
    public void onGameOver(String message) {
        Gdx.app.postRunnable(() -> {
            screen.showGameOver(message);
        });
    }
}