package be.javasaurusstudios.histosnap.view.listeners.imagelist.impl;

import be.javasaurusstudios.histosnap.view.listeners.imagelist.ListenerProvider;
import be.javasaurusstudios.histosnap.view.HistoSnap;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 *
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public class ListSavePopupProvider implements ListenerProvider {

    @Override
    public void setUp(JComponent component) {

        if (!(component instanceof JLabel)) {
            return;
        }

        HistoSnap main = HistoSnap.instance;
        
        JLabel lbImage = (JLabel) component;

        JPopupMenu menu = new JPopupMenu();
        JMenuItem item = new JMenuItem("Save...");
        menu.add(item);

        item.addActionListener((ActionEvent e) -> {
            main.save();
        });

        lbImage.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (HistoSnap.CURRENT_IMAGE != null && (e.getModifiers() & InputEvent.BUTTON3_MASK) != 0) {
                    menu.show(e.getComponent(), e.getX(), e.getY());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

    }

}
