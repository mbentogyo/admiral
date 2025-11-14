package com.ThePod.Admirals;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.ThePod.Admirals.board.AttackResult;
import com.ThePod.Admirals.board.CellState;
import com.ThePod.Admirals.board.Coordinates;

import java.util.HashMap;
import java.util.Map;

// This Group holds ONE 15x10 board and all its parts.
// It contains the definitions for Tile, Ship, and FireButton
public class GameBoard extends Group {

    public static final int GRID_COLS = 15;
    public static final int GRID_ROWS = 10;
    public static final float TILE_WIDTH = 32f;
    public static final float TILE_HEIGHT = 32f;

    private TextureRegion boardBackground;
    private Tile[][] tiles;
    private Array<Ship> ships;
    private Map<Integer, Ship> shipMap; // Maps CellState value to a Ship
    private FireButton fireButton;
    
    private Tile selectedTile = null;
    private PlayScreen playScreen; // Reference to the main screen to call attack
    private boolean isMyBoard;

    private float gridStartX, gridStartY; // Relative position of the grid
    private TextureAtlas atlas; // Store atlas for inner class

    public GameBoard(TextureAtlas atlas, PlayScreen playScreen, boolean isMyBoard) {
        this.atlas = atlas; // Store atlas
        this.playScreen = playScreen;
        this.isMyBoard = isMyBoard;
        this.tiles = new Tile[GRID_ROWS][GRID_COLS];
        this.ships = new Array<>();
        this.shipMap = new HashMap<>();
        this.boardBackground = atlas.findRegion("Board");

        float boardWidth = 512f;
        float boardHeight = 350f;
        setSize(boardWidth, boardHeight);

        // Calculate grid's relative start position
        gridStartX = (boardWidth - (GRID_COLS * TILE_WIDTH)) / 2;
        gridStartY = (boardHeight - (GRID_ROWS * TILE_HEIGHT)) / 2;

        int[][] initialLayout = null;
        if (isMyBoard) {
            initialLayout = GameManager.getMyBoard();
        }

        // 1. Create all 150 tiles
        for (int row = 0; row < GRID_ROWS; row++) {
            for (int col = 0; col < GRID_COLS; col++) {
                final Tile tile = new Tile(row, col, atlas, isMyBoard);
                float tileX = gridStartX + (col * TILE_WIDTH);
                float tileY = gridStartY + ((GRID_ROWS - 1 - row) * TILE_HEIGHT); // Flipped Y
                tile.setPosition(tileX, tileY);
                
                // Add click listener
                tile.addListener(new ClickListener(Input.Buttons.LEFT) {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        onTileClicked(tile);
                    }
                });
                
                this.tiles[row][col] = tile;
                this.addActor(tile);
            }
        }

        // 2. Create ShipActors (if this is my board)
        if (isMyBoard && initialLayout != null) {
            findAndCreateShips(initialLayout, atlas);
        }
        
        // 3. Create Fire Button (only for enemy board)
        if (!isMyBoard) {
            fireButton = new FireButton(atlas);
            fireButton.setPosition(boardWidth / 2 - fireButton.getWidth() / 2, -70); // Below board
            this.addActor(fireButton);
        }
    }
    
    // --- Public Methods ---
    
    public void setBoardInteractive(boolean interactive) {
        // This enables/disables clicks on the tiles and fire button
        this.setTouchable(interactive ? Touchable.childrenOnly : Touchable.disabled);
        if (fireButton != null) {
            fireButton.setDisabled(true); // Always disable button until tile is selected
        }
    }
    
    public Coordinates getSelectedTileCoords() {
        if (selectedTile != null) {
            return new Coordinates(selectedTile.col, selectedTile.row);
        }
        return null;
    }

    // Called when *our* attack result comes back (for enemy board)
    public void applyMyAttackResult(String message, Runnable onComplete) {
        if (selectedTile == null) {
            onComplete.run();
            return;
        }

        if (message.startsWith("SUNK")) {
            String shipName = message.split(" ")[1];
            Gdx.app.log("GameBoard", "Enemy ship sunk: " + shipName);
            // We need to know *where* the ship is. This is missing from GameManager.
            // For now, just mark the tile as HIT.
            selectedTile.playHitAnimation(onComplete);
            
        } else if (message.equals("HIT")) {
            selectedTile.playHitAnimation(onComplete);
        } else { // MISS
            selectedTile.playMissAnimation(onComplete);
        }
        selectedTile = null;
    }
    
    // Called when *enemy* attacks *us*
    public void applyEnemyAttack(Coordinates coords, AttackResult result, Runnable onComplete) {
        Tile tile = tiles[coords.getRow()][coords.getColumn()];
        
        if (result == AttackResult.HIT) {
            tile.playHitAnimation(onComplete);
        } else if (result == AttackResult.MISS) {
            tile.playMissAnimation(onComplete);
        } else {
            // It's a SINK
            tile.playHitAnimation(() -> {
                int cellValue = GameManager.getMyBoard()[coords.getRow()][coords.getColumn()];
                if (shipMap.containsKey(cellValue)) {
                    shipMap.get(cellValue).setSunk();
                }
                onComplete.run();
            });
        }
    }

    // --- Internal Logic ---

    // Called by a Tile's click listener
    private void onTileClicked(Tile tile) {
        if (isMyBoard) return; // Can't click my own board

        if (selectedTile != null) {
            selectedTile.setSelected(false); // Deselect old
        }
        selectedTile = tile;
        selectedTile.setSelected(true); // Select new
        
        if (fireButton != null) {
            fireButton.setDisabled(false); // Enable fire button
        }
    }
    
    // Finds and creates all Ship actors for this board
    private void findAndCreateShips(int[][] layout, TextureAtlas atlas) {
        boolean[][] visited = new boolean[GRID_ROWS][GRID_COLS];
        for (int r = 0; r < GRID_ROWS; r++) {
            for (int c = 0; c < GRID_COLS; c++) {
                int cellValue = layout[r][c];
                if (cellValue > 1 && !visited[r][c]) {
                    // This is the start of a ship
                    int length = 0;
                    boolean isHorizontal = (c + 1 < GRID_COLS && layout[r][c+1] == cellValue);
                    
                    if (isHorizontal) {
                        for (int i = c; i < GRID_COLS && layout[r][i] == cellValue; i++) {
                            visited[r][i] = true;
                            length++;
                        }
                    } else {
                        for (int i = r; i < GRID_ROWS && layout[i][c] == cellValue; i++) {
                            visited[i][c] = true;
                            length++;
                        }
                    }
                    
                    // Create the ShipActor
                    Ship ship = new Ship(r, c, length, isHorizontal, cellValue, atlas, gridStartX, gridStartY);
                    ships.add(ship);
                    shipMap.put(cellValue, ship);
                    this.addActor(ship); // Add ship to this group
                }
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        // 1. Draw the board background
        batch.draw(boardBackground, getX(), getY(), getWidth(), getHeight());
        
        // 2. Draw all children (tiles, ships, and button)
        super.drawChildren(batch, parentAlpha);
    }
    
    // *** ERROR WAS HERE: The closing brace } was above this line ***

    // ========================================================================
    // INNER CLASS: Tile
    // ========================================================================
    class Tile extends Actor {
        private Animation<TextureRegion> waterAnimation;
        private Animation<TextureRegion> deadWaterAnimation;
        private Animation<TextureRegion> aimAnimation;
        private Animation<TextureRegion> fireAnimation;
        private Animation<TextureRegion> splashAnimation;

        private Animation<TextureRegion> baseAnimation;
        private Animation<TextureRegion> overlayAnimation; // Aim or Fire
        private Animation<TextureRegion> effectAnimation; // Splash

        private float baseTime = 0f;
        private float overlayTime = 0f;
        private float effectTime = 0f;
        
        private boolean isPlayingEffect = false;
        private Runnable onEffectComplete = null;
        
        public final int row;
        public final int col;

        public Tile(int row, int col, TextureAtlas atlas, boolean isMyBoard) {
            this.row = row;
            this.col = col;
            loadAnimations(atlas);
            this.baseAnimation = isMyBoard ? waterAnimation : deadWaterAnimation;
            setSize(TILE_WIDTH, TILE_HEIGHT);
            baseTime = (float) (Math.random() * 2.0); // De-sync water
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
            
            // Aim (1-2 @ 150ms)
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
        
        public void setSelected(boolean isSelected) {
            if (isSelected) {
                this.overlayAnimation = aimAnimation;
                this.overlayTime = 0f;
            } else {
                if (this.overlayAnimation == aimAnimation) {
                    this.overlayAnimation = null;
                }
            }
        }

        public void playHitAnimation(Runnable onComplete) {
            setSelected(false);
            this.baseAnimation = waterAnimation;
            playSplash(() -> {
                this.overlayAnimation = fireAnimation; // Add fire
                this.overlayTime = 0f;
                onComplete.run();
            });
        }

        public void playMissAnimation(Runnable onComplete) {
            setSelected(false);
            this.baseAnimation = waterAnimation;
            playSplash(onComplete);
        }

        private void playSplash(Runnable onComplete) {
            this.onEffectComplete = onComplete;
            this.isPlayingEffect = true;
            this.effectTime = 0f;
        }

        @Override
        public void act(float delta) {
            super.act(delta);
            baseTime += delta;
            if (overlayAnimation != null) overlayTime += delta;
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
            
            // Layer 2: Overlay (Aim or Fire)
            if (overlayAnimation != null) {
                TextureRegion frame = overlayAnimation.getKeyFrame(overlayTime, true);
                batch.draw(frame, 
                    getX() + (getWidth() - frame.getRegionWidth()) / 2, 
                    getY() + (getHeight() - frame.getRegionHeight()) / 2);
            }

            // Layer 3: Effect (Splash)
            if (isPlayingEffect) {
                TextureRegion frame = splashAnimation.getKeyFrame(effectTime, false);
                batch.draw(frame, 
                    getX() + (getWidth() - frame.getRegionWidth()) / 2, 
                    getY() + (getHeight() - frame.getRegionHeight()) / 2);
            }
        }
    }

    // ========================================================================
    // INNER CLASS: Ship
    // ========================================================================
    class Ship extends Actor {
        private TextureRegion shipTexture;
        private TextureRegion sunkTexture;
        private boolean isSunk = false;

        public Ship(int row, int col, int length, boolean isHorizontal, int cellValue, TextureAtlas atlas, float gridStartX, float gridStartY) {
            String shipName = getShipNameFromValue(cellValue);
            this.shipTexture = atlas.findRegion(shipName);
            this.sunkTexture = atlas.findRegion(shipName + "_Sunk");
            
            float shipX = gridStartX + col * TILE_WIDTH;
            float shipY = gridStartY + ((GRID_ROWS - 1 - row) * TILE_HEIGHT);
            float shipWidth = isHorizontal ? length * TILE_WIDTH : TILE_WIDTH;
            float shipHeight = isHorizontal ? TILE_HEIGHT : length * TILE_HEIGHT;

            if (isHorizontal) {
                setPosition(shipX, shipY);
                setSize(shipWidth, shipHeight);
                setRotation(0);
            } else {
                setPosition(shipX + TILE_WIDTH, shipY);
                setSize(shipHeight, shipWidth);
                setRotation(270);
            }
        }
        
        public void setSunk() { this.isSunk = true; }

        private String getShipNameFromValue(int cellValue) {
            if (cellValue == CellState.CARRIER.getValue()) return "Carrier";
            if (cellValue == CellState.CORVETTE.getValue()) return "Corvette";
            if (cellValue == CellState.DESTROYER.getValue()) return "Destroyer";
            if (cellValue == CellState.FRIGATE.getValue()) return "Frigate";
            if (cellValue == CellState.SUBMARINE.getValue()) return "Submarine";
            if (cellValue == CellState.PATROL_BOAT.getValue()) return "PatrolBoat";
            return null;
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            TextureRegion currentTexture = isSunk ? sunkTexture : shipTexture;
            if (currentTexture == null) return;
            batch.draw(currentTexture, getX(), getY(), 0, 0, getWidth(), getHeight(), 1, 1, getRotation());
        }
    }
    
    // ========================================================================
    // INNER CLASS: FireButton
    // ========================================================================
    class FireButton extends Actor {
        private Animation<TextureRegion> clickAnimation;
        private TextureRegion inactiveTexture;
        private TextureRegion hoverTexture;
        private TextureRegion clickedTexture; 

        private float stateTime = 0;
        private boolean isHovered = false;
        private boolean isClicked = false;
        private boolean isDisabled = true;

        public FireButton(TextureAtlas atlas) {
            inactiveTexture = atlas.findRegion("Attack_Inactive");
            hoverTexture = atlas.findRegion("Attack_Hovered");
            clickedTexture = atlas.findRegion("Attack_Clicked"); 
            
            // Create the "click" animation (Fire 1-3)
            Array<TextureRegion> clickFrames = new Array<>();
            clickFrames.add(atlas.findRegion("Fire", 1));
            clickFrames.add(atlas.findRegion("Fire", 2));
            clickFrames.add(atlas.findRegion("Fire", 3));
            clickAnimation = new Animation<>(0.1f, clickFrames, Animation.PlayMode.LOOP);

            setSize(inactiveTexture.getRegionWidth(), inactiveTexture.getRegionHeight());
            setTouchable(Touchable.enabled);

            addListener(new ClickListener(Input.Buttons.LEFT) {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (!isDisabled) {
                        Coordinates coords = getSelectedTileCoords();
                        if (coords != null) {
                            playScreen.onFireButtonAttack(coords);
                        }
                    }
                }
                
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    if (!isDisabled) isHovered = true;
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    isHovered = false;
                }

                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    if (!isDisabled) {
                        isClicked = true;
                        stateTime = 0f;
                        return true;
                    }
                    return false;
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    isClicked = false;
                }
            });
        }

        public void setDisabled(boolean disabled) {
            this.isDisabled = disabled;
            if (disabled) {
                isHovered = false;
                isClicked = false;
            }
        }

        @Override
        public void act(float delta) {
            super.act(delta);
            if (isClicked) stateTime += delta;
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            TextureRegion frame;
            if (isDisabled) {
                frame = inactiveTexture;
            } else if (isClicked) {
                frame = clickedTexture;
            } else if (isHovered) {
                frame = hoverTexture;
            } else {
                frame = inactiveTexture;
            }
            batch.draw(frame, getX(), getY(), getWidth(), getHeight());
        }
    }
}