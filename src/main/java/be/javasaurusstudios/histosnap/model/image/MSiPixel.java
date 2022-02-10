package be.javasaurusstudios.histosnap.model.image;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * A pixel with mass to charge ratios and related intensities.
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public class MSiPixel implements Comparable {

    private final int x;
    private final int y;
    private Double[] mz;
    private Double[] i;

    public MSiPixel(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Double[] getMz() {
        return mz;
    }

    public void setMz(Double[] mz) {
        this.mz = mz;
    }

    public Double[] getI() {
        return i;
    }

    public void setI(Double[] i) {
        this.i = i;
    }

    /**
     * Gets a descriptive statistics object for the intensities in this particular pixel
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
     * @param percentile the maximal percentile to consider
     */
    public void RemoveHotSpots(int percentile) {
        DescriptiveStatistics stats = getStat();
        double threshold = stats.getPercentile(percentile);
        for (int j = 0; j < i.length; j++) {
            i[j] = Math.min(threshold, i[j]);
            if (i[j] < 0) {
                i[j] = 0.0;
            }
        }
    }

    /**
     * Clamps the intensity to a set value
     * @param intensity the maximal intensity to consider
     */
    public void clampIntensity(double intensity) {
        for (int j = 0; j < i.length; j++) {
            i[j] = Math.min(intensity, i[j]);
            if (i[j] < 0) {
                i[j] = 0.0;
            }
        }
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
