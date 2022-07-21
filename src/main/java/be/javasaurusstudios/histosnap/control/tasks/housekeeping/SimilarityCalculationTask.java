package be.javasaurusstudios.histosnap.control.tasks.housekeeping;

import be.javasaurusstudios.histosnap.control.util.UILogger;
import be.javasaurusstudios.histosnap.model.SimilarityResult;
import be.javasaurusstudios.histosnap.model.task.WorkingTask;
import be.javasaurusstudios.histosnap.view.MSImagizer;
import be.javasaurusstudios.histosnap.view.component.ProgressBar;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * This class represents a working task to calculate the similarity between a
 * reference and a set of images
 *
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public class SimilarityCalculationTask extends WorkingTask {

    //The reference image
    private final BufferedImage refImage;
    //The name of the reference image
    private final String referenceName;
    //The other images to compare to the reference
    private final BufferedImage[] images;
    //The names of the images that will be considering
    private final String[] names;
    //A map containing a link between the name and the original buffered Image
    private final HashMap<String, BufferedImage> originals = new HashMap<>();
    //The similarity threshold = how much tolerance is allowed to be exceeded before an image is considered an "outlier"
    private final double threshold;

    /**
     * Constructor
     *
     * @param minX The minimum X value of the frame that needs to be checked
     * @param maxX The maximum X value of the frame that needs to be checked
     * @param minY The minimum Y value of the frame that needs to be checked
     * @param maxY The maximum Y value of the frame that needs to be checked
     * @param images The collection of images that will be considered
     * @param names The names of the images that need be considered
     * @param reference The reference image
     * @param referenceName The name of the reference image
     * @param percentage the allowed similarity tolerance
     */
    public SimilarityCalculationTask( int minX, int maxX, int minY, int maxY, BufferedImage[] images, String[] names, BufferedImage reference, String referenceName, double percentage) {
        super();
        this.names = names;
        this.refImage = createSubImage(reference, minX, maxX, minY, maxY);
        this.threshold = percentage;
        this.referenceName = referenceName;
        this.images = createSubImages(images, minX, maxX, minY, maxY);
        for (int i = 0; i < names.length; i++) {
            originals.put(names[i], images[i]);
        }

    }

    /**
     * Constructor
     *
     * @param images The collection of images that will be considered
     * @param names The names of the images that need be considered
     * @param reference The reference image
     * @param referenceName The name of the reference image
     * @param percentage the allowed similarity tolerance
     */
    public SimilarityCalculationTask( BufferedImage[] images, String[] names, BufferedImage reference, String referenceName, double percentage) {
        super();
        this.names = names;
        this.refImage = reference;
        this.threshold = percentage;
        this.referenceName = referenceName;
        this.images = images;
        for (int i = 0; i < names.length; i++) {
            originals.put(names[i], images[i]);
        }
    }

    @Override
    public Object call() throws Exception {
        return CheckForDerivations();
    }

    /**
     * Extracts sub images based on the originals in a reference frame
     *
     * @param originals the original images
     * @param minX The minimum X value of the frame that needs to be checked
     * @param maxX The maximum X value of the frame that needs to be checked
     * @param minY The minimum Y value of the frame that needs to be checked
     * @param maxY The maximum Y value of the frame that needs to be checked
     * @return
     */
    private BufferedImage[] createSubImages(BufferedImage[] originals, int minX, int maxX, int minY, int maxY) {
        int count = 0;
        BufferedImage[] subImages = new BufferedImage[originals.length];
        //PROBLEM ---> we are calculating coordinates on the entire label
        for (BufferedImage original : originals) {
            subImages[count] = createSubImage(original, minX, maxX, minY, maxY);
            count++;
        }
        return subImages;
    }

    /**
     * Create a single sub-image
     *
     * @param original the original image
     * @param minX The minimum X value of the frame that needs to be checked
     * @param maxX The maximum X value of the frame that needs to be checked
     * @param minY The minimum Y value of the frame that needs to be checked
     * @param maxY The maximum Y value of the frame that needs to be checked
     * @return
     */
    private BufferedImage createSubImage(BufferedImage original, int minX, int maxX, int minY, int maxY) {
        BufferedImage subImage = new BufferedImage(Math.abs(maxX - minX), Math.abs(maxY - minY), original.getType());
        for (int i = minX + 1; i < maxX - 1; i++) {
            for (int j = minY + 1; j < maxY - 1; j++) {
                int c = original.getRGB(i, j);
                subImage.setRGB(i - minX, j - minY, c);
            }
        }
        return subImage;
    }

    /**
     * Check for derivations
     *
     * @return a list of similarity results
     * @throws Exception
     */
    private List<SimilarityResult> CheckForDerivations() throws Exception {
        //TODO devise multiple strategies for this?
        DescriptiveStatistics stats = new DescriptiveStatistics();
        List<SimilarityResult> results = new ArrayList<>();
        for (int i = 0; i < images.length - 1; i++) {
            if (!referenceName.equalsIgnoreCase(names[i])) {
                 MSImagizer.instance.getProgressBar().setText("Calculating similarities (" + i + "/" + images.length + ")");
                double percentage = Compare(refImage == null ? images[0] : refImage, images[i + 1]);
                if (percentage >= threshold) {
                    stats.addValue(percentage);
                    results.add(new SimilarityResult(names[i + 1], images[i + 1], originals.get(names[i + 1]), percentage));
                }
            }
        }

        return results;
    }

    /**
     * Compares two bufferedimages
     *
     * @param img1 the first image
     * @param img2 the second image
     * @return a value indicating similarity
     * @throws IOException when the images are not of the same dimension
     */
    private double Compare(BufferedImage img1, BufferedImage img2) throws IOException {

        double percentage = 0;
        int w1 = img1.getWidth();
        int w2 = img2.getWidth();
        int h1 = img1.getHeight();
        int h2 = img2.getHeight();
        if ((w1 != w2) || (h1 != h2)) {
           UILogger.Log("Both images should have same dimensions",UILogger.Level.ERROR);
        } else {
            long diff = 0;
            for (int j = 0; j < h1; j++) {
                for (int i = 0; i < w1; i++) {

                    //Getting the RGB values of a pixel
                    int pixel1 = img1.getRGB(i, j);
                    Color color1 = new Color(pixel1, true);
                    int r1 = color1.getRed();
                    int g1 = color1.getGreen();
                    int b1 = color1.getBlue();
                    int pixel2 = img2.getRGB(i, j);
                    Color color2 = new Color(pixel2, true);
                    int r2 = color2.getRed();
                    int g2 = color2.getGreen();
                    int b2 = color2.getBlue();
                    //sum of differences of RGB values of the two images
                    long data = Math.abs(r1 - r2) + Math.abs(g1 - g2) + Math.abs(b1 - b2);
                    diff = diff + data;
                }

            }
            double avg = diff / (w1 * h1 * 3);
            percentage = (avg / 255) * 100;

        }
        return percentage;
    }

    @Override
    public String getFinishMessage() {
        return "Analysis completed ";
    }

}
