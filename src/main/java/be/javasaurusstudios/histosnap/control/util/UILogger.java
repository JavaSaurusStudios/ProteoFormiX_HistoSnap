package be.javasaurusstudios.histosnap.control.util;

import be.javasaurusstudios.histosnap.view.MSImagizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.DefaultListModel;
import javax.swing.JList;

/**
 *
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public class UILogger {

    public enum Level {
        NONE, INFO, ERROR, DEBUG
    }

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public static JList LOGGING_AREA;
    public static int cacheSize = 100;

    public static void Log(String msg) {
        Log(msg, Level.INFO);
    }

    public static void Log(String msg, Level lv) {
        String logMessage = formatter.format(LocalDateTime.now()) + " : " + (lv == Level.NONE ? "" : "[" + lv + "] : ") + msg;

        System.out.println(logMessage);

        if (MSImagizer.instance == null) {
            return;
        }

        MSImagizer.instance.getProgressBar().setText(msg);

        if (LOGGING_AREA == null) {
            return;
        }

        DefaultListModel model = (DefaultListModel) (LOGGING_AREA.getModel());

        model.addElement(logMessage);
        if (model.size() >= cacheSize) {
            model.removeElementAt(0);
        }
    }

}
