package be.javasaurusstudios.histosnap.control.tasks.housekeeping;

import be.javasaurusstudios.histosnap.view.component.ProgressBarFrame;
import be.javasaurusstudios.histosnap.model.task.WorkingTask;
import static be.javasaurusstudios.histosnap.view.MSImagizer.CACHE;
import java.io.File;

/**
 * This class represents a working task to load session content
 *
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public class SessionSavingTask extends WorkingTask {

    private final File outputFolder;

    private final boolean silent;

    /***
     * Constructor for a session storing task
     * @param progessBar the progress bar
     * @param outputFolder the folder to store the images at
     * @param silent boolean indicating if this should be stored silently
     */
    public SessionSavingTask(ProgressBarFrame progessBar, File outputFolder, boolean silent) {
        super(progessBar);
        this.outputFolder = outputFolder;
        this.silent = silent;
    }

    @Override
    public Object call() throws Exception {
        if (!silent) {
            progressBar.setText("Saving session...");
        }
        if (CACHE.getFirst() != null) {
            CACHE.getSession().setSessionFolder(outputFolder);
            CACHE.getSession().SaveSession(silent ? null : progressBar);
        }
        progressBar.setVisible(false);
        return true;
    }

    @Override
    public String getFinishMessage() {
        return "Session was saved !";
    }

}
