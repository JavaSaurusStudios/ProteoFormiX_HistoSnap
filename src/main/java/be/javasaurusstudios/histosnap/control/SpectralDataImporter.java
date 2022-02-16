package be.javasaurusstudios.histosnap.control;

import be.javasaurusstudios.histosnap.model.image.MSiFrame;
import be.javasaurusstudios.histosnap.model.image.MSiPixel;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple importer for intermediary MSiSpectrum files
 *
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public class SpectralDataImporter {

    /**
     * Imports a csv file according to the provided specifications to be
     * imported
     *
     * @param file the import file
     * @return an MSiFrame
     * @throws FileNotFoundException
     * @throws IOException
     */
    public MSiFrame ReadFile(File file) throws FileNotFoundException, IOException {
        MSiFrame frame = new MSiFrame();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            int x = 0;
            int y = 0;
            int maxX = 0, maxY = 0;
            double minMz = -1, maxMz = 0;

            List<Double> mz = new ArrayList<>();
            List<Double> intensities = new ArrayList<>();

            for (String line; (line = br.readLine()) != null;) {

                if (line.startsWith(">")) {
                    if (mz.size() > 0) {
                        MSiPixel pixel = new MSiPixel(x - 1, y);
                        for (int i = 0; i < intensities.size(); i++) {
                            pixel.addDataPoint(mz.get(i), intensities.get(i));
                        }

                        frame.AddPixel(pixel);
                    }
                    x = Integer.parseInt(line.split("\t")[1]);
                    y = Integer.parseInt(line.split("\t")[2]);

                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);

                    mz.clear();
                    intensities.clear();
                } else {

                    double tmpMz = Float.parseFloat(line.split("\t")[0]);
                    maxMz = Math.max(maxMz, tmpMz);
                    if (minMz == -1) {
                        minMz = tmpMz;
                    } else {
                        minMz = Math.min(minMz, tmpMz);
                    }
                    mz.add(tmpMz);
                    intensities.add(Double.parseDouble(line.split("\t")[1]));
                }

            }
            frame.setWidth(maxX - 1);
            frame.setHeight(maxY - 1);
            frame.setMaxMz(maxMz);
            frame.setMinMz(minMz);
        }

        return frame;
    }

    /**
     * Translates a collection of lines into an MSiFrame
     *
     * @param lines the input lines
     * @return a MSiFrame
     */
    public MSiFrame ReadLines(List<String> lines) {
        MSiFrame frame = new MSiFrame();
        int x = 0;
        int y = 0;
        int maxX = 0, maxY = 0;
        double minMz = -1, maxMz = 0;

        List<Double> mz = new ArrayList<>();
        List<Double> intensities = new ArrayList<>();

        for (String line : lines) {

            if (line.startsWith(">")) {
                if (mz.size() > 0) {
                    MSiPixel pixel = new MSiPixel(x - 1, y);
                    for (int i = 0; i < intensities.size(); i++) {
                        pixel.addDataPoint(mz.get(i), intensities.get(i));
                    }
                    frame.AddPixel(pixel);
                }
                x = Integer.parseInt(line.split("\t")[1]);
                y = Integer.parseInt(line.split("\t")[2]);

                maxX = Math.max(maxX, x);
                maxY = Math.max(maxY, y);

                mz.clear();
                intensities.clear();
            } else {

                double tmpMz = Float.parseFloat(line.split("\t")[0]);
                maxMz = Math.max(maxMz, tmpMz);
                if (minMz == -1) {
                    minMz = tmpMz;
                } else {
                    minMz = Math.min(minMz, tmpMz);
                }
                mz.add(tmpMz);
                intensities.add(Double.parseDouble(line.split("\t")[1]));
            }

        }
        frame.setWidth(maxX - 1);
        frame.setHeight(maxY - 1);
        frame.setMaxMz(maxMz);
        frame.setMinMz(minMz);
        return frame;
    }

}
