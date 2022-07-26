package be.javasaurusstudios.histosnap.view.prompt.impl;

import be.javasaurusstudios.histosnap.control.MSiImageCache;
import be.javasaurusstudios.histosnap.control.util.UILogger;
import be.javasaurusstudios.histosnap.model.image.MSiImage;
import be.javasaurusstudios.histosnap.view.MSImagizer;
import static be.javasaurusstudios.histosnap.view.MSImagizer.lastDirectory;
import be.javasaurusstudios.histosnap.view.prompt.UserPrompt;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    private final List<String> selectedImageNames;
    private final MSiImageCache cache;

    public SaveFramesDialog(List<String> selectedImages, MSiImageCache cache) {
        this.selectedImageNames = selectedImages;
        this.cache = cache;
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

                for (int i = 0; i < selectedImageNames.size(); i++) {
                    MSiImage tmp = cache.getImage(selectedImageNames.get(i));
                    tmp.CreateImage(MSImagizer.instance.getCurrentMode(), MSImagizer.instance.getCurrentRange().getColors());
                    File fileToSave = new File(fileToStore, selectedImageNames.get(i) + ".png");
                  /*  BufferedImage bImage = ImageUtils.SetImageTitle(
                            
                            tmp.getScaledImage(MSImagizer.instance.getExportScale()), selectedImageNames.get(i)
                    
                    );
                    ImageIO.write(bImage, "png", fileToSave);*/
                   ImageIO.write(tmp.getScaledImage(MSImagizer.instance.getExportScale()), "png", fileToSave);
                }
                UILogger.Log("Exported " + selectedImageNames.size() + " frames to " + fileToStore.getAbsolutePath(), UILogger.Level.INFO);
                JOptionPane.showMessageDialog(parent, "Exported " + selectedImageNames.size() + " frames to " + fileToStore.getAbsolutePath());
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
