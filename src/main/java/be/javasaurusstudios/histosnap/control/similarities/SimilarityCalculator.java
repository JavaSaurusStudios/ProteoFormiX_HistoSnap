/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.javasaurusstudios.histosnap.control.similarities;

import be.javasaurusstudios.histosnap.control.tasks.SimilarityCalculationTask;
import be.javasaurusstudios.histosnap.control.tasks.WorkingTaskPostProcess;
import be.javasaurusstudios.histosnap.control.tasks.WorkingThread;
import be.javasaurusstudios.histosnap.control.util.UILogger;
import be.javasaurusstudios.histosnap.view.MSImagizer;
import java.awt.image.BufferedImage;
import javax.swing.JOptionPane;

/**
 *
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public class SimilarityCalculator {

    /**
     * Calculate similarities
     *
     * @param percentage the threshold to consider as "outlier"
     * @param images the images to compare
     * @param names the names of the images to compare
     * @param refImage the reference image
     * @param refName the name of the reference image
     * @param postProcessing potential postprocessing tasks (for example
     * reporting, exporting outliers images, etc)
     */
    public static void DoSimilarities(double percentage, BufferedImage[] images, String[] names, BufferedImage refImage, String refName, WorkingTaskPostProcess... postProcessing) {
        try {
            SimilarityCalculationTask task = new SimilarityCalculationTask(
                    MSImagizer.instance.getProgressFrame(),
                    images,
                    names,
                    refImage,
                    refName,
                    percentage);
            for (WorkingTaskPostProcess postProcess : postProcessing) {
                task.AddPostProcessing(postProcess);
            }
            new WorkingThread(MSImagizer.instance, task).execute();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(MSImagizer.instance,
                    "Something went wrong..." + ex.getMessage(),
                    "Failed to calculate similarities...",
                    JOptionPane.ERROR_MESSAGE);
            UILogger.Log("Failed to calculate similarities", UILogger.Level.ERROR);
            return;
        }
    }

    /**
     * @param percentage the threshold to consider as "outlier"
     * @param minX
     * @param maxX
     * @param minY
     * @param maxY
     * @param images the images to compare
     * @param names the names of the images to compare
     * @param refImage the reference image
     * @param refName the name of the reference image
     * @param postProcessing potential postprocessing tasks (for example
     * reporting, exporting outliers images, etc)
     */
    public static void DoSimilarities(double percentage, int minX, int maxX, int minY, int maxY, BufferedImage[] images, String[] names, BufferedImage refImage, String refName, WorkingTaskPostProcess... postProcessing) {
        try {
            SimilarityCalculationTask task = new SimilarityCalculationTask(
                    MSImagizer.instance.getProgressFrame(),
                    minX,
                    maxX,
                    minY,
                    maxY,
                    images,
                    names,
                    refImage,
                    refName,
                    percentage);
            for (WorkingTaskPostProcess postProcess : postProcessing) {
                task.AddPostProcessing(postProcess);
            }
            new WorkingThread(MSImagizer.instance, task).execute();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(MSImagizer.instance,
                    "Something went wrong..." + ex.getMessage(),
                    "Failed to calculate similarities...",
                    JOptionPane.ERROR_MESSAGE);
            UILogger.Log("Failed to calculate similarities", UILogger.Level.ERROR);
            return;
        }
    }

}
