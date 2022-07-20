package be.javasaurusstudios.histosnap.view.listeners.imagelist.impl;

import be.javasaurusstudios.histosnap.model.image.MSiFrame;
import be.javasaurusstudios.histosnap.model.image.MultiMSiImage;
import be.javasaurusstudios.histosnap.view.listeners.imagelist.ListenerProvider;
import be.javasaurusstudios.histosnap.view.MSImagizer;
import static be.javasaurusstudios.histosnap.view.MSImagizer.MSI_IMAGE;
import java.util.ArrayList;
import java.util.List;
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
                    MSI_IMAGE = MSImagizer.CACHE.getImage((String) imageCacheList.getSelectedValue());
                    main.UpdateImage();
                } else {
                    if (imageCacheList.getSelectedValuesList().size() > 1) {
                        List<MSiFrame> frames = new ArrayList<>();
                        for (Object value : imageCacheList.getSelectedValuesList()) {
                            frames.add(MSImagizer.CACHE.getImage((String) value).getFrame());
                        }
                        if (!frames.isEmpty()) {
                            MSI_IMAGE = MultiMSiImage.Generate(frames);
                            main.UpdateImage();
                        }
                    }
                }
            }
        });
    }

}
