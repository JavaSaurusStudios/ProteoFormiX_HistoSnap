package be.javasaurusstudios.histosnap.control.tasks.imaging;

import be.javasaurusstudios.histosnap.control.MzRangeExtractor;
import be.javasaurusstudios.histosnap.control.filter.DHBMatrixClusterMasses;
import be.javasaurusstudios.histosnap.control.util.UILogger;
import be.javasaurusstudios.histosnap.model.image.MSiFrame;
import be.javasaurusstudios.histosnap.model.image.MSiImage;
import be.javasaurusstudios.histosnap.model.image.MultiMSiImage;
import be.javasaurusstudios.histosnap.view.MSImagizer;
import be.javasaurusstudios.histosnap.view.component.ProgressBarFrame;
import be.javasaurusstudios.histosnap.model.task.WorkingTask;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 * This class represents a task to extract an image given a mz range
 *
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public class ImageDHBClusterTask extends WorkingTask {

    ///Java Swing UI elements  
    //The parent JFrame 
    private final JFrame parent;
    //The textfield for the input file
    private final JTextField tfInput;
    //the label for the image icon
    private final JLabel imageIcon;

    ///Calculator values
    //the tolerance bin
    private float tolerance = .2f;
    //The maximal mz value to consider
    private float maxMZ = -1;
    //The minimal mz value to consider
    private float minMZ = -1;
    //Boolean indicating if the images should be combined
    private boolean generateBackground;

    public ImageDHBClusterTask(JFrame parent, JTextField tfInput, JLabel imageIcon, float minMz, float maxMz, ProgressBarFrame progressBar, boolean generateBackground) {
        super(progressBar);
        this.parent = parent;
        this.tfInput = tfInput;
        this.imageIcon = imageIcon;
        this.minMZ = minMz;
        this.maxMZ = maxMz;
        this.generateBackground = generateBackground;
    }

    @Override
    public Object call() throws Exception {
        Process(tfInput, imageIcon, generateBackground);
        return "Done.";
    }

    /**
     * Processes the input file into an image
     *
     * @param tfInput the input file
     * @param imageIcon the icon to report to
     * @param scale the pixel scale
     * @param range the color range
     * @param autoSave boolean indicating if the image should be saved as an
     * intermediate
     * @throws Exception
     */
    private void Process(JTextField tfInput, JLabel imageIcon, boolean makeBackground) throws Exception {

        if (minMZ == -1 || maxMZ == -1) {
            throw new Exception("Please check the mz range...");
        }

        imageIcon.setText("");
        try {
            String in = tfInput.getText();

            File inFile = new File(in);
            if (!inFile.exists()) {
                progressBar.setVisible(false);
                JOptionPane.showMessageDialog(parent,
                        "Please specify an input imzml file",
                        "Invalid input file",
                        JOptionPane.ERROR_MESSAGE);
                UILogger.Log("Invalid input file", UILogger.Level.ERROR);
                return;
            }

            File idbFile = new File(inFile.getAbsolutePath().replace(".imzml", ".ibd"));
            if (!idbFile.exists()) {
                progressBar.setVisible(false);
                JOptionPane.showMessageDialog(parent,
                        "The corresponding ibd file could not be found in the provided file directory./nPlease verify that an idb file exist with the EXACT same name as the provided imzml.",
                        "Invalid input file",
                        JOptionPane.ERROR_MESSAGE);
                UILogger.Log("Invalid input file", UILogger.Level.ERROR);
                return;
            }

            ExecuteImage(in, "Background", makeBackground);

        } catch (Exception ex) {
            progressBar.setVisible(false);
            ex.printStackTrace();
        }
    }

    private void ExecuteImage(String in, String extractionName, boolean generateBackground) throws Exception {

        List<float[]> ranges = new LinkedList<>();
        List<DHBMatrixClusterMasses> masses = new LinkedList<>();

        for (DHBMatrixClusterMasses DHBMatrixMass : DHBMatrixClusterMasses.values()) {
            if (DHBMatrixMass.getMonoIsotopicMass() >= minMZ && DHBMatrixMass.getMonoIsotopicMass() <= maxMZ) {
                float mZ = DHBMatrixMass.getMonoIsotopicMass();
                ranges.add(new float[]{mZ - tolerance, mZ + tolerance});
                masses.add(DHBMatrixMass);
            }
        }

        String tmp = in + ".DHB.tmp.txt";
        MzRangeExtractor extractor = new MzRangeExtractor(in, tmp);
        MultiMSiImage extractImageRange = extractor.extractImageRange(ranges, progressBar);

        for (int i = 0; i < ranges.size(); i++) {
            String name = masses.get(i) + "(" + masses.get(i).getMonoIsotopicMass() + ")";
            MSiFrame frame = extractImageRange.getFrames().get(i);
            frame.setName(name);
            MSiImage image = new MSiImage(frame);
            image.setName(name);
            image.RemoveHotSpots(99);
            MSImagizer.AddToCache(image);
        }

        MSiImage displayImage;
        if (generateBackground) {
            displayImage = MSiImage.CreateCombinedImage(extractImageRange);

            displayImage.setName(extractionName);
            displayImage.CreateImage(MSImagizer.instance.getCurrentMode(), MSImagizer.instance.getCurrentRange().getColors());

        }

        parent.repaint();

    }

    @Override
    public String getFinishMessage() {
        return "Image extraction completed.";
    }

}
