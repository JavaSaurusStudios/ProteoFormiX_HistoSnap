package be.javasaurusstudios.msimagizer.view.prompt.impl;

import static be.javasaurusstudios.msimagizer.control.similarities.SimilarityCalculator.DoSimilarities;
import be.javasaurusstudios.msimagizer.control.tasks.WorkingTaskPostProcess;
import be.javasaurusstudios.msimagizer.control.util.ImageUtils;
import be.javasaurusstudios.msimagizer.control.util.UILogger;
import be.javasaurusstudios.msimagizer.model.SimilarityResult;
import be.javasaurusstudios.msimagizer.model.image.MSiImage;
import be.javasaurusstudios.msimagizer.view.MSImagizer;
import be.javasaurusstudios.msimagizer.view.component.ImageLabel;
import be.javasaurusstudios.msimagizer.view.prompt.UserPrompt;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public class SaveSimilaritiesDialog implements UserPrompt {

    private final List<MSiImage> selectedImages;
    private final ImageLabel imgLabel;

    public SaveSimilaritiesDialog(ImageLabel imgLabel, List<MSiImage> selectedImages) {
        this.selectedImages = selectedImages;
        this.imgLabel = imgLabel;
    }

    @Override
    public void Show() {

        final JSlider toleranceSlider = new JSlider(0, 0, 100, 5);
        final JLabel valueDisplay = new JLabel("5%");

        toleranceSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                valueDisplay.setText((((float) toleranceSlider.getValue())) + " %");
            }
        });

        MSiImage[] images = selectedImages.toArray(new MSiImage[selectedImages.size()]);
        JComboBox selectedImageBox = new JComboBox(images);

        JTextField outputFile = new JTextField();
        JButton saveFramesLocationButton = new JButton("...");

        saveFramesLocationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final JFrame parent = MSImagizer.instance;
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Save similarity...");
                fileChooser.setCurrentDirectory(MSImagizer.lastDirectory);
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.setFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        return f.isDirectory();
                    }

                    @Override
                    public String getDescription() {
                        return "Output Folder";
                    }
                });
                int userSelection = fileChooser.showSaveDialog(parent);

                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    MSImagizer.lastDirectory = fileChooser.getSelectedFile();
                    outputFile.setText(fileChooser.getSelectedFile().getAbsolutePath());
                }
            }
        });

        final JComponent[] inputs = new JComponent[]{
            new JLabel("Minimal Difference Threshold"),
            valueDisplay,
            toleranceSlider,
            new JLabel("Reference Image"),
            selectedImageBox,
            new JLabel("Output File"),
            outputFile,
            saveFramesLocationButton};

        int result = JOptionPane.showConfirmDialog(MSImagizer.instance, inputs, "Save animation...", JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            File outputFolder = new File(outputFile.getText());
            outputFolder.mkdirs();
            Process(outputFolder, ((float) toleranceSlider.getValue()) / 100, (MSiImage) selectedImageBox.getSelectedItem());
        }

    }

    public void Process(File outputFolder, double threshold, MSiImage selectedImage) {

        UILogger.Log("Calculating similarities based on "+selectedImage.getName(), UILogger.Level.NONE);
        
        selectedImage.CreateImage(MSImagizer.instance.getCurrentMode(), MSImagizer.instance.getCurrentRange().getColors());
        BufferedImage refImage = selectedImage.getScaledImage(MSImagizer.instance.getCurrentScale());
        String refName = selectedImage.getName();

        final BufferedImage[] images = new BufferedImage[selectedImages.size()];
        final String[] names = new String[selectedImages.size()];

        Point startingPoint = imgLabel.getStartingPoint();
        Point endingPoint = imgLabel.getEndingPoint();

        final boolean drawHighlight = (startingPoint != null && endingPoint != null);
        final int minX = drawHighlight ? Math.min(startingPoint.x, endingPoint.x) : 0;
        final int maxX = drawHighlight ? Math.max(startingPoint.x, endingPoint.x) : refImage.getWidth();
        final int minY = drawHighlight ? Math.min(startingPoint.y, endingPoint.y) : 0;
        final int maxY = drawHighlight ? Math.max(startingPoint.y, endingPoint.y) : refImage.getHeight();

        WorkingTaskPostProcess postProcess = new WorkingTaskPostProcess() {
            @Override
            public void run() {
                FileWriter out = null;
                try {
                    List<SimilarityResult> results = (ArrayList<SimilarityResult>) result;
                    File outputFile = new File(outputFolder, "similarities.txt");
                    out = new FileWriter(outputFile);
                    for (SimilarityResult sr : results) {
                        out.append(sr.getName() + "\t" + sr.getSimilarity()).append(System.lineSeparator());
                    }
                    out.flush();

                    JOptionPane.showMessageDialog(MSImagizer.instance, "Exported similarity report to " + outputFile.getAbsolutePath());

                    for (SimilarityResult sr : results) {
                        if (sr.getSimilarity() >= threshold) {
                            BufferedImage bImage = drawHighlight ? ImageUtils.HighlightZone(sr.getOriginal(), minX, maxX, minY, maxY) : sr.getOriginal();
                            bImage = ImageUtils.SetImageTitle(bImage, sr.getName());
                            ImageIO.write(bImage, "png", new File(outputFolder, sr.getName().replaceAll(" ", "_") + ".png"));
                        }
                    }

                } catch (IOException ex) {
                    Logger.getLogger(MSImagizer.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    MSImagizer.instance.getProgressFrame().setVisible(false);
                    try {

                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(MSImagizer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }
        };

        for (int i = 0; i < selectedImages.size(); i++) {
            selectedImages.get(i).CreateImage(MSImagizer.instance.getCurrentMode(), MSImagizer.instance.getCurrentRange().getColors());
            images[i] = selectedImages.get(i).getScaledImage(MSImagizer.instance.getCurrentScale());
            names[i] = selectedImages.get(i).getName();
        }

        try {
            if (!drawHighlight) {
                DoSimilarities(threshold, images, names, refImage, refName, postProcess);
            } else {
                DoSimilarities(threshold,
                        minX,
                        maxX,
                        minY,
                        maxY,
                        images, names, refImage, refName, postProcess);
            }

        } catch (Throwable e) {
            e.printStackTrace();

        }
    }

}
