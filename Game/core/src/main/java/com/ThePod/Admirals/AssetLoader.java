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

public class AssetLoader {
    
     private static AssetLoader instance;
     private AssetManager manager;

     // --- Cached Assets ---
     public TextureAtlas admiralsUiAtlas;
     public BitmapFont operatorFont;
     
     // --- Cached Sounds ---
     public Sound attackButtonEasterSound;
     public Sound attackButtonSound;
     public Sound attackHitSound;
     public Sound attackMissSound;
     public Sound buttonClickedSound;
     public Sound buttonHoverSound;
     public Sound clickShipSound;
     public Sound hoverShipSound;
     public Sound hoverTileSound;
     public Sound inputCode1Sound;
     public Sound inputCode2Sound;
     public Sound inputCode3Sound;
     public Sound inputBackspaceSound;
     public Sound launchLine1Sound;
     public Sound launchLine2Sound;
     public Sound launchLine3Sound;
     public Sound launchLine4Sound;
     public Sound launchLine5Sound;
     public Sound shipRotateSound;
     public Sound shipPlacedInvalidSound;
     public Sound shipPlacedInvalid2Sound;
     public Sound shipPlacedValidSound;
     public Sound sunkenShipSound;
     
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
         
         // Load SoundFX
         manager.load("Attack_Button_EasterEgg.ogg", Sound.class);
         manager.load("Attack_Button.ogg", Sound.class);
         manager.load("Attack_Hit.ogg", Sound.class);
         manager.load("Attack_Miss.ogg", Sound.class);
         manager.load("Button_Clicked.ogg", Sound.class);
         manager.load("Button_Hover.ogg", Sound.class);
         manager.load("Click_Ship.ogg", Sound.class);
         manager.load("Hover_Ship.ogg", Sound.class);
         manager.load("Hover_Tile.ogg", Sound.class);
         manager.load("InputCode_1.ogg", Sound.class);
         manager.load("InputCode_2.ogg", Sound.class);
         manager.load("InputCode_3.ogg", Sound.class);
         manager.load("InputCode_Backspace.ogg", Sound.class);
         manager.load("LaunchLine_1.ogg", Sound.class);
         manager.load("LaunchLine_2.ogg", Sound.class);
         manager.load("LaunchLine_3.ogg", Sound.class);
         manager.load("LaunchLine_4.ogg", Sound.class);
         manager.load("LaunchLine_5.ogg", Sound.class);
         manager.load("Ship_Rotate.ogg", Sound.class);
         manager.load("ShipPlaced_Invalid.ogg", Sound.class);
         manager.load("ShipPlaced_Invalid2.ogg", Sound.class);
         manager.load("ShipPlaced_Valid.ogg", Sound.class);
         manager.load("Sunken_Ship.ogg", Sound.class);
     }

     public void cacheAssets() {
        // Get the loaded assets and assign them to our public fields
        admiralsUiAtlas = manager.get("Admirals_Ui.atlas", TextureAtlas.class);
        operatorFont = manager.get("8Bit_Operator.ttf", BitmapFont.class);
        
        // Get Sounds
        attackButtonEasterSound = manager.get("Attack_Button_EasterEgg.ogg", Sound.class);
        attackButtonSound = manager.get("Attack_Button.ogg", Sound.class);
        attackHitSound = manager.get("Attack_Hit.ogg", Sound.class);
        attackMissSound = manager.get("Attack_Miss.ogg", Sound.class);
        buttonClickedSound = manager.get("Button_Clicked.ogg", Sound.class);
        buttonHoverSound = manager.get("Button_Hover.ogg", Sound.class);
        clickShipSound = manager.get("Click_Ship.ogg", Sound.class);
        hoverShipSound = manager.get("Hover_Ship.ogg", Sound.class);
        hoverTileSound = manager.get("Hover_Tile.ogg", Sound.class);
        inputCode1Sound = manager.get("InputCode_1.ogg", Sound.class);
        inputCode2Sound = manager.get("InputCode_2.ogg", Sound.class);
        inputCode3Sound = manager.get("InputCode_3.ogg", Sound.class);
        inputBackspaceSound = manager.get("InputCode_Backspace.ogg", Sound.class);
        launchLine1Sound = manager.get("LaunchLine_1.ogg", Sound.class);
        launchLine2Sound = manager.get("LaunchLine_2.ogg", Sound.class);
        launchLine3Sound = manager.get("LaunchLine_3.ogg", Sound.class);
        launchLine4Sound = manager.get("LaunchLine_4.ogg", Sound.class);
        launchLine5Sound = manager.get("LaunchLine_5.ogg", Sound.class);
        shipRotateSound = manager.get("Ship_Rotate.ogg", Sound.class);
        shipPlacedInvalidSound = manager.get("ShipPlaced_Invalid.ogg", Sound.class);
        shipPlacedInvalid2Sound = manager.get("ShipPlaced_Invalid2.ogg", Sound.class);
        shipPlacedValidSound = manager.get("ShipPlaced_Valid.ogg", Sound.class);
        sunkenShipSound = manager.get("Sunken_Ship.ogg", Sound.class);
     }
     
     public boolean update() {
         return manager.update();
     }
     
     public float getProgress() {
         return manager.getProgress();
     }
     
     public boolean isFinished() {
         return manager.isFinished();
     }
     
     public void finishLoading() {
         manager.finishLoading();
     }
     
     public <T> T get(String fileName, Class<T> type) {
         return manager.get(fileName, type);
     }
     
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
     
     public void dispose() {
         manager.dispose();
         // Nullify references
         admiralsUiAtlas = null;
         operatorFont = null;
         
         // Nullify sounds
         attackButtonEasterSound = null;
         attackButtonSound = null;
         attackHitSound = null;
         attackMissSound = null;
         buttonClickedSound = null;
         buttonHoverSound = null;
         clickShipSound = null;
         hoverShipSound = null;
         hoverTileSound = null;
         inputCode1Sound = null;
         inputCode2Sound = null;
         inputCode3Sound = null;
         inputBackspaceSound = null;
         launchLine1Sound = null;
         launchLine2Sound = null;
         launchLine3Sound = null;
         launchLine4Sound = null;
         launchLine5Sound = null;
         shipRotateSound = null;
         shipPlacedInvalidSound = null;
         shipPlacedInvalid2Sound = null;
         shipPlacedValidSound = null;
         sunkenShipSound = null;
     }
}