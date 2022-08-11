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

    public RandomBackgroundExtractor(JFrame parent,  String path, JLabel lbImage) {
        super(parent, path, lbImage);
    }

    @Override
    protected void handleImageGeneration(LinkedHashMap<String, JComponent> inputs) {
        //get SampleCount
        int sampleCount = getIntValue(inputs, "samples-field");

        float toleranceValue = getFloatValue(inputs, "tolerance-field");
        if (toleranceValue <= 0) {
            throw new NullPointerException();
        }
        float lowerMzBoundary = getFloatValue(inputs, "minMz-field");
        float upperMzBoundary = getFloatValue(inputs, "maxMz-field");
        float intensityThreshold = getFloatValue(inputs, "minI-field");
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
