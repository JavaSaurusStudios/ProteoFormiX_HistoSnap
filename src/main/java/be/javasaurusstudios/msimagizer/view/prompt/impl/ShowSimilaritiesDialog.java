package be.javasaurusstudios.msimagizer.view.prompt.impl;

import be.javasaurusstudios.msimagizer.model.image.MSiImage;
import be.javasaurusstudios.msimagizer.view.MSImagizer;
import be.javasaurusstudios.msimagizer.view.component.SimilaritySetup;
import be.javasaurusstudios.msimagizer.view.prompt.UserPrompt;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public class ShowSimilaritiesDialog implements UserPrompt {

    private final List<MSiImage> selectedImages;

    public ShowSimilaritiesDialog(List<MSiImage> selectedImages) {
        this.selectedImages = selectedImages;
    }

    @Override
    public void Show() {

        if (selectedImages.size() > 1) {
            SimilaritySetup setup = new SimilaritySetup(MSImagizer.instance);
            setup.SetImages(selectedImages);
            setup.setVisible(true);
            setup.setLocationRelativeTo(MSImagizer.instance);
            MSImagizer.instance.setVisible(false);
        } else {
            JOptionPane.showMessageDialog(MSImagizer.instance, "Please select at least 2 images", "Invalid selection...", JOptionPane.ERROR_MESSAGE);
        }
    }

}
