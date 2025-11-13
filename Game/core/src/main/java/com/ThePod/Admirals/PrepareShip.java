package com.ThePod.Admirals;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.audio.Sound;
import com.ThePod.Admirals.board.CellState;

// Represents a single, draggable ship on the PrepareShipsScreen
public class PrepareShip {

    private TextureRegion texture;
    private Rectangle bounds;
    private Viewport viewport;

    private float homeX, homeY; // The position to snap back to
    private float homeWidth, homeHeight; // Original dimensions

    public boolean isDragging = false;
    public boolean isPlaced = false;
    public boolean isHorizontal = true;

    private float dragOffsetX, dragOffsetY;
    
    // Ship-specific properties
    private int shipLength;
    private int cellStateValue;
    
    // Sound effects
    private Sound hoverShipSound;
    private Sound clickShipSound;
    private Sound shipRotateSound;
    private Sound shipPlacedValidSound;
    private Sound shipPlacedInvalidSound;
    private Sound shipPlacedInvalid2Sound;
    private boolean wasHoveredLastFrame = false;
    private static int invalidSoundIndex = 0; // Static to alternate between all ships

    public PrepareShip(TextureRegion texture, float x, float y, float width, float height, Viewport viewport, int shipLength, int cellStateValue) {
        this.texture = texture;
        this.viewport = viewport;
        
        // Store home state
        this.homeX = x;
        this.homeY = y;
        this.homeWidth = width;
        this.homeHeight = height;
        
        this.bounds = new Rectangle(x, y, width, height);
        
        // Store game logic properties
        this.shipLength = shipLength;
        this.cellStateValue = cellStateValue;
        
        // Load hover sound
        AssetLoader assets = AssetLoader.getInstance();
        hoverShipSound = assets.hoverShipSound;
        clickShipSound = assets.clickShipSound;
        shipRotateSound = assets.shipRotateSound;
        shipPlacedValidSound = assets.shipPlacedValidSound;
        shipPlacedInvalidSound = assets.shipPlacedInvalidSound;
        shipPlacedInvalid2Sound = assets.shipPlacedInvalid2Sound;
        clickShipSound = assets.clickShipSound;
        shipRotateSound = assets.shipRotateSound;
    }

    // Update method - call this every frame to check for hover
    public void update(float delta) {
        // Get mouse position
        Vector2 mouse = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        viewport.unproject(mouse);
        
        // Check if mouse is over the ship
        boolean isHovered = bounds.contains(mouse);
        
        // Play sound only when first hovering (not dragging)
        if (isHovered && !wasHoveredLastFrame && !isDragging) {
            if (hoverShipSound != null) {
                hoverShipSound.play(0.5f);
            }
        }
        
        wasHoveredLastFrame = isHovered;
    }

    // Called by the screen when dragging
    public void updateDragPosition(Vector2 mouse) {
        bounds.x = mouse.x - dragOffsetX;
        bounds.y = mouse.y - dragOffsetY;
    }
    
    // Called when the ship is placed on the grid
    public void placeAt(float gridSnapX, float gridSnapY) {
        this.isDragging = false;
        this.isPlaced = true;
        this.bounds.setPosition(gridSnapX, gridSnapY);
        
        // Play valid placement sound
        if (shipPlacedValidSound != null) {
            shipPlacedValidSound.play(0.7f);
        }
    }
    
    // Called when a ship is dropped in an invalid spot
    public void snapToHome() {
        this.isDragging = false;
        this.isPlaced = false;
        this.bounds.setPosition(homeX, homeY);
        
        // Reset rotation and bounds
        this.isHorizontal = true;
        this.bounds.setSize(homeWidth, homeHeight);
        
        // Play invalid placement sound (alternating between two sounds)
        if (invalidSoundIndex == 0) {
            if (shipPlacedInvalidSound != null) {
                shipPlacedInvalidSound.play(0.7f);
            }
        } else {
            if (shipPlacedInvalid2Sound != null) {
                shipPlacedInvalid2Sound.play(0.7f);
            }
        }
        
        // Toggle between 0 and 1 for next time
        invalidSoundIndex = (invalidSoundIndex + 1) % 2;
    }
    
    // Called on right-click
    public void rotate() {
        this.isHorizontal = !this.isHorizontal;
        
        // Swap logical bounds dimensions for rotation
        float oldWidth = bounds.width;
        bounds.width = bounds.height;
        bounds.height = oldWidth;
        
        // Play rotate sound
        if (shipRotateSound != null) {
            shipRotateSound.play(0.6f);
        }
    }

    // Call this *inside* batch.begin()
    public void render(SpriteBatch batch) {
        if (texture != null) {
            
            if (isHorizontal) {
                // Draw normally
                batch.draw(texture,
                        bounds.x, bounds.y,
                        0, 0, // origin
                        homeWidth, homeHeight,
                        1, 1,
                        0); // angle
            } else {
                // Draw rotated
                // We must offset the x-position by the texture's height
                // to align the new top-left corner with the bounds.
                batch.draw(texture,
                        bounds.x + homeHeight, bounds.y, // new draw position
                        0, 0, // origin
                        homeWidth, homeHeight,
                        1, 1,
                        90); // angle
            }
        }
    }
    
    // Getters for logic
    public Rectangle getBounds() { return bounds; }
    public boolean isDragging() { return isDragging; }
    public boolean isPlaced() { return isPlaced; }
    public boolean isHorizontal() { return isHorizontal; }
    public int getLength() { return shipLength; }
    public int getCellStateValue() { return cellStateValue; }

    // Setters for screen controller
    public void setDragging(boolean dragging, Vector2 mouse) {
        this.isDragging = dragging;
        if (dragging) {
            // Calculate offset from the ship's logical bounds corner
            this.dragOffsetX = mouse.x - bounds.x;
            this.dragOffsetY = mouse.y - bounds.y;
            
            // Play click sound when starting to drag
            if (clickShipSound != null) {
                clickShipSound.play(0.6f);
            }
        }
    }

    public void setPlaced(boolean placed) {
        this.isPlaced = placed;
    }
}