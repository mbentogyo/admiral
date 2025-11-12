// This is the animated version, corrected to use two specific region names.
package com.ThePod.Admirals;

// Import Statements
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.Gdx;

// Reusable Class For Handling UI Buttons And Their Animations
// This version animates between two *named* regions.
public class UiButton {

    // Local Variables
    private final Animation<TextureRegion> hoverAnimation;
    private final TextureRegion idleFrame;
    private final Rectangle bounds;
    private final Viewport viewport;
    private float hoverTime = 0f;
    private boolean isHovered = false;
    private boolean hasPlayed = false;

    private Runnable onClick; // Function to call when the button is clicked
    
    /**
     * Constructor for UiButton
     * Initializes the button and creates an animation between two named frames.
     * @param atlas The TextureAtlas containing the regions.
     * @param idleRegionName The name of the region for the idle (un-hovered) state.
     * @param hoverRegionName The name of the region for the final (hovered) state.
     * @param totalTime The time it takes to animate from idle to hover.
     * @param x The x position.
     * @param y The y position.
     * @param width The width.
     * @param height The height.
     * @param viewport The game's viewport for coordinate unprojection.
     */
    public UiButton(TextureAtlas atlas, String idleRegionName, String hoverRegionName, float totalTime,
                    float x, float y, float width, float height, Viewport viewport) {
        
        idleFrame = atlas.findRegion(idleRegionName);
        TextureRegion hoverFrame = atlas.findRegion(hoverRegionName);
        
        // Ensure frames were found
        if (idleFrame == null) {
            Gdx.app.log("UiButton", "Could not find idle region: " + idleRegionName);
        }
        if (hoverFrame == null) {
            Gdx.app.log("UiButton", "Could not find hover region: " + hoverRegionName);
        }

        // Create an animation array with the two frames
        Array<TextureRegion> frames = new Array<>();
        if (idleFrame != null) {
            frames.add(idleFrame);
        }
        if (hoverFrame != null) {
            frames.add(hoverFrame);
        }

        // Create the animation
        // The totalTime is the duration for one frame to the next
        if (frames.size > 1) {
            hoverAnimation = new Animation<>(totalTime, frames, Animation.PlayMode.NORMAL);
        } else {
            // Fallback if frames are missing
            Gdx.app.log("UiButton", "Animation requires two valid regions.");
            hoverAnimation = new Animation<>(0, idleFrame); // Empty animation, just hold idle
        }

        bounds = new Rectangle(x, y, width, height);
        this.viewport = viewport;
    }

    // Trigger the onClick function when the button is clicked 
    public void setOnClick(Runnable onClick) {
        this.onClick = onClick;
    }

    // Call this from your Screen's render method
    public void update(float delta) {
        // Create a vector to hold mouse coordinates
        Vector2 mouse = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        
        // Convert screen coordinates to world/viewport coordinates
        viewport.unproject(mouse);

        if (bounds.contains(mouse)) {
            isHovered = true;
            if (!hasPlayed) {
                hoverTime += delta;
            }

            // Check if animation is finished
            if (hoverAnimation.getAnimationDuration() > 0 && hoverAnimation.isAnimationFinished(hoverTime)) {
                hoverTime = hoverAnimation.getAnimationDuration(); // Clamp
                hasPlayed = true;
            }

            // Click trigger
            if (Gdx.input.justTouched() && onClick != null) {
                onClick.run();
            }

        } else {
            isHovered = false;
            hoverTime = 0;
            hasPlayed = false;
        }
    }

    // Call this from your Screen's render method (inside batch.begin/end)
    public void render(SpriteBatch batch) {
        if (isHovered && (hoverTime > 0 || hasPlayed)) {
            TextureRegion frame = hoverAnimation.getKeyFrame(hoverTime);
            if (frame != null && frame.getTexture() != null) {
                batch.draw(frame, bounds.x, bounds.y, bounds.width, bounds.height);
            }
        } else {
            if (idleFrame != null && idleFrame.getTexture() != null) {
                batch.draw(idleFrame, bounds.x, bounds.y, bounds.width, bounds.height);
            }
        }
    }
}