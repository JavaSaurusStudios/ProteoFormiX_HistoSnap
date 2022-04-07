package be.javasaurusstudios.histosnap.control.util.color;

import java.awt.Color;
import java.util.LinkedList;

/**
 * This class provides some easy methods to manipulate colors /color ranges
 *
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public class ColorUtils {

    /**
     * Interpolate between two colors
     *
     * @param a the first color
     * @param b the second color
     * @param f the ratio (1=second, 0 = first)
     * @return the linearly interpolated color
     */
    public static Color interpolate(Color a, Color b, double f) {
        double rf = 1 - f;
        int red = (int) (a.getRed() * rf + b.getRed() * f);
        int green = (int) (a.getGreen() * rf + b.getGreen() * f);
        int blue = (int) (a.getBlue() * rf + b.getBlue() * f);
        int alpha = (int) (a.getAlpha() * rf + b.getAlpha() * f);

        return new Color(Math.min(255, red), Math.min(255, green), Math.min(255, blue), Math.min(255, alpha));
    }

    /**
     * Creates a color based on a bin of colors and a value
     *
     * @param f the linear movement (between 0 and 1)
     * @param colors the range of provided colors
     * @return the color bin the f value is contained within
     */
    public static Color getHeatMapColor(double f, Color... colors) {
        double binSize = 1.0 / colors.length;

        for (int bin = 0; bin < colors.length; bin++) {
            if (f <= (binSize * bin)) {
                return colors[bin];
            }
        }
        return colors[0];
    }

    /**
     * Creates a inverted color based on a bin of colors and a value
     *
     * @param f the linear movement (between 0 and 1)
     * @param colors the range of provided colors
     * @return the color bin the f value is contained within
     */
    public static Color getHeatMapColorInverse(double f, Color... colors) {
        double binSize = 1.0 / colors.length;

        for (int bin = colors.length - 1; bin >= 0; bin--) {
            if (f >= (binSize * bin)) {
                return colors[bin];
            }
        }
        return colors[colors.length - 1];
    }

    /**
     * Creates a multi gradient
     *
     * @param numSteps amount of steps in a gradient
     * @param colors the colors the gradient needs to consider
     * @return an array of colors in the multi gradient
     */
    public static Color[] createGradient(final int numSteps, final Color... colors) {
        LinkedList<Color> colorList = new LinkedList<>();
        for (int i = 0; i < colors.length - 1; i++) {
            Color[] partition = createGradient(numSteps / colors.length, colors[i], colors[i + 1]);
            for (Color color : partition) {
                colorList.add(color);
            }
        }

        return colorList.toArray(new Color[colorList.size()]);
    }

    /**
     * Creates a gradient between three colors
     *
     * @param numSteps amount of steps in a gradient
     * @param one first color
     * @param two second color
     * @param three third color
     * @return an array of colors in the multi gradient
     */
    public static Color[] createGradient(final int numSteps, final Color one, final Color two, final Color three) {

        Color[] partition1 = createGradient(numSteps / 2, one, two);
        Color[] partition2 = createGradient(numSteps / 2, two, three);
        Color[] total = new Color[partition1.length + partition2.length];
        for (int i = 0; i < total.length; i++) {
            if (i < partition1.length) {
                total[i] = partition1[i];
            } else {
                total[i] = partition2[i - partition1.length];
            }
        }
        return total;
    }

    /**
     * Creates a gradient between two colors
     *
     * @param numSteps amount of steps in a gradient
     * @param one first color
     * @param two second color
     * @return an array of colors in the multi gradient
     */
    public static Color[] createGradient(final int numSteps, final Color one, final Color two) {
        int r1 = one.getRed();
        int g1 = one.getGreen();
        int b1 = one.getBlue();
        int a1 = one.getAlpha();

        int r2 = two.getRed();
        int g2 = two.getGreen();
        int b2 = two.getBlue();
        int a2 = two.getAlpha();

        int newR;
        int newG;
        int newB;
        int newA;

        Color[] gradient = new Color[numSteps];
        double iNorm;
        for (int i = 0; i < numSteps; i++) {
            iNorm = i / (double) numSteps; //a normalized [0:1] variable
            newR = (int) (r1 + iNorm * (r2 - r1));
            newG = (int) (g1 + iNorm * (g2 - g1));
            newB = (int) (b1 + iNorm * (b2 - b1));
            newA = (int) (a1 + iNorm * (a2 - a1));
            gradient[i] = new Color(newR, newG, newB, newA);
        }

        return gradient;
    }

    public static Color Average(Color first, Color second) {
        return new Color(Average(first.getRGB(), second.getRGB()));
    }

    /**
     * Calculate the average value between two int colors
     *
     * @param argb1 input rgb value 1
     * @param argb2 input rgb value 2
     * @return the average rgb int
     */
    public static int Average(int argb1, int argb2) {
        return (((argb1 & 0xFF) + (argb2 & 0xFF)) >> 1)
                | //b
                (((argb1 >> 8 & 0xFF) + (argb2 >> 8 & 0xFF)) >> 1) << 8
                | //g
                (((argb1 >> 16 & 0xFF) + (argb2 >> 16 & 0xFF)) >> 1) << 16
                | //r
                (((argb1 >> 24 & 0xFF) + (argb2 >> 24 & 0xFF)) >> 1) << 24;  //a
    }

}
