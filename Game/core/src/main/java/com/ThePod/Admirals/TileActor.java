package com.ThePod.Admirals;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;
import com.ThePod.Admirals.board.CellState;

// Represents a single tile on the board as a Scene2D Actor
public class TileActor extends Actor {

    // Animations
    private Animation<TextureRegion> waterAnimation;
    private Animation<TextureRegion> deadWaterAnimation;
    private Animation<TextureRegion> aimAnimation;
    private Animation<TextureRegion> fireAnimation;
    private Animation<TextureRegion> splashAnimation;

    // State
    private Animation<TextureRegion> baseAnimation;
    private TextureRegion shipTexture; 
    private Animation<TextureRegion> overlayAnimation; 
    private boolean isPlayingEffect = false;
    private float baseTime = 0f;
    private float overlayTime = 0f;
    private float effectTime = 0f;
    private Runnable onEffectComplete = null;

    public final int row;
    public final int col;

    public TileActor(int row, int col, TextureAtlas atlas, boolean isMyBoard, int cellStateValue) {
        this.row = row;
        this.col = col;
        
        // Load all 5 animations
        loadAnimations(atlas);
        
        if (isMyBoard) {
            this.baseAnimation = waterAnimation;
            if (cellStateValue > CellState.NONE.getValue()) {
                // This tile has a ship on it
                this.shipTexture = getShipTextureFromState(cellStateValue, atlas);
                // We assume ships are not rotated here, as the int[][] has no rotation data
            }
        } else {
            // This is an enemy board tile
            this.baseAnimation = deadWaterAnimation;
        }

        setSize(32, 32);
    }

    private void loadAnimations(TextureAtlas atlas) {
        // Water (1-32 @ 100ms)
        Array<TextureRegion> waterFrames = new Array<>();
        for (int i = 1; i <= 32; i++) waterFrames.add(atlas.findRegion("Water", i));
        waterAnimation = new Animation<>(0.1f, waterFrames, Animation.PlayMode.LOOP);

        // DeadWater (1-32 @ 100ms)
        Array<TextureRegion> deadWaterFrames = new Array<>();
        for (int i = 1; i <= 32; i++) deadWaterFrames.add(atlas.findRegion("DeadWater", i));
        deadWaterAnimation = new Animation<>(0.1f, deadWaterFrames, Animation.PlayMode.LOOP);
        
        // Aim (1-2 @ 150ms = 300ms total)
        Array<TextureRegion> aimFrames = new Array<>();
        aimFrames.add(atlas.findRegion("Aim", 1));
        aimFrames.add(atlas.findRegion("Aim", 2));
        aimAnimation = new Animation<>(0.15f, aimFrames, Animation.PlayMode.LOOP);

        // Fire (1-3 @ 100ms)
        Array<TextureRegion> fireFrames = new Array<>();
        for (int i = 1; i <= 3; i++) fireFrames.add(atlas.findRegion("Fire", i));
        fireAnimation = new Animation<>(0.1f, fireFrames, Animation.PlayMode.LOOP);
        
        // Splash (1-10 @ 100ms, one-shot)
        Array<TextureRegion> splashFrames = new Array<>();
        for (int i = 1; i <= 10; i++) splashFrames.add(atlas.findRegion("Splash", i));
        splashAnimation = new Animation<>(0.1f, splashFrames, Animation.PlayMode.NORMAL);
    }

    // This is needed to show the player's ships
    private TextureRegion getShipTextureFromState(int cellStateValue, TextureAtlas atlas) {
        if (cellStateValue == CellState.CARRIER.getValue()) return atlas.findRegion("Carrier");
        if (cellStateValue == CellState.CORVETTE.getValue()) return atlas.findRegion("Corvette");
        if (cellStateValue == CellState.DESTROYER.getValue()) return atlas.findRegion("Destroyer");
        if (cellStateValue == CellState.FRIGATE.getValue()) return atlas.findRegion("Frigate");
        if (cellStateValue == CellState.SUBMARINE.getValue()) return atlas.findRegion("Submarine");
        if (cellStateValue == CellState.PATROL_BOAT.getValue()) return atlas.findRegion("PatrolBoat");
        return null;
    }

    // Select this tile (show aim animation)
    public void setSelected(boolean isSelected) {
        if (isSelected) {
            this.overlayAnimation = aimAnimation;
            this.overlayTime = 0f;
        } else {
            if (this.overlayAnimation == aimAnimation) {
                this.overlayAnimation = null; // Stop aiming
            }
        }
    }

    // Play Hit (Splash -> Fire)
    public void playHitAnimation(Runnable onComplete) {
        setSelected(false); // Remove aimer
        playSplash(() -> {
            this.overlayAnimation = fireAnimation;
            this.overlayTime = 0f;
            onComplete.run();
        });
    }

    // Play Miss (Splash -> Water)
    public void playMissAnimation(Runnable onComplete) {
        setSelected(false); // Remove aimer
        playSplash(() -> {
            this.baseAnimation = waterAnimation;
            onComplete.run();
        });
    }

    // Play the one-shot splash animation
    private void playSplash(Runnable onComplete) {
        this.onEffectComplete = onComplete;
        this.isPlayingEffect = true;
        this.effectTime = 0f;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        baseTime += delta;
        
        if (overlayAnimation != null) {
            overlayTime += delta;
        }
        
        if (isPlayingEffect) {
            effectTime += delta;
            if (splashAnimation.isAnimationFinished(effectTime)) {
                isPlayingEffect = false;
                effectTime = 0;
                if (onEffectComplete != null) {
                    onEffectComplete.run();
                    onEffectComplete = null;
                }
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        // Layer 1: Base Tile (Water or DeadWater)
        batch.draw(baseAnimation.getKeyFrame(baseTime, true), getX(), getY(), getWidth(), getHeight());

        // Layer 2: Ship (if it's my board)
        if (shipTexture != null) {
            // This does not account for rotation, as the int[][] has no rotation data
            batch.draw(shipTexture, getX(), getY(), getWidth(), getHeight());
        }

        // Layer 3: Overlay (Aim or Fire)
        if (overlayAnimation != null) {
            TextureRegion overlayFrame = overlayAnimation.getKeyFrame(overlayTime, true);
            batch.draw(overlayFrame, 
                getX() + (getWidth() - overlayFrame.getRegionWidth()) / 2, 
                getY() + (getHeight() - overlayFrame.getRegionHeight()) / 2
            );
        }

        // Layer 4: Effect (Splash)
        if (isPlayingEffect) {
            TextureRegion effectFrame = splashAnimation.getKeyFrame(effectTime, false);
            batch.draw(effectFrame, 
                getX() + (getWidth() - effectFrame.getRegionWidth()) / 2, 
                getY() + (getHeight() - effectFrame.getRegionHeight()) / 2
            );
        }
    }
}