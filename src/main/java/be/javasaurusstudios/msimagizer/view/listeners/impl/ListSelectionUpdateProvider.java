package be.javasaurusstudios.msimagizer.view.listeners.impl;

import be.javasaurusstudios.msimagizer.model.image.MSiImage;
import be.javasaurusstudios.msimagizer.view.listeners.ListenerProvider;
import be.javasaurusstudios.msimagizer.view.MSImagizer;
import static be.javasaurusstudios.msimagizer.view.MSImagizer.MSI_IMAGE;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public class ListSelectionUpdateProvider implements ListenerProvider {

    @Override
    public void SetUp(JComponent component) {
        JList imageCacheList = (JList) component;

        MSImagizer main = MSImagizer.instance;

        imageCacheList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (imageCacheList.getSelectedValuesList().size() == 1) {
                    MSI_IMAGE = (MSiImage) imageCacheList.getSelectedValue();
                    main.UpdateImage();
                }
            }
        });
    }

}
