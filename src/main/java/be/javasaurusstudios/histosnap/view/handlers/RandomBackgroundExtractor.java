package be.javasaurusstudios.histosnap.view.handlers;

import be.javasaurusstudios.histosnap.control.tasks.WorkingThread;
import be.javasaurusstudios.histosnap.control.tasks.imaging.ImageRandomizerTask;
import be.javasaurusstudios.histosnap.control.util.color.ColorRange;
import be.javasaurusstudios.histosnap.model.image.MSiImage;
import be.javasaurusstudios.histosnap.view.component.ProgressBar;
import java.util.LinkedHashMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 *
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public class RandomBackgroundExtractor extends BackgroundExtractionHandler {

    public RandomBackgroundExtractor(JFrame parent, ProgressBar progressFrame, String path, JLabel lbImage, int currentScale, ColorRange currentRange, MSiImage.ImageMode currentMode) {
        super(parent, progressFrame, path, lbImage, currentScale, currentRange, currentMode);
    }

    @Override
    protected void HandleImageGeneration(LinkedHashMap<String, JComponent> inputs) {
        //get SampleCount
        int sampleCount = GetIntValue(inputs, "samples-field");

        float toleranceValue = GetFloatValue(inputs, "tolerance-field");
        if (toleranceValue <= 0) {
            throw new NullPointerException();
        }
        float lowerMzBoundary = GetFloatValue(inputs, "minMz-field");
        float upperMzBoundary = GetFloatValue(inputs, "maxMz-field");
        float intensityThreshold = GetFloatValue(inputs, "minI-field");
        ImageRandomizerTask task = new ImageRandomizerTask(
                parent,
                path,
                lbImage,
                lowerMzBoundary,
                upperMzBoundary,
                intensityThreshold,
                sampleCount
        );
        task.setNotifyWhenRead(false);
        new WorkingThread(parent, task).execute();
    }
}
