package be.javasaurusstudios.histosnap.view.handlers;

import be.javasaurusstudios.histosnap.control.util.UILogger;
import be.javasaurusstudios.histosnap.control.util.color.ColorRange;
import be.javasaurusstudios.histosnap.model.image.MSiImage;
import be.javasaurusstudios.histosnap.view.component.ProgressBar;
import java.util.LinkedHashMap;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public abstract class BackgroundExtractionHandler {

    /**
     * The parent JFrame
     */
    protected final JFrame parent;
    /**
     * The progress bar
     */
    protected final ProgressBar progressFrame;
    /**
     * The path to the data
     */
    protected final String path;
    /**
     * The image label
     */
    protected final JLabel lbImage;
    /**
     * The scale for this background
     */
    protected final int currentScale;
    /**
     * The color range for the background
     */
    protected final ColorRange currentRange;
    /**
     * THe current image mode
     */
    protected final MSiImage.ImageMode currentMode;

    /**
     * Constructor for a background process
     * @param parent the parentframe
     * @param progressFrame the progress bar
     * @param path the path to the data
     * @param lbImage the image canvas 
     * @param currentScale the current scale
     * @param currentRange the current color range
     * @param currentMode  the current image mode (relative to which parameter)
     */
    public BackgroundExtractionHandler(JFrame parent, ProgressBar progressFrame, String path, JLabel lbImage, int currentScale, ColorRange currentRange, MSiImage.ImageMode currentMode) {
        this.parent = parent;
        this.progressFrame = progressFrame;
        this.path = path;
        this.lbImage = lbImage;
        this.currentScale = currentScale;
        this.currentRange = currentRange;
        this.currentMode = currentMode;
    }

    public void Show(boolean isRandom) {
        JTextField samples = new JTextField();
        JTextField mzTolerance = new JTextField(".05");
        JTextField intensityThreshold = new JTextField("0.01");
        JTextField lowerRangeMZ = new JTextField();
        JTextField upperRangeMZ = new JTextField();

        samples.setText("10");
        lowerRangeMZ.setText("900");
        upperRangeMZ.setText("1200");

        final LinkedHashMap<String, JComponent> inputs = new LinkedHashMap<>();
        inputs.put("tolerance-label", new JLabel("Minimal Deviation (%)"));
        inputs.put("tolerance-field", mzTolerance);
        inputs.put("minMz-label", new JLabel("Minimal MZ"));
        inputs.put("minMz-field", lowerRangeMZ);
        inputs.put("maxMz-label", new JLabel("Maximal Mz"));
        inputs.put("maxMz-field", upperRangeMZ);
        inputs.put("minI-label", new JLabel("Intensity Threshold"));
        inputs.put("minI-field", intensityThreshold);
        if (isRandom) {
            inputs.put("samples-label", new JLabel("#Samples"));
            inputs.put("samples-field", samples);
        } else {
            inputs.put("combine-bg-checkbox", new JCheckBox("Auto-Combine"));
        }

        JComponent[] inputArray = new JComponent[inputs.values().size()];
        int i = 0;
        for (JComponent component : inputs.values()) {
            inputArray[i] = component;
            i++;
        }

        int result = JOptionPane.showConfirmDialog(parent, inputArray, isRandom ? "Generate Random Images..." : "Generating DHBMatrix Clusters", JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            HandleImageGeneration(inputs);
        }

    }

    protected int GetIntValue(LinkedHashMap<String, JComponent> inputs, String fieldName) {
        int value;
        JTextField valueField = (JTextField) inputs.get(fieldName);
        try {
            value = Integer.parseInt(valueField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(parent,
                    valueField.getText() + " is an invalid entry." + fieldName + " should be an integer value > 0",
                    "Failed to calculate similarities...",
                    JOptionPane.ERROR_MESSAGE);
            UILogger.Log("Failed to calculate similarities : invalid samplecount provided", UILogger.Level.ERROR);
            return -9999;
        }
        return value;
    }

    protected float GetFloatValue(LinkedHashMap<String, JComponent> inputs, String fieldName) {
        float value;
        JTextField valueField = (JTextField) inputs.get(fieldName);
        try {
            value = Float.parseFloat(valueField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(parent,
                    valueField.getText() + " is an invalid entry." + fieldName + " should be an integer value > 0",
                    "Failed to calculate similarities...",
                    JOptionPane.ERROR_MESSAGE);
            UILogger.Log("Failed to calculate similarities : invalid samplecount provided", UILogger.Level.ERROR);
            return -9999;
        }
        return value;
    }

    protected abstract void HandleImageGeneration(LinkedHashMap<String, JComponent> inputs);

}
