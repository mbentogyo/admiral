package com.ThePod.Admirals;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter;

//// Use This To Load Assets Into The Game And Fetch Them For Use
/// Centralized Assset Loader Using Singleton Pattern
/// Access via AssetLoader.getInstance()
public class AssetLoader {
    
     private static AssetLoader instance;
     private AssetManager manager;

     // --- Cached Assets ---
     // Add public fields here for assets you want to access frequently
     public TextureAtlas admiralsUiAtlas;
     public BitmapFont operatorFont;
     
     private AssetLoader() {
         manager = new AssetManager();
         FileHandleResolver resolver = new InternalFileHandleResolver();
         manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
         manager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
     }
     
     public static AssetLoader getInstance() {
         if (instance == null) {
             instance = new AssetLoader();
         }
         return instance;
     }
     
     // Load All Game Assets To Be Used Here
     public void loadAssets() {
         
         // Load texture atlases
         manager.load("Admirals_Ui.atlas", TextureAtlas.class);

         // Load Frames
         manager.load("Menu_Frame.png", Texture.class);
         manager.load("Connecting_Frame.png", Texture.class);
         manager.load("Play_Frame.png", Texture.class);

         // Load Font
         FreeTypeFontLoaderParameter fontParams = new FreeTypeFontLoaderParameter();
         fontParams.fontFileName = "8Bit_Operator.ttf";
         fontParams.fontParameters.size = 32;
         manager.load("8Bit_Operator.ttf", BitmapFont.class, fontParams);
         
         
     }

    
      // Assigns loaded assets to the public fields for easy access.
      // Must be called AFTER finishLoading() or once update() returns true.
     public void cacheAssets() {
        // Get the loaded assets and assign them to our public fields
        admiralsUiAtlas = manager.get("Admirals_Ui.atlas", TextureAtlas.class);

        // Get Font
        operatorFont = manager.get("8Bit_Operator.ttf", BitmapFont.class);
        
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
         // Nullify references
         admiralsUiAtlas = null;
         operatorFont = null;
         
     }
}