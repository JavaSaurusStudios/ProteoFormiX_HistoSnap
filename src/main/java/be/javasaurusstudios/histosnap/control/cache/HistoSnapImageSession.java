package be.javasaurusstudios.histosnap.control.cache;

import be.javasaurusstudios.histosnap.model.image.MSiFrame;
import be.javasaurusstudios.histosnap.view.HistoSnap;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 * This class represents a session to store MSiFrames in for later
 *
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public class HistoSnapImageSession extends ArrayList<MSiFrame> {

    private static final long serialVersionUID = 1234567L;
    /**
     * The current session folder
     */
    private File sessionFolder;

    /**
     * Constructor for the session
     *
     * @param location the location of the session
     */
    public HistoSnapImageSession(String location) {
        this.sessionFolder = new File(location);
        if (!this.sessionFolder.exists()) {
            if (!this.sessionFolder.mkdirs()) {
                JOptionPane.showMessageDialog(HistoSnap.instance, "Can not create required directories at " + location, "Error ",
                        JOptionPane.ERROR_MESSAGE);

            }
        }
    }

    /**
     * Stores an image into the current session, so it does not need to be kept
     * in memory
     *
     * @param image the image to store
     */
    public void storeInSession(MSiFrame image) {
        serialize(new File(sessionFolder, image.getName() + ".hsi").getAbsolutePath(), image);
    }

    /**
     * Checks if an image exists in the
     *
     * @param name the name of the session
     * @return boolean indicating the existence of the image
     */
    public boolean isInSession(String name) {
        return new File(sessionFolder, name + ".hsi").exists();
    }

    /**
     * Updates the name of an image in the session
     *
     * @param oldName
     * @param newName
     */
    public void renameImage(String oldName, String newName) {
        if (isInSession(oldName)) {
            if (isInSession(newName)) {

            } else {
                MSiFrame tmp = getFromSession(oldName);
                tmp.setName(newName);
                storeInSession(tmp);
                removeFromSession(oldName);
            }
        }
    }

    /**
     * Saves the session into the specified folder
     */
    public void saveSession() {
        for (MSiFrame frame : this) {
            HistoSnap.instance.getProgressBar().setText("Saving " + frame.getName() + " ...");
            storeInSession(frame);
        }
    }

    /**
     * Restores a saved session
     */
    public void restoreSession() {
        File[] files = sessionFolder.listFiles((File dir, String name) -> name.endsWith(".hsi"));

        for (File file : files) {
            HistoSnap.instance.getProgressBar().setText("Loading " + file.getName());
            MSiFrame tmp = getFromSession(file.getName());
            add(tmp);
        }

    }

    /**
     * Retrieves an image from the session
     *
     * @param name the session name
     * @return an image object
     */
    public MSiFrame getFromSession(String name) {
        MSiFrame sessionImage = deSerialize(sessionFolder.getAbsolutePath(), name);
        if (sessionImage == null) {
            JOptionPane.showConfirmDialog(HistoSnap.instance, name + " could not be found in the current session !");
        }
        return sessionImage;
    }

    /**
     * Removes an image from the session
     *
     * @param name the name of the image to be removed
     */
    public void removeFromSession(String name) {
        File t = new File(sessionFolder, name + ".hsi");
        if (t.exists()) {
            if (!t.delete()) {
                JOptionPane.showMessageDialog(HistoSnap.instance, "Could not delete session : " + name, "Dialog",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Serializes the image to a file
     *
     * @param location the storage location
     * @param image the image
     */
    private void serialize(String location, MSiFrame image) {
        try (
                FileOutputStream fout = new FileOutputStream(location, false);
                ObjectOutputStream oos = new ObjectOutputStream(fout);) {
            System.out.println("STORING TO " + location);
            oos.writeObject(image);
            oos.flush();
        } catch (IOException ex) {
            JOptionPane.showConfirmDialog(HistoSnap.instance, ex.getMessage());
        }
    }

    /**
     * Deserializes an image from a file
     *
     * @param location The session location
     * @param name the name of the image to be retrieved
     * @return the image
     */
    private MSiFrame deSerialize(String location, String name) {
        MSiFrame readCase = null;
        try (
                FileInputStream streamIn = new FileInputStream(location + "/" + (name.toLowerCase().endsWith(".hsi") ? name : (name + ".hsi")));
                ObjectInputStream objectinputstream = new ObjectInputStream(streamIn);) {
            System.out.println("READING FROM " + location + "/" + name + ".hsi");
            readCase = (MSiFrame) objectinputstream.readObject();
        } catch (Exception ex) {
            JOptionPane.showConfirmDialog(HistoSnap.instance, ex.getMessage());
        }
        return readCase;
    }

    public File getSessionFolder() {
        return sessionFolder;
    }

    public void setSessionFolder(File sessionFolder) {
        this.sessionFolder = sessionFolder;
    }

}
