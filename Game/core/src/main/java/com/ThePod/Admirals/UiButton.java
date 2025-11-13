package com.ThePod.Admirals;

import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.audio.Sound;  // Add this import

public class UiButton {

    private final Animation<TextureRegion> hoverAnimation;
    private final TextureRegion idleFrame;
    private final Rectangle bounds;
    private final Viewport viewport;
    private float hoverTime = 0f;
    private boolean isHovered = false;
    private boolean hasPlayed = false;
    private boolean wasHoveredLastFrame = false;

    private Sound hoverSound;  // Add this field
    private Sound clickSound;  // Add this field
    private Runnable onClick;
    
    public UiButton(TextureAtlas atlas, String idleRegionName, String hoverRegionName, float totalTime,
                    float x, float y, float width, float height, Viewport viewport) {
        
        idleFrame = atlas.findRegion(idleRegionName);
        TextureRegion hoverFrame = atlas.findRegion(hoverRegionName);
        
        if (idleFrame == null) {
            Gdx.app.log("UiButton", "Could not find idle region: " + idleRegionName);
        }
        if (hoverFrame == null) {
            Gdx.app.log("UiButton", "Could not find hover region: " + hoverRegionName);
        }

        Array<TextureRegion> frames = new Array<>();
        if (idleFrame != null) {
            frames.add(idleFrame);
        }
        if (hoverFrame != null) {
            frames.add(hoverFrame);
        }

        if (frames.size > 1) {
            hoverAnimation = new Animation<>(totalTime, frames, Animation.PlayMode.NORMAL);
        } else {
            Gdx.app.log("UiButton", "Animation requires two valid regions.");
            hoverAnimation = new Animation<>(0, idleFrame);
        }

        bounds = new Rectangle(x, y, width, height);
        this.viewport = viewport;

        // Load default sounds from AssetLoader
        AssetLoader assets = AssetLoader.getInstance();
        hoverSound = assets.buttonHoverSound;  // Changed from getSound()
        clickSound = assets.buttonClickedSound;  // Changed from getSound()
    }

    public void setOnClick(Runnable onClick) {
        this.onClick = onClick;
    }

    public void setHoverSound(Sound hoverSound) {
        this.hoverSound = hoverSound;
    }

    public void setClickSound(Sound clickSound) {
        this.clickSound = clickSound;
    }

    public void update(float delta) {
        Vector2 mouse = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        viewport.unproject(mouse);

        if (bounds.contains(mouse)) {
            isHovered = true;
            
            // Play hover sound only once when first hovering
            if (!wasHoveredLastFrame && hoverSound != null) {
                hoverSound.play(0.5f);
            }
            
            if (!hasPlayed) {
                hoverTime += delta;
            }

            if (hoverAnimation.getAnimationDuration() > 0 && hoverAnimation.isAnimationFinished(hoverTime)) {
                hoverTime = hoverAnimation.getAnimationDuration();
                hasPlayed = true;
            }

            if (Gdx.input.justTouched() && onClick != null) {
                if (clickSound != null) clickSound.play(0.7f);
                onClick.run();
            }

            wasHoveredLastFrame = true;  // Add this
        } else {
            isHovered = false;
            hoverTime = 0;
            hasPlayed = false;
            wasHoveredLastFrame = false;  // Add this
        }
    }

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