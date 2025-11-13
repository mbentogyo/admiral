package com.ThePod.Admirals;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle; 
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ThePod.Admirals.board.CellState;

// Manages the 15x10 grid of tiles for ship placement
public class BoardPrepGrid {

    public static final int GRID_COLS = 15;
    public static final int GRID_ROWS = 10;
    
    private PrepGridTile[][] tiles; // Visual tiles
    private int[][] boardState;     // Logical state

    private static final float TILE_WIDTH = 32f;
    private static final float TILE_HEIGHT = 32f;
    
    // Grid position
    private float gridStartX;
    private float gridStartY;
    private Rectangle gridBounds;

    public BoardPrepGrid(TextureAtlas atlas, UiDisplay gameBoard) {
        // Initialize the logical board state
        boardState = new int[GRID_ROWS][GRID_COLS];
        
        // Initialize the visual tiles
        tiles = new PrepGridTile[GRID_ROWS][GRID_COLS];

        // This centers the 480x320 grid inside the 512x350 board
        gridStartX = gameBoard.getBounds().x + (gameBoard.getBounds().width - (GRID_COLS * TILE_WIDTH)) / 2;
        gridStartY = gameBoard.getBounds().y + (gameBoard.getBounds().height - (GRID_ROWS * TILE_HEIGHT)) / 2;
        gridBounds = new Rectangle(gridStartX, gridStartY, GRID_COLS * TILE_WIDTH, GRID_ROWS * TILE_HEIGHT);
        
        for (int row = 0; row < GRID_ROWS; row++) {
            for (int col = 0; col < GRID_COLS; col++) {
                // Set logical state (default to 1)
                boardState[row][col] = CellState.NONE.getValue(); 

                // We flip the Y-axis calculation (GRID_ROWS - 1 - row) so row 0 is at the top
                float tileX = gridStartX + (col * TILE_WIDTH);
                float tileY = gridStartY + ((GRID_ROWS - 1 - row) * TILE_HEIGHT);

                // Create the visual tile
                tiles[row][col] = new PrepGridTile(atlas, tileX, tileY, TILE_WIDTH, TILE_HEIGHT);
            }
        }
    }

    // Public getter for the game logic manager
    public int[][] getBoardState() {
        return boardState;
    }

    // Get the (row, col) from world coordinates (like mouse)
    public int[] getTileCoords(float worldX, float worldY) {
        if (!gridBounds.contains(worldX, worldY)) {
            return null; // Not on the grid
        }
        
        // Translate world coords to grid-relative coords
        float relativeX = worldX - gridStartX;
        float relativeY = worldY - gridStartY;
        
        int col = (int) (relativeX / TILE_WIDTH);
        // Invert Y-axis for row calculation (row 0 is at the top)
        int row = GRID_ROWS - 1 - (int) (relativeY / TILE_HEIGHT);

        // Clamp values just in case
        if (col < 0) col = 0;
        if (col >= GRID_COLS) col = GRID_COLS - 1;
        if (row < 0) row = 0;
        if (row >= GRID_ROWS) row = GRID_ROWS - 1;

        return new int[]{row, col};
    }

    // Check if a ship placement is valid
    public boolean isPlacementValid(int row, int col, int length, boolean isHorizontal) {
        for (int i = 0; i < length; i++) {
            int r = row;
            int c = col;
            
            if (isHorizontal) {
                c += i;
            } else {
                r += i;
            }
            
            // Check bounds
            if (r < 0 || r >= GRID_ROWS || c < 0 || c >= GRID_COLS) {
                return false; // Out of bounds
            }
            
            // Check overlap
            if (boardState[r][c] != CellState.NONE.getValue()) {
                return false; // Overlaps another ship
            }
        }
        return true; // All tiles are valid
    }

    // Show the placement preview (GOOD or BAD)
    public void showPreview(int row, int col, int length, boolean isHorizontal) {
        boolean isValid = isPlacementValid(row, col, length, isHorizontal);
        String state = isValid ? "GOOD" : "BAD";
        
        for (int i = 0; i < length; i++) {
            int r = row;
            int c = col;
            
            if (isHorizontal) {
                c += i;
            } else {
                r += i;
            }
            
            // Set tile visual state (handles out-of-bounds check internally)
            setTileState(r, c, -1, state); // Use -1 as a temporary logical state
        }
    }

    // Reset all preview tiles back to their real state
    public void clearPreview() {
        for (int row = 0; row < GRID_ROWS; row++) {
            for (int col = 0; col < GRID_COLS; col++) {
                if (boardState[row][col] == CellState.NONE.getValue()) {
                    tiles[row][col].setTexture("NEUTRAL");
                }
            }
        }
    }
    
    // Place a ship, updating the logical state and visual tiles
    public void placeShip(int row, int col, int length, boolean isHorizontal, int cellStateValue) {
        for (int i = 0; i < length; i++) {
            int r = row;
            int c = col;
            
            if (isHorizontal) {
                c += i;
            } else {
                r += i;
            }
            
            // Set permanent state
            setTileState(r, c, cellStateValue, "OCCUPIED");
        }
    }
    
    // Clear a ship's position from the board (when it's picked up)
    public void clearShip(PrepareShip ship) {
        int cellValue = ship.getCellStateValue();
        for (int row = 0; row < GRID_ROWS; row++) {
            for (int col = 0; col < GRID_COLS; col++) {
                if (boardState[row][col] == cellValue) {
                    setTileState(row, col, CellState.NONE.getValue(), "NEUTRAL");
                }
            }
        }
    }

    // Public method to change a tile's logical AND visual state
    public void setTileState(int row, int col, int stateValue, String visualState) {
        if (row < 0 || row >= GRID_ROWS || col < 0 || col >= GRID_COLS) {
            return; // Out of bounds
        }
        // Only set logical state if it's not a temporary preview
        if (stateValue != -1) { 
            boardState[row][col] = stateValue;
        }
        tiles[row][col].setTexture(visualState);
    }
    
    // Getters for grid position
    public float getGridStartX() { return gridStartX; }
    public float getGridStartY() { return gridStartY; }

    public void render(SpriteBatch batch) {
        for (int row = 0; row < GRID_ROWS; row++) {
            for (int col = 0; col < GRID_COLS; col++) {
                tiles[row][col].render(batch);
            }
        }
    }
}