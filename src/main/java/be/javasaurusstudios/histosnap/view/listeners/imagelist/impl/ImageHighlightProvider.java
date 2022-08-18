package be.javasaurusstudios.histosnap.view.listeners.imagelist.impl;

import be.javasaurusstudios.histosnap.view.HistoSnap;
import be.javasaurusstudios.histosnap.view.component.ImageLabel;
import be.javasaurusstudios.histosnap.view.listeners.imagelist.ListenerProvider;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JComponent;

/**
 *
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public class ImageHighlightProvider implements ListenerProvider {

    /**
     * The label to draw on
     */
    private ImageLabel imgLabel;

    public ImageLabel getImgLabel() {
        return imgLabel;
    }

    @Override
    public void setUp(JComponent component) {

        if (!(component instanceof ImageLabel)) {
            return;
        }

        imgLabel = (ImageLabel) component;

        imgLabel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (!HistoSnap.instance.iAnnotationMode()) {
                    return;
                }
                imgLabel.setHighlightStart(e.getPoint());
                imgLabel.setMouseDown(true);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!HistoSnap.instance.iAnnotationMode()) {
                    return;
                }
                imgLabel.setMouseDown(false);
                imgLabel.setHighLightEnd(e.getPoint());
                imgLabel.createAnnotation(e.getPoint());
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
                if (!HistoSnap.instance.iAnnotationMode()) {
                    return;
                }
                imgLabel.setHighLightEnd(e.getPoint());
                HistoSnap.instance.repaint();

            }

            @Override
            public void mouseMoved(MouseEvent e) {

            }
        });

    }

}
