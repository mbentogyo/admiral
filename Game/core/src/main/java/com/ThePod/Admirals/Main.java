package com.ThePod.Admirals;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx; 
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

// {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
public class Main extends Game {

    // Universal Enablers To Get View Port & Sprites
    public SpriteBatch batch;
    public ScreenCamera screenCamera;
    public CursorHandler cursorHandler; // New field

    @Override
    public void create() {
    
        batch = new SpriteBatch();
        screenCamera = new ScreenCamera();

        // Initialize and load assets
        AssetLoader.getInstance().loadAssets();
        AssetLoader.getInstance().finishLoading();

        // Assign loaded assets to the easy-access fields in AssetLoader
        AssetLoader.getInstance().cacheAssets(); 

        cursorHandler = new CursorHandler();
        Gdx.input.setInputProcessor(cursorHandler);
        // --- END NEW LINES ---

        // Sets The First Screen As Game Opens
        setScreen(new MainMenu(this));
    }

    @Override
    public void dispose() {
        batch.dispose();
        AssetLoader.getInstance().dispose();
        // Cursors are system resources, but we can reset to default
        Gdx.graphics.setSystemCursor(com.badlogic.gdx.graphics.Cursor.SystemCursor.Arrow);
        super.dispose();
    }
}