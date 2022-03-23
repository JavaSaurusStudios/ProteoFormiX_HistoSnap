package be.javasaurusstudios.histosnap.control;

import be.javasaurusstudios.histosnap.control.cache.HistoSnapDBFile;
import be.javasaurusstudios.histosnap.model.image.MSiImage;
import be.javasaurusstudios.histosnap.model.image.MSiFrame;
import be.javasaurusstudios.histosnap.view.component.ProgressBarFrame;
import be.javasaurusstudios.histosnap.control.util.PythonExtractor;
import be.javasaurusstudios.histosnap.control.util.SystemUtils;
import be.javasaurusstudios.histosnap.control.util.UILogger;
import be.javasaurusstudios.histosnap.view.MSImagizer;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 * A wrapper that calls the python script to extract a certain mz range from a
 * file
 *
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public class MzRangeExtractor {

    //The path to the input file
    private final String in;
    //The path to the output file
    private final String out;

    /**
     * Constructor
     *
     * @param in the input file path
     * @param out the output file path
     */
    public MzRangeExtractor(String in, String out) {
        this.in = in;
        this.out = out;
    }

    /**
     * Extracts spectra into an MSiImage object using the python library (see
     * docs for more inf)
     *
     * @param mzMin The minimal MZ to consider
     * @param mzMax The maximal MZ to consider
     * @param progressBar The progressbar to indicate progress (can be null)
     * @return the extracted image
     * @throws IOException
     * @throws URISyntaxException
     * @throws Exception
     */
    public MSiImage ExtractImage(float mzMin, float mzMax, ProgressBarFrame progressBar) throws IOException, URISyntaxException, Exception {

        if (MSImagizer.instance.isHighMemory()) {
            SystemUtils.MemoryState memoryState = SystemUtils.getMemoryState();
            DecimalFormat df = new DecimalFormat("#.##");
            String memory = df.format(SystemUtils.getMaxMemory());
            int dialogResult;
            switch (memoryState) {
                case HIGH:
                    return ExtractImageInMemory(mzMin, mzMax, progressBar);
                case MEDIUM:
                    dialogResult = JOptionPane.showConfirmDialog(
                            progressBar,
                            memory + " GB available memory was detected. This might be insufficient. Please consider System Settings > Low Memory Mode if the process times out. Do you wish to continue?",
                            "Memory Settings",
                            JOptionPane.YES_NO_OPTION);
                    if (dialogResult == JOptionPane.YES_OPTION) {
                        return ExtractImageInMemory(mzMin, mzMax, progressBar);
                    } else {
                        return null;
                    }
                case LOW:
                    dialogResult = JOptionPane.showConfirmDialog(
                            progressBar,
                            memory + " GB available memory was detected. This will likely be insufficient, even for small projects. Please use System Settings > Low Memory Mode if the process times out. Do you wish to continue (not recommended)?",
                            "Memory Settings",
                            JOptionPane.YES_NO_OPTION);
                    if (dialogResult == JOptionPane.YES_OPTION) {
                        return ExtractImageInMemory(mzMin, mzMax, progressBar);
                    } else {
                        return null;
                    }
                default:
                    JOptionPane.showMessageDialog(
                            progressBar,
                            "Insufficient memory (" + memory + " GB) available. Please enable System Settings > Low Memory Mode",
                            "Memory Settings",
                            JOptionPane.PLAIN_MESSAGE);
                    return null;
            }
        } else {

            return ExtractImageDatabase(mzMin, mzMax, progressBar);
        }
    }

    /**
     * Extracts spectra into an MSiImage object using the python library (see
     * docs for more inf)
     *
     * @param mzMin The minimal MZ to consider
     * @param mzMax The maximal MZ to consider
     * @param progressBar The progressbar to indicate progress (can be null)
     * @return the extracted image
     * @throws IOException
     * @throws URISyntaxException
     * @throws Exception
     */
    public MSiImage ExtractImageDatabase(float mzMin, float mzMax, ProgressBarFrame progressBar) throws IOException, URISyntaxException, Exception {

        UILogger.Log("Extracting image from hard drive...", UILogger.Level.INFO);

        long time = System.currentTimeMillis();

        File dbFile = new File(in + ".db");
        if (!dbFile.exists()) {
            progressBar.setText("Generating database file...");
            UILogger.Log("Creating database, this may take a while...", UILogger.Level.INFO);
            String pythonFile = PythonExtractor.getPythonScript("CreateDB.py").getAbsolutePath();

            String[] cmds = new String[]{"python", pythonFile, "--input", in};
            ProcessBuilder builder = new ProcessBuilder(cmds);
            Process process = builder.start();

            if (progressBar != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                            String line = null;
                            while ((line = reader.readLine()) != null) {
                                progressBar.setText(line);
                                //                        UILogger.Log(line);
                            }
                        } catch (IOException ex) {
                            Logger.getLogger(MzRangeExtractor.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }).start();
            }

            process.waitFor();
        }

 
        HistoSnapDBFile file = new HistoSnapDBFile(dbFile);
        UILogger.Log("Processing between " + mzMin + " and " + mzMax, UILogger.Level.INFO);
        MSiImage image = file.getImage(mzMin, mzMax);

        System.out.println("Completed loading file in " + ((System.currentTimeMillis() - time) / 1000) + " seconds");

        File tmp = new File(out);
        if (tmp.exists()) {
            tmp.delete();
        }
        return image;
    }

    /**
     * Extracts spectra into an MSiImage object using the python library (see
     * docs for more inf)
     *
     * @param mzMin The minimal MZ to consider
     * @param mzMax The maximal MZ to consider
     * @param progressBar The progressbar to indicate progress (can be null)
     * @return the extracted image
     * @throws IOException
     * @throws URISyntaxException
     * @throws Exception
     */
    public MSiImage ExtractImageInMemory(float mzMin, float mzMax, ProgressBarFrame progressBar) throws IOException, URISyntaxException, Exception {

        UILogger.Log("Extracting image from memory...", UILogger.Level.INFO);

        long time = System.currentTimeMillis();


        String pythonFile = PythonExtractor.getPythonScript("Extract.py").getAbsolutePath();


        String[] cmds = new String[]{"python", pythonFile, "--mzMin", "" + mzMin, "--mzMax", "" + mzMax, "--input", in, "--output", out};

        ProcessBuilder builder = new ProcessBuilder(cmds);
        //  builder.inheritIO();
        Process process = builder.start();

        if (progressBar != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            progressBar.setText(line);
                            //                        UILogger.Log(line);
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(MzRangeExtractor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }).start();
        }

        process.waitFor();

        MSiFrame frame = new SpectralDataImporter().ReadFile(new File(out));
        
        if(frame.getWidth()<=0||frame.getHeight()<=0){
            return null;
        }
        
        frame.setParentFile(in);
        
        UILogger.Log("Processing between " + frame.getMinMz() + " and " + frame.getMaxMz(), UILogger.Level.INFO);
        MSiImage image = new MSiImage(frame);

        System.out.println("Completed loading file in " + ((System.currentTimeMillis() - time) / 1000) + " seconds");

        File tmp = new File(out);
        if (tmp.exists()) {
            tmp.delete();
        }
        return image;
    }

    /**
     * Extracts all spectra into an MSiImage Object
     *
     * @return
     * @throws IOException
     * @throws URISyntaxException
     * @throws Exception
     */
    public MSiImage ExtractFull() throws IOException, URISyntaxException, Exception {
        return ExtractImage(-1, Float.MAX_VALUE, null);
    }

}
