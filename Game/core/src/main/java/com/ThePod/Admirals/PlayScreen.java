package com.ThePod.Admirals;

import com.ThePod.Admirals.board.AttackResult;
import com.ThePod.Admirals.board.Coordinates;
import com.ThePod.Admirals.network.callback.TurnCallback;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import lombok.Getter;

public class PlayScreen implements Screen {

    @Getter private static PlayScreen instance;

    private Main game;
    private Viewport viewport;
    private Stage stage;
    private Texture background;
    private TextureAtlas atlas;
    private BitmapFont font;

    private GameBoard myBoard;
    private GameBoard enemyBoard;

    private String hudMessage = "";

    public PlayScreen(Main game) {
        this.game = game;

        // Set viewport and stage
        this.viewport = game.screenCamera.getViewport();
        this.stage = new Stage(this.viewport, game.batch);
        instance = this;

        // Load assets
        this.atlas = AssetLoader.getInstance().admiralsUiAtlas;
        this.font = AssetLoader.getInstance().operatorFont;
        this.background = AssetLoader.getInstance().getTexture("Play_Frame.png");

        // Create boards
        myBoard = new GameBoard(atlas, this, true);
        enemyBoard = new GameBoard(atlas, this, false);

        // Set positions BEFORE adding to stage
        myBoard.setPosition(75, 140);
        enemyBoard.setPosition(690, 140);

        // Add to stage
        stage.addActor(myBoard);
        stage.addActor(enemyBoard);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);

        game.screenCamera.update();
        game.batch.setProjectionMatrix(game.screenCamera.getCamera().combined);

        // Draw background and HUD
        game.batch.begin();
        game.batch.draw(background, 0, 0, ScreenCamera.WORLD_WIDTH, ScreenCamera.WORLD_HEIGHT);
        font.setColor(Color.WHITE);
        font.draw(game.batch, "Your Board", myBoard.getX() + 180, myBoard.getY() + 390);
        font.draw(game.batch, "Enemy Board", enemyBoard.getX() + 180, enemyBoard.getY() + 390);
        font.draw(game.batch, hudMessage, 550, 650);
        game.batch.end();

        // Update and draw stage
        stage.act(delta);
        stage.draw();
    }

    public void onFireButtonAttack(Coordinates coords) {
        GameManager.attack(coords);
    }

    public static void onMyTurn() {
        instance.hudMessage = "Your Turn: Select a target";
        instance.enemyBoard.setBoardInteractive(true);
    }

    public static void onEnemyTurn() {
        instance.hudMessage = "Enemy Turn...";
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
