package com.ThePod.Admirals;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.audio.Music;

//// Use This To Load Assets Into The Game And Fetch Them For Use
/// Centralized Assset Loader Using Singleton Pattern
/// Access via AssetLoader.getInstance()
public class AssetLoader {
    
     private static AssetLoader instance;
    private AssetManager manager;
    
    private AssetLoader() {
        manager = new AssetManager();
    }
    
    public static AssetLoader getInstance() {
        if (instance == null) {
            instance = new AssetLoader();
        }
        return instance;
    }
    
    // Load All Game Assets To Be Used Here
    public void loadAssets() {
        // Example: Load textures
        // manager.load("sprites/player.png", Texture.class);
        // manager.load("sprites/enemy.png", Texture.class);
        
        // Example: Load texture atlases
        // manager.load("sprites/game.atlas", TextureAtlas.class);
        
        // Example: Load sounds
        // manager.load("sounds/shoot.wav", Sound.class);
        
        // Example: Load music
        // manager.load("music/theme.mp3", Music.class);
        
        // Example: Load fonts
        // manager.load("fonts/arial.fnt", BitmapFont.class);

        manager.load("MainMenu_BG.png", Texture.class);
    }
    
    // Call this in your render loop to process loading
    public boolean update() {
        return manager.update();
    }
    
    // Get loading progress (0.0 to 1.0)
    public float getProgress() {
        return manager.getProgress();
    }
    
    // Check if all assets are loaded
    public boolean isFinished() {
        return manager.isFinished();
    }
    
    // Block until all assets are loaded (use sparingly)
    public void finishLoading() {
        manager.finishLoading();
    }
    
    // Generic get method
    public <T> T get(String fileName, Class<T> type) {
        return manager.get(fileName, type);
    }
    
    // Convenience methods for common asset types
    public Texture getTexture(String fileName) {
        return manager.get(fileName, Texture.class);
    }
    
    public TextureAtlas getAtlas(String fileName) {
        return manager.get(fileName, TextureAtlas.class);
    }
    
    public Sound getSound(String fileName) {
        return manager.get(fileName, Sound.class);
    }
    
    public Music getMusic(String fileName) {
        return manager.get(fileName, Music.class);
    }
    
    public BitmapFont getFont(String fileName) {
        return manager.get(fileName, BitmapFont.class);
    }
    
    // Dispose of all assets when game closes
    public void dispose() {
        manager.dispose();
    }

}
