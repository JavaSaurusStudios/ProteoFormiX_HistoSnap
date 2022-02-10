package be.javasaurusstudios.histosnap.control;

import be.javasaurusstudios.histosnap.model.image.MSiImage;
import java.util.ArrayList;

/**
 * The cache for images (mainly for UI purposes)
 *
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public class MSiImageCache {

    // The list of images to keep track of 
    private final ArrayList<MSiImage> imageCache = new ArrayList<>();

    public MSiImageCache() {

    }

    /**
     * Clears the cache
     */
    public void Clear() {
        imageCache.clear();
    }

    /**
     * Adds an image to the cache
     * @param image the image to add 
     */
    public void Add(MSiImage image) {
        imageCache.add(image);
    }

    /**
     * Returns the list of currently cached images
     * @return 
     */
    public ArrayList<MSiImage> getImageList() {
        return imageCache;
    }

}
