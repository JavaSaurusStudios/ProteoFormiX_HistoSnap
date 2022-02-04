package be.javasaurusstudios.msimagizer.model.image;

import be.javasaurusstudios.msimagizer.control.util.ImageUtils;
import be.javasaurusstudios.msimagizer.control.util.color.ColorUtils;
import be.javasaurusstudios.msimagizer.view.MSImagizer;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * This class represents a named MSiImage, which consists of a frame
 *
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public class MSiImage extends BufferedImage {

    /**
     * Enum containing the options for reference variable to determine the
     * color/brightness for a pixel
     */
    public enum ImageMode {
        MIN, MAX, MEAN, MEDIAN, Q1, Q3, Q90, Q95, Q99
    }

    //The MSiFrame
    private final MSiFrame frame;
    //The name for this frame (for example based on the mz-range)
    private String name;

    /**
     * Constructor
     *
     * @param frame the input frame
     */
    public MSiImage(MSiFrame frame) {
        super(frame.getWidth() + 1, frame.getHeight() + 2, BufferedImage.TYPE_INT_ARGB);
        this.frame = frame;
        this.name = "Default - " + System.currentTimeMillis();
    }

    /**
     * Clamps the intensity
     *
     * @param intensity the maximal intensity
     */
    public void LimitIntensity(double intensity) {
        this.frame.clampIntensity(intensity);
    }

    /**
     * Removes hot spots
     *
     * @param bgPercentile the percentile to clamp to
     */
    public void RemoveHotSpots(int bgPercentile) {
        this.frame.RemoveHotSpots(bgPercentile);
    }

    /**
     * Save to the specified output file
     *
     * @param outputfile the requested outputfile
     * @throws IOException
     */
    public void SaveToFile(File outputfile) throws IOException {
        SaveToFile(outputfile, 1);
    }

    /**
     * Save to the specified output file
     *
     * @param outputfile the requested outputfile
     * @param scale the pixel scale for the output image
     * @throws IOException
     */
    public void SaveToFile(File outputfile, int scale) throws IOException {
        ImageIO.write(scale == 1 ? this : getScaledImage(scale), "png", outputfile);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MSiFrame getFrame() {
        return frame;
    }

    /**
     * Generates an image based on the reference mode en colors
     *
     * @param mode the reference mode
     * @param range the range
     */
    public void CreateImage(ImageMode mode, Color... range) {

        //clear background
        for (int i = 1; i < getWidth(); i++) {
            for (int j = 1; j < getHeight(); j++) {
                setRGB(i, j, range[0].getRGB());
            }
        }

        DescriptiveStatistics stat = new DescriptiveStatistics();
        for (MSiPixel pixel : frame.getPixels()) {
            double frameValue = pixel.getStat().getMax();
            if (!Double.isNaN(frameValue)) {
                stat.addValue(frameValue);
            }
        }

        double reference = getStat(mode, stat);

        for (MSiPixel pixel : frame.getPixels()) {
            double check = getStat(mode, pixel.getStat());
            double rel = Math.min(1, Math.max(0, check / reference));
            Color color = rel == 0 ? range[0] : ColorUtils.getHeatMapColor(rel, range);
            if (pixel.getX() > 0 && pixel.getX() < getWidth() && pixel.getY() > 0 && pixel.getY() < getHeight()) {
                setRGB(pixel.getX(), pixel.getY(), color.getRGB());
            }
        }

    }

    /**
     * Resizes the image based on the input scale
     *
     * @param scale the input scale (preferably a power of 2)
     * @return a new scaled image
     */
    public BufferedImage getScaledImage(int scale) {
        BufferedImage before = this;
        int w = before.getWidth();
        int h = before.getHeight();
        BufferedImage after = new BufferedImage(w * scale, h * scale, BufferedImage.TYPE_INT_ARGB);
        AffineTransform at = new AffineTransform();
        at.scale(scale, scale);
        AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return scaleOp.filter(before, after);
    }

    /**
     * Returns the statistics in a certain mode
     *
     * @param mode The reference mode
     * @param stat the Input statistics object
     * @return the parameter that is applicable for the selected reference mode
     */
    private double getStat(ImageMode mode, DescriptiveStatistics stat) {
        switch (mode) {
            case MEAN:
                return stat.getMean();

            case MAX:
                return stat.getMax();

            case MIN:
                return stat.getMin();
            case MEDIAN:
                return stat.getPercentile(50);
            case Q1:
                return stat.getPercentile(25);
            case Q3:
                return stat.getPercentile(75);
            case Q90:
                return stat.getPercentile(90);
            case Q95:
                return stat.getPercentile(95);
            case Q99:
                return stat.getPercentile(99);
        }
        return stat.getMean();
    }

    @Override
    public String toString() {
        return getName();
    }


    public static MSiImage CreateCombinedImage(List<MSiImage> images) {

        MSiFrame frame = new MSiFrame();
        frame.setWidth(images.get(0).getFrame().getWidth());
        frame.setHeight(images.get(0).getFrame().getHeight());

        for (int x = 0; x < frame.getWidth(); x++) {
            for (int y = 0; y < frame.getHeight(); y++) {

                MSiPixel newPixel = new MSiPixel(x, y);

                DescriptiveStatistics iStats = new DescriptiveStatistics();

                List<Double> mz = new ArrayList<>();
                List<Double> intensity = new ArrayList<>();

                for (int i = 0; i < images.size(); i++) {
                    for (MSiPixel otherPixel : images.get(i).getFrame().getPixels()) {
                        if (otherPixel.getX() == x && otherPixel.getY() == y) {

                            for (int j = 0; j < otherPixel.getMz().length; j++) {
                                iStats.addValue(otherPixel.getI()[j]);
                            }
                            mz.addAll(Arrays.asList(otherPixel.getMz()));
                            intensity.addAll(Arrays.asList(otherPixel.getI()));
                        }
                    }
                }

                //filter out to 95th percentile
                List<Double> mzPass = new ArrayList<>();
                List<Double> intensityPass = new ArrayList<>();

                double threshold = iStats.getPercentile(50);
                for (int i = mz.size() - 1; i > 0; i--) {
                    if (intensity.get(i) > threshold) {
                        mzPass.add(mz.get(i));
                        intensityPass.add(intensity.get(i));
                    }
                }

                newPixel.setMz(mzPass.toArray(new Double[mzPass.size()]));
                newPixel.setI(intensityPass.toArray(new Double[intensityPass.size()]));

                frame.AddPixel(newPixel);
            }
        }

        MSiImage image = new MSiImage(frame);
        image.setName("Combined");

        return image;
    }

}
