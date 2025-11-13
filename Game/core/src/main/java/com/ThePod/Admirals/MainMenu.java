package com.ThePod.Admirals;


import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport; 

public class MainMenu implements Screen {

    private Main game;
    private Texture background;

    //Ui Elements & Local Variables
    private UiButton playButton;
    private UiButton exitButton;
    private UiDisplay centerPiece; 
    
    public MainMenu(Main game) {
        this.game = game;
    }
    
    @Override
    public void show() {
        // Load the background texture when screen is shown
        background = AssetLoader.getInstance().getTexture("Menu_Frame.png");
        TextureAtlas atlas = AssetLoader.getInstance().admiralsUiAtlas;
        Viewport viewport = game.screenCamera.getViewport();

        // Instantiate & Postion Ui Elements Here
        centerPiece = new UiDisplay(atlas, "CenterPiece", 1, 0f, 710, 300, 250, 250, viewport);
        playButton = new UiButton(atlas, "Play_Inactive", "Play_Active", 0.2f, 750, 220, 160, 60, viewport);
        exitButton = new UiButton(atlas, "Exit_Inactive", "Exit_Active", 0.2f, 750, 150, 160, 60, viewport);


        // Click Handlers
        playButton.setOnClick(() -> {
            game.setScreen(new ConnectMenu(game));
        });
    
        exitButton.setOnClick(() -> {
            // Empty click handler
        });
        
    }
    
    @Override
    public void render(float delta) {
        // Clear screen
        ScreenUtils.clear(0, 0, 0, 1);
        
        // Update the camera
        game.screenCamera.update();

        // This updates the button's hover state and checks for clicks
        playButton.update(delta);
        exitButton.update(delta);
        centerPiece.update(delta); 

        // Tell the SpriteBatch to use the camera's view
        game.batch.setProjectionMatrix(game.screenCamera.getCamera().combined);

        // Draw background
        game.batch.begin();
        game.batch.draw(background, 0, 0, ScreenCamera.WORLD_WIDTH, ScreenCamera.WORLD_HEIGHT);

        // Draw Ui Objects
        centerPiece.render(game.batch); 
        playButton.render(game.batch);
        exitButton.render(game.batch); 

        game.batch.end();
    }
    
    @Override
    public void resize(int width, int height) {
        // Update the viewport when the window is resized
        game.screenCamera.resize(width, height);
    }
    
    @Override
    public void hide() {
    }
    
    @Override
    public void dispose() {
        // Don't dispose the texture here - AssetLoader manages it
    }

    // Unused methods
    @Override public void pause() {}     // Not used but required by Screen interface
    @Override public void resume() {}    // Not used but required by Screen interface
}