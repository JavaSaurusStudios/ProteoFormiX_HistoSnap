package be.javasaurusstudios.histosnap.control;

import be.javasaurusstudios.histosnap.control.cache.HistoSnapImageSession;
import be.javasaurusstudios.histosnap.model.image.MSiFrame;
import be.javasaurusstudios.histosnap.model.image.MSiImage;
import be.javasaurusstudios.histosnap.view.MSImagizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * The cache for images (mainly for UI purposes)
 *
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public class MSiImageCache extends ArrayList<String> {

    private static final long serialVersionUID = 1234567L;
    //The session to keep track of
    private HistoSnapImageSession session;

    //dictionary
    private HashMap<String, MSiImage> images;

    public static boolean OVERRIDE_SESSION = true;

    public MSiImageCache() {
        this.session = new HistoSnapImageSession(".\\Sessions\\default");
        this.images = new HashMap<>();
    }

    public HistoSnapImageSession getSession() {
        return session;
    }

    /**
     * Sets the current session to the cache
     *
     * @param session the session
     */
    public void setSession(HistoSnapImageSession session) {
        if (this.session != null && !OVERRIDE_SESSION) {
            this.session.saveSession();
        }

        this.session = session;
        session.restoreSession();
        clear();
        for (MSiFrame frame : session) {
            MSImagizer.instance.getProgressBar().setText("Loading " + frame.getName());
            add(frame.getName());
            images.put(frame.getName(), new MSiImage(frame));
        }
    }

    /**
     * Clears the cache
     */
    public void clear() {
        images.clear();
        for (String name : this) {
            session.removeFromSession(name);
        }
        this.clear();
    }

    /**
     * Adds an image to the cache
     *
     * @param image the image to add
     * @return
     */
    public boolean add(MSiImage image) {
        session.storeInSession(image.getFrame());
        images.put(image.getName(), image);
        return add(image.getName());
    }

    /**
     * Removes from the cache
     *
     * @param image
     */
    public boolean remove(MSiImage image) {
        session.removeFromSession(image.getName());
        images.remove(image.getName());
        return remove(image.getName());
    }

    /**
     * Adds multiple images into the cache (and session)
     *
     * @param images
     * @return
     */
    public boolean addToCache(Collection<MSiImage> images) {
        for (MSiImage image : images) {
            session.storeInSession(image.getFrame());
            this.images.put(image.getName(), image);
            if (!add(image.getName())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Removes multiple images into the cache (and session)
     *
     * @param images
     * @return
     */
    public boolean removeImagesFromCache(Collection<MSiImage> images) {
        for (MSiImage image : images) {
            session.removeFromSession(image.getName());
            this.images.remove(image.getName());
            if (!remove(image.getName())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Removes multiple images into the cache (and session)
     *
     * @param images
     * @return
     */
    public boolean removeImagesFromCacheByName(List<String> images) {
        for (int i = images.size() - 1; i >= 0; i--) {
            String image = images.get(i);
            session.removeFromSession(image);
            images.remove(image);
            if (!remove(image)) {
                return false;
            }
        }
        return true;

    }

    /**
     * Retrieves an image from the cache
     *
     * @param imageName the image name to retrieve
     *
     * @return
     */
    public MSiImage getImage(String imageName) {
        MSiImage value;
        if (images.containsKey(imageName)) {
            value = images.get(imageName);
        } else if (session.isInSession(imageName)) {
            value = new MSiImage(session.getFromSession(imageName));
        } else {
            value = null;
        }
        images.put(imageName, value);
        return value;
    }

    public void updateImageName(String oldName, String newName) {
        if (images.containsKey(oldName)) {
            MSiImage tmp = images.get(oldName);
            tmp.setName(newName);
            images.put(newName, tmp);
            images.remove(oldName);
        }
        session.renameImage(oldName, newName);
    }

    /**
     * Returns the cached images
     *
     * @param imageNames the names of the frames
     * @return a list of cached images
     */
    public ArrayList<MSiImage> getCachedImages(List<String> imageNames) {
        ArrayList<MSiImage> selectedImages = new ArrayList<>();
        for (String imageName : imageNames) {
            selectedImages.add(getImage(imageName));
        }
        return selectedImages;
    }

    /**
     * Returns the first msi image in the cache
     *
     * @return the first image or null
     */
    public MSiImage getFirst() {
        if (!images.isEmpty()) {
            return images.values().iterator().next();
        }
        if (isEmpty()) {
            return null;
        }
        return this.getImage(super.get(0));
    }

}
