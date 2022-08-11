package be.javasaurusstudios.histosnap.view.listeners.imagelist.impl;

import be.javasaurusstudios.histosnap.control.util.UILogger;
import be.javasaurusstudios.histosnap.model.image.MSiImage;
import be.javasaurusstudios.histosnap.view.listeners.imagelist.ListenerProvider;
import be.javasaurusstudios.histosnap.view.MSImagizer;
import static be.javasaurusstudios.histosnap.view.MSImagizer.CACHE;
import static be.javasaurusstudios.histosnap.view.MSImagizer.MSI_IMAGE;
import be.javasaurusstudios.histosnap.view.component.ImageLabel;
import be.javasaurusstudios.histosnap.view.prompt.impl.SaveAnimationDialog;
import be.javasaurusstudios.histosnap.view.prompt.impl.SaveFramesDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

/**
 *
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public class ListActionPopupProvider implements ListenerProvider {

    private JMenuItem deleteItem;
    private JMenuItem renameItem;
    private JMenuItem saveAnimationItem;
    private JMenuItem saveFrameItem;
    private JMenuItem generateCombinedImage;

    public ListActionPopupProvider() {

    }

    @Override
    public void setUp(JComponent component) {

        final MSImagizer parent = MSImagizer.instance;

        if (!(component instanceof JList)) {
            return;
        }

        JList imageCacheList = (JList) component;

        JPopupMenu menu = new JPopupMenu();
        deleteItem = new JMenuItem("Delete...");
        menu.add(deleteItem);
        deleteItem.addActionListener((ActionEvent e) -> {
            List<String> selectedImageNames = imageCacheList.getSelectedValuesList();
            CACHE.removeImagesFromCacheByName(selectedImageNames);
            UILogger.log("Deleted " + selectedImageNames.size() + " image(s)", UILogger.Level.INFO);
            MSImagizer.updateCacheUI();
            if (!CACHE.isEmpty()) {
                MSI_IMAGE = CACHE.getFirst();
                imageCacheList.setSelectedIndex(0);
            }
            parent.updateImage();
        });

        renameItem = new JMenuItem("Rename...");
        menu.add(renameItem);
        renameItem.addActionListener((ActionEvent e) -> {
            List<String> selectedImages = imageCacheList.getSelectedValuesList();
            if (selectedImages.size() > 1 || selectedImages.isEmpty()) {
                System.out.println("Only works on single selections");
                return;
            }
            String tmpName = selectedImages.get(0);
            String newName = JOptionPane.showInputDialog(parent, "Enter a new name", MSI_IMAGE.getName());
            CACHE.updateImageName(tmpName, newName);
            DefaultListModel model = (DefaultListModel) imageCacheList.getModel();
            model.setElementAt(newName, imageCacheList.getSelectedIndex());
            UILogger.log("Updated " + tmpName + " to " + newName, UILogger.Level.INFO);
            parent.updateImage();
        });

        generateCombinedImage = new JMenuItem("Combine...");
        //    menu.add(generateCombinedImage);
        generateCombinedImage.addActionListener((ActionEvent e) -> {
            List<String> selectedImageNames = imageCacheList.getSelectedValuesList();
            if (selectedImageNames.size() <= 1) {
                return;
            }
            try {
                UILogger.log("Creating combined image for " + selectedImageNames.size() + " images...", UILogger.Level.INFO);
                
                MSImagizer.instance.getProgressFrame().setVisible(true);
                
                MSImagizer.instance.getProgressFrame().setText("Generating combined image...");
                MSiImage image = MSiImage.createCombinedImage(CACHE.getCachedImages(selectedImageNames));
                
                MSImagizer.addToCache(image);
                
                MSImagizer.MSI_IMAGE = image;
                MSImagizer.instance.getProgressFrame().setText("Removing hotspots");
                MSImagizer.MSI_IMAGE.removeHotSpots(99);
                MSImagizer.instance.getProgressFrame().setText("Generating heatmap...");
                MSImagizer.MSI_IMAGE.createImage(MSImagizer.instance.getCurrentMode(), MSImagizer.instance.getCurrentRange().getColors());
                MSImagizer.CURRENT_IMAGE = MSImagizer.MSI_IMAGE.getScaledImage(MSImagizer.instance.getCurrentScale());
            } finally {
                MSImagizer.instance.getProgressFrame().setVisible(false);
            }
        });

        saveAnimationItem = new JMenuItem("Save Animation...");
        menu.add(saveAnimationItem);
        saveAnimationItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<String> selectedImageNames = imageCacheList.getSelectedValuesList();
                if (selectedImageNames.size() <= 1) {
                    return;
                }
                new SaveAnimationDialog(selectedImageNames).show();
            }
        });

        saveFrameItem = new JMenuItem("Save Frame(s)...");
        menu.add(saveFrameItem);
        saveFrameItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<String> selectedImagesNames = imageCacheList.getSelectedValuesList();
                new SaveFramesDialog(selectedImagesNames, CACHE).show();
            }
        });

        imageCacheList.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (imageCacheList != null && imageCacheList.getSelectedValuesList().size() >= 1 && (imageCacheList.getVisibleRowCount() > 0 & (e.getModifiers() & InputEvent.BUTTON3_MASK) != 0)) {

                    renameItem.setEnabled(imageCacheList.getSelectedValuesList().size() >= 1);
                    deleteItem.setEnabled(imageCacheList.getSelectedValuesList().size() >= 1);
                    saveFrameItem.setEnabled(imageCacheList.getSelectedValuesList().size() >= 1);
                    saveAnimationItem.setEnabled(imageCacheList.getSelectedValuesList().size() >= 2);

                    menu.show(e.getComponent(), e.getX(), e.getY());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }

}
