package com.ThePod.Admirals;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;

// Reusable Class For Handling static or animated UI Display Elements
public class UiDisplay {

    // Local Variables
    private final Rectangle bounds;
    private final Viewport viewport;
    private final Animation<TextureRegion> animation;
    private TextureRegion staticFrame;
    private final boolean isAnimated;
    private final TextureAtlas atlas; // Store atlas for setRegion

    private float stateTime = 0f;

    // Constructor 1: Handles animated displays (using numbered frames) or single static (non-indexed) displays
    public UiDisplay(TextureAtlas atlas, String regionName, int frameCount, float totalTime,
                       float x, float y, float width, float height, Viewport viewport) {

        this.atlas = atlas; // Store atlas
        Array<TextureRegion> frames = new Array<>();
        
        if (frameCount <= 1) {
            // Handle single static, non-indexed frame
            TextureRegion frame = atlas.findRegion(regionName); // No index
            if (frame != null) {
                frames.add(frame);
            }
        } else {
            // Handle animation
            for (int i = 1; i <= frameCount; i++) {
                TextureRegion frame = atlas.findRegion(regionName, i);
                if (frame != null) {
                    frames.add(frame);
                }
            }
        }

        this.bounds = new Rectangle(x, y, width, height);
        this.viewport = viewport;

        if (frames.size > 1) {
            // It's animated
            this.isAnimated = true;
            this.animation = new Animation<>(totalTime / (frameCount - 1), frames, Animation.PlayMode.LOOP);
            this.staticFrame = null;
        } else {
            // It's static
            this.isAnimated = false;
            this.animation = null;
            this.staticFrame = frames.size > 0 ? frames.get(0) : null;
        }
    }

    // Constructor 2: Handles a single STATIC display using a specific INDEX
    public UiDisplay(TextureAtlas atlas, String regionName, int index,
                       float x, float y, float width, float height, Viewport viewport) {
        
        this.atlas = atlas; // Store atlas
        this.bounds = new Rectangle(x, y, width, height);
        this.viewport = viewport;
        this.isAnimated = false; // This constructor is for static indexed images
        this.animation = null;
        this.staticFrame = atlas.findRegion(regionName, index); // Find the specific indexed region
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

    // Public getter for the bounds, needed by BoardPrepGrid
    public Rectangle getBounds() {
        return this.bounds;
    }

    // Allows changing the visual region (Only for Static Displays)
    public void setRegion(String regionName, int index) {
        if (!isAnimated && this.atlas != null) {
            TextureRegion newRegion = atlas.findRegion(regionName, index); // Indexed
            if (newRegion != null) {
                this.staticFrame = newRegion;
            }
        }
    }
}