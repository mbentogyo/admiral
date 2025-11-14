package com.ThePod.Admirals;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ThePod.Admirals.board.AttackResult;
import com.ThePod.Admirals.board.Coordinates;

// This screen is the main "controller" for the game.
public class PlayScreen implements Screen {

    private Main game;
    private Viewport viewport;
    private Stage stage;
    private Texture background;
    private TextureAtlas atlas;
    private BitmapFont font;

    private GameUiState currentState;
    private BoardActor myBoardActor;
    private BoardActor enemyBoardActor;
    private GameHud hud;
    
    private GameManagerCallbackImpl callbackImpl;
    private TileActor lastAttackedTile = null; // To know which tile to update

    public PlayScreen(Main game) {
        this.game = game;
        this.viewport = game.screenCamera.getViewport();
        this.stage = new Stage(this.viewport, game.batch);
    }

    @Override
    public void show() {
        // Set the input processor to the stage
        Gdx.input.setInputProcessor(stage);

        // Get assets
        this.atlas = AssetLoader.getInstance().admiralsUiAtlas;
        this.font = AssetLoader.getInstance().operatorFont;
        background = AssetLoader.getInstance().getTexture("Play_Frame.png");
        
        // Instantiate your BoardActors
        myBoardActor = new BoardActor(atlas, this, true); // true = isMyBoard
        enemyBoardActor = new BoardActor(atlas, this, false); // false = isEnemyBoard
        
        // Position the boards (leaving space from the border)
        myBoardActor.setPosition(75, 140);
        enemyBoardActor.setPosition(690, 140);
        
        // Instantiate GameHud
        hud = new GameHud(atlas, font);
        
        // Add all actors to the stage
        stage.addActor(myBoardActor);
        stage.addActor(enemyBoardActor);
        stage.addActor(hud);
        
        // Set up the click listener for the "Fire" button
        hud.getFireButton().addListener(new ClickListener(Input.Buttons.LEFT) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onFireButtonClicked();
            }
        });

        // Set up callback and start GameManager
        callbackImpl = new GameManagerCallbackImpl(this);
        GameManager.start(callbackImpl); // This will call callback.setUp()
    }

    // This is called by a TileActor
    public void onTileClicked(BoardActor board, TileActor tile) {
        if (currentState != GameUiState.MY_TURN) {
            return; // Can't click if not my turn
        }
        
        // We only care about clicks on the enemy board
        if (board == enemyBoardActor) {
            enemyBoardActor.handleTileClick(tile);
            hud.getFireButton().setDisabled(false);
        }
    }
    
    // This is called by the Fire Button
    private void onFireButtonClicked() {
        TileActor selectedTile = enemyBoardActor.getSelectedTile();
        
        // Check if we are in the right state and have a target
        if (currentState == GameUiState.MY_TURN && selectedTile != null) {
            Gdx.app.log("PlayScreen", "Fire button confirmed! Attacking (" + selectedTile.row + ", " + selectedTile.col + ")");
            
            enterAnimatingState(); // Lock the UI
            
            // Store which tile we attacked
            this.lastAttackedTile = selectedTile;
            
            // Send the attack to the GameManager
            // Your friend's Coordinates(x,y) are (col, row)
            GameManager.attack(new Coordinates(selectedTile.col, selectedTile.row)); 
            
            enemyBoardActor.clearSelection(); // Hide the aimer
        }
    }

    @Override
    public void render(float delta) {
        // Clear screen
        ScreenUtils.clear(0, 0, 0, 1);
        
        // Update the camera
        game.screenCamera.update();
        game.batch.setProjectionMatrix(game.screenCamera.getCamera().combined);

        // Draw background
        game.batch.begin();
        game.batch.draw(background, 0, 0, ScreenCamera.WORLD_WIDTH, ScreenCamera.WORLD_HEIGHT);
        // Draw labels
        font.setColor(Color.WHITE);
        font.draw(game.batch, "Your Board", myBoardActor.getX() + 180, myBoardActor.getY() + 390);
        font.draw(game.batch, "Enemy Board", enemyBoardActor.getX() + 180, enemyBoardActor.getY() + 390);
        game.batch.end();

        // Update and draw the stage (and all actors in it)
        stage.act(delta);
        stage.draw();
    }

    // --- State Management Methods ---
    
    public void enterSetupWaitState() {
        this.currentState = GameUiState.SETUP_WAIT;
        hud.showMessage("Waiting for opponent...");
        hud.getFireButton().setDisabled(true);
        enemyBoardActor.setBoardInteractive(false);
        myBoardActor.setBoardInteractive(false);
    }
    
    public void setEnemyReady() {
        hud.showMessage("Enemy is ready! Starting game...");
    }

    public void enterMyTurnState() {
        this.currentState = GameUiState.MY_TURN;
        hud.showMessage("Your Turn: Select a target");
        hud.getFireButton().setDisabled(true); // Can't fire until a tile is selected
        enemyBoardActor.setBoardInteractive(true); // Can click enemy
        myBoardActor.setBoardInteractive(false);
        enemyBoardActor.clearSelection();
    }
    
    public void enterEnemyTurnState() {
        this.currentState = GameUiState.ENEMY_TURN;
        hud.showMessage("Enemy Turn...");
        hud.getFireButton().setDisabled(true);
        enemyBoardActor.setBoardInteractive(false);
        myBoardActor.setBoardInteractive(false);
    }

    public void enterAnimatingState() {
        this.currentState = GameUiState.ANIMATING;
        hud.showMessage("...");
        hud.getFireButton().setDisabled(true);
        enemyBoardActor.setBoardInteractive(false);
        myBoardActor.setBoardInteractive(false);
    }

    public void showGameOver(String message) {
        this.currentState = GameUiState.GAME_OVER;
        hud.showMessage("Game Over: " + message);
        hud.getFireButton().setDisabled(true);
        enemyBoardActor.setBoardInteractive(false);
        myBoardActor.setBoardInteractive(false);
    }

    // --- Callbacks from GameManagerCallbackImpl ---
    
    // Result of OUR attack
    public void onMyAttackResult(String message) {
        enterAnimatingState();
        hud.showMessage(message);
        
        if (lastAttackedTile == null) {
             // Should not happen, but as a failsafe:
            Gdx.app.log("PlayScreen", "ERROR: onMyAttackResult called but lastAttackedTile was null.");
            return;
        }

        // Determine result from message
        if (message.startsWith("HIT") || message.startsWith("SINK")) {
            lastAttackedTile.playHitAnimation(() -> {
                // Animation is done, GameManager will call onEnemyTurn()
            });
        } else { // MISS
            lastAttackedTile.playMissAnimation(() -> {
                // Animation is done, GameManager will call onEnemyTurn()
            });
        }
        lastAttackedTile = null; // Clear the target
    }

    // Result of ENEMY's attack
    public void onEnemyAttack(Coordinates coords, AttackResult result, String message) {
        enterAnimatingState();
        hud.showMessage(message);
        
        // Tell our board to animate the hit
        myBoardActor.applyResult(coords, result, () -> {
           // Animation is done, GameManager will call onMyTurn()
        });
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