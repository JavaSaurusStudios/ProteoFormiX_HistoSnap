/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.javasaurusstudios.msimagizer.view.prompt.impl;

import be.javasaurusstudios.msimagizer.control.util.AnimationExporter;
import be.javasaurusstudios.msimagizer.control.util.ImageUtils;
import be.javasaurusstudios.msimagizer.control.util.UILogger;
import be.javasaurusstudios.msimagizer.model.image.MSiImage;
import be.javasaurusstudios.msimagizer.view.MSImagizer;
import static be.javasaurusstudios.msimagizer.view.MSImagizer.lastDirectory;
import be.javasaurusstudios.msimagizer.view.prompt.UserPrompt;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
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

    private final List<MSiImage> selectedImages;

    public SaveFramesDialog(List<MSiImage> selectedImages) {
        this.selectedImages = selectedImages;
    }

    @Override
    public void Show() {
        final JFrame parent = MSImagizer.instance;
        JTextField outputFile = new JTextField();
        JButton saveFramesLocationButton = new JButton("...");

        saveFramesLocationButton.addActionListener(
                new ActionListener() {
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
                int userSelection = fileChooser.showSaveDialog(parent);

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

        int result = JOptionPane.showConfirmDialog(parent, inputs, "Save frames...", JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            File fileToStore = new File(outputFile.getText());
            fileToStore.mkdirs();

            try {

                for (int i = 0; i < selectedImages.size(); i++) {
                    selectedImages.get(i).CreateImage(MSImagizer.instance.getCurrentMode(), MSImagizer.instance.getCurrentRange().getColors());
                    File fileToSave = new File(fileToStore, selectedImages.get(i).getName() + ".png");
                    BufferedImage bImage = ImageUtils.SetImageTitle(selectedImages.get(i).getScaledImage(MSImagizer.instance.getCurrentScale()), selectedImages.get(i).getName());
                    ImageIO.write(bImage, "png", fileToSave);
                }
                UILogger.Log("Exported " + selectedImages.size() + " frames to " + fileToStore.getAbsolutePath(), UILogger.Level.INFO);
                JOptionPane.showMessageDialog(parent, "Exported " + selectedImages.size() + " frames to " + fileToStore.getAbsolutePath());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent,
                        "Could not save this file : " + ex.getMessage(),
                        "Failed to export frames...",
                        JOptionPane.ERROR_MESSAGE);
                UILogger.Log("Failed to export frames...", UILogger.Level.ERROR);
                return;
            }
        }

    }

}
