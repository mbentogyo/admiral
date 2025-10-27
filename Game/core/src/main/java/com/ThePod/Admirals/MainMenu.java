package com.ThePod.Admirals;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;

public class MainMenu implements Screen {
    private Main game;
    private Texture background;
    
    public MainMenu(Main game) {
        this.game = game;
    }
    
    @Override
    public void show() {
        // Load the background texture when screen is shown
        background = AssetLoader.getInstance().getTexture("MainMenu_BG.png");
    }
    
    @Override
    public void render(float delta) {
        // Clear screen
        ScreenUtils.clear(0, 0, 0, 1);
        
        // Draw background
        game.batch.begin();
        game.batch.draw(background, 0, 0, game.viewport.getWorldWidth(), game.viewport.getWorldHeight());
        game.batch.end();
    }
    
    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height, true);
    }
    
    @Override
    public void pause() {
    }
    
    @Override
    public void resume() {
    }
    
    @Override
    public void hide() {
    }
    
    @Override
    public void dispose() {
        // Don't dispose the texture here - AssetLoader manages it
    }
}