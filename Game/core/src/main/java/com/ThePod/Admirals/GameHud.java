package com.ThePod.Admirals;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

// The HUD is a Scene2D Group that holds the Fire button and status label
public class GameHud extends Group {

    private Label statusLabel;
    private UiButtonActor fireButton;

    public GameHud(TextureAtlas atlas, BitmapFont font) {
        // Create the status label
        LabelStyle style = new LabelStyle(font, Color.WHITE);
        statusLabel = new Label("Connecting...", style);
        statusLabel.setPosition(40, 680); // Position at top-left
        
        // Create the fire button
        fireButton = new UiButtonActor(atlas, "Attack_Inactive", "Attack_Hovered", "Attack_Clicked");
        fireButton.setPosition(1100, 300); // Position on the right side
        
        // Add them to this group
        addActor(statusLabel);
        addActor(fireButton);
    }
    
    public void showMessage(String message) {
        statusLabel.setText(message);
    }
    
    public UiButtonActor getFireButton() {
        return fireButton;
    }
}