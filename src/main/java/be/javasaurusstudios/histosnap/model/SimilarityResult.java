package be.javasaurusstudios.histosnap.model;

import java.awt.image.BufferedImage;

/**
 * A result similarity
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public class SimilarityResult {

    /**
     * The name of the similarity result between two images
     */
    private final String name;
    /**
     * The image to consider
     */
    private final BufferedImage img;
    /**
     * The similarity percentage between this 
     */
    private final Double similarity;
    /**
     * The original image (reference image?)
     */
    private final BufferedImage original;

    /**
     * Constructor
     * @param name the name for the similarity result (should be the name of the image to consider)
     * @param img the input image
     * @param original the reference image
     * @param similarity the similarity value
     */
    public SimilarityResult(String name, BufferedImage img, BufferedImage original, Double similarity) {
        this.name = name;
        this.img = img;
        this.original = original;
        this.similarity = similarity;

    }

    public String getName() {
        return name;
    }

    public BufferedImage getImg() {
        return img;
    }

    public Double getSimilarity() {
        return similarity;
    }

    public BufferedImage getOriginal() {
        return original;
    }

}
