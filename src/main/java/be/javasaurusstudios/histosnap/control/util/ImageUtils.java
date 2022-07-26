package be.javasaurusstudios.histosnap.control.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import javax.swing.ImageIcon;

/**
 * This class contains useful methods to manipulate a buffered image
 *
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public class ImageUtils {

    /**
     * Adds a title to a buffered image
     *
     * @param image the input image
     * @param title the title to put on the image
     * @param fontSize
     * @return the buffered image with a title
     */
    public static BufferedImage SetImageTitle(BufferedImage image, String title, int fontSize) {
        return SetImageSubTitle(image, title, fontSize, 0, 0);
    }

    /**
     * Adds a title to a buffered image
     *
     * @param image the input image
     * @param title the title to put on the image
     * @param fontSize
     * @param x
     *  * @param y
     * @param y
     * @return the buffered image with a title
     */
    public static BufferedImage SetImageSubTitle(BufferedImage image, String title, int fontSize, int x, int y) {
        if (title == null) {
            title = "";
        }
        BufferedImage framedImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());

        Graphics2D graph = framedImage.createGraphics();
        graph.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
        graph.setPaint(Color.white);
        Font font = new Font("Monospaced", Font.BOLD, fontSize);
        graph.setFont(font);
        FontMetrics fontMetrics = graph.getFontMetrics(font);
        graph.drawString(title, (int) (x - (fontMetrics.getStringBounds(title, graph).getWidth() / 2)), y);
        graph.dispose();

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
    public static BufferedImage HighlightZone(BufferedImage image, int minX, int maxX, int minY, int maxY) {
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

    public static BufferedImage CreatePooledImage(BufferedImage[] images) {
        BufferedImage average = new BufferedImage(images[0].getWidth(), images[0].getHeight(), BufferedImage.TYPE_INT_ARGB);
        WritableRaster raster = average.getRaster().createCompatibleWritableRaster();
        for (int k = 0; k < images[0].getHeight(); ++k) {
            for (int j = 0; j < images[0].getWidth(); ++j) {
                float sum = 0.0f;
                for (int i = 0; i < images.length; ++i) {
                    sum = sum + images[i].getRaster().getSample(j, k, 0);
                }
                raster.setSample(j, k, 0, Math.round(sum / images.length));
            }
        }
        average.setData(raster);
        return average;

    }

    public static ImageIcon scaleIcon(String iconPath, int width, int height) {
        ImageIcon imageIcon = new ImageIcon(
                ImageUtils.class.getResource(iconPath)
        );
        Image image = imageIcon.getImage(); // transform it 
        Image newimg = image.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
        return new ImageIcon(newimg);  // transform it back
    }

}
