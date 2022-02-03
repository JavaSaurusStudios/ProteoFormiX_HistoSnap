package be.javasaurusstudios.msimagizer.view.component;

import be.javasaurusstudios.msimagizer.control.tasks.WorkingThread;
import be.javasaurusstudios.msimagizer.control.tasks.SimilarityCalculationTask;
import be.javasaurusstudios.msimagizer.control.tasks.WorkingTaskPostProcess;
import be.javasaurusstudios.msimagizer.model.image.MSiImage;
import be.javasaurusstudios.msimagizer.model.SimilarityResult;
import be.javasaurusstudios.msimagizer.control.util.ImageUtils;
import be.javasaurusstudios.msimagizer.control.util.UILogger;
import be.javasaurusstudios.msimagizer.view.MSImagizer;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public class SimilaritySetup extends javax.swing.JFrame {

    //The similarity threshold value
    private static double similarityThreshold = 5;
    //The jList object that holds the images that can be manipulated
    private static JList imageCacheList;
    //boolean state indicating if the mouse is held down
    private static boolean mouseDown = false;
    //The starting point, where the dragging started
    private static Point startingPoint;
    //The ending point, where dragging ended
    private static Point endingPoint;
    //the imagizer instance (the parent)
    private final MSImagizer imagizer;
    //the collection of images that can be used to calculate similarities
    private List<MSiImage> selectedImages;

    /**
     * Creates new form SimilaritySetup
     *
     * @param imagizer
     */
    public SimilaritySetup(MSImagizer imagizer) {

        initComponents();
        imageCacheList = imageList;
        imageList.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        this.imagizer = imagizer;

        InitListSelection();
        InitImageMouseListener();

        SetSimilaritySlider();

        super.setSize(imagizer.getSize());
        super.setTitle(imagizer.getTitle() + " - similarities");
        super.setLocation(imagizer.getLocation());

    }

    /**
     * Update the images in the similarity setup frame
     *
     * @param images the selected images
     */
    public void SetImages(List<MSiImage> images) {
        DefaultListModel model = new DefaultListModel();
        for (MSiImage cachedImage : images) {
            model.addElement(cachedImage);
        }
        this.selectedImages = images;
        imageList.setModel(model);
        SetImage(images.get(0));
    }

    /**
     * Initializes the list functionality (auto update to the selected value)
     */
    private void InitListSelection() {
        imageList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (imageCacheList.getSelectedValuesList().size() == 1) {
                    SetImage((MSiImage) imageCacheList.getSelectedValue());
                }
            }
        });
    }

    private void SetImage(MSiImage msiImage) {
        msiImage.CreateImage(imagizer.getCurrentMode(), imagizer.getCurrentRange().getColors());
        BufferedImage scaled = msiImage.getScaledImage(imagizer.getCurrentScale());
        imgLabel.setSize(scaled.getWidth(), scaled.getHeight());
        imgLabel.setIcon(new ImageIcon(scaled));

    }

    /**
     * Set up a mouse listener to handle dragging, this is used to make a
     * subselection of the image to act as a frame of reference
     */
    private void InitImageMouseListener() {

        imgLabel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                mouseDown = true;
                startingPoint = e.getPoint();
                if (SwingUtilities.isRightMouseButton(e)) {
                    startingPoint = null;
                    endingPoint = null;
                    mouseDown = false;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mouseDown = false;
                endingPoint = e.getPoint();
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
                endingPoint = e.getPoint();
                repaint();
            }

            @Override
            public void mouseMoved(MouseEvent e) {

            }
        });

    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics gr = imgLabel.getGraphics();
        gr.setColor(Color.red);
        if (startingPoint != null && endingPoint != null) {

            if (startingPoint.x > endingPoint.x) {
                Point tmp = endingPoint;
                endingPoint = startingPoint;
                startingPoint = tmp;
            }

            gr.drawRect(startingPoint.x, startingPoint.y, endingPoint.x - startingPoint.x, endingPoint.y - startingPoint.y);
        }
    }

    /**
     * Updates the simlilarity slider
     */
    private void SetSimilaritySlider() {
        jSlider1.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent evt) {
                JSlider slider = (JSlider) evt.getSource();
                int value = slider.getValue();
                similarityThreshold = (double) value / 10;
                similarityLabel.setText(similarityThreshold + " %");
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        imageList = new javax.swing.JList<>();
        btnProcess = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        tfOutputField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        btnSelectOutput = new javax.swing.JButton();
        jSlider1 = new javax.swing.JSlider();
        jLabel3 = new javax.swing.JLabel();
        similarityLabel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        imgLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        imageList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(imageList);

        btnProcess.setText("Run");
        btnProcess.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProcessActionPerformed(evt);
            }
        });

        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        jLabel1.setText("Select reference image");

        tfOutputField.setText("-");

        jLabel2.setText("Output Folder");

        btnSelectOutput.setText("...");
        btnSelectOutput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectOutputActionPerformed(evt);
            }
        });

        jSlider1.setMaximum(1000);
        jSlider1.setMinorTickSpacing(1);

        jLabel3.setText("Similarity Threshold (%)");

        similarityLabel.setText("5%");

        imgLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 350, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(imgLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 326, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 321, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(imgLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addGap(12, 12, 12)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnCancel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnProcess))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSlider1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(tfOutputField))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnSelectOutput, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(similarityLabel, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnCancel, btnProcess});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel2, jLabel3});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(similarityLabel))
                .addGap(9, 9, 9)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSelectOutput)
                    .addComponent(tfOutputField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnProcess)
                    .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        imagizer.setVisible(true);
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnSelectOutputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectOutputActionPerformed

        final JFrame parent = this;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save similarity...");
        fileChooser.setCurrentDirectory(MSImagizer.lastDirectory);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "Output Folder";
            }
        });
        int userSelection = fileChooser.showSaveDialog(parent);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            MSImagizer.lastDirectory = fileChooser.getSelectedFile();
            tfOutputField.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }


    }//GEN-LAST:event_btnSelectOutputActionPerformed

    private void btnProcessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProcessActionPerformed

        final JFrame parent = this;

        MSiImage selectedImage = imageCacheList.getSelectedIndex() >= 0 ? (MSiImage) imageCacheList.getSelectedValue() : selectedImages.get(0);
        selectedImage.CreateImage(imagizer.getCurrentMode(), imagizer.getCurrentRange().getColors());
        BufferedImage refImage = selectedImage.getScaledImage(imagizer.getCurrentScale());
        String refName = selectedImage.getName();

        final BufferedImage[] images = new BufferedImage[selectedImages.size()];
        final String[] names = new String[selectedImages.size()];

        final boolean drawHighlight = (startingPoint != null && endingPoint != null);
        final int minX = drawHighlight ? Math.min(startingPoint.x, endingPoint.x) : 0;
        final int maxX = drawHighlight ? Math.max(startingPoint.x, endingPoint.x) : refImage.getWidth();
        final int minY = drawHighlight ? Math.min(startingPoint.y, endingPoint.y) : 0;
        final int maxY = drawHighlight ? Math.max(startingPoint.y, endingPoint.y) : refImage.getHeight();

        WorkingTaskPostProcess postProcess = new WorkingTaskPostProcess() {
            @Override
            public void run() {
                FileWriter out = null;
                try {
                    List<SimilarityResult> results = (ArrayList<SimilarityResult>) result;
                    File outputFile = new File(tfOutputField.getText(), "similarities.txt");
                    out = new FileWriter(outputFile);
                    for (SimilarityResult sr : results) {
                        out.append(sr.getName() + "\t" + sr.getSimilarity()).append(System.lineSeparator());
                    }
                    out.flush();
                    imagizer.getProgressFrame().setVisible(false);
                    JOptionPane.showMessageDialog(parent, "Exported similarity report to " + outputFile.getAbsolutePath());

                    for (SimilarityResult sr : results) {
                        if (sr.getSimilarity() >= jSlider1.getValue() / 10) {
                            BufferedImage bImage = drawHighlight ? HighlightZone(sr, minX, maxX, minY, maxY) : sr.getOriginal();
                            bImage = ImageUtils.SetImageTitle(bImage, sr.getName());
                            ImageIO.write(bImage, "png", new File(tfOutputField.getText(), sr.getName().replaceAll(" ", "_") + ".png"));
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(MSImagizer.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        imagizer.setVisible(true);
                        parent.setVisible(false);
                        parent.dispose();
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(MSImagizer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }
        };

        for (int i = 0; i < selectedImages.size(); i++) {
            selectedImages.get(i).CreateImage(imagizer.getCurrentMode(), imagizer.getCurrentRange().getColors());
            images[i] = selectedImages.get(i).getScaledImage(imagizer.getCurrentScale());
            names[i] = selectedImages.get(i).getName();
        }

        try {
            if (!drawHighlight) {
                DoSimilarities(similarityThreshold, images, names, refImage, refName, postProcess);
            } else {
                DoSimilarities(similarityThreshold,
                        minX,
                        maxX,
                        minY,
                        maxY,
                        images, names, refImage, refName, postProcess);
            }

        } catch (Throwable e) {
            e.printStackTrace();

        }


    }//GEN-LAST:event_btnProcessActionPerformed

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
    private void DoSimilarities(double percentage, BufferedImage[] images, String[] names, BufferedImage refImage, String refName, WorkingTaskPostProcess... postProcessing) {
        try {
            SimilarityCalculationTask task = new SimilarityCalculationTask(
                    imagizer.getProgressFrame(),
                    images,
                    names,
                    refImage,
                    refName,
                    percentage);
            for (WorkingTaskPostProcess postProcess : postProcessing) {
                task.AddPostProcessing(postProcess);
            }
            new WorkingThread(this, task).execute();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Something went wrong..." + ex.getMessage(),
                    "Failed to calculate similarities...",
                    JOptionPane.ERROR_MESSAGE);
            UILogger.Log("Failed to calculate similarities",UILogger.Level.ERROR);
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
    private void DoSimilarities(double percentage, int minX, int maxX, int minY, int maxY, BufferedImage[] images, String[] names, BufferedImage refImage, String refName, WorkingTaskPostProcess... postProcessing) {
        try {
            SimilarityCalculationTask task = new SimilarityCalculationTask(
                    imagizer.getProgressFrame(),
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
            new WorkingThread(this, task).execute();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Something went wrong..." + ex.getMessage(),
                    "Failed to calculate similarities...",
                    JOptionPane.ERROR_MESSAGE);
            UILogger.Log("Failed to calculate similarities",UILogger.Level.ERROR);
            return;
        }
    }

    /**
     * Creates a buffered image highlighting the selected "region of interest"
     *
     * @param result the similarity result
     * @param minX the lower left X for the rectangle
     * @param maxX the upper right X for the rectangle
     * @param minY the lower left Y for the rectangle
     * @param maxY the upper left Y for the rectangle
     * @return a buffered image with a highlighted region of interest
     */
    private BufferedImage HighlightZone(SimilarityResult result, int minX, int maxX, int minY, int maxY) {
        BufferedImage image = result.getOriginal();
        BufferedImage framedImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());

        Graphics2D graph = framedImage.createGraphics();
        graph.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
        graph.setColor(Color.red);
        graph.drawRect(
                minX,
                minY,
                Math.abs(maxX - minX),
                Math.abs(maxY - minY)
        );
        return framedImage;
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnProcess;
    private javax.swing.JButton btnSelectOutput;
    private javax.swing.JList<String> imageList;
    private javax.swing.JLabel imgLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSlider jSlider1;
    private javax.swing.JLabel similarityLabel;
    private javax.swing.JTextField tfOutputField;
    // End of variables declaration//GEN-END:variables
}
