package be.javasaurusstudios.histosnap.control;

import be.javasaurusstudios.histosnap.control.cache.HistoSnapImageSession;
import be.javasaurusstudios.histosnap.model.image.MSiFrame;
import be.javasaurusstudios.histosnap.model.image.MSiImage;
import be.javasaurusstudios.histosnap.view.component.ProgressBarFrame;
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

    //The session to keep track of
    private HistoSnapImageSession session;

    //dictionary
    private HashMap<String, MSiImage> images;

    private static final boolean overrideSession = true;

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
     * @param progessBar the progress bar
     */
    public void setSession(HistoSnapImageSession session, ProgressBarFrame progessBar) {
        if (this.session != null && !overrideSession) {
            this.session.SaveSession(progessBar);
        }

        this.session = session;
        session.RestoreSession(progessBar);
        Clear();
        for (MSiFrame frame : session) {
            if (progessBar != null) {
                progessBar.setText("Loading " + frame.getName());
            }
            add(frame.getName());
            images.put(frame.getName(), new MSiImage(frame));
        }
    }

    /**
     * Clears the cache
     */
    public void Clear() {
        images.clear();
        for (String name : this) {
            session.RemoveFromSession(name);
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
        session.StoreInSession(image.getFrame());
        images.put(image.getName(), image);
        return add(image.getName());
    }

    /**
     * Removes from the cache
     *
     * @param image
     */
    public boolean remove(MSiImage image) {
        session.RemoveFromSession(image.getName());
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
            session.StoreInSession(image.getFrame());
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
            session.RemoveFromSession(image.getName());
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
    public boolean removeImagesFromCacheByName(Collection<String> images) {
        for (String image : images) {
            session.RemoveFromSession(image);
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
        } else if (session.IsInSession(imageName)) {
            value = new MSiImage(session.GetFromSession(imageName));
        } else {
            value = null;
        }
        images.put(imageName, value);
        return value;
    }

    public void UpdateImageName(String oldName, String newName) {
        if (images.containsKey(oldName)) {
            MSiImage tmp = images.get(oldName);
            tmp.setName(newName);
            images.put(newName, tmp);
            images.remove(oldName);
        }
        session.RenameImage(oldName, newName);
    }

    /**
     * Returns the cached images
     *
     * @param imageNames the names of the frames
     * @return a list of cached images
     */
    public ArrayList<MSiImage> GetCachedImages(List<String> imageNames) {
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
