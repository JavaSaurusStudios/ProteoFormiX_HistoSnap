package be.javasaurusstudios.msimagizer.control;

import be.javasaurusstudios.msimagizer.model.image.MSiImage;
import be.javasaurusstudios.msimagizer.model.image.MSiFrame;
import be.javasaurusstudios.msimagizer.view.component.ProgressBarFrame;
import be.javasaurusstudios.msimagizer.control.util.PythonExtractor;
import be.javasaurusstudios.msimagizer.control.util.UILogger;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

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

        long time = System.currentTimeMillis();

        String pythonFile = PythonExtractor.getPythonScript("ExtractAndDenoise.py").getAbsolutePath();

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
        UILogger.Log("Processing between " + frame.getMinMz()+" and " + frame.getMaxMz(),UILogger.Level.INFO);
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
