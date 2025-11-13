package com.ThePod.Admirals;

// Import Statements
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.Gdx;

/**
 * Reusable Class For Handling UI Display Elements (non-interactive).
 * Handles both static single-frame regions and multi-frame looping animations.
 */
public class UiDisplay {

    // Local Variables
    private final Rectangle bounds;
    private final Viewport viewport;
    private final Animation<TextureRegion> animation;
    private TextureRegion staticFrame;
    private final boolean isAnimated;
    private final TextureAtlas atlas; // <-- New field to store the atlas

    private float stateTime = 0f;

    /**
     * Constructor: Handles both static and animated displays.
     * @param atlas The TextureAtlas containing the region(s).
     * @param regionName The base name of the region.
     * @param frameCount If 1, loads a single, non-numbered region. If > 1, loads numbered frames (e.g., "regionName_1", "regionName_2").
     * @param totalTime The total duration of one animation loop (if animated).
     * @param x The x position.
     * @param y The y position.
     * @param width The width.
     * @param height The height.
     * @param viewport The game's viewport.
     */
    public UiDisplay(TextureAtlas atlas, String regionName, int frameCount, float totalTime,
                     float x, float y, float width, float height, Viewport viewport) {

        this.atlas = atlas; // <-- Store the atlas
        this.bounds = new Rectangle(x, y, width, height);
        this.viewport = viewport;

        if (frameCount <= 1) {
            // This is a static, single-frame display
            this.isAnimated = false;
            this.animation = null;
            this.staticFrame = atlas.findRegion(regionName); // Find region by its exact name
            if (this.staticFrame == null) {
                Gdx.app.log("UiDisplay", "Could not find static region: " + regionName);
            }
        } else {
            // This is an animated display
            this.isAnimated = true;
            this.staticFrame = null;
            
            Array<TextureRegion> frames = new Array<>();
            for (int i = 1; i <= frameCount; i++) {
                TextureRegion frame = atlas.findRegion(regionName, i); // Find numbered frames
                if (frame != null) {
                    frames.add(frame);
                } else {
                    Gdx.app.log("UiDisplay", "Could not find animation frame: " + regionName + "_" + i);
                }
            }

            if (frames.size > 0) {
                // The old code used (frameCount - 1), which is typical for frame duration, but totalTime / frames.size is safer
                float frameDuration = totalTime / (float) frames.size; 
                this.animation = new Animation<>(frameDuration, frames, Animation.PlayMode.LOOP);
            } else {
                Gdx.app.log("UiDisplay", "No animation frames found for: " + regionName);
                this.animation = null; // Fallback
            }
        }
    }

    // Update Animation Frame (Only if Animated)
    public void update(float delta) {
        if (isAnimated) {
            stateTime += delta;
        }
    }

    // Render Call — Draws Static or Animated Frame
    public void render(SpriteBatch batch) {
        if (isAnimated && animation != null) {
            TextureRegion frame = animation.getKeyFrame(stateTime);
            if (frame != null) {
                batch.draw(frame, bounds.x, bounds.y, bounds.width, bounds.height);
            }
        } else if (staticFrame != null) {
            batch.draw(staticFrame, bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }

    /**
     * Allows changing the visual region (Only for Static Displays).
     * @param regionName The name of the new static region to display.
     */
    public void setRegion(String regionName) {
        if (!isAnimated) {
            // --- This is the improved part ---
            // It now uses the stored atlas instead of the old static Loader
            TextureRegion newRegion = this.atlas.findRegion(regionName);
            if (newRegion != null) {
                this.staticFrame = newRegion;
            } else {
                Gdx.app.log("UiDisplay", "setRegion failed, could not find: " + regionName);
            }
        }
    }

    // --- HOW TO USE ---
    //
    // In Local Variable
    // private UiDisplay mainTitle;
    //
    // In show()
    // // Example for an animated title with 4 frames
    // mainTitle = new UiDisplay(atlas, "Ui_MainTitle", 4, 1.0f, 220, 300, 800, 300, screenCamera.getViewport());
    // // Example for a static, non-animated image
    // // someImage = new UiDisplay(atlas, "Static_Image", 1, 0f, 100, 100, 50, 50, screenCamera.getViewport());
    //
    // In render()
    // mainTitle.update(delta);
    // mainTitle.render(batch); // In Draw Call/Batch
    //
    // To change a static image:
    // // someImage.setRegion("New_Static_Image");
}