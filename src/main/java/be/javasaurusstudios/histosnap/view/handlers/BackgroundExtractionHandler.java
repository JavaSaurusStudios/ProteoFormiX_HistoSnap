/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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

    protected final JFrame parent;
    protected final ProgressBar progressFrame;
    protected final JTextField tfInput;
    protected final JLabel lbImage;
    protected final int currentScale;
    protected final ColorRange currentRange;
    protected final MSiImage.ImageMode currentMode;

    public BackgroundExtractionHandler(JFrame parent, ProgressBar progressFrame, JTextField tfInput, JLabel lbImage, int currentScale, ColorRange currentRange, MSiImage.ImageMode currentMode) {
        this.parent = parent;
        this.progressFrame = progressFrame;
        this.tfInput = tfInput;
        this.lbImage = lbImage;
        this.currentScale = currentScale;
        this.currentRange = currentRange;
        this.currentMode = currentMode;
    }

    public void Show(boolean isRandom) {
        JTextField samples = new JTextField();
        JTextField mzTolerance = new JTextField(".05");
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
