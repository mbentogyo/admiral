package com.ThePod.Admirals;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

// Manages a single indicator light that can toggle between red and green
public class IndicatorLight {

    private TextureRegion redTexture;
    private TextureRegion greenTexture;
    private TextureRegion currentTexture;
    private Rectangle bounds;

    public IndicatorLight(TextureAtlas atlas, float x, float y, float width, float height) {
        this.redTexture = atlas.findRegion("Light_Red");
        this.greenTexture = atlas.findRegion("Light_Green");
        
        // Default to red
        this.currentTexture = redTexture;
        
        this.bounds = new Rectangle(x, y, width, height);
    }

    // Call this to change the light's color
    public void setGreen(boolean isGreen) {
        if (isGreen) {
            currentTexture = greenTexture;
        } else {
            currentTexture = redTexture;
        }
    }

    public void render(SpriteBatch batch) {
        if (currentTexture != null) {
            batch.draw(currentTexture, bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }
}