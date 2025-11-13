package com.ThePod.Admirals;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

// Reusable Class For Handling Camera and Viewport
// This maintains a virtual screen size to make UI scaling consistent
public class ScreenCamera {

    // Set the virtual size of our game world
    public static final float WORLD_WIDTH = 1280f;
    public static final float WORLD_HEIGHT = 720f;

    // Camera and Viewport
    private final OrthographicCamera camera;
    private final Viewport viewport;

    // Constructor for ScreenCamera
    // Initializes the camera and viewport
    // Handles screen resizing behavior and camera updates
    public ScreenCamera() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        viewport.apply();
        // Center the camera on our virtual world
        camera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0);
        camera.update();
    }

    // Returns the camera
    public OrthographicCamera getCamera() {
        return camera;
    }

    // Returns the viewport
    public Viewport getViewport() {
        return viewport;
    }

    // Call this from the Screen's resize method
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    // Call this from the Screen's render method
    public void update() {
        camera.update();
    }
}