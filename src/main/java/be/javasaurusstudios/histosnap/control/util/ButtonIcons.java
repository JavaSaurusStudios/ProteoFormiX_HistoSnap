package be.javasaurusstudios.histosnap.control.util;

import javax.swing.ImageIcon;

/**
 * This class represents all icons in the toolbar
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public enum ButtonIcons {
    OPEN("/icons8-add-new-64.png"),
    EXTRACT("/icons8-unpacking-64.png"),
    SAVE_SINGLE("/icons8-full-image-64.png"),
    SAVE_FRAMES("/icons8-image-gallery-64.png"),
    SAVE_GIF("/icons8-movie-64.png"),
    ZOOM_IN("/icons8-zoom-in-64.png"),
    ZOOM_OUT("/icons8-zoom-out-64.png"),
    ENABLE_ANNOTATE("/icons8-create-64.png"),
    ANNOTATE_SQUARE("/icons8-rectangle-64.png"),
    ANNOTATE_CIRCLE("/icons8-circle-50.png"),
    SELECT_COLOR("/icons8-paint-palette-64.png"),
    UNDO("/icons8-undo-60.png"),
    REDO("/icons8-redo-60.png"),
    CLEAR("/icons8-clear-symbol-64.png"),
    SHOW_GRID("/icons8-grid-64.png"),
    SHOW_TITLE("/icons8-abc-60.png"),
    COL_INCRASE("/icons8-export-64.png"),
    COL_DECREASE("/icons8-import-64.png"),
    ENABLE_LOG("/icons8-log-64.png"),
    MODE_MEMORY("/icons8-memory-slot-64.png"),
    MODE_DB("/icons8-hdd-64.png");

    private final String path;

    private ButtonIcons(String path) {
        this.path = path;
    }

    public ImageIcon getIcon() {
        return ImageUtils.scaleIcon(path, 32, 32);
    }

}
