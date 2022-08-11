package be.javasaurusstudios.histosnap.model.image.annotation;

import java.awt.Color;
import java.awt.Graphics;

/**
 * A generic interface describing an annotation shape to be drawn on screen
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public interface AnnotationShape  {

    public void draw(Graphics g, int xOffset, int yOffset);

    public Color getColor();
    
}
