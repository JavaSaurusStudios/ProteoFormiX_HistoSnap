package be.javasaurusstudios.histosnap.control.util;

/**
 *
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public class SystemUtils {

    public enum MemoryState {
        TOO_LOW, LOW, MEDIUM, HIGH
    }

    /**
     * Get the maximal memory this application can use in the JVM
     * @return the maximal available memory (in gigabytes)
     */
    public static double getMaxMemory() {
        double size_bytes = (Runtime.getRuntime().maxMemory());
        double size_kb = size_bytes / 1024;
        double size_mb = size_kb / 1024;
        return size_mb / 1024;
    }

    /**
     * Returns the memory state of this tool, which is how much memory deficit there is
     * @return an estimation indicating if the "high memory mode" can be used
     */
    public static MemoryState getMemoryState() {
        double maxMemory = getMaxMemory();
        if (maxMemory < 4) {
            return MemoryState.TOO_LOW;
        }
        if (maxMemory < 12) {
            return MemoryState.LOW;
        }
        if (maxMemory < 24) {
            return MemoryState.MEDIUM;
        }
        return MemoryState.HIGH;
    }

}
