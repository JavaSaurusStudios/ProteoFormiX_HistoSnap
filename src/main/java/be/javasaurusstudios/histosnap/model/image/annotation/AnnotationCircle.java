package be.javasaurusstudios.histosnap.model.image.annotation;

import java.awt.Color;
import java.awt.Graphics;

/**
 * This class represents a circle shaped annotation area
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public class AnnotationCircle implements AnnotationShape {

    private final Color color;
    private final int x;
    private final int y;
    private final int width;
    private final int height;

    public AnnotationCircle(int x, int y, int radius, Color color) {
        this.x = x;
        this.y = y;
        this.width = radius;
        this.height = radius;
        this.color = color;
    }

    public AnnotationCircle(int x, int y, int width, int height, Color color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
    }

    @Override
    public void draw(Graphics g, int xOffset, int yOffset) {
        g.setColor(color);
        g.drawArc(xOffset + x, yOffset + y, width, height, 0, 360);
    }

    @Override
    public Color getColor() {
        return color;
    }

}
