/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.javasaurusstudios.histosnap.view.component;

import javax.swing.JLabel;
import javax.swing.JProgressBar;

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

    public void setVisible(boolean visible) {
        bar.setVisible(visible);
        label.setVisible(visible);
    }

}
