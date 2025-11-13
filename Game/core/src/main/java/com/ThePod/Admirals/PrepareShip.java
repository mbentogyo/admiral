package com.ThePod.Admirals;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
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
    }
    
    // Called when a ship is dropped in an invalid spot
    public void snapToHome() {
        this.isDragging = false;
        this.isPlaced = false;
        this.bounds.setPosition(homeX, homeY);
        
        // Reset rotation and bounds
        this.isHorizontal = true;
        this.bounds.setSize(homeWidth, homeHeight);
    }
    
    // Called on right-click
    public void rotate() {
        this.isHorizontal = !this.isHorizontal;
        
        // Swap logical bounds dimensions for rotation
        float oldWidth = bounds.width;
        bounds.width = bounds.height;
        bounds.height = oldWidth;
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
        }
    }

    public void setPlaced(boolean placed) {
        this.isPlaced = placed;
    }
}