package com.ThePod.Admirals;

import com.ThePod.Admirals.board.AttackResult;
import com.ThePod.Admirals.board.Coordinates;
import com.ThePod.Admirals.exception.AdmiralsException;
import com.ThePod.Admirals.network.ClientConnection;
import com.ThePod.Admirals.network.HostConnection;
import com.ThePod.Admirals.network.callback.ConnectionCallback;
import com.ThePod.Admirals.network.callback.TurnCallback;
import com.ThePod.Admirals.util.CodeGenerator;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.audio.Sound;

public class ConnectMenu implements Screen {

    private Main game;
    private Texture background;
    private Viewport viewport;
    private TextureAtlas atlas;

    //Ui Elements & Local Variables
    private UiButton backButton;
    private UiTextButton hostingButton;
    private UiTextButton connectButton;
    private UiDisplay waitingAnimation;
    private UiTextButton submitConnectButton;

    // Scene2D for Text Input
    private Stage stage;
    private Skin skin;
    private TextField connectCodeInput;

    // Font and layout for prompts
    private BitmapFont font;
    private GlyphLayout staticPromptLayout;
    private GlyphLayout dynamicPromptLayout;
    private float staticPromptX, staticPromptY;

    // State
    private boolean isHosting = false;
    private boolean isConnecting = false;
    private String dynamicPromptText = "Waiting for connection...";

    private InputMultiplexer inputMultiplexer;

    // Sound effects for text input
    private Sound inputSound1;
    private Sound inputSound2;
    private Sound inputSound3;
    private Sound backspaceSound;
    private int soundIndex = 0; // Cycles through 0, 1, 2
    private int lastTextLength = 0; // Track text length to detect changes

    // Constructor to get the main game object
    public ConnectMenu(Main game) {
        this.game = game;
    }

    // @param newText The new text to display.
    public void setDynamicPrompt(String newText) {
        this.dynamicPromptText = newText;

        if (dynamicPromptLayout != null && font != null) {
            dynamicPromptLayout.setText(font, dynamicPromptText);
        }
    }

    @Override
    public void show() {
        // Load assets
        background = AssetLoader.getInstance().getTexture("Connecting_Frame.png");
        atlas = AssetLoader.getInstance().admiralsUiAtlas;
        viewport = game.screenCamera.getViewport();
        font = AssetLoader.getInstance().operatorFont;

        // Load input sounds
        AssetLoader assets = AssetLoader.getInstance();
        inputSound1 = assets.inputCode1Sound;
        inputSound2 = assets.inputCode2Sound;
        inputSound3 = assets.inputCode3Sound;
        backspaceSound = assets.inputBackspaceSound;

        // Create Scene2D Stage and Skin for TextField
        stage = new Stage(viewport, game.batch);
        skin = new Skin();
        skin.add("default-font", font, BitmapFont.class);

        // Create a 1x1 white pixel for the cursor
        Pixmap cursorPixmap = new Pixmap(2, 32, Pixmap.Format.RGBA8888);
        cursorPixmap.setColor(Color.WHITE);
        cursorPixmap.fill();
        skin.add("cursor", new Texture(cursorPixmap));
        cursorPixmap.dispose();

        // Create the TextField style
        TextField.TextFieldStyle style = new TextField.TextFieldStyle();
        style.font = skin.getFont("default-font");
        style.fontColor = Color.WHITE;
        style.background = new TextureRegionDrawable(atlas.findRegion("ShipContainer"));

        // Add padding so text is not on the edge
        style.background.setLeftWidth(35f);
        style.background.setRightWidth(15f);
        style.background.setTopHeight(10f);
        style.background.setBottomHeight(10f);

        style.cursor = skin.getDrawable("cursor");
        skin.add("default", style);

        // Create the TextField
        connectCodeInput = new TextField("", skin);
        connectCodeInput.setSize(320, 60);
        connectCodeInput.setPosition((ScreenCamera.WORLD_WIDTH - connectCodeInput.getWidth()) / 2, 400);

        // Add a TextFieldListener to detect text changes
        connectCodeInput.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                int currentLength = textField.getText().length();

                // Check if text was added (length increased)
                if (currentLength > lastTextLength) {
                    // Play the next sound in sequence
                    switch (soundIndex) {
                        case 0:
                            inputSound1.play(0.6f);
                            break;
                        case 1:
                            inputSound2.play(0.6f);
                            break;
                        case 2:
                            inputSound3.play(0.6f);
                            break;
                    }
                    // Cycle to next sound (0 -> 1 -> 2 -> 0)
                    soundIndex = (soundIndex + 1) % 3;
                }
                // Check if text was deleted (length decreased)
                else if (currentLength < lastTextLength) {
                    backspaceSound.play(0.6f);
                }

                // Update last length
                lastTextLength = currentLength;
            }
        });

        stage.addActor(connectCodeInput);

        // Instantiate UI Elements
        backButton = new UiButton(atlas, "Back_Inactive", "Back_Active", 0.2f, 110, 580, 64, 44, viewport);

        // Static Prompt Text
        staticPromptLayout = new GlyphLayout(font, "Host Or Connecting?");
        staticPromptX = (ScreenCamera.WORLD_WIDTH - staticPromptLayout.width) / 2;
        staticPromptY = 450;

        // Dynamic Prompt Text
        dynamicPromptLayout = new GlyphLayout(font, dynamicPromptText);

        // Clickable Text Buttons
        hostingButton = new UiTextButton("Hosting", font, 400, 350);
        connectButton = new UiTextButton("Connect", font, 800, 350);

        // New "Connect" button for the text input
        submitConnectButton = new UiTextButton("Connect", font, 600, 320);

        // Waiting Animation
        float animWidth = 320;
        float animHeight = 45;
        float animX = (ScreenCamera.WORLD_WIDTH - animWidth) / 2;
        float animY = 300;
        waitingAnimation = new UiDisplay(atlas, "Waiting", 8, 0.8f, animX, animY, animWidth, animHeight, viewport);

        ConnectionCallback connectionCallback = getConnectionCallback();

        // Click Handlers
        backButton.setOnClick(() -> {
            if (isHosting || isConnecting) {
                isHosting = false;
                isConnecting = false;
                Gdx.input.setInputProcessor(game.cursorHandler);
            } else {
                game.setScreen(new MainMenu(game));
            }
        });

        hostingButton.setOnClick(() -> {
            isHosting = true;
            System.out.println("Starting hosting");
            GameManager.newInstance(new HostConnection(connectionCallback));
        });

        connectButton.setOnClick(() -> {
            isConnecting = true;
            Gdx.input.setInputProcessor(inputMultiplexer);
            stage.setKeyboardFocus(connectCodeInput);
            // Reset the text length tracker when entering connect mode
            lastTextLength = 0;
            soundIndex = 0;
        });

        submitConnectButton.setOnClick(() -> {
            System.out.println("Connecting with code: " + connectCodeInput.getText());

            String code = "";
            try {
                code = CodeGenerator.decode(connectCodeInput.getText());
            } catch (IllegalArgumentException e) {
                //TODO toast to user that code is invalid
                return;
            }

            GameManager.newInstance(new ClientConnection(code, connectionCallback));
        });

        // Set up Input Multiplexer
        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stage);
        inputMultiplexer.addProcessor(game.cursorHandler);

        setDynamicPrompt("Your Code: " + GameManager.getCode());
    }

    private ConnectionCallback getConnectionCallback() {
        PrepareShipsScreen prepareShipsScreen = new PrepareShipsScreen(game);

        //TurnCallback
        TurnCallback turnCallback = new TurnCallback() {
            @Override
            public void setUp() {
                Gdx.app.postRunnable(() -> game.setScreen(prepareShipsScreen));
            }

            @Override
            public void onMyTurn() {
                System.out.println("MY TURN");
                Gdx.app.postRunnable(PlayScreen::onMyTurn);
            }

            @Override
            public void onEnemyTurn() {
                System.out.println("ENEMY TURN");
                Gdx.app.postRunnable(PlayScreen::onEnemyTurn);
            }

            @Override
            public void myAttack(String message) {

            }

            @Override
            public void enemyAttack(Coordinates coordinates, AttackResult result, String message) {

            }

            @Override
            public void onGameOver(String message) {

            }

            @Override
            public void onEnemyReady() {
                Gdx.app.postRunnable(() -> prepareShipsScreen.setEnemyIndicator(true));
            }
        };

        // ConnectionCallback
        ConnectionCallback connectionCallback = new ConnectionCallback() {
            @Override
            public void onConnect() {
                System.out.println("Successfully connected to client");
                GameManager.start(turnCallback);
            }

            @Override
            public void onDisconnect(AdmiralsException e) {
                if (e == null) System.out.println("Successfully disconnected from client");
                else System.out.println("Unsuccessfully connected to client: " +  e.getMessage());
            }
        };
        return connectionCallback;
    }

    @Override
    public void render(float delta) {
        // Clear screen
        ScreenUtils.clear(0, 0, 0, 1);

        // Update the camera
        game.screenCamera.update();

        // Update UI elements based on state
        backButton.update(delta);

        if (isHosting) {
            waitingAnimation.update(delta);
        } else if (isConnecting) {
            stage.act(delta);
            submitConnectButton.update(delta, viewport);
        } else {
            hostingButton.update(delta, game.screenCamera.getViewport());
            connectButton.update(delta, game.screenCamera.getViewport());
        }

        // Tell the SpriteBatch to use the camera's view
        game.batch.setProjectionMatrix(game.screenCamera.getCamera().combined);

        // Draw background
        game.batch.begin();
        game.batch.draw(background, 0, 0, ScreenCamera.WORLD_WIDTH, ScreenCamera.WORLD_HEIGHT);

        // Draw Ui Objects
        backButton.render(game.batch);

        font.setColor(Color.WHITE);

        if (isHosting) {
            // Draw the dynamic prompt
            float dynamicX = (ScreenCamera.WORLD_WIDTH - dynamicPromptLayout.width) / 2;
            font.draw(game.batch, dynamicPromptLayout, dynamicX, staticPromptY);

            // Draw the "Waiting" animation
            waitingAnimation.render(game.batch);

        } else if (isConnecting) {
            // The TextField is drawn by the stage
            // Draw the "Connect" button underneath
            submitConnectButton.render(game.batch);

        } else {
            // Draw the static prompt
            font.draw(game.batch, staticPromptLayout, staticPromptX, staticPromptY);

            // Draw the text buttons
            hostingButton.render(game.batch);
            connectButton.render(game.batch);
        }

        font.setColor(Color.WHITE);

        game.batch.end();

        // Draw Stage
        if (isConnecting) {
            stage.draw();
        }
    }

    @Override
    public void resize(int width, int height) {
        // Update the viewport
        game.screenCamera.resize(width, height);
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() {
        // This is called when the screen is switched away from
        Gdx.input.setInputProcessor(game.cursorHandler);
    }

    @Override
    public void dispose() {
        // AssetLoader handles disposing the background texture
        stage.dispose();
        skin.dispose();
    }

    // Unused methods
    @Override public void pause() {}
    @Override public void resume() {}
}
