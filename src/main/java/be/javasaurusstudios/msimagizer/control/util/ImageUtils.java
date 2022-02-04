package be.javasaurusstudios.msimagizer.control.util;

import be.javasaurusstudios.msimagizer.model.SimilarityResult;
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
    
        /**
     * Creates a buffered image highlighting the selected "region of interest"
     *
     * @param minX the lower left X for the rectangle
     * @param maxX the upper right X for the rectangle
     * @param minY the lower left Y for the rectangle
     * @param maxY the upper left Y for the rectangle
     * @return a buffered image with a highlighted region of interest
     */
    public static BufferedImage HighlightZone(BufferedImage image,int minX, int maxX, int minY, int maxY) {
        BufferedImage framedImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());

        Graphics2D graph = framedImage.createGraphics();
        graph.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
        graph.setColor(Color.red);
        graph.drawRect(
                minX,
                minY,
                Math.abs(maxX - minX),
                Math.abs(maxY - minY)
        );
        return framedImage;
    }
    
    
}
