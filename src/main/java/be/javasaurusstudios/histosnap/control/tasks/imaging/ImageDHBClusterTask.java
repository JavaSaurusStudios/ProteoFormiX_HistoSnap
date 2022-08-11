package be.javasaurusstudios.histosnap.control.tasks.imaging;

import be.javasaurusstudios.histosnap.control.MzRangeExtractor;
import be.javasaurusstudios.histosnap.control.masses.DHBMatrixClusterMasses;
import be.javasaurusstudios.histosnap.control.util.UILogger;
import be.javasaurusstudios.histosnap.model.image.MSiFrame;
import be.javasaurusstudios.histosnap.model.image.MSiImage;
import be.javasaurusstudios.histosnap.model.image.MultiMSiImage;
import be.javasaurusstudios.histosnap.view.MSImagizer;
import be.javasaurusstudios.histosnap.model.task.WorkingTask;
import be.javasaurusstudios.histosnap.view.component.ProgressBar;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

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
    private final String path;
    //the label for the image icon
    private final JLabel imageIcon;

    ///Calculator values
    //the tolerance bin
    private float tolerance = .2f;
    //The maximal mz value to consider
    private float maxMZ = -1;
    //The minimal mz value to consider
    private float minMZ = -1;
    //The minimal intensity to consider
    private float minI = -1;
    //Boolean indicating if the images should be combined
    private boolean generateBackground;

    public ImageDHBClusterTask(JFrame parent, String path, JLabel imageIcon, float minMz, float maxMz, float minI, boolean generateBackground) {
        super();
        this.parent = parent;
        this.path = path;
        this.imageIcon = imageIcon;
        this.minMZ = minMz;
        this.maxMZ = maxMz;
        this.minI = minI;
        this.generateBackground = generateBackground;
    }

    @Override
    public Object call() throws Exception {
        Process(path, imageIcon, generateBackground);
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
    private void Process(String path, JLabel imageIcon, boolean makeBackground) throws Exception {

        if (minMZ == -1 || maxMZ == -1) {
            throw new Exception("Please check the mz range...");
        }

        imageIcon.setText("");
        try {
            String in = path;

            File inFile = new File(in);
            if (!inFile.exists()) {
                MSImagizer.instance.getProgressBar().setVisible(false);
                JOptionPane.showMessageDialog(parent,
                        "Please specify an input imzml file",
                        "Invalid input file",
                        JOptionPane.ERROR_MESSAGE);
                UILogger.Log("Invalid input file", UILogger.Level.ERROR);
                return;
            }

            File idbFile = new File(inFile.getAbsolutePath().replace(".imzml", ".ibd"));
            if (!idbFile.exists()) {
                MSImagizer.instance.getProgressBar().setVisible(false);
                JOptionPane.showMessageDialog(parent,
                        "The corresponding ibd file could not be found in the provided file directory./nPlease verify that an idb file exist with the EXACT same name as the provided imzml.",
                        "Invalid input file",
                        JOptionPane.ERROR_MESSAGE);
                UILogger.Log("Invalid input file", UILogger.Level.ERROR);
                return;
            }

            ExecuteImage(in, "Background", makeBackground);

        } catch (Exception ex) {
            MSImagizer.instance.getProgressBar().setVisible(false);
            ex.printStackTrace();
        }
    }

    /**
     * Generates the image
     *
     * @param in the input file
     * @param extractionName the name of his project
     * @param generateBackground indicating if a background needs to be
     * generated
     * @throws Exception
     */
    private void ExecuteImage(String in, String extractionName, boolean generateBackground) throws Exception {

        ProgressBar bar = MSImagizer.instance.getProgressBar();

        List<float[]> ranges = new LinkedList<>();
        List<DHBMatrixClusterMasses> masses = new LinkedList<>();

        bar.setValueText(0, "Generating mass ranges for DHB Clusters", true);
        for (DHBMatrixClusterMasses DHBMatrixMass : DHBMatrixClusterMasses.values()) {
            if (DHBMatrixMass.getMonoIsotopicMass() >= minMZ && DHBMatrixMass.getMonoIsotopicMass() <= maxMZ) {
                float mZ = DHBMatrixMass.getMonoIsotopicMass();
                ranges.add(new float[]{mZ - tolerance, mZ + tolerance});
                masses.add(DHBMatrixMass);
            }
        }

        if (ranges.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "The specified range (" + minMZ + " - " + maxMZ + ") does not contain DHB matrix molecules");
            return;
        }

        String tmp = in + ".DHB.tmp.txt";
        MzRangeExtractor extractor = new MzRangeExtractor(in, tmp);
        MultiMSiImage extractImageRange = extractor.extractImageRange(ranges, minI);

        bar.setValueText(0, "Generating mass ranges for DHB Clusters", true);
        float value = 0;
        for (int i = 0; i < ranges.size(); i++) {
            value = ((float) i / ranges.size());
            String name = masses.get(i) + "(" + masses.get(i).getMonoIsotopicMass() + ")";
            bar.setValueText(value, name, false);
            MSiFrame frame = extractImageRange.getFrames().get(i);
            frame.setName(name);
            MSiImage image = new MSiImage(frame);
            image.setName(name);
            bar.setValueText(value, "Processing...", true);
            image.RemoveHotSpots(99);
            MSImagizer.AddToCache(image);
        }

        MSiImage displayImage;
        if (generateBackground) {
            bar.setValueText(value, "Combining images...", true);
            displayImage = MSiImage.CreateCombinedImage(extractImageRange);
            displayImage.setName(extractionName);
            displayImage.CreateImage(MSImagizer.instance.getCurrentMode(), MSImagizer.instance.getCurrentRange().getColors());
            MSImagizer.AddToCache(displayImage);
        }

        parent.repaint();

    }

    @Override
    public String getFinishMessage() {
        return "Image extraction completed.";
    }

}
