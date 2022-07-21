package be.javasaurusstudios.histosnap.control.tasks.imaging;

import be.javasaurusstudios.histosnap.control.MzRangeExtractor;
import be.javasaurusstudios.histosnap.control.util.UILogger;
import be.javasaurusstudios.histosnap.model.image.MSiImage;
import be.javasaurusstudios.histosnap.view.MSImagizer;
import be.javasaurusstudios.histosnap.model.task.WorkingTask;
import be.javasaurusstudios.histosnap.view.component.ProgressBar;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
public class ImageRandomizerTask extends WorkingTask {

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
    //the amount of samples
    private float samples;
    //the random
    private final Random rnd;

    public ImageRandomizerTask(JFrame parent, JTextField tfInput, JLabel imageIcon, float minMz, float maxMz, int samples, ProgressBar progressBar) {
        super(progressBar);
        this.parent = parent;
        this.tfInput = tfInput;
        this.imageIcon = imageIcon;
        this.minMZ = minMz;
        this.maxMZ = maxMz;
        this.samples = samples;
        this.rnd = new Random();
    }

    @Override
    public Object call() throws Exception {
        Process(tfInput, imageIcon);
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
    private void Process(JTextField tfInput, JLabel imageIcon) throws Exception {

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

            ExecuteImage(in, "Background");

        } catch (Exception ex) {
            progressBar.setVisible(false);
            ex.printStackTrace();
        }
    }

    private void ExecuteImage(String in, String extractionName) throws Exception {

        //TODO further subdivide this to create multiple, smaller tasks
        
        List<MSiImage> rndImages = new ArrayList<>();

        for (int i = 0; i < samples; i++) {
            float mZ = minMZ + (rnd.nextFloat() * (maxMZ - minMZ));
            String tmp = in + mZ + ".tmp.txt";
            MzRangeExtractor extractor = new MzRangeExtractor(in, tmp);
            MSiImage image = extractor.extractSingleImage(mZ - tolerance, mZ + tolerance, progressBar);
            rndImages.add(image);
            if (image == null) {
                return;
            }
            image.setName("" + mZ);
            image.RemoveHotSpots(99);
        }

        MSiImage compiledImage = MSiImage.CreateCombinedImage(rndImages);
        MSImagizer.AddToCache(compiledImage);
        compiledImage.setName(extractionName);
        compiledImage.CreateImage(MSImagizer.instance.getCurrentMode(), MSImagizer.instance.getCurrentRange().getColors());
        MSImagizer.CURRENT_IMAGE = compiledImage.getScaledImage(MSImagizer.instance.getExportScale());
        if (imageIcon != null) {
            ImageIcon icon = new ImageIcon(compiledImage);
            imageIcon.setIcon(icon);
            imageIcon.setText("");
        }
        parent.repaint();

    }

    @Override
    public String getFinishMessage() {
        return "Image extraction completed.";
    }

}
