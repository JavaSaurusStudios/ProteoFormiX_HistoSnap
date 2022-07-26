package be.javasaurusstudios.histosnap.control.cache;

import be.javasaurusstudios.histosnap.model.image.MSiFrame;
import be.javasaurusstudios.histosnap.view.MSImagizer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public class HistoSnapImageSession extends ArrayList<MSiFrame> {

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
            this.sessionFolder.mkdirs();
        }
    }

    /**
     * Stores an image into the current session, so it does not need to be kept
     * in memory
     *
     * @param image the image to store
     */
    public void StoreInSession(MSiFrame image) {
        serialize(new File(sessionFolder, image.getName() + ".hsi").getAbsolutePath(), image);
    }

    /**
     * Checks if an image exists in the
     *
     * @param name the name of the session
     * @return boolean indicating the existence of the image
     */
    public boolean IsInSession(String name) {
        return new File(sessionFolder, name + ".hsi").exists();
    }

    /**
     * Updates the name of an image in the session
     *
     * @param oldName
     * @param newName
     */
    public void RenameImage(String oldName, String newName) {
        if (IsInSession(oldName)) {
            if (IsInSession(newName)) {

            } else {
                MSiFrame tmp = GetFromSession(oldName);
                tmp.setName(newName);
                StoreInSession(tmp);
                RemoveFromSession(oldName);
            }
        }
    }

    /**
     * Saves the session into the specified folder
     */
    public void SaveSession() {
        for (MSiFrame frame : this) {
            MSImagizer.instance.getProgressBar().setText("Saving " + frame.getName() + " ...");
            StoreInSession(frame);
        }
    }

    /**
     * Restores a saved session
     */
    public void RestoreSession() {
        File[] files = sessionFolder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".hsi");
            }
        });

        for (File file : files) {
            MSImagizer.instance.getProgressBar().setText("Loading " + file.getName());
            MSiFrame tmp = GetFromSession(file.getName());
            add(tmp);
        }

    }

    /**
     * Retrieves an image from the session
     *
     * @param name the session name
     * @return an image object
     */
    public MSiFrame GetFromSession(String name) {
        MSiFrame sessionImage = deSerialize(sessionFolder.getAbsolutePath(), name);
        if (sessionImage == null) {
            JOptionPane.showConfirmDialog(MSImagizer.instance, name + " could not be found in the current session !");
        }
        return sessionImage;
    }

    /**
     * Removes an image from the session
     *
     * @param name the name of the image to be removed
     */
    public void RemoveFromSession(String name) {
        File t = new File(sessionFolder, name + ".hsi");
        if (t.exists()) {
            t.delete();
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
            JOptionPane.showConfirmDialog(MSImagizer.instance, ex.getMessage());
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
            JOptionPane.showConfirmDialog(MSImagizer.instance, ex.getMessage());
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
