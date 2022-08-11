package be.javasaurusstudios.histosnap.model.task;

import be.javasaurusstudios.histosnap.control.tasks.WorkingTaskPostProcess;
import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * This class represents a task that runs in the background and can report to a ProgressBarFrame
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public abstract class WorkingTask implements Callable {

    // A list of postprocessing tasks that'll run once the result is available
    private final ArrayList<WorkingTaskPostProcess> postprocessing = new ArrayList<>();

    // Boolean indicating if this task needs to notify when the task has completed
    protected boolean notifyWhenReady = true;

    /**
     * Constructor
     */
    public WorkingTask() {

    }

    /**
     * Adds a post processing job
     * @param process the processing job to add
     * @return this workingtask
     */
    public WorkingTask addPostProcessing(WorkingTaskPostProcess process) {
        postprocessing.add(process);
        return this;
    }

    /**
     * Runs post processing
     * @param result  the result of this task
     */
    public void finish(Object result) {
        for (WorkingTaskPostProcess event : postprocessing) {
            event.SetResult(result).run();
        }
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
