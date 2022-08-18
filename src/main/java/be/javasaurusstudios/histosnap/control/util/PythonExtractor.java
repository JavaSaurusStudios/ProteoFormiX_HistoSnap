package be.javasaurusstudios.histosnap.control.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class handles the extraction of a python script to run locally to the
 * jar file
 *
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public class PythonExtractor {

    /**
     * Extracts a script from the jar resources into the local folder
     *
     * @param scriptName the name of the script that needs to be exported
     * @param autoDelete indicating the file to be deleted post extraction (one
     * time use files)
     * @return the file location of the exported script
     * @throws Exception
     */
    public static File getPythonScript(String scriptName, boolean autoDelete) throws Exception {

        File jarLocation = new File(PythonExtractor.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        File python = new File(jarLocation.getParentFile(), scriptName);

        if (python.exists()) {
            System.out.println("Python script detected...");

        } else {
            UILogger.log("Extracting python script to " + python.getAbsolutePath(), UILogger.Level.INFO);
            python = new File(exportSource("/" + scriptName));
        }
        if (autoDelete) {
            python.deleteOnExit();
        }
        return python;
    }

    /**
     * Extracts a script from the jar resources into the local folder
     *
     * @param scriptName the name of the script that needs to be exported
     * @return the file location of the exported script
     * @throws Exception
     */
    public static File getPythonScript(String scriptName) throws Exception {
        return getPythonScript(scriptName, false);
    }

    /**
     * Exports a resource into the local folder
     *
     * @param resourceName the name of the resource
     * @return The file path for the resource
     * @throws Exception
     */
    private static String exportSource(String resourceName) throws Exception {
        InputStream stream = null;
        OutputStream resStreamOut = null;
        String jarFolder;
        try {
            stream = PythonExtractor.class.getResourceAsStream(resourceName);//note that each / is a directory down in the "jar tree" been the jar the root of the tree
            if (stream == null) {
                throw new Exception("Cannot get resource \"" + resourceName + "\" from Jar file.");
            }

            int readBytes;
            byte[] buffer = new byte[4096];
            jarFolder = new File(PythonExtractor.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile().getPath().replace('\\', '/');
            resStreamOut = new FileOutputStream(jarFolder + resourceName);
            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }
            stream.close();
            resStreamOut.close();
        } catch (Exception ex) {
            if (stream != null) {
                stream.close();
            }
            if (resStreamOut != null) {
                resStreamOut.close();
            }
            throw ex;
        } finally {
            if (stream != null) {
                stream.close();
            }
            if (resStreamOut != null) {
                resStreamOut.close();
            }
        }

        return jarFolder + resourceName;
    }

}
