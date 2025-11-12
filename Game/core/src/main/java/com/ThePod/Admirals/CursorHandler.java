package com.ThePod.Admirals;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

// Custom Cursor Handler Class
// This class handles setting the custom cursor
public class CursorHandler extends InputAdapter {

    // Constructor
    // Initializes the custom cursor
    public CursorHandler() {
        // Load the custom cursor from the atlas via the AssetLoader
        TextureAtlas atlas = AssetLoader.getInstance().admiralsUiAtlas; // This will now be resolved
        
        // Make sure the atlas was loaded
        if (atlas == null) {
            Gdx.app.log("CursorHandler", "Admirals_Ui.atlas not loaded!");
            return;
        }

        TextureRegion cursorRegion = atlas.findRegion("Cursor");

        if (cursorRegion != null) {
            // Create a Pixmap for the custom cursor
            Texture cursorTexture = cursorRegion.getTexture();

            // Prepare the texture data if it's not already
            if (!cursorTexture.getTextureData().isPrepared()) {
                cursorTexture.getTextureData().prepare();
            }

            // Create a new pixmap for our cursor
            Pixmap pixmap = new Pixmap(cursorRegion.getRegionWidth(), cursorRegion.getRegionHeight(), Pixmap.Format.RGBA8888);

            // Copy the texture region's pixels from the atlas's pixmap into our new pixmap
            pixmap.drawPixmap(
                    cursorTexture.getTextureData().consumePixmap(), // The atlas's full pixmap
                    0, 0,                                         // Destination X, Y in our new pixmap
                    cursorRegion.getRegionX(),                    // Source X in the atlas pixmap
                    cursorRegion.getRegionY(),                    // Source Y in the atlas pixmap
                    cursorRegion.getRegionWidth(),                // Source Width
                    cursorRegion.getRegionHeight()                // Source Height
            );

            // Set the custom cursor with the top-left pixel (0,0) as the click point
            Gdx.graphics.setCursor(Gdx.graphics.newCursor(pixmap, 0, 0));
            
            // Dispose of the Pixmap after setting the cursor
            // The cursor is now managed by the system, so we can free this
            pixmap.dispose();
        } else {
            Gdx.app.log("CursorHandler", "Could not find 'Cursor' region in atlas!");
        }
    }
}