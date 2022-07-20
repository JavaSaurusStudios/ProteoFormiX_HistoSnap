package be.javasaurusstudios.histosnap.model.image;

import be.javasaurusstudios.histosnap.control.util.color.ColorUtils;
import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * This class represents a named MSiImage, which consists of a frame
 *
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public class MultiMSiImage extends MSiImage {

    List<MSiFrame> frames;

    /**
     * Creates a new multi msi image based on input frames and a column nr
     *
     * @param frames
     * @param columns
     * @return a new instance of a multi image
     */
    public static MultiMSiImage Generate(List<MSiFrame> frames, int columns) {
        int singleWidth = frames.get(0).getWidth();
        int singleHeight = frames.get(0).getHeight();
        int rowsNeeded = frames.size() / columns;
        return new MultiMSiImage(frames, columns, rowsNeeded, singleWidth, singleHeight);
    }

    /**
     * Creates a new multi msi image based on input frames and a column nr
     *
     * @param frames
     * @return a new instance of a multi image
     */
    public static MultiMSiImage Generate(List<MSiFrame> frames) {
        return Generate(frames, 3);
    }
    private final int cols;
    private final int rows;

    /**
     * Constructor
     *
     * @param frame the input frame
     */
    private MultiMSiImage(List<MSiFrame> frames, int cols, int rows, int width, int height) {
        super(frames.get(0), cols * width, rows * height);
        this.frames = frames;
        this.cols = cols;
        this.rows = rows;
    }

    /**
     * Generates an image based on the reference mode en colors
     *
     * @param mode the reference mode
     * @param range the range
     */
    @Override
    public void CreateImage(ImageMode mode, Color... range) {

        int singleWidth = frames.get(0).getWidth();
        int singleHeight = frames.get(0).getHeight();
        //clear background
        for (int i = 1; i < this.getWidth(); i++) {
            for (int j = 1; j < this.getHeight(); j++) {
                this.setRGB(i, j, range[0].getRGB());
            }
        }

        for (int i = 0; i < frames.size(); i++) {

            MSiFrame frame = frames.get(i);

            int xCoordinate = i % this.cols;
            int yCoordinate = i / this.cols;

            int xOffset = xCoordinate * singleWidth;
            int yOffset = yCoordinate * singleHeight;

            DescriptiveStatistics stat = new DescriptiveStatistics();
            for (MSiPixel pixel : frame.getPixels()) {
                double frameValue = (mode == ImageMode.TOTAL_ION_CURRENT) ? pixel.getStat().getSum() : pixel.getStat().getMax();
                if (!Double.isNaN(frameValue)) {
                    stat.addValue(frameValue);
                }
            }

            double reference = (mode == ImageMode.TOTAL_ION_CURRENT) ? stat.getMean() : getStat(mode, stat);

            for (MSiPixel pixel : frame.getPixels()) {
                double check = getStat(mode, pixel.getStat());
                double rel = Math.min(1, Math.max(0, check / reference));

                Color color;
                if (mode == ImageMode.TOTAL_ION_CURRENT) {
                    //TODO check if TIC is inverted as a special case or everything has to be inverted
                    color = rel == 0 ? range[0] : ColorUtils.getHeatMapColor(1 - rel, range);
                } else {
                    color = rel == 0 ? range[range.length - 1] : ColorUtils.getHeatMapColorInverse(rel, range);
                }

                //THIS IS THE TRICKY PART, THIS IS WHERE IT ACTUALLY GETS PUT ON SCREEN...
                if (pixel.getX() > 0 && pixel.getX() < singleWidth && pixel.getY() > 0 && pixel.getY() < singleHeight) {
                    this.setRGB(xOffset + pixel.getX(), yOffset + pixel.getY(), color.getRGB());
                }
            }

        }
    }

    @Override
    public String toString() {
        return getName();
    }

}
