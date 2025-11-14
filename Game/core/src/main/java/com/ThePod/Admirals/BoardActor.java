package com.ThePod.Admirals;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.ThePod.Admirals.board.AttackResult;
import com.ThePod.Admirals.board.Coordinates;
import com.ThePod.Admirals.board.MyBoard; // My Board
import com.ThePod.Admirals.board.CellState; // Cell State Enum

// Represents one 15x10 game board (player or enemy)
// This is a Scene2D Group that holds the Board sprite and 150 TileActors
public class BoardActor extends Group {

    public static final int GRID_COLS = 15;
    public static final int GRID_ROWS = 10;
    public static final float TILE_WIDTH = 32f;
    public static final float TILE_HEIGHT = 32f;

    private TextureRegion boardBackground;
    private TileActor[][] tiles;
    private TileActor selectedTile = null;

    public BoardActor(TextureAtlas atlas, PlayScreen playScreen, boolean isMyBoard) {
        this.tiles = new TileActor[GRID_ROWS][GRID_COLS];
        this.boardBackground = atlas.findRegion("Board");

        // Set the size of this actor to the board background
        float boardWidth = 512f;
        float boardHeight = 350f;
        setSize(boardWidth, boardHeight);

        // Calculate the grid's starting position (bottom-left) relative to this Actor
        float gridStartX = (boardWidth - (GRID_COLS * TILE_WIDTH)) / 2;
        float gridStartY = (boardHeight - (GRID_ROWS * TILE_HEIGHT)) / 2;

        int[][] initialLayout = null;
        if (isMyBoard) {

            // TODO: Need a getter in GameManager to access myBoard.
            // Using a blank layout for now until the getter exists.
            initialLayout = GameManager.getMyBoard();
            // int[][] initialLayout = GameManager.getMyBoard().getGrid();
            // once available
        }

        // Create and add all 150 tiles
        for (int row = 0; row < GRID_ROWS; row++) {
            for (int col = 0; col < GRID_COLS; col++) {

                int stateValue = (initialLayout != null) ? initialLayout[row][col] : CellState.NONE.getValue();
                final TileActor tile = new TileActor(row, col, atlas, isMyBoard, stateValue);

                // Position the tile
                float tileX = gridStartX + (col * TILE_WIDTH);
                float tileY = gridStartY + ((GRID_ROWS - 1 - row) * TILE_HEIGHT); // Flipped Y
                tile.setPosition(tileX, tileY);

                // Add a click listener for mouse clicks
                tile.addListener(new ClickListener(Input.Buttons.LEFT) {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        playScreen.onTileClicked(BoardActor.this, tile);
                    }
                });

                this.tiles[row][col] = tile;
                this.addActor(tile); // Add the tile as a child of this Group
            }
        }
    }

    // Called by PlayScreen when a tile is clicked
    public void handleTileClick(TileActor tile) {
        if (selectedTile != null) {
            selectedTile.setSelected(false); // Deselect the old one
        }
        selectedTile = tile;
        selectedTile.setSelected(true); // Select the new one
    }

    // Get the currently aimed-at tile
    public TileActor getSelectedTile() {
        return selectedTile;
    }

    // Clear the "Aim"
    public void clearSelection() {
        if (selectedTile != null) {
            selectedTile.setSelected(false);
            selectedTile = null;
        }
    }

    // Set the entire board's interactivity
    public void setBoardInteractive(boolean isInteractive) {
        setTouchable(isInteractive ? Touchable.enabled : Touchable.disabled);
    }

    // Called by PlayScreen to show attack results
    public void applyResult(Coordinates coords, AttackResult result, Runnable onComplete) {

        TileActor tile = tiles[coords.y][coords.x];

        if (result == AttackResult.HIT) {
            tile.playHitAnimation(onComplete);
        } else {
            tile.playMissAnimation(onComplete);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        batch.draw(boardBackground, getX(), getY(), getWidth(), getHeight());

        super.drawChildren(batch, parentAlpha);
    }
}
