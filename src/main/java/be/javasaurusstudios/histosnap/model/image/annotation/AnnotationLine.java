package be.javasaurusstudios.histosnap.model.image.annotation;

import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public class AnnotationLine implements AnnotationShape {

    private final Color color;
    private final int x;
    private final int y;
    private final int x2;
    private final int y2;

    /**
     * Constructor for a line
     * @param x the x coordinate for the starting point
     * @param y the y coordinate for the starting point
     * @param x2 the x coordinate for the ending point
     * @param y2 the y coordinate for the ending point
     * @param color the color for the line
     */
    public AnnotationLine(int x, int y, int x2, int y2, Color color) {
        this.x = x;
        this.y = y;
        this.x2 = x2;
        this.y2 = y2;
        this.color = color;
    }

    @Override
    public void draw(Graphics g, int xOffset, int yOffset) {
        g.setColor(color);
        g.drawLine(xOffset + x, yOffset + y, xOffset + x2, yOffset + y2);
    }

    @Override
    public Color getColor() {
        return color;
    }

}
