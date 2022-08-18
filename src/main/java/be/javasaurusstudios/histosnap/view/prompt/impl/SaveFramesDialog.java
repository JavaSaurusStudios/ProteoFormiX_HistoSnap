package be.javasaurusstudios.histosnap.view.prompt.impl;

import be.javasaurusstudios.histosnap.control.MSiImageCache;
import be.javasaurusstudios.histosnap.control.util.UILogger;
import be.javasaurusstudios.histosnap.model.image.MSiImage;
import be.javasaurusstudios.histosnap.view.HistoSnap;
import static be.javasaurusstudios.histosnap.view.HistoSnap.lastDirectory;
import be.javasaurusstudios.histosnap.view.prompt.UserPrompt;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
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
public class SaveFramesDialog implements UserPrompt {

    private final List<String> selectedImageNames;
    private final MSiImageCache cache;

    public SaveFramesDialog(List<String> selectedImages, MSiImageCache cache) {
        this.selectedImageNames = selectedImages;
        this.cache = cache;
    }

    @Override
    public void show() {
        JTextField outputFile = new JTextField();
        JButton saveFramesLocationButton = new JButton("...");

        saveFramesLocationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e
            ) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Save frame(s)...");
                fileChooser.setCurrentDirectory(lastDirectory);
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.setFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        return f.isDirectory();
                    }

                    @Override
                    public String getDescription() {
                        return "Output directory";
                    }
                });
                int userSelection = fileChooser.showSaveDialog(HistoSnap.instance);

                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    outputFile.setText(fileChooser.getSelectedFile().getAbsolutePath());
                }
            }
        }
        );

        final JComponent[] inputs = new JComponent[]{
            outputFile,
            new JLabel("Output Folder"),
            outputFile,
            saveFramesLocationButton,};

        int result = JOptionPane.showConfirmDialog(HistoSnap.instance, inputs, "Save frames...", JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            File fileToStore = new File(outputFile.getText());

            if (fileToStore.exists() && fileToStore.isDirectory()) {
                //check if folder is approved
            } else {
                if (!fileToStore.mkdirs()) {
                    JOptionPane.showMessageDialog(HistoSnap.instance, "Failed to create directory : " + fileToStore.getAbsolutePath(), "Warning",
                            JOptionPane.ERROR_MESSAGE);
                }
            }

            try {

                for (int i = 0; i < selectedImageNames.size(); i++) {
                    MSiImage tmp = cache.getImage(selectedImageNames.get(i));
                    tmp.createImage(HistoSnap.instance.getCurrentMode(), HistoSnap.instance.getCurrentRange().getColors());
                    File fileToSave = new File(fileToStore, selectedImageNames.get(i) + ".png");
                    /*  BufferedImage bImage = ImageUtils.SetImageTitle(
                            
                            tmp.getScaledImage(MSImagizer.instance.getExportScale()), selectedImageNames.get(i)
                    
                    );
                    ImageIO.write(bImage, "png", fileToSave);*/
                    ImageIO.write(tmp.getScaledImage(HistoSnap.instance.getExportScale()), "png", fileToSave);
                }
                UILogger.log("Exported " + selectedImageNames.size() + " frames to " + fileToStore.getAbsolutePath(), UILogger.Level.INFO);
                JOptionPane.showMessageDialog(HistoSnap.instance, "Exported " + selectedImageNames.size() + " frames to " + fileToStore.getAbsolutePath());
            } catch (HeadlessException | IOException ex) {
                JOptionPane.showMessageDialog(HistoSnap.instance,
                        "Could not save this file : " + ex.getMessage(),
                        "Failed to export frames...",
                        JOptionPane.ERROR_MESSAGE);
                UILogger.log("Failed to export frames...", UILogger.Level.ERROR);
                return;
            }
        }

    }

}
