package be.javasaurusstudios.histosnap.control;

import be.javasaurusstudios.histosnap.control.cache.HistoSnapDBFile;
import be.javasaurusstudios.histosnap.model.image.MSiImage;
import be.javasaurusstudios.histosnap.model.image.MSiFrame;
import be.javasaurusstudios.histosnap.control.util.PythonExtractor;
import be.javasaurusstudios.histosnap.control.util.SystemUtils;
import be.javasaurusstudios.histosnap.control.util.UILogger;
import be.javasaurusstudios.histosnap.model.image.MultiMSiImage;
import be.javasaurusstudios.histosnap.view.MSImagizer;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
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
     * @param ranges
     * @return the extracted image
     * @throws IOException
     * @throws URISyntaxException
     * @throws Exception
     */
    public MultiMSiImage extractImageRange(List<float[]> ranges, float minI) throws IOException, URISyntaxException, Exception {

        MSImagizer.instance.getProgressBar().setValueText(0, "Starting extraction in " + SystemUtils.getMemoryState(), true);

        Collections.sort(ranges, (float[] o1, float[] o2) -> Float.compare(o1[0], o2[0]));

        if (MSImagizer.instance == null || MSImagizer.instance.isHighMemory()) {
            SystemUtils.MemoryState memoryState = SystemUtils.getMemoryState();
            DecimalFormat df = new DecimalFormat("#.##");
            String memory = df.format(SystemUtils.getMaxMemory());
            int dialogResult;
            switch (memoryState) {
                case HIGH:
                    return extractImageRangeMem(ranges, minI);
                case MEDIUM:
                    dialogResult = JOptionPane.showConfirmDialog(
                            MSImagizer.instance,
                            memory + " GB available memory was detected. This might be insufficient. Please consider System Settings > Low Memory Mode if the process times out. Do you wish to continue?",
                            "Memory Settings",
                            JOptionPane.YES_NO_OPTION);
                    if (dialogResult == JOptionPane.YES_OPTION) {
                        return extractImageRangeMem(ranges, minI);
                    } else {
                        return null;
                    }
                case LOW:
                    dialogResult = JOptionPane.showConfirmDialog(
                            MSImagizer.instance,
                            memory + " GB available memory was detected. This will likely be insufficient, even for small projects. Please use System Settings > Low Memory Mode if the process times out. Do you wish to continue (not recommended)?",
                            "Memory Settings",
                            JOptionPane.YES_NO_OPTION);
                    if (dialogResult == JOptionPane.YES_OPTION) {
                        return extractImageRangeMem(ranges, minI);
                    } else {
                        return null;
                    }
                default:
                    JOptionPane.showMessageDialog(
                            MSImagizer.instance,
                            "Insufficient memory (" + memory + " GB) available. Please enable System Settings > Low Memory Mode",
                            "Memory Settings",
                            JOptionPane.PLAIN_MESSAGE);
                    return null;
            }
        } else {

            return extractImageRangeDb(ranges, minI);
        }
    }

    ////DATABASE
    private MultiMSiImage extractImageRangeDb(List<float[]> ranges, float minI) throws Exception {

        UILogger.log("Extracting image from hard drive...", UILogger.Level.INFO);

        float minMz = Float.MAX_VALUE;
        float maxMz = Float.MIN_VALUE;
        for (float[] range : ranges) {
            minMz = Math.min(range[0], minMz);
            maxMz = Math.max(range[1], maxMz);
        }

        long time = System.currentTimeMillis();

        File dbFile = new File(in + ".db");
        if (!dbFile.exists()) {
            MSImagizer.instance.getProgressBar().setText("Generating database file...");
            UILogger.log("Creating database, this may take a while...", UILogger.Level.INFO);
            String pythonFile = PythonExtractor.getPythonScript("CreateDB.py").getAbsolutePath();

            //TODO add intensity limiter
            String[] cmds = new String[]{"python", pythonFile, "--input", in};
            ProcessBuilder builder = new ProcessBuilder(cmds);
            MSImagizer.instance.getProgressBar().runExtractionProcess(builder.start());
        }
        HistoSnapDBFile file = new HistoSnapDBFile(dbFile);
        UILogger.log("Processing between " + minMz + " and " + maxMz, UILogger.Level.INFO);

        MSiFrame frame = file.getImage(minMz, maxMz).getFrame();

        System.out.println("Completed loading file in " + ((System.currentTimeMillis() - time) / 1000) + " seconds");

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        List<Future<MSiFrame>> subFrames = new LinkedList<>();
        for (float[] range : ranges) {
            subFrames.add(executor.submit(() -> {
                MSiFrame subFrame = frame.createSubFrame(range[0], range[1]);
                subFrame.setName(range[0] + " - " + range[1]);
                return subFrame;
            }));
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.DAYS);

        List<MSiFrame> frames = new ArrayList<>();
        for (Future<MSiFrame> subFrame : subFrames) {
            frames.add(subFrame.get());
        }

        System.out.println("Completed loading file in " + ((System.currentTimeMillis() - time) / 1000) + " seconds");

        File tmp = new File(out);
        if (tmp.exists()) {
            if (!tmp.delete()) {
                JOptionPane.showMessageDialog(MSImagizer.instance,
                        "Could not delete " + tmp.getAbsolutePath(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }

        return MultiMSiImage.generate(frames);

    }

    ////MEMORY
    private MultiMSiImage extractImageRangeMem(List<float[]> ranges, float minI) throws Exception {

        if (ranges.isEmpty()) {
            JOptionPane.showMessageDialog(MSImagizer.instance, "There is no range available");
            return null;
        }

        UILogger.log("Extracting image from memory...", UILogger.Level.INFO);

        //TODO check if ranges are close together, if not we split them up in subranges to speed up the extraction
        //Alternatively, we go over the python script to generate the bins
        float minMz = Float.MAX_VALUE;
        float maxMz = Float.MIN_VALUE;
        String minMzString = "\"";
        String maxMzString = "\"";
        for (float[] range : ranges) {
            minMz = Math.min(range[0], minMz);
            maxMz = Math.max(range[1], maxMz);
            minMzString += range[0] + " ";
            maxMzString += range[1] + " ";
        }

        minMzString = minMzString.trim();
        maxMzString = maxMzString.trim();

        minMzString += "\"";
        maxMzString += "\"";

        String pythonFile = PythonExtractor.getPythonScript("Extract.py").getAbsolutePath();

        String[] cmds = new String[]{"python", pythonFile, "--mzMin", "" + minMzString, "--mzMax", "" + maxMzString, "--input", "\"" + in + "\"", "--output", "\"" + out + "\"", "--threshold", "" + minI};

        for (String cmd : cmds) {
            System.out.print(cmd + " ");
        }
        System.out.println();

        ProcessBuilder builder = new ProcessBuilder(cmds);
        //  builder.inheritIO();
        MSImagizer.instance.getProgressBar().runExtractionProcess(builder.start());

        MSiFrame frame = new SpectralDataImporter().readFile(new File(out));

        if (frame.getWidth() <= 0 || frame.getHeight() <= 0) {
            return null;
        }

        frame.setParentFile(in);

        UILogger.log("Processing between " + ranges.get(0)[0] + " and " + ranges.get(ranges.size() - 1)[1], UILogger.Level.INFO);

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        List<Future<MSiFrame>> subFrames = new LinkedList<>();

        for (float[] range : ranges) {
            subFrames.add(executor.submit(() -> {
                MSiFrame subFrame = frame.createSubFrame(range[0], range[1]);
                subFrame.setName(range[0] + " - " + range[1]);
                return subFrame;
            }));
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.DAYS);

        List<MSiFrame> frames = new ArrayList<>();
        for (Future<MSiFrame> subFrame : subFrames) {
            frames.add(subFrame.get());
        }

        File tmp = new File(out);
        if (tmp.exists()) {
            if (!tmp.delete()) {
                JOptionPane.showMessageDialog(
                        MSImagizer.instance, 
                        "Could not delete " + tmp.getAbsolutePath(), 
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        return MultiMSiImage.generate(frames);
    }

    /**
     * Extracts all spectra into an MSiImage Object
     *
     * @param minI
     * @return
     * @throws IOException
     * @throws URISyntaxException
     * @throws Exception
     */
    public MSiImage extractFull(float minI) throws IOException, URISyntaxException, Exception {
        float[] range = new float[]{-1f, Float.MAX_VALUE};
        ArrayList<float[]> ranges = new ArrayList<>();
        ranges.add(range);
        return extractImageRange(ranges, minI);
    }

}
