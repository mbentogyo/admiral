package com.ThePod.Admirals;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

// Handles a clickable, hoverable text-based button
public class UiTextButton {

    private BitmapFont font;
    private String text;
    private Rectangle bounds;
    private GlyphLayout layout;
    private Runnable onClick;

    private boolean isHovered = false;
    private Color defaultColor = Color.WHITE;
    private Color hoverColor = Color.YELLOW;

    // x and y are the TOP-LEFT corner for the text
    public UiTextButton(String text, BitmapFont font, float x, float y) {
        this.text = text;
        this.font = font;
        this.layout = new GlyphLayout(font, text);

        // LibGdx font drawing uses y as the top coordinate
        // The bounds rectangle y must be (y - height)
        float boundsX = x;
        float boundsY = y - layout.height;
        float boundsWidth = layout.width;
        float boundsHeight = layout.height;
        
        this.bounds = new Rectangle(boundsX, boundsY, boundsWidth, boundsHeight);
    }

    public void setOnClick(Runnable onClick) {
        this.onClick = onClick;
    }

    public void update(float delta, Viewport viewport) {
        // Get mouse position in world coordinates
        Vector2 mouse = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        viewport.unproject(mouse);

        // Check for hover
        if (bounds.contains(mouse)) {
            isHovered = true;

            // Check for click
            if (Gdx.input.justTouched() && onClick != null) {
                onClick.run();
            }
        } else {
            isHovered = false;
        }
    }

    public void render(SpriteBatch batch) {
        // Set color based on hover state
        if (isHovered) {
            font.setColor(hoverColor);
        } else {
            font.setColor(defaultColor);
        }

        // Draw the text
        // We use the layout to draw, which respects the x,y as top-left
        font.draw(batch, layout, bounds.x, bounds.y + bounds.height);
    }

    // Call this to reset the font color when done
    public void resetFontColor() {
        font.setColor(defaultColor);
    }

    // Helper to get the width for centering
    public float getWidth() {
        return layout.width;
    }
}