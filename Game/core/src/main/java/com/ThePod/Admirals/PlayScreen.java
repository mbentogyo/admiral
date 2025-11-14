package com.ThePod.Admirals;

import com.ThePod.Admirals.board.AttackResult;
import com.ThePod.Admirals.board.Coordinates;
import com.ThePod.Admirals.network.callback.TurnCallback;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import lombok.Getter;

// This screen is the main "controller" and implements your friend's TurnCallback
public class PlayScreen implements Screen {

    // Enum for UI state
    public enum GameUiState {
        SETUP_WAIT,
        MY_TURN,
        ENEMY_TURN,
        ANIMATING,
        GAME_OVER
    }

    @Getter private static PlayScreen instance;

    private Main game;
    private Viewport viewport;
    private Stage stage;
    private Texture background;
    private TextureAtlas atlas;
    private BitmapFont font;

    private GameBoard myBoard;
    private GameBoard enemyBoard;

    private GameUiState currentState;
    private String hudMessage = "";

    public PlayScreen(Main game) {
        this.game = game;
        this.viewport = game.screenCamera.getViewport();
        this.stage = new Stage(this.viewport, game.batch);
        instance = this;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        this.atlas = AssetLoader.getInstance().admiralsUiAtlas;
        this.font = AssetLoader.getInstance().operatorFont;
        this.background = AssetLoader.getInstance().getTexture("Play_Frame.png");

        // --- Create the two boards ---
        // We pass 'this' (the PlayScreen) to the boards so they can call back

        // 1. Create My Board (Player)
        // FIX: Added 'this' as the second argument
        myBoard = new GameBoard(atlas, this, true); // true = isMyBoard
        myBoard.setPosition(75, 140);

        // 2. Create Enemy Board
        // FIX: Added 'this' as the second argument
        enemyBoard = new GameBoard(atlas, this, false); // false = isMyBoard
        enemyBoard.setPosition(690, 140);

        // 3. Add them to the stage
        stage.addActor(myBoard);
        stage.addActor(enemyBoard);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);

        game.screenCamera.update();
        game.batch.setProjectionMatrix(game.screenCamera.getCamera().combined);

        // Draw background
        game.batch.begin();
        game.batch.draw(background, 0, 0, ScreenCamera.WORLD_WIDTH, ScreenCamera.WORLD_HEIGHT);

        // Draw HUD Labels
        font.setColor(Color.WHITE);
        font.draw(game.batch, "Your Board", myBoard.getX() + 180, myBoard.getY() + 390);
        font.draw(game.batch, "Enemy Board", enemyBoard.getX() + 180, enemyBoard.getY() + 390);

        // Draw HUD Message
        font.draw(game.batch, hudMessage, 550, 650);

        game.batch.end();

        // Update and draw the stage (and all actors in it)
        stage.act(delta);
        stage.draw();
    }

    // --- Public method for boards to call ---

    public void onFireButtonAttack(Coordinates coords) {
        if (currentState == GameUiState.MY_TURN) {
            Gdx.app.log("PlayScreen", "Fire button confirmed! Attacking " + coords.toString());
            enterAnimatingState();
            GameManager.attack(coords); // Send to your friend's logic
        }
    }

    // --- State Management ---

    private void enterAnimatingState() {
        this.currentState = GameUiState.ANIMATING;
        this.hudMessage = "...";
        enemyBoard.setBoardInteractive(false);
    }

    // --- TurnCallback Implementation ---

    public static void onMyTurn() {
        instance.currentState = GameUiState.MY_TURN;
        instance.hudMessage = "Your Turn: Select a target";
        instance.enemyBoard.setBoardInteractive(true);
    }

    public static void onEnemyTurn() {
        instance.currentState = GameUiState.ENEMY_TURN;
        instance.hudMessage = "Enemy Turn...";
        instance.enemyBoard.setBoardInteractive(false);
    }

    public static void myAttack(String message) {
        instance.enterAnimatingState();
        instance.hudMessage = message; // "HIT", "MISS", "SUNK [SHIP]"

        // Tell the enemy board what happened so it can update its visuals
        instance.enemyBoard.applyMyAttackResult(message, () -> {
            // This runs after the animation
            // The GameManager will call onEnemyTurn() next
        });
    }

    public static void enemyAttack(Coordinates coordinates, AttackResult result, String message) {
        instance.enterAnimatingState();
        instance.hudMessage = message; // "HIT", "MISS", "SUNK [SHIP]"

        // Tell *my* board what happened
        instance.myBoard.applyEnemyAttack(coordinates, result, () -> {
            // This runs after the animation
            // The GameManager will call onMyTurn() next
        });
    }

    public static void onGameOver(String message) {
        instance.currentState = GameUiState.GAME_OVER;
        instance.hudMessage = "Game Over: " + message;
        instance.enemyBoard.setBoardInteractive(false);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
