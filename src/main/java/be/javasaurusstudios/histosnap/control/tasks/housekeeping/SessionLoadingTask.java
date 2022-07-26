package be.javasaurusstudios.histosnap.control.tasks.housekeeping;

import be.javasaurusstudios.histosnap.control.cache.HistoSnapImageSession;
import be.javasaurusstudios.histosnap.control.util.UILogger;
import be.javasaurusstudios.histosnap.model.task.WorkingTask;
import be.javasaurusstudios.histosnap.view.MSImagizer;
import static be.javasaurusstudios.histosnap.view.MSImagizer.CACHE;
import static be.javasaurusstudios.histosnap.view.MSImagizer.instance;
import static be.javasaurusstudios.histosnap.view.MSImagizer.lastDirectory;
import be.javasaurusstudios.histosnap.view.component.ProgressBar;
import java.io.File;
import javax.swing.JOptionPane;

/**
 * This class represents a working task to load session content
 *
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public class SessionLoadingTask extends WorkingTask {

    private final File inputFolder;
    private final String path;

    /**
     * Constructor for a task to load a session
     *
     * @param progessBar The progressbar
     * @param inputFolder the session folder
     * @param tfInput textfield for the session folder
     */
    public SessionLoadingTask(ProgressBar progessBar, File inputFolder, String path) {
        super();
        this.inputFolder = inputFolder;
        this.path = path;
    }

    @Override
    public Object call() throws Exception {
        MSImagizer.instance.getProgressBar().setText("Changing sessions...");
        CACHE.setSession(new HistoSnapImageSession(inputFolder.getAbsolutePath()));
        MSImagizer.MSI_IMAGE = CACHE.getFirst();
        MSImagizer.instance.UpdateCacheUI();
        MSImagizer.instance.UpdateImage();

        if (CACHE.getFirst() != null) {
            File selectedFile = new File(CACHE.getFirst().getFrame().getParentFile());
         MSImagizer.instance.setPath(selectedFile.getAbsolutePath());
            lastDirectory = selectedFile.getParentFile();
            if (selectedFile.exists()) {
                   UILogger.Log("Done...", UILogger.Level.INFO);
            } else {
                JOptionPane.showMessageDialog(instance,
                        selectedFile.getAbsolutePath() + " could not be found...",
                        "Failed to restore session...",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        MSImagizer.instance.getProgressBar().setVisible(false);

        return true;
    }

    @Override
    public String getFinishMessage() {
        return "Successfully set session to " + inputFolder.getName();
    }

}
