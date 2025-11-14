package com.ThePod.Admirals;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;

// This is a Scene2D Actor for your UI buttons
public class UiButtonActor extends Actor {

    private TextureRegion inactiveFrame;
    private TextureRegion hoverFrame;
    private TextureRegion clickedFrame;
    private Sound clickSound;
    
    private boolean isHovered = false;
    private boolean isClicked = false;
    private boolean isDisabled = false;

    public UiButtonActor(TextureAtlas atlas, String inactive, String hover, String clicked) {
        this.inactiveFrame = atlas.findRegion(inactive);
        this.hoverFrame = atlas.findRegion(hover);
        this.clickedFrame = atlas.findRegion(clicked);
        
        // TODO: Load shoot sound
        // this.clickSound = AssetLoader.getInstance().getSound("sound.ogg");
        
        setSize(inactiveFrame.getRegionWidth(), inactiveFrame.getRegionHeight());
        
        // Add mouse listeners for desktop
        addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (!isDisabled) {
                    isHovered = true;
                }
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                isHovered = false;
                isClicked = false;
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (button == Input.Buttons.LEFT && !isDisabled) {
                    isClicked = true;
                    // if (clickSound != null) clickSound.play();
                    return true; // We handled this
                }
                return false;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (button == Input.Buttons.LEFT && !isDisabled && isClicked) {
                    isClicked = false;
                    // The "click" action fires on touchUp (handled by the ClickListener in PlayScreen)
                }
            }
        });
    }

    public void setDisabled(boolean isDisabled) {
        this.isDisabled = isDisabled;
        if (isDisabled) {
            isHovered = false;
            isClicked = false;
            setTouchable(Touchable.disabled);
        } else {
            setTouchable(Touchable.enabled);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        TextureRegion currentFrame = inactiveFrame;
        
        if (!isDisabled) {
            if (isClicked) {
                currentFrame = clickedFrame;
            } else if (isHovered) {
                currentFrame = hoverFrame;
            }
        }
        
        batch.draw(currentFrame, getX(), getY(), getWidth(), getHeight());
    }
}