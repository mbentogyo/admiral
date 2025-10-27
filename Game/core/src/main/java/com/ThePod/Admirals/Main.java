package com.ThePod.Admirals;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {

    // Universal Enablers To Get View Port & Sprites
    public SpriteBatch batch;
    public Viewport viewport;

    @Override
    public void create() {
    
        batch = new SpriteBatch();
        viewport = new ScreenViewport();

        // Initialize and load assets
        AssetLoader.getInstance().loadAssets();
        AssetLoader.getInstance().finishLoading();

        // Sets The First Screen As Game Opens
        setScreen(new MainMenu(this));
    }

    @Override
    public void dispose() {
        batch.dispose();
        AssetLoader.getInstance().dispose();
        super.dispose();
    }
}