/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.javasaurusstudios.histosnap.view.handlers;

import be.javasaurusstudios.histosnap.control.tasks.WorkingThread;
import be.javasaurusstudios.histosnap.control.tasks.imaging.ImageDHBClusterTask;
import be.javasaurusstudios.histosnap.control.util.color.ColorRange;
import be.javasaurusstudios.histosnap.model.image.MSiImage;
import be.javasaurusstudios.histosnap.view.component.ProgressBar;
import java.util.LinkedHashMap;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 *
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public class DHBBackgroundExtractionHandler extends BackgroundExtractionHandler {

    public DHBBackgroundExtractionHandler(JFrame parent, ProgressBar progressFrame, JTextField tfInput, JLabel lbImage, int currentScale, ColorRange currentRange, MSiImage.ImageMode currentMode) {
        super(parent, progressFrame, tfInput, lbImage, currentScale, currentRange, currentMode);
    }

    @Override
    protected void HandleImageGeneration(LinkedHashMap<String, JComponent> inputs) {

        boolean autoCombine = ((JCheckBox) inputs.get("combine-bg-checkbox")).isSelected();

        float toleranceValue = GetFloatValue(inputs, "tolerance-field");
        if (toleranceValue <= 0) {
            throw new NullPointerException();
        }
        float lowerMzBoundary = GetFloatValue(inputs, "minMz-field");
        float upperMzBoundary = GetFloatValue(inputs, "maxMz-field");

        ImageDHBClusterTask task = new ImageDHBClusterTask(
                parent,
                tfInput,
                lbImage,
                lowerMzBoundary,
                upperMzBoundary,
                progressFrame,
                autoCombine
        );
        task.setNotifyWhenRead(false);
        new WorkingThread(parent, task).execute();
    }
}
