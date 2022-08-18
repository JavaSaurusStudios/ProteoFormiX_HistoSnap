package be.javasaurusstudios.histosnap.view.prompt.impl;

import be.javasaurusstudios.histosnap.control.MSiImageCache;
import be.javasaurusstudios.histosnap.control.util.AnimationExporter;
import be.javasaurusstudios.histosnap.control.util.UILogger;
import be.javasaurusstudios.histosnap.model.image.MSiImage;
import be.javasaurusstudios.histosnap.model.image.MultiMSiImage;
import be.javasaurusstudios.histosnap.view.HistoSnap;
import static be.javasaurusstudios.histosnap.view.HistoSnap.CACHE;
import static be.javasaurusstudios.histosnap.view.HistoSnap.lastDirectory;
import be.javasaurusstudios.histosnap.view.prompt.UserPrompt;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public class SaveAnimationDialog implements UserPrompt {
    
    private final List<String> selectedImageNames;
   
    
    public SaveAnimationDialog(List<String> selectedImageNames) {
        this.selectedImageNames = selectedImageNames;
    }
    
    @Override
    public void show() {
  
        JTextField timeBetweenFrames = new JTextField();
        JTextField outputFile = new JTextField();
        JButton saveAnimationLocationButton = new JButton("...");
        timeBetweenFrames.setText("" + 1000 / 60);
        
        saveAnimationLocationButton.addActionListener((ActionEvent e) -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save animation...");
            fileChooser.setCurrentDirectory(lastDirectory);
            fileChooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.isDirectory() || f.getAbsolutePath().toLowerCase().endsWith(".gif");
                }
                
                @Override
                public String getDescription() {
                    return "Output gif animation";
                }
            });
            int userSelection = fileChooser.showSaveDialog(HistoSnap.instance);
            
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                outputFile.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });
        
        final JComponent[] inputs = new JComponent[]{
            new JLabel("Time Between Frames (milliseconds)"),
            timeBetweenFrames,
            outputFile,
            new JLabel("Output File"),
            outputFile,
            saveAnimationLocationButton,};
        
        int result = JOptionPane.showConfirmDialog(HistoSnap.instance, inputs, "Save animation...", JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            File fileToStore = new File(outputFile.getText());
            
            if (!fileToStore.getAbsolutePath().toLowerCase().endsWith(".gif")) {
                fileToStore = new File(fileToStore.getAbsolutePath() + ".gif");
            }
            
            if (fileToStore.exists()) {
                int response = JOptionPane.showConfirmDialog(HistoSnap.instance, "File already exists. Override?", "Saving...",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (response != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            
            try {
                int ms = Integer.parseInt(timeBetweenFrames.getText());
                UILogger.log("Reading images from session...", UILogger.Level.INFO);
                
                BufferedImage[] images;
                MSiImage currentImage = HistoSnap.MSI_IMAGE;
                
                if (currentImage instanceof MultiMSiImage) {
                    MultiMSiImage img = (MultiMSiImage) currentImage;
                    images = new BufferedImage[img.getFrames().size()];
                    for (int i = 0; i < images.length; i++) {
                        images[i] = img.createSingleImage(i, 
                                HistoSnap.instance.getCurrentMode(),
                                HistoSnap.instance.getCurrentRange().getColors());
                    }
                } else {
                    List<MSiImage> selectedImages = CACHE.getCachedImages(selectedImageNames);
                    if (selectedImages.isEmpty()) {
                        return;
                    }
                    images = new BufferedImage[selectedImages.size()];
                    for (int i = 0; i < images.length; i++) {
                        selectedImages.get(i).createImage(HistoSnap.instance.getCurrentMode(),
                                HistoSnap.instance.getCurrentRange().getColors());
                    }

             
                }
                
                AnimationExporter.save(images, fileToStore, ms, true);
                UILogger.log("Exported animation to " + fileToStore.getAbsolutePath(), UILogger.Level.INFO);
                JOptionPane.showMessageDialog(HistoSnap.instance, "Exported animation to " + fileToStore.getAbsolutePath());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(HistoSnap.instance,
                        "Could not save this file : " + ex.getMessage(),
                        "Failed to export animation...",
                        JOptionPane.ERROR_MESSAGE);
                UILogger.log("Failed to export animation...", UILogger.Level.ERROR);
                return;
            }
        }
    }
    
}
