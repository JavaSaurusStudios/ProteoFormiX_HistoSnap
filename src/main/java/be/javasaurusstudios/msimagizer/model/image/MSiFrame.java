package be.javasaurusstudios.msimagizer.model.image;

import be.javasaurusstudios.msimagizer.control.util.UILogger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public class MSiFrame {

    /**
     * The collection of pixels in this frame
     */
    private final List<MSiPixel> pixels;
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
     * A helper object to keep track of frame statistics
     */
    private final DescriptiveStatistics stat;

    /**
     * Constructor
     */
    public MSiFrame() {
        pixels = new ArrayList<>();
        stat = new DescriptiveStatistics();
    }

    /**
     * Adds a new pixel to the collection
     *
     * @param pixel the new pixel
     */
    public void AddPixel(MSiPixel pixel) {
        pixels.add(pixel);
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

    public List<MSiPixel> getPixels() {
        return pixels;
    }

    /**
     * Removes hot spots in the background based on a percentile
     *
     * @param percentile the percentile value to be applied
     */
    public void RemoveHotSpots(int percentile) {

        UILogger.Log("Postprocessing image...",UILogger.Level.INFO);

        int threads = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);
        ExecutorService executor = Executors.newFixedThreadPool(threads);

        List<List<MSiPixel>> parts = new ArrayList<List<MSiPixel>>();
        final int N = pixels.size();
        for (int i = 0; i < N; i += threads) {
            parts.add(new ArrayList<MSiPixel>(pixels.subList(i, Math.min(N, i + threads)))
            );
        }

        for (List<MSiPixel> part : parts) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    for (MSiPixel pixel : part) {
                        pixel.RemoveHotSpots(percentile);
                    }
                }
            });
        }

        executor.shutdown();

        try {
            executor.awaitTermination(threads, TimeUnit.DAYS);
        } catch (InterruptedException ex) {
            Logger.getLogger(MSiFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

        UILogger.Log("Done !",UILogger.Level.INFO);
        UILogger.Log("-----------------------------",UILogger.Level.INFO);
    }

    /**
     * Prints the contents of this frame to the console
     */
    public void Print() {
        Collections.sort(pixels);
        pixels.forEach((pixel) -> {
            UILogger.Log(pixel.getX() + "," + pixel.getY(),UILogger.Level.INFO);
        });
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
     * @param intensityThreshold
     * @return the new smaller frame
     */
    public MSiFrame CreateSubFrame(double minMz, double maxMz, double intensityThreshold) {
        MSiFrame subFrame = new MSiFrame();
        subFrame.setHeight(height);
        subFrame.setWidth(width);
        for (MSiPixel pixel : getPixels()) {
            MSiPixel subPixel = new MSiPixel(pixel.getX(), pixel.getY());
            ArrayList<Double> mzValues = new ArrayList<>();
            ArrayList<Double> intensityValues = new ArrayList<>();
            for (int i = 0; i < pixel.getMz().length; i++) {
                if (pixel.getI()[i] >= intensityThreshold) {
                    if (pixel.getMz()[i] >= minMz && pixel.getMz()[i] <= maxMz) {
                        mzValues.add(pixel.getMz()[i]);
                        intensityValues.add(pixel.getI()[i]);
                    }
                }
            }
            subPixel.setI(intensityValues.toArray(new Double[intensityValues.size()]));
            subPixel.setMz(mzValues.toArray(new Double[mzValues.size()]));
            subFrame.AddPixel(subPixel);
        }

        return subFrame;
    }

    public MSiFrame CreateSubFrame(double minMz, double maxMz) {
        return CreateSubFrame(minMz, maxMz, 0);
    }

}
