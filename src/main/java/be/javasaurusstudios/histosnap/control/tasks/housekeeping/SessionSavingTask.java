package be.javasaurusstudios.histosnap.control.tasks.housekeeping;

import be.javasaurusstudios.histosnap.model.task.WorkingTask;
import be.javasaurusstudios.histosnap.view.MSImagizer;
import static be.javasaurusstudios.histosnap.view.MSImagizer.CACHE;
import java.io.File;

/**
 * This class represents a working task to load session content
 *
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public class SessionSavingTask extends WorkingTask {

    /**
     * The output folder for the session to be saved in
     */
    private final File outputFolder;
    /**
     * Boolean indicating a message should be broadcasted on session saving
     */
    private final boolean silent;

    /**
     * *
     * Constructor for a session storing task
     *
     * @param outputFolder the folder to store the images at
     * @param silent boolean indicating if this should be stored silently
     */
    public SessionSavingTask(File outputFolder, boolean silent) {
        super();
        this.outputFolder = outputFolder;
        this.silent = silent;
    }

    @Override
    public Object call() throws Exception {
        if (!silent) {
            MSImagizer.instance.getProgressBar().setText("Saving session...");
        }
        if (CACHE.getFirst() != null) {
            CACHE.getSession().setSessionFolder(outputFolder);
            CACHE.getSession().saveSession();
        }
        MSImagizer.instance.getProgressBar().setVisible(false);
        return true;
    }

    @Override
    public String getFinishMessage() {
        return "Session was saved !";
    }

}
