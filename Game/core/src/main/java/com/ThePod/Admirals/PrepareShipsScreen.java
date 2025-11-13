package com.ThePod.Admirals;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.audio.Sound;
import com.ThePod.Admirals.board.CellState;
import com.ThePod.Admirals.GameManager;

public class PrepareShipsScreen implements Screen {

    private Main game;
    private Texture background;
    private Viewport viewport;

    // Ui Elements
    private UiDisplay gameBoard;
    private UiDisplay shipContainer5;
    private UiDisplay shipContainer4_1;
    private UiDisplay shipContainer4_2;
    private UiDisplay shipContainer4_3;
    private UiDisplay shipContainer3;
    private UiDisplay shipContainer2;

    // Ship array and selection
    private Array<PrepareShip> ships;
    private PrepareShip selectedShip = null;

    // Ship declarations
    private PrepareShip carrier;
    private PrepareShip corvette;
    private PrepareShip frigate;
    private PrepareShip destroyer;
    private PrepareShip submarine;
    private PrepareShip patrolBoat;

    // The new grid system
    private BoardPrepGrid prepGrid;

    // Indicator Lights
    private IndicatorLight playerIndicator;
    private IndicatorLight enemyIndicator;

    // Indicator Labels
    private BitmapFont font;
    private GlyphLayout playerLabelLayout;
    private GlyphLayout enemyLabelLayout;
    private float playerLabelX, playerLabelY;
    private float enemyLabelX, enemyLabelY;

    // Play Button
    private UiButton playButton;
    private boolean isLayoutLocked = false;

    // Sound effects
    private Sound hoverTileSound;
    private int lastHoveredRow = -1;
    private int lastHoveredCol = -1;

    // Constructor to get the main game object
    public PrepareShipsScreen(Main game) {
        this.game = game;
    }

    // Public methods to control indicators

    //TODO: Turn Green When Player Is Ready
    public void setPlayerIndicator(boolean isGreen) {
        if (playerIndicator != null) {
            playerIndicator.setGreen(isGreen);
        }
    }

    //TODO: Turn Green When Enemy Is ready
    public void setEnemyIndicator(boolean isGreen) {
        if (enemyIndicator != null) {
            enemyIndicator.setGreen(isGreen);
        }
    }

    // Check if all ships are on the board
    private boolean areAllShipsPlaced() {
        for (PrepareShip ship : ships) {
            if (!ship.isPlaced()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void show() {
        // Load the background texture when screen is shown
        background = AssetLoader.getInstance().getTexture("Play_Frame.png");
        TextureAtlas atlas = AssetLoader.getInstance().admiralsUiAtlas;
        font = AssetLoader.getInstance().operatorFont; // Get the font
        this.viewport = game.screenCamera.getViewport();

        // Load tile hover sound
        AssetLoader assets = AssetLoader.getInstance();
        hoverTileSound = assets.hoverTileSound;

        ships = new Array<>();

        // Instantiate & Postion Ui Elements Here
        gameBoard = new UiDisplay(atlas, "Board", 1, 0f, 150, 150, 512, 350, viewport);

        // Create the grid
        prepGrid = new BoardPrepGrid(atlas, gameBoard);

        // Create Indicators
        float boardLeftX = gameBoard.getBounds().x;
        float boardTopY = gameBoard.getBounds().y + gameBoard.getBounds().height;
        float indicatorY = boardTopY + 10; // 10 units above board
        float indicatorWidth = 30;
        float indicatorHeight = 19;
        float labelPadding = 5; // Padding between light and text
        float indicatorPadding = 80; // Padding between "You" and "Opponent" groups

        // Player Indicator and Label
        playerIndicator = new IndicatorLight(atlas, boardLeftX, indicatorY, indicatorWidth, indicatorHeight);
        playerLabelLayout = new GlyphLayout(font, "You");
        playerLabelX = boardLeftX + indicatorWidth + labelPadding;
        playerLabelY = indicatorY + (indicatorHeight / 2) + (playerLabelLayout.height / 2);

        // Enemy Indicator and Label
        float enemyGroupStartX = boardLeftX + indicatorWidth + playerLabelLayout.width + indicatorPadding;
        enemyIndicator = new IndicatorLight(atlas, enemyGroupStartX, indicatorY, indicatorWidth, indicatorHeight);
        enemyLabelLayout = new GlyphLayout(font, "Opponent");
        enemyLabelX = enemyGroupStartX + indicatorWidth + labelPadding;
        enemyLabelY = indicatorY + (indicatorHeight / 2) + (enemyLabelLayout.height / 2);

        // Position the ship containers to the right of the board
        float containerX = 700;
        float containerY = 458;
        float padding = 10;
        float containerHeight = 42;

        // ShipContainer 5
        float c5_w = 170;
        shipContainer5 = new UiDisplay(atlas, "ShipContainer", 5, containerX, containerY, c5_w, containerHeight, viewport);
        carrier = new PrepareShip(atlas.findRegion("Carrier"), containerX + (c5_w - 161) / 2, containerY + (containerHeight - 32) / 2, 161, 32, viewport, 5, CellState.CARRIER.getValue());
        ships.add(carrier);

        // ShipContainer 4_1
        containerY -= (containerHeight + padding);
        float c4_w = 138;
        shipContainer4_1 = new UiDisplay(atlas, "ShipContainer", 4, containerX, containerY, c4_w, containerHeight, viewport);
        corvette = new PrepareShip(atlas.findRegion("Corvette"), containerX + (c4_w - 127) / 2, containerY + (containerHeight - 32) / 2, 127, 32, viewport, 4, CellState.CORVETTE.getValue());
        ships.add(corvette);

        // ShipContainer 4_2
        containerY -= (containerHeight + padding);
        shipContainer4_2 = new UiDisplay(atlas, "ShipContainer", 4, containerX, containerY, c4_w, containerHeight, viewport);
        frigate = new PrepareShip(atlas.findRegion("Frigate"), containerX + (c4_w - 128) / 2, containerY + (containerHeight - 32) / 2, 128, 32, viewport, 4, CellState.FRIGATE.getValue());
        ships.add(frigate);

        // ShipContainer 4_3
        containerY -= (containerHeight + padding);
        shipContainer4_3 = new UiDisplay(atlas, "ShipContainer", 4, containerX, containerY, c4_w, containerHeight, viewport);
        destroyer = new PrepareShip(atlas.findRegion("Destroyer"), containerX + (c4_w - 127) / 2, containerY + (containerHeight - 31) / 2, 127, 31, viewport, 4, CellState.DESTROYER.getValue());
        ships.add(destroyer);

        // ShipContainer 3
        containerY -= (containerHeight + padding);
        float c3_w = 106;
        shipContainer3 = new UiDisplay(atlas, "ShipContainer", 3, containerX, containerY, c3_w, containerHeight, viewport);
        submarine = new PrepareShip(atlas.findRegion("Submarine"), containerX + (c3_w - 98) / 2, containerY + (containerHeight - 32) / 2, 98, 32, viewport, 3, CellState.SUBMARINE.getValue());
        ships.add(submarine);

        // ShipContainer 2
        containerY -= (containerHeight + padding);
        float c2_w = 74;
        shipContainer2 = new UiDisplay(atlas, "ShipContainer", 2, containerX, containerY, c2_w, containerHeight, viewport);
        patrolBoat = new PrepareShip(atlas.findRegion("PatrolBoat"), containerX + (c2_w - 64) / 2, containerY + (containerHeight - 32) / 2, 64, 32, viewport, 2, CellState.PATROL_BOAT.getValue());
        ships.add(patrolBoat);

        // Play Button
        float playButtonWidth = 128; // Made bigger
        float playButtonHeight = 40; // Made bigger
        float playButtonX = containerX; // Aligned with containers' left edge
        float playButtonY = containerY - playButtonHeight - padding; // Below last container
        playButton = new UiButton(atlas, "Play_Inactive", "Play_Active", 0.2f, playButtonX, playButtonY, playButtonWidth, playButtonHeight, viewport);

        playButton.setOnClick(() -> {
            // Only fire once if all ships are placed and not already locked
            if (areAllShipsPlaced() && !isLayoutLocked) {
                isLayoutLocked = true;
                setPlayerIndicator(true); // Turn light green

                // Get the board state and send it to the game manager
                int[][] boardState = prepGrid.getBoardState();
                GameManager.setupFinished(boardState);
                // TODO: Give board to game manager

                // Log for debugging
                Gdx.app.log("PrepareShipsScreen", "Layout locked and board state submitted to GameManager.");
            }
        });
    }

    // Main input controller
    private void handleInput() {
        // Stop all ship movement if the layout is locked
        if (isLayoutLocked) {
            return;
        }

        Vector2 mouse = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        viewport.unproject(mouse);

        prepGrid.clearPreview();

        // Handle tile hover sound
        int[] coords = prepGrid.getTileCoords(mouse.x, mouse.y);
        if (coords != null) {
            // Only play sound if we moved to a different tile
            if (coords[0] != lastHoveredRow || coords[1] != lastHoveredCol) {
                if (hoverTileSound != null) {
                    hoverTileSound.play(0.4f);
                }
                lastHoveredRow = coords[0];
                lastHoveredCol = coords[1];
            }
        } else {
            // Reset when not hovering over grid
            lastHoveredRow = -1;
            lastHoveredCol = -1;
        }

        // Handle picking up a ship (Left Click)
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if (selectedShip == null) {
                // Not carrying a ship, try to pick one up
                // Loop in reverse so we pick up top-most ship first
                for (int i = ships.size - 1; i >= 0; i--) {
                    PrepareShip ship = ships.get(i);
                    if (ship.getBounds().contains(mouse.x, mouse.y)) {
                        selectedShip = ship;
                        selectedShip.setDragging(true, mouse);

                        // If it was already placed, clear its old spot
                        if (ship.isPlaced()) {
                            prepGrid.clearShip(ship);
                            ship.setPlaced(false);
                        }
                        break;
                    }
                }
            }
        }

        // Handle dropping/placing a ship (Left Click Release)
        if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            if (selectedShip != null) {
                // We were dragging a ship, and let go
                int[] coords2 = prepGrid.getTileCoords(mouse.x, mouse.y);

                // Check for a valid placement
                if (coords2 != null && prepGrid.isPlacementValid(coords2[0], coords2[1], selectedShip.getLength(), selectedShip.isHorizontal())) {

                    // Calculate snapX and snapY *exactly* as we do in the dragging section
                    float snapX = prepGrid.getGridStartX() + coords2[1] * 32f;
                    float snapY;

                    if (selectedShip.isHorizontal()) {
                        // Horizontal snap is simple: snap to the top-left tile
                        snapY = prepGrid.getGridStartY() + ((BoardPrepGrid.GRID_ROWS - 1 - coords2[0]) * 32f);
                    } else {
                        // Vertical snap: The ship's bounds.y is its *bottom* tile
                        // We must calculate the Y coord of the bottom-most tile
                        int bottomRow = coords2[0] + selectedShip.getLength() - 1;

                        if (bottomRow >= BoardPrepGrid.GRID_ROWS) {
                            bottomRow = BoardPrepGrid.GRID_ROWS - 1;
                        }

                        // Get the Y coord of that bottom tile
                        snapY = prepGrid.getGridStartY() + ((BoardPrepGrid.GRID_ROWS - 1 - bottomRow) * 32f);
                    }

                    prepGrid.placeShip(coords2[0], coords2[1], selectedShip.getLength(), selectedShip.isHorizontal(), selectedShip.getCellStateValue());
                    selectedShip.placeAt(snapX, snapY);
                } else {
                    // Invalid drop (off grid or overlapping)
                    selectedShip.snapToHome();
                }
                selectedShip = null;
            }
        }

        // Handle rotation (Right Click)
        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            if (selectedShip != null) {
                selectedShip.rotate();
            }
        }

        // Handle dragging and preview
        if (selectedShip != null && selectedShip.isDragging()) {
            selectedShip.updateDragPosition(mouse);

            int[] coords3 = prepGrid.getTileCoords(mouse.x, mouse.y);
            if (coords3 != null) {
                // Show preview on grid
                prepGrid.showPreview(coords3[0], coords3[1], selectedShip.getLength(), selectedShip.isHorizontal());

                float snapX = prepGrid.getGridStartX() + coords3[1] * 32f;
                float snapY;

                // This logic must be identical to the "placing" logic
                if (selectedShip.isHorizontal()) {
                    // Horizontal snap is simple: snap to the top-left tile
                    // use coords[0] (the row) for the Y calculation
                    snapY = prepGrid.getGridStartY() + ((BoardPrepGrid.GRID_ROWS - 1 - coords3[0]) * 32f);
                } else {
                    // Vertical snap: The ship's bounds.y is its *bottom* tile
                    // calculate the Y coord of the bottom-most tile
                    int bottomRow = coords3[0] + selectedShip.getLength() - 1;

                    if (bottomRow >= BoardPrepGrid.GRID_ROWS) {
                        bottomRow = BoardPrepGrid.GRID_ROWS - 1;
                    }

                    // Get the Y coord of that bottom tile
                    snapY = prepGrid.getGridStartY() + ((BoardPrepGrid.GRID_ROWS - 1 - bottomRow) * 32f);
                }

                // Now set the position, which the render method will use
                selectedShip.getBounds().setPosition(snapX, snapY);
            }
        }
    }

    @Override
    public void render(float delta) {
        // Clear screen
        ScreenUtils.clear(0, 0, 0, 1);

        // Update the camera
        game.screenCamera.update();

        // Run all input logic
        handleInput();

        // Update all ships (for hover sounds)
        for (PrepareShip ship : ships) {
            ship.update(delta);
        }

        // Update all UI elements
        gameBoard.update(delta);
        shipContainer5.update(delta);
        shipContainer4_1.update(delta);
        shipContainer4_2.update(delta);
        shipContainer4_3.update(delta);
        shipContainer3.update(delta);
        shipContainer2.update(delta);

        // Only update button hover if all ships are placed and not locked
        if (areAllShipsPlaced() && !isLayoutLocked) {
            playButton.update(delta);
        }

         // Tell the SpriteBatch to use the camera's view
        game.batch.setProjectionMatrix(game.screenCamera.getCamera().combined);

        // Draw background
        game.batch.begin();
        game.batch.draw(background, 0, 0, ScreenCamera.WORLD_WIDTH, ScreenCamera.WORLD_HEIGHT);

        // Draw Ui Objects (Containers)
        gameBoard.render(game.batch);

        // Draw the grid
        prepGrid.render(game.batch);

        // Draw indicators
        playerIndicator.render(game.batch);
        enemyIndicator.render(game.batch);

        // Draw indicator labels
        font.setColor(Color.WHITE);
        font.draw(game.batch, playerLabelLayout, playerLabelX, playerLabelY);
        font.draw(game.batch, enemyLabelLayout, enemyLabelX, enemyLabelY);

        // Draw Ship Containers
        shipContainer5.render(game.batch);
        shipContainer4_1.render(game.batch);
        shipContainer4_2.render(game.batch);
        shipContainer4_3.render(game.batch);
        shipContainer3.render(game.batch);
        shipContainer2.render(game.batch);

        // Draw the Play button
        playButton.render(game.batch);

        // Draw Draggable Ships
        // Draw placed ships first, then the selected one on top
        for (PrepareShip ship : ships) {
            if (ship != selectedShip) {
                ship.render(game.batch);
            }
        }
        if (selectedShip != null) {
            selectedShip.render(game.batch); // Draw selected ship last
        }

        game.batch.end();

    }

    @Override
    public void resize(int width, int height) {
        // Update the viewport when the window is resized
        game.screenCamera.resize(width, height);
    }

    // Unused methods
    @Override public void hide() {}
    @Override public void dispose() {}
    @Override public void pause() {}
    @Override public void resume() {}

}
