/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.javasaurusstudios.histosnap.view.component;

import be.javasaurusstudios.histosnap.control.MzRangeExtractor;
import be.javasaurusstudios.histosnap.control.util.UILogger;
import be.javasaurusstudios.histosnap.view.MSImagizer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

/**
 *
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public class ProgressBar {

    private final JProgressBar bar;
    private final JLabel label;

    public ProgressBar(JProgressBar bar, JLabel label) {
        this.bar = bar;
        this.label = label;
        bar.setIndeterminate(true);
    }

    public void setText(String txt) {
        label.setText(txt);
    }

    public void setValue(float value, boolean indeterminate) {
        try {
            bar.setValue((int) (Math.max(0, Math.min(100, (value * 100)))));
            bar.setIndeterminate(indeterminate);
        } catch (java.lang.ArithmeticException e) {
            bar.setIndeterminate(true);
        }
    }

    public void setValueText(float value, String txt, boolean indeterminate) {
        setValue(value, indeterminate);
        setText(txt);
    }

    public void setVisible(boolean visible) {
        bar.setVisible(visible);
        label.setVisible(visible);
    }

    public void RunExtractionProcess(Process process) throws InterruptedException {
        SwingWorker worker = new SwingWorker<Void, Float>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        String tmp = line.replace(" %", "");
                        try {
                            float value = Float.parseFloat(tmp);
                            publish(value);
                        } catch (NumberFormatException e) {
                            MSImagizer.instance.getProgressBar().setValueText(0, "Working...", true);
                        }

                        //                        UILogger.Log(line);
                    }
                    MSImagizer.instance.getProgressBar().setValueText(0, "Finalizing", true);
                } catch (IOException ex) {
                    Logger.getLogger(MzRangeExtractor.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }

            @Override
            protected void process(List<Float> chunks) {
                for (Float value : chunks) {
                    MSImagizer.instance.getProgressBar().setValueText(value / 100, "Processing Spectra : " + Math.round(value) + "%", false);
                }
            }
        };
        worker.execute();
        process.waitFor();
    }

}
