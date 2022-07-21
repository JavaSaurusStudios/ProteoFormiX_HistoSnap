package be.javasaurusstudios.histosnap.model.task;

import be.javasaurusstudios.histosnap.control.tasks.WorkingTaskPostProcess;
import be.javasaurusstudios.histosnap.view.component.ProgressBar;
import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * This class represents a task that runs in the background and can report to a ProgressBarFrame
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public abstract class WorkingTask implements Callable {

    // The progressbar to report to
    protected ProgressBar progressBar;

    // A list of postprocessing tasks that'll run once the result is available
    private final ArrayList<WorkingTaskPostProcess> postprocessing = new ArrayList<>();

    // Boolean indicating if this task needs to notify when the task has completed
    protected boolean notifyWhenReady = true;

    /**
     * Constructor
     * @param progressBar the progress bar (can be null) 
     */
    public WorkingTask(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    /**
     * Adds a post processing job
     * @param process the processing job to add
     * @return this workingtask
     */
    public WorkingTask AddPostProcessing(WorkingTaskPostProcess process) {
        postprocessing.add(process);
        return this;
    }

    /**
     * Runs post processing
     * @param result  the result of this task
     */
    public void Finish(Object result) {
        for (WorkingTaskPostProcess event : postprocessing) {
            event.SetResult(result).run();
        }
    }

    /**
     * Returns the progress bar
     * @return 
     */
    public ProgressBar getProgressBar() {
        return progressBar;
    }

    
    
    /**
     * Return a message to be displayed when the task has completed
     * @return a essage
     */
    public String getFinishMessage() {
        return "Task completed";
    }

    /**
     * Prevent this task from showing a dialog when finished
     * @return this task
     */
    public WorkingTask mute() {
        notifyWhenReady = false;
        return this;
    }

    public boolean isNotifyWhenReady() {
        return notifyWhenReady;
    }

    public WorkingTask setNotifyWhenRead(boolean notify){
        this.notifyWhenReady=notify;
        return this;
    }
    
}
