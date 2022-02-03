package be.javasaurusstudios.msimagizer.control.util.color;

import java.awt.Color;

/**
 * Enum containing the options for color ranges
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public enum ColorRange {

    BLUE_YELLOW(ColorUtils.createGradient(10000,
            new Color(70, 13, 95),
            new Color(70, 49, 125),
            new Color(56, 87, 140),
            new Color(36, 145, 141),
            new Color(42, 172, 125),
            new Color(123, 209, 81),
            new Color(229, 228, 29))),
    GREEN_RED(ColorUtils.createGradient(10000,
            new Color(0, 104, 55),
            new Color(26, 152, 80),
            new Color(102, 89, 99),
            new Color(166, 217, 106),
            new Color(217, 239, 139),
            new Color(254, 224, 139),
            new Color(253, 174, 97),
            new Color(244, 109, 67),
            new Color(215, 48, 39),
            new Color(165, 0, 38))),
    GREEN_PINK(ColorUtils.createGradient(10000,
            new Color(39, 100, 25),
            new Color(77, 146, 33),
            new Color(127, 188, 65),
            new Color(184, 225, 134),
            new Color(230, 245, 208),
            new Color(253, 224, 239),
            new Color(241, 182, 218),
            new Color(222, 119, 174),
            new Color(197, 17, 125),
            new Color(142, 1, 82))),
    RED_BLUE(ColorUtils.createGradient(10000,
            new Color(165, 0, 38),
            new Color(215, 48, 39),
            new Color(244, 109, 67),
            new Color(253, 174, 97),
            new Color(254, 224, 144),
            new Color(224, 243, 248),
            new Color(171, 217, 233),
            new Color(116, 173, 209),
            new Color(69, 117, 180),
            new Color(49, 54, 149))),
    GRAY_SCALE(ColorUtils.createGradient(10000, Color.BLACK, Color.white));

    private final Color[] colors;

    private ColorRange(Color... colors) {
        this.colors = colors;
    }

    public Color[] getColors() {
        return colors;
    }

}
