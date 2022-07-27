package be.javasaurusstudios.histosnap.model.image;

import be.javasaurusstudios.histosnap.control.util.color.ColorUtils;
import be.javasaurusstudios.histosnap.view.MSImagizer;
import be.javasaurusstudios.histosnap.view.component.ProgressBar;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * This class represents a named MSiImage, which consists of a frame
 *
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public class MultiMSiImage extends MSiImage {

    private final List<MSiFrame> frames;

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
        int rowsNeeded = 1 + (int) Math.floor(frames.size() / columns);
        return new MultiMSiImage(
                frames,
                frames.size() < columns ? frames.size() : columns,
                frames.size() <= columns ? 1 : rowsNeeded,
                singleWidth,
                singleHeight);
    }

    /**
     * Creates a new multi msi image based on input frames and a column nr
     *
     * @param frames
     * @return a new instance of a multi image
     */
    public static MultiMSiImage Generate(List<MSiFrame> frames) {
        return Generate(frames, MSImagizer.instance.getColumnCount());
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

        ProgressBar bar = MSImagizer.instance.getProgressBar();

        bar.setValueText(0, "Generating image...", true);

        Graphics graphics = getGraphics();
        graphics.setColor(Color.LIGHT_GRAY);
        graphics.setFont(new Font("Arial Black", Font.BOLD, 20));

        int singleWidth = frames.get(0).getWidth();
        int singleHeight = frames.get(0).getHeight();
        //clear background
        for (int i = 1; i < this.getWidth(); i++) {
            for (int j = 1; j < this.getHeight(); j++) {
                this.setRGB(i, j, range[0].getRGB());
            }
        }

        int xCoordinate = 0;
        int yCoordinate = 0;

        float value = 0;
        for (int z = 0; z < frames.size(); z++) {
            value++;
            bar.setValueText(value / frames.size(), "Generating image :" + 100 * value / frames.size() + "%", false);

            MSiFrame frame = frames.get(z);
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

            frame.getPixels().forEach((pixel) -> {
                double check = getStat(mode, pixel.getStat());
                double rel = Math.min(1, Math.max(0, check / reference));
                rel = mode == ImageMode.TOTAL_ION_CURRENT ? 1 - rel : rel;
                Color color = rel == 0 ? range[0] : ColorUtils.getHeatMapColor(rel, range);

                //THIS IS THE TRICKY PART, THIS IS WHERE IT ACTUALLY GETS PUT ON SCREEN...
                int fullPixelX = pixel.getX() + xOffset;
                int fullPixelY = (pixel.getY() + yOffset);
                if (fullPixelX > 0 && fullPixelX < getWidth()
                        && fullPixelY > 0 && fullPixelY < getHeight()) {
                    //draw a border
                    this.setRGB(fullPixelX, fullPixelY, color.getRGB());
                }
            });

            frame.setxCoordinate(xCoordinate);
            frame.setyCoordinate((rows - 1) - yCoordinate);

            frame.setRect(new Rectangle(xOffset, yOffset, singleWidth, singleHeight));

            xCoordinate++;
            if (xCoordinate >= cols) {
                xCoordinate = 0;
                yCoordinate++;
            }

        }

        DrawGrid(singleWidth, singleHeight, cols, rows);

        xCoordinate = 0;
        yCoordinate = 0;

        bar.setValueText(0, "Annotating frames...", true);

        for (int z = 0; z < frames.size(); z++) {
            int xOffset = xCoordinate * singleWidth;
            int yOffset = yCoordinate * singleHeight;
            annotateFrame(frames.get(z), 12, xOffset, yOffset);

            xCoordinate++;
            if (xCoordinate >= cols) {
                xCoordinate = 0;
                yCoordinate++;
            }

        }
    }

    /**
     * Generates an image based on the reference mode en colors
     *
     * @param index
     * @param mode the reference mode
     * @param range the range
     * @return
     */
    public BufferedImage CreateSingleImage(int index, ImageMode mode, Color... range) {

        if (index > 0 && index < getFrames().size()) {
            activeFrame = getFrames().get(index);
        }

        //clear background
        for (int i = 1; i < this.getWidth(); i++) {
            for (int j = 1; j < this.getHeight(); j++) {
                this.setRGB(i, j, range[0].getRGB());
            }
        }

        DescriptiveStatistics stat = new DescriptiveStatistics();
        for (MSiPixel pixel : activeFrame.getPixels()) {
            double frameValue = getStat(mode, pixel.getStat());
            if (!Double.isNaN(frameValue)) {
                stat.addValue(frameValue);
            }
        }

        //  double reference = (mode == ImageMode.TOTAL_ION_CURRENT) ? stat.getMean() : getStat(mode, stat);
        double reference = stat.getMean();

        for (MSiPixel pixel : activeFrame.getPixels()) {
            double check = getStat(mode, pixel.getStat());
            double rel = Math.min(1, Math.max(0, check / reference));
            rel = mode == ImageMode.TOTAL_ION_CURRENT ? 1 - rel : rel;
            Color color = rel == 0 ? range[0] : ColorUtils.getHeatMapColor(rel, range);

            if (pixel.getX() > 0 && pixel.getX() < activeFrame.getWidth() && pixel.getY() > 0 && pixel.getY() < activeFrame.getHeight()) {
                this.setRGB(pixel.getX(), pixel.getY(), color.getRGB());
            }
        }

        DrawGrid(activeFrame.getWidth(), activeFrame.getHeight(), 1, 1);
        annotateFrame(activeFrame, 12, 0, 0);

        //not sure if needed
        BufferedImage copy = new BufferedImage(activeFrame.getWidth(), activeFrame.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = copy.createGraphics();
        g.drawImage(this, 0, 0, null);
        g.dispose();
        return copy;
    }

    @Override
    public MSiFrame getClickedFrame(int x, int y) {

        for (MSiFrame frame : getFrames()) {

            int checkWidthStart = frame.getXCoordinate() * frame.getWidth();
            int checkWidthEnd = checkWidthStart + frame.getWidth();
            int checkHeightStart = frame.getYCoordinate() * frame.getHeight();
            int checkHeightEnd = checkHeightStart + frame.getHeight();

            if (x > checkWidthStart && x < checkWidthEnd) {
                if (y < checkHeightStart && y > checkHeightEnd) {
                    return frame;
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return getName();
    }

    public List<MSiFrame> getFrames() {
        return frames;
    }

}
