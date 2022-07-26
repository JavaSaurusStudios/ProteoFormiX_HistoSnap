/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.javasaurusstudios.histosnap.model.image.annotation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 *
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public class AnnotationRect extends Rectangle implements AnnotationShape {

    private final Color color;

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
