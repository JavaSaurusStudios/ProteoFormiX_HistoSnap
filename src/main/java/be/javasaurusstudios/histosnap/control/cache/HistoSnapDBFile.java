package be.javasaurusstudios.histosnap.control.cache;

import be.javasaurusstudios.histosnap.control.util.UILogger;
import be.javasaurusstudios.histosnap.model.image.MSiFrame;
import be.javasaurusstudios.histosnap.model.image.MSiImage;
import be.javasaurusstudios.histosnap.model.image.MSiPixel;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 * This class represents a hard drive cached sqlite database to query (avoiding
 * memory usage)
 *
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public class HistoSnapDBFile {

    /**
     * The original input file (imzml)
     */
    private final File inputFile;
    /**
     * The database connection
     */
    private Connection c;

    /**
     * *
     * Constructor
     *
     * @param inputFile the inputfile
     */
    public HistoSnapDBFile(File inputFile) {
        this.inputFile = inputFile;
        if (checkForCache()) {
            connectToCache();
        }
    }

    /**
     * Checks if the database file already exists
     *
     * @return boolean state
     */
    private boolean checkForCache() {
        return inputFile != null && inputFile.exists();
    }

    /**
     * Connects to the cache
     */
    private void connectToCache() {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:" + inputFile.getAbsolutePath());
            System.out.println("Opened database successfully");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieve an image in a mass range
     *
     * @param min the minimal mz value
     * @param max the maximal mz value
     * @return the resulting MSiImage object
     * @throws SQLException
     */
    public MSiImage getImage(float min, float max) throws SQLException {
        MSiImage image = new MSiImage(getFullFrame(min, max));
        return image;
    }

    /**
     * Retrieve an frame in a mass range
     *
     * @param min the minimal mz value
     * @param max the maximal mz value
     * @return the resulting MSiImage object
     * @throws SQLException
     */
    public MSiFrame getFrame(float min, float max) throws SQLException {
        int[] dimensions = getDimensions();
        MSiFrame frame = new MSiFrame();
        frame.setParentFile(inputFile.getAbsolutePath());
        int i = 0;
        int pixelSize = dimensions[0] * dimensions[1];
        for (int x = 0; x < dimensions[0]; x++) {
            for (int y = 0; y < dimensions[1]; y++) {
                MSiPixel pixel = getPixel(x, y, min, max);
                if (pixel != null) {
                    frame.AddPixel(pixel);
                }
                i++;
                System.out.println(i + " / " + pixelSize);
            }
        }
        frame.setWidth(dimensions[0]);
        frame.setHeight(dimensions[1]);
        return frame;
    }

    /**
     * Retrieve an MSI pixel
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the resulting pixel
     * @throws SQLException
     */
    public MSiPixel getPixel(int x, int y) throws SQLException {
        return getPixel(x, y, 0, Float.MAX_VALUE);
    }

    /**
     * Retrieve the entire frame between two given mass to charge values
     *
     * @param min the minimal mz value
     * @param max the maximal mz value
     * @return the resulting MSiFrame
     * @throws SQLException
     */
    public MSiFrame getFullFrame(float min, float max) throws SQLException {
        long time = System.currentTimeMillis();

        String sql = "SELECT x,y,mz,i FROM pixels WHERE mz<=" + max + " AND mz>=" + min + ";";
        PreparedStatement stmt = c.prepareStatement(sql);

        MSiFrame frame = new MSiFrame();
        int[] dimensions = getDimensions();

        frame.setWidth(dimensions[0]);
        frame.setHeight(dimensions[1]);

        ResultSet rs = stmt.executeQuery();

        int width = dimensions[0] + 1;
        int height = dimensions[1] + 1;
        MSiPixel[][] pixels = new MSiPixel[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                pixels[i][j] = new MSiPixel(i, j);
            }
        }

        while (rs.next()) {
            pixels[rs.getInt("x")][rs.getInt("y")].addDataPoint(rs.getDouble("mz"), rs.getDouble("i"));
        }
        UILogger.Log("Reading HistoSnap database file...", UILogger.Level.INFO);
        System.out.println("Executed query in " + ((System.currentTimeMillis() - time) / 1000) + " seconds");

        for (int i = 0; i < dimensions[0]; i++) {
            for (int j = 0; j < dimensions[1]; j++) {
                frame.AddPixel(pixels[i][j]);
            }
        }

        return frame;
    }

    /**
     * Retrieve an MSI pixel within a mz range
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param min the minimal mz value
     * @param max the maximal mz value
     * @return the resulting pixel
     * @throws SQLException
     */
    public MSiPixel getPixel(int x, int y, float min, float max) throws SQLException {
        String sql = "SELECT mz,i FROM pixels WHERE x=" + x + " AND y=" + y + " AND mz<=" + max + " AND mz>=" + min + ";";

        PreparedStatement stmt = c.prepareStatement(sql);

        ResultSet rs = stmt.executeQuery();
        LinkedList<Double> mzValues = new LinkedList<>();
        LinkedList<Double> iValues = new LinkedList<>();
        while (rs.next()) {
            mzValues.addLast(rs.getDouble("mz"));
            iValues.addLast(rs.getDouble("i"));
        }

        if (mzValues.isEmpty()) {
            return null;
        }

        MSiPixel pixel = new MSiPixel(x, y);
        for (int i = 0; i < mzValues.size(); i++) {
            pixel.addDataPoint(mzValues.get(i), iValues.get(i));
        }

        return pixel;
    }

    /**
     * Retrieves the dimensions of an image
     *
     * @return the dimensions
     * @throws SQLException
     */
    public int[] getDimensions() throws SQLException {
        String sql = "SELECT MAX(x),MAX(y) FROM pixels ;";
        PreparedStatement stmt = c.prepareStatement(sql);

        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return new int[]{rs.getInt(1), rs.getInt(2)};
        }
        return new int[]{0, 0};
    }

}
