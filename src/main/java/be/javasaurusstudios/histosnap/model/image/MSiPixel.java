package be.javasaurusstudios.histosnap.model.image;

import java.util.LinkedList;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * A pixel with mass to charge ratios and related intensities.
 *
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public class MSiPixel implements Comparable {

    private final int x;
    private final int y;
    private LinkedList<Double> mz;
    private LinkedList<Double> i;

    public MSiPixel(int x, int y) {
        this.x = x;
        this.y = y;
        this.mz = new LinkedList<>();
        this.i = new LinkedList<>();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public LinkedList<Double> getMz() {
        return mz;
    }

    public LinkedList<Double> getI() {
        return i;
    }

    public void addDataPoint(double mz, double i) {
        this.mz.addLast(mz);
        this.i.addLast(i);
    }

    /**
     * Gets a descriptive statistics object for the intensities in this
     * particular pixel
     *
     * @return the descriptive stats object
     */
    public DescriptiveStatistics getStat() {
        DescriptiveStatistics stat = new DescriptiveStatistics();
        for (Double intensity : i) {
            stat.addValue(intensity);
        }
        return stat;
    }

    /**
     * Clamps the values to the given percentile value
     *
     * @param percentile the maximal percentile to consider
     */
    public void RemoveHotSpots(int percentile) {
        DescriptiveStatistics stats = getStat();
        LinkedList<Double> tmpI = new LinkedList<>();
        double threshold = stats.getPercentile(percentile);
        for (Double intensity : i) {
            Double tmp = Math.min(threshold, intensity);
            if (tmp < 0) {
                tmp = 0.0;
            }
            tmpI.add(tmp);
        }
        this.i = tmpI;
    }

    /**
     * Clamps the intensity to a set value
     *
     * @param clampValue the maximal intensity to consider
     */
    public void clampIntensity(double clampValue) {
        LinkedList<Double> tmpI = new LinkedList<>();
        for (Double intensity : i) {
            Double tmp = Math.min(intensity, clampValue);
            if (tmp < 0) {
                tmp = 0.0;
            }
            tmpI.add(tmp);
        }
        this.i = tmpI;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + this.x;
        hash = 59 * hash + this.y;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MSiPixel other = (MSiPixel) obj;
        if (this.x != other.x) {
            return false;
        }
        if (this.y != other.y) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(Object o) {

        if (!(o instanceof MSiPixel)) {
            return -1;
        }
        MSiPixel b = (MSiPixel) o;
        int result = Integer.compare((int) getX(), (int) b.getX());
        if (result == 0) {
            result = Integer.compare((int) getY(), (int) b.getY());
        }
        return result;
    }

}
