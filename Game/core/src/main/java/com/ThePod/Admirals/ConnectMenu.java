package com.ThePod.Admirals;

import com.ThePod.Admirals.exception.AdmiralsException;
import com.ThePod.Admirals.network.ClientConnection;
import com.ThePod.Admirals.network.HostConnection;
import com.ThePod.Admirals.network.callback.ConnectionCallback;
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
        style.background.setLeftWidth(35f); // Increased from 15f
        style.background.setRightWidth(15f);
        style.background.setTopHeight(10f); // Increased from 5f
        style.background.setBottomHeight(10f); // Increased from 5f

        style.cursor = skin.getDrawable("cursor");
        skin.add("default", style);

        // Create the TextField
        connectCodeInput = new TextField("", skin);
        connectCodeInput.setSize(320, 60); // Set to ShipContainer size
        connectCodeInput.setPosition((ScreenCamera.WORLD_WIDTH - connectCodeInput.getWidth()) / 2, 400);
        stage.addActor(connectCodeInput); // Add to stage

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


        // Click Handlers
        backButton.setOnClick(() -> {
            if (isHosting || isConnecting) {
                isHosting = false;
                isConnecting = false;
                Gdx.input.setInputProcessor(game.cursorHandler); // Restore default processor
            } else {
                game.setScreen(new MainMenu(game));
            }
        });

        hostingButton.setOnClick(() -> {
            isHosting = true; // Toggle state
            System.out.println("Starting hosting");
            GameManager.newInstance(new HostConnection(new ConnectionCallback() {
                @Override
                public void onConnect() {
                    System.out.println("Successfully connected to client");
                }

                @Override
                public void onDisconnect(AdmiralsException e) {
                    System.out.println("Unsuccessfully connected to client: " +  e.getMessage());
                }
            }));
        });

        connectButton.setOnClick(() -> {
            isConnecting = true; // Toggle state
            Gdx.input.setInputProcessor(inputMultiplexer); // Set combined input
            stage.setKeyboardFocus(connectCodeInput); // Auto-focus the text field
        });

        submitConnectButton.setOnClick(() -> {
            System.out.println("Connecting with code: " + connectCodeInput.getText()); // Just For Debugging

            //TODO ClientConnection
            GameManager.newInstance(new ClientConnection(CodeGenerator.decode(connectCodeInput.getText()), new ConnectionCallback() {
                @Override
                public void onConnect() {
                    System.out.println("Successfully connected to server");
                }

                @Override
                public void onDisconnect(AdmiralsException e) {
                    System.out.println("Unsuccessfully connected to server: " +  e.getMessage());
                }
            }));
        });

        // Set up Input Multiplexer
        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stage);
        inputMultiplexer.addProcessor(game.cursorHandler);

        setDynamicPrompt("Your Code: " + GameManager.getCode());
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
            stage.act(delta); // Update the stage (and textfield)
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
