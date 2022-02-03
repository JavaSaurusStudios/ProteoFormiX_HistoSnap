package be.javasaurusstudios.msimagizer.control.tasks;

/**
 * This class is a model for a task that needs to be run in its own thread,
 * returning a result of an unspecified type
 *
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public abstract class WorkingTaskPostProcess implements Runnable {

    /**
     * The resulting object
     */
    protected Object result;

    public WorkingTaskPostProcess() {

    }

    /**
     * Sets the result of the task
     *
     * @param result the result of this task
     * @return this working task
     */
    public WorkingTaskPostProcess SetResult(Object result) {
        this.result = result;
        return this;
    }

}
