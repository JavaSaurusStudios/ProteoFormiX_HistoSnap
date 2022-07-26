package be.javasaurusstudios.histosnap.view.listeners.input;

import be.javasaurusstudios.histosnap.view.MSImagizer;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.JLabel;

/**
 *
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public class ScaleScrollListener implements MouseWheelListener, KeyListener {

    private final MSImagizer parent;
    private boolean ctrlHeld;

    public ScaleScrollListener(MSImagizer parent, JLabel imgLabel) {
        this.parent = parent;
        imgLabel.addMouseWheelListener(this);
        this.parent.addKeyListener(this);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (ctrlHeld) {
            int notches = e.getWheelRotation();
            if (notches < 0) {
                parent.increaseCurrentScale();
            } else if (notches > 0) {
                parent.decreaseCurrentScale();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!ctrlHeld && (e.getKeyCode() == KeyEvent.VK_CONTROL)) {
            ctrlHeld = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

        if (ctrlHeld && (e.getKeyCode() == KeyEvent.VK_CONTROL)) {
            ctrlHeld = false;
        }

    }
}
