package be.javasaurusstudios.histosnap.control.tasks.housekeeping;

import be.javasaurusstudios.histosnap.control.cache.HistoSnapImageSession;
import be.javasaurusstudios.histosnap.control.util.UILogger;
import be.javasaurusstudios.histosnap.model.task.WorkingTask;
import be.javasaurusstudios.histosnap.view.HistoSnap;
import static be.javasaurusstudios.histosnap.view.HistoSnap.CACHE;
import static be.javasaurusstudios.histosnap.view.HistoSnap.instance;
import static be.javasaurusstudios.histosnap.view.HistoSnap.lastDirectory;
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
 

    /**
     * Constructor for a task to load a session
     *
     * @param progessBar The progressbar
     * @param inputFolder the session folder
     */
    public SessionLoadingTask(ProgressBar progessBar, File inputFolder) {
        super();
        this.inputFolder = inputFolder;
    }

    @Override
    public Object call() throws Exception {
        HistoSnap.instance.getProgressBar().setText("Changing sessions...");
        CACHE.setSession(new HistoSnapImageSession(inputFolder.getAbsolutePath()));
        HistoSnap.MSI_IMAGE = CACHE.getFirst();
        HistoSnap.updateCacheUI();
        HistoSnap.instance.updateImage();

        if (CACHE.getFirst() != null) {
            File selectedFile = new File(CACHE.getFirst().getFrame().getParentFile());
         HistoSnap.instance.setPath(selectedFile.getAbsolutePath());
            lastDirectory = selectedFile.getParentFile();
            if (selectedFile.exists()) {
                   UILogger.log("Done...", UILogger.Level.INFO);
            } else {
                JOptionPane.showMessageDialog(instance,
                        selectedFile.getAbsolutePath() + " could not be found...",
                        "Failed to restore session...",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        HistoSnap.instance.getProgressBar().setVisible(false);

        return true;
    }

    @Override
    public String getFinishMessage() {
        return "Successfully set session to " + inputFolder.getName();
    }

}
