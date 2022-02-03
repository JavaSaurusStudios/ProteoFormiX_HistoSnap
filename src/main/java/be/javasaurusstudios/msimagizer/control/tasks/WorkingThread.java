package be.javasaurusstudios.msimagizer.control.tasks;

import be.javasaurusstudios.msimagizer.control.util.UILogger;
import be.javasaurusstudios.msimagizer.model.task.WorkingTask;
import be.javasaurusstudios.msimagizer.view.component.ProgressBarFrame;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/**
 * This class represents a working thread for tasks in the background, while
 * updating the front-end
 *
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public class WorkingThread extends SwingWorker {

    //The parent frame
    private final JFrame parent;
    //The progress bar to update
    private final ProgressBarFrame progressFrame;
    // The current task to run
    private final WorkingTask task;
//boolean indicating if this thread is busy
    private boolean working = false;

    /**
     * Constructor
     *
     * @param parent the parent frame
     * @param task the task to run
     */
    public WorkingThread(JFrame parent, WorkingTask task) {
        this.parent = parent;
        this.progressFrame = task.getProgressBar();
        this.task = task;
    }

    /**
     * A Thread to ensure the progressFrame moves with the UI, allowing the user
     * to drag and relocate the parent frame
     */
    private final Thread movingThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (working) {
                if (progressFrame.isVisible()) {
                    progressFrame.setAlwaysOnTop(true);
                    progressFrame.setLocationRelativeTo(parent);
                }
            }
        }
    });

    /**
     * Runs the task in the background and reports to the progress frame
     *
     * @return the result of the provided task
     * @throws Exception
     */
    @Override
    protected Object doInBackground() throws Exception {
        working = true;
        movingThread.start();
        progressFrame.setLocationRelativeTo(parent);
        progressFrame.setVisible(true);
        Object call = task.call();
        task.Finish(call);
        return call;
    }

    /**
     * Runs when the job has finished, example to disable the progressframe
     */
    @Override
    protected void done() {
        progressFrame.setVisible(false);
        working = false;
        movingThread.interrupt();
        UILogger.Log(task.getFinishMessage(),UILogger.Level.INFO);
        UILogger.Log("-----------------------------",UILogger.Level.NONE);
        if (task.isNotifyWhenReady()) {
            JOptionPane.showMessageDialog(parent, task.getFinishMessage());
        }
    }
}
