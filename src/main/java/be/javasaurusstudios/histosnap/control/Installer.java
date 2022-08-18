package be.javasaurusstudios.histosnap.control;

import be.javasaurusstudios.histosnap.control.util.ButtonIcons;
import be.javasaurusstudios.histosnap.control.util.PythonExtractor;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 *
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public class Installer {

    private static JDialog dialog;

    public static void install() throws Exception {

        Object[] options = {"Yes", "No"};
        int n = JOptionPane.showOptionDialog(null,
                getCheckFile().exists()
                ? "Do you want to rerun the installation script for HistoSnap's dependencies?"
                : "It seems you are running histosnap for the first time. Do you want to install the dependencies?",
                "Check dependencies",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                ButtonIcons.ICON.getIcon(), //do not use a custom Icon
                options, //the titles of buttons
                options[0]); //default button title

        if (n == 1) {
            return;
        }

        if (!hasPythonInstalled()) {
            JOptionPane.showMessageDialog(null, "It seems Python3 is not installed on this system. Please visit https://www.python.org/downloads/ and install Python3 for your target operating system");
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        boolean installed = runPythonScript(PythonExtractor.getPythonScript("get-pip.py"));
                        if (installed) {
                            installed = runPythonScript(PythonExtractor.getPythonScript("install-pyimzml.py"));
                            if (installed) {
                                getCheckFile().createNewFile();
                                closeWaitingDialog();
                                JOptionPane.showMessageDialog(null, "As far as we can tell, you are ready to go !");
                            }
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(Installer.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        closeWaitingDialog();
                    }
                }
            }).start();
            showWaitingDialog();
        }

    }

    public static File getCheckFile() throws URISyntaxException, IOException {
        File jarLocation = new File(PythonExtractor.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        File checkFile = new File(jarLocation, "histosnap.init");
        System.out.println("checkFile is at " + checkFile.getAbsolutePath());
        return checkFile;
    }

    private static boolean runPythonScript(String cmd) throws IOException, InterruptedException {
        setMessage("Executing " + cmd);
        String[] cmdArgs = ("python " + cmd).split(" ");
        ProcessBuilder processBuilder = new ProcessBuilder(cmdArgs);
        processBuilder.inheritIO();
        Process process = processBuilder.start();
        int exitCode = process.waitFor();
        return exitCode == 0;
    }

    private static boolean runPythonScript(File script) throws IOException, InterruptedException {

        return runPythonScript(script.getAbsolutePath());
    }

    private static boolean hasPythonInstalled() throws IOException, InterruptedException {
        return runPythonScript("--version");
    }

    private static void showWaitingDialog() {
        final JOptionPane optionPane = new JOptionPane("Please wait...", JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
        dialog = new JDialog();
        dialog.setTitle("Installing dependencies...");
        dialog.setLocationRelativeTo(null);
        dialog.setModal(true);
        dialog.setContentPane(optionPane);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.pack();
        dialog.setVisible(true);
    }

    private static void setMessage(String message) {
        if (dialog != null) {
            ((JOptionPane) dialog.getContentPane()).setMessage(message);
        }
    }

    private static void closeWaitingDialog() {
        if (dialog != null) {
            dialog.setVisible(false);
            dialog.dispose();
            dialog = null;
        }
    }

}
