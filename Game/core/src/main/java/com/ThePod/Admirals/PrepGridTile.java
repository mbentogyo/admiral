package com.ThePod.Admirals;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

// Manages a single tile on the preparation board
public class PrepGridTile {

    // All possible textures a tile can have
    private TextureRegion texNeutral;
    private TextureRegion texHover;
    private TextureRegion texGood;
    private TextureRegion texBad;
    private TextureRegion texOccupied;

    // The current texture to draw
    private TextureRegion currentTexture;
    private Rectangle bounds;

    public PrepGridTile(TextureAtlas atlas, float x, float y, float width, float height) {
        // Load all 5 tile textures from the atlas
        this.texNeutral = atlas.findRegion("PrepTile_Neutral");
        this.texHover = atlas.findRegion("PrepTile_Hover");
        this.texGood = atlas.findRegion("PrepTile_Good");
        this.texBad = atlas.findRegion("PrepTile_Bad");
        this.texOccupied = atlas.findRegion("PrepTile_Occupied");

        this.bounds = new Rectangle(x, y, width, height);
        
        // Default state is Neutral
        this.currentTexture = texNeutral;
    }

    public void render(SpriteBatch batch) {
        if (currentTexture != null) {
            batch.draw(currentTexture, bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }

    // Allows the grid manager to change this tile's appearance
    public void setTexture(String state) {
        switch (state) {
            case "HOVER":
                currentTexture = texHover;
                break;
            case "GOOD":
                currentTexture = texGood;
                break;
            case "BAD":
                currentTexture = texBad;
                break;
            case "OCCUPIED":
                currentTexture = texOccupied;
                break;
            case "NEUTRAL":
            default:
                currentTexture = texNeutral;
                break;
        }
    }

    public Rectangle getBounds() {
        return bounds;
    }
}