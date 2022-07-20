package be.javasaurusstudios.histosnap.model.image;

import be.javasaurusstudios.histosnap.control.util.ImageUtils;
import be.javasaurusstudios.histosnap.control.util.color.ColorUtils;
import be.javasaurusstudios.histosnap.view.MSImagizer;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * This class represents a named MSiImage, which consists of a frame
 *
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public class MSiImage extends BufferedImage implements Serializable {

    /**
     * Enum containing the options for reference variable to determine the
     * color/brightness for a pixel
     */
    public enum ImageMode {
        TOTAL_ION_CURRENT, MIN, MAX, MEAN, MEDIAN, Q1, Q3, Q90, Q95, Q99
    }

    //The MSiFrame
    protected MSiFrame activeFrame;
    //The name for this frame (for example based on the mz-range)
    protected String name;

    /**
     * Constructor
     *
     * @param frame the input frame
     */
    public MSiImage(MSiFrame frame) {
        super(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
        this.activeFrame = frame;
        this.name = this.activeFrame.getName();
        if (this.name == null || this.name.isEmpty()) {
            this.name = "Default - " + System.currentTimeMillis();
            this.activeFrame.setName(this.name);
        }
        InitFrame(frame);
    }

    public MSiImage(MSiFrame frame, int width, int height) {
        super(width, height, BufferedImage.TYPE_INT_ARGB);
        InitFrame(frame);
    }

    protected void InitFrame(MSiFrame frame) {
        this.activeFrame = frame;
        this.name = this.activeFrame.getName();
        if (this.name == null || this.name.isEmpty()) {
            this.name = "Default - " + System.currentTimeMillis();
            this.activeFrame.setName(this.name);
        }
    }

    protected void annotateFrame(MSiFrame frame, int fontSize, int xOffset, int yOffset) {
        if (MSImagizer.instance.isDrawTitle()) {
            int titleX = xOffset + (int) Math.ceil(frame.getWidth() / 2);
            int titleY = yOffset + frame.getHeight() - 5;
            BufferedImage tmp = ImageUtils.SetImageSubTitle(
                    this,
                    frame.getName(),
                    (int) (Math.max(fontSize, Math.ceil((double) fontSize / MSImagizer.instance.getCurrentScale()))),
                    titleX,
                    titleY);
            Graphics g = getGraphics();
            g.clearRect(0, 0, getWidth(), getHeight());
            g.drawImage(tmp, 0, 0, null);
            g.dispose();
        }
    }

    /**
     * Clamps the intensity
     *
     * @param intensity the maximal intensity
     */
    public void LimitIntensity(double intensity) {
        this.activeFrame.clampIntensity(intensity);
    }

    /**
     * Removes hot spots
     *
     * @param bgPercentile the percentile to clamp to
     */
    public void RemoveHotSpots(int bgPercentile) {
        this.activeFrame.RemoveHotSpots(bgPercentile);
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
        this.activeFrame.setName(name);
    }

    public MSiFrame getFrame() {
        return activeFrame;
    }

    /**
     * Generates an image based on the reference mode en colors
     *
     * @param mode the reference mode
     * @param range the range
     */
    public void CreateImage(ImageMode mode, Color... range) {

        //clear background
        for (int i = 1; i < this.getWidth(); i++) {
            for (int j = 1; j < this.getHeight(); j++) {
                this.setRGB(i, j, range[0].getRGB());
            }
        }

        DescriptiveStatistics stat = new DescriptiveStatistics();
        for (MSiPixel pixel : activeFrame.getPixels()) {
            double frameValue = (mode == ImageMode.TOTAL_ION_CURRENT) ? pixel.getStat().getSum() : pixel.getStat().getMax();
            if (!Double.isNaN(frameValue)) {
                stat.addValue(frameValue);
            }
        }

        double reference = (mode == ImageMode.TOTAL_ION_CURRENT) ? stat.getMean() : getStat(mode, stat);

        for (MSiPixel pixel : activeFrame.getPixels()) {
            double check = getStat(mode, pixel.getStat());
            double rel = Math.min(1, Math.max(0, check / reference));

            Color color;
            if (mode == ImageMode.TOTAL_ION_CURRENT) {
                //TODO check if TIC is inverted as a special case or everything has to be inverted
                color = rel == 0 ? range[0] : ColorUtils.getHeatMapColor(1 - rel, range);
            } else {
                color = rel == 0 ? range[range.length - 1] : ColorUtils.getHeatMapColorInverse(rel, range);
            }

            if (pixel.getX() > 0 && pixel.getX() < this.getWidth() && pixel.getY() > 0 && pixel.getY() < this.getHeight()) {
                this.setRGB(pixel.getX(), pixel.getY(), color.getRGB());
            }
        }

        DrawGrid(getWidth(), getHeight(), 1, 1);
        annotateFrame(activeFrame, 12, 0, 0);
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
    protected double getStat(ImageMode mode, DescriptiveStatistics stat) {
        switch (mode) {
            case TOTAL_ION_CURRENT:
                return stat.getSum();
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

    protected void DrawGrid(int singleWidth, int singleHeight, int cols, int rows) {
        //draw grid on top
        if (MSImagizer.instance.isDrawGrid()) {
            for (int i = 0; i <= cols; i++) {
                int startingX = i * singleWidth;
                for (int j = startingX - 1; j < startingX + 1; j++) {
                    DrawVertical(j);
                }
            }

            for (int i = 0; i <= rows; i++) {
                int startingY = i * singleHeight;
                for (int j = startingY - 1; j < startingY + 1; j++) {
                    DrawHorizontal(j);
                }
            }
        }
    }

    private void DrawHorizontal(int y) {
        for (int j = 0; j < getWidth(); j++) {
            int h = y;
            if (h >= 0 && h < getHeight()) {
                this.setRGB(j, h, Color.white.getRGB());
            }
        }
    }

    private void DrawVertical(int x) {
        for (int j = 0; j < getHeight(); j++) {
            int w = x;
            if (w >= 0 && w < getWidth()) {
                this.setRGB(w, j, Color.white.getRGB());
            }
        }
    }

    public static MSiImage CreateCombinedImage(MultiMSiImage img) {
        List<MSiImage> images = new ArrayList<>();
        for (MSiFrame frame : img.getFrames()) {
            images.add(new MSiImage(frame));
        }
        return CreateCombinedImage(images);
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

                            for (int j = 0; j < otherPixel.getMz().size(); j++) {
                                iStats.addValue(otherPixel.getI().get(j));
                            }
                            mz.addAll(otherPixel.getMz());
                            intensity.addAll(otherPixel.getI());
                        }
                    }
                }

                //filter out to 95th percentile
                double threshold = iStats.getPercentile(50);
                for (int i = mz.size() - 1; i > 0; i--) {
                    if (intensity.get(i) > threshold) {
                        newPixel.addDataPoint(mz.get(i), intensity.get(i));
                    }
                }

                frame.AddPixel(newPixel);
            }
        }

        MSiImage image = new MSiImage(frame);
        image.setName("Combined");

        return image;
    }

}
