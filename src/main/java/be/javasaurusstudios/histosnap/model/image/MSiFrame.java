package be.javasaurusstudios.histosnap.model.image;

import be.javasaurusstudios.histosnap.control.util.UILogger;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public class MSiFrame implements Serializable {

    private static final long serialVersionUID = 1234567L;
    /**
     * The collection of pixels in this frame
     */
    private final HashSet<MSiPixel> pixels;
    /**
     * The width of the frame
     */
    private int width;
    /**
     * The height of the frame
     */
    private int height;
    /**
     * The maximal MZ value to consider
     */
    private double maxMz;
    /**
     * The minimal MZ value to consider
     */
    private double minMz;
    /**
     * The input parentFile
     */
    private String parentFile;
    /**
     * The name of the frame
     */
    /**
     * The xCoordinate of this frame in a composite image
     */
    private int xCoordinate;
    /**
     * The yCoordiante of this image in a composite image
     */
    private int yCoordinate;
    /**
     * Status indicating this frame is highlighted
     */
    private boolean isHighlighted;
    /**
     * The name of this frame
     */
    private String name;

    /**
     * A helper object to keep track of frame statistics
     */
    private final DescriptiveStatistics stat;
    /**
     * The rectangle this frame is contained in
     */
    private Rectangle rectangle;

    /**
     * Constructor
     */
    public MSiFrame() {
        pixels = new HashSet<>();
        stat = new DescriptiveStatistics();
    }

    /**
     * Adds a new pixel to the collection
     *
     * @param pixel the new pixel
     */
    public void addPixel(MSiPixel pixel) {
        if (!pixels.contains(pixel)) {
            pixels.add(pixel);
        }
        for (Double intensity : pixel.getI()) {
            stat.addValue(intensity);
        }
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public double getMaxMz() {
        return maxMz;
    }

    public void setMaxMz(double maxMz) {
        this.maxMz = maxMz;
    }

    public double getMinMz() {
        return minMz;
    }

    public void setMinMz(double minMz) {
        this.minMz = minMz;
    }

    public DescriptiveStatistics getStat() {
        return stat;
    }

    public HashSet<MSiPixel> getPixels() {
        return pixels;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getXCoordinate() {
        return xCoordinate;
    }

    public void setxCoordinate(int xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public int getYCoordinate() {
        return yCoordinate;
    }

    public void setyCoordinate(int yCoordinate) {
        this.yCoordinate = yCoordinate;
    }

    public String getParentFile() {
        return parentFile;
    }

    public void setParentFile(String parentFile) {
        this.parentFile = parentFile;
    }

    public MSiPixel getPixel(int x, int y) {
        List<MSiPixel> results = pixels.stream().filter(p -> p.getX() == x && p.getY() == y).collect(Collectors.toList());
        return results == null || results.isEmpty() ? new MSiPixel(x, y) : results.get(0);
    }

    /**
     * Removes hot spots in the background based on a percentile
     *
     * @param percentile the percentile value to be applied
     */
    public void removeHotSpots(int percentile) {

        UILogger.log("Postprocessing " + getName() + "...", UILogger.Level.INFO);

        int threads = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);
        ExecutorService executor = Executors.newFixedThreadPool(threads);

        List<MSiPixel> tmpList = new ArrayList<>();
        tmpList.addAll(pixels);

        List<List<MSiPixel>> parts = new ArrayList<>();
        final int N = tmpList.size();
        for (int i = 0; i < N; i += threads) {
            parts.add(new ArrayList<>(tmpList.subList(i, Math.min(N, i + threads)))
            );
        }

        parts.forEach((part) -> {
            Future<?> submit = executor.submit(() -> {
                part.forEach((pixel) -> {
                    pixel.removeHotSpots(percentile);
                });
            });
        });

        executor.shutdown();

        try {
            executor.awaitTermination(threads, TimeUnit.DAYS);
        } catch (InterruptedException ex) {
            Logger.getLogger(MSiFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Clamp the intensity to not exceed the given value
     *
     * @param maxIntensity the maximal intensity
     */
    public void clampIntensity(double maxIntensity) {
        pixels.forEach((pixel) -> {
            pixel.clampIntensity(maxIntensity);
        });
    }

    /**
     * Creates a smaller frame to calculate on
     *
     * @param minMz the minimal MZ
     * @param maxMz the maximal Mz
     * @return the new smaller frame
     */
    public MSiFrame createSubFrame(double minMz, double maxMz) {
        UILogger.log("Creating sub frame between " + minMz + " and " + maxMz);
        MSiFrame subFrame = new MSiFrame();
        subFrame.setHeight(height);
        subFrame.setWidth(width);
        getPixels().stream().map((pixel) -> {
            MSiPixel subPixel = new MSiPixel(pixel.getX(), pixel.getY());
            for (int i = 0; i < pixel.getMz().size(); i++) {
                if (pixel.getMz().get(i) >= minMz && pixel.getMz().get(i) <= maxMz) {
                    subPixel.addDataPoint(pixel.getMz().get(i), pixel.getI().get(i));
                }
            }
            return subPixel;
        }).forEachOrdered((subPixel) -> {
            subFrame.addPixel(subPixel);
        });

        return subFrame;
    }

    public void setHighlighted(boolean b) {
        isHighlighted = b;
    }

    public void setRect(Rectangle rectangle) {
        this.rectangle = rectangle;
    }

    public Rectangle getRect() {
        return this.rectangle;
    }

}
