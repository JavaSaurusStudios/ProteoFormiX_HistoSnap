/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.javasaurusstudios.histosnap.model.image.annotation;

import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public interface AnnotationShape  {

    public void draw(Graphics g, int xOffset, int yOffset);

    public Color getColor();
    
}
