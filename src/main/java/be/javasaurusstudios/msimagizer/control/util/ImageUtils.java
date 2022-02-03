package be.javasaurusstudios.msimagizer.control.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * This class contains useful methods to manipulate a buffered image
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public class ImageUtils {

    /**
     * Adds a title to a buffered image
     * @param image the input image
     * @param title the title to put on the image
     * @return the buffered image with a title
     */
    public static BufferedImage SetImageTitle(BufferedImage image, String title) {
        BufferedImage framedImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());

        Graphics2D graph = framedImage.createGraphics();
        graph.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
        graph.setColor(Color.white);
        graph.setFont(new Font("Courier New", 1, 17));
        graph.drawString(title, 1, 1);

        return framedImage;
    }
}
