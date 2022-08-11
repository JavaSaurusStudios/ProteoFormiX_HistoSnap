package be.javasaurusstudios.histosnap.model.image.annotation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * This class represents a square annotation shape
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public class AnnotationRect extends Rectangle implements AnnotationShape {

    /**
     * The color for the rectangle
     */
    private final Color color;

    /**
     * Constructor for a rectangle shaped annotation
     * @param x the x coordinate (bot-left)
     * @param y the y coordinate (bot-left)
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param color the color of the rectangle
     */
    public AnnotationRect(int x, int y, int width, int height, Color color) {
        super(x, y, width, height);
        this.color = color;
    }

    @Override
    public void draw(Graphics g, int xOffset, int yOffset) {
        g.setColor(color);
        g.drawRect(xOffset + x, yOffset + y, width, height);
    }

    @Override
    public Color getColor() {
        return color;
    }

}
