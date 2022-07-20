package be.javasaurusstudios.histosnap.view.listeners.imagelist.impl;

import be.javasaurusstudios.histosnap.view.MSImagizer;
import be.javasaurusstudios.histosnap.view.component.ImageLabel;
import be.javasaurusstudios.histosnap.view.listeners.imagelist.ListenerProvider;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

/**
 *
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public class ImageHighlightProvider implements ListenerProvider {

    private ImageLabel imgLabel;

    public ImageLabel getImgLabel() {
        return imgLabel;
    }

    @Override
    public void SetUp(JComponent component) {

        imgLabel = (ImageLabel) component;

        imgLabel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                imgLabel.setMouseDown(true);
                imgLabel.SetHighlightStart(e.getPoint());
                if (SwingUtilities.isRightMouseButton(e)) {
                    imgLabel.SetHighlightStart(null);
                    imgLabel.setHighLightEnd(null);
                    imgLabel.setMouseDown(false);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                imgLabel.setMouseDown(false);
                imgLabel.setHighLightEnd(e.getPoint());
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

        imgLabel.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                imgLabel.setHighLightEnd(e.getPoint());
                MSImagizer.instance.repaint();
            }

            @Override
            public void mouseMoved(MouseEvent e) {

            }
        });

    }

}
