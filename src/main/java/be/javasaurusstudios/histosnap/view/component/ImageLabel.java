package be.javasaurusstudios.histosnap.view.component;

import be.javasaurusstudios.histosnap.model.image.MSiFrame;
import be.javasaurusstudios.histosnap.model.image.MultiMSiImage;
import be.javasaurusstudios.histosnap.model.image.annotation.AnnotationCircle;
import be.javasaurusstudios.histosnap.model.image.annotation.AnnotationRect;
import be.javasaurusstudios.histosnap.view.MSImagizer;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.LinkedList;
import javax.swing.JLabel;
import be.javasaurusstudios.histosnap.model.image.annotation.AnnotationShape;

/**
 *
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public class ImageLabel extends JLabel {

    private int undoCacheSize = 30;
    //The starting point, where the dragging started
    private Point startingPoint;
    //The ending point, where dragging ended
    private Point endingPoint;
    //boolean indicating if the mouse is held down
    private boolean mouseDown;
    private final LinkedList<AnnotationShape> annotationShapes;

    private final LinkedList<AnnotationShape> undoneAnnotationShapes;

    public ImageLabel() {
        super();
        this.annotationShapes = new LinkedList<>();
        this.undoneAnnotationShapes = new LinkedList<>();
    }

    public LinkedList<AnnotationShape> getAnnotationShapes() {
        return annotationShapes;
    }

    public void addAnnotationShape(AnnotationShape shape) {
        annotationShapes.add(shape);
        MSImagizer.instance.UpdateImage();
    }

    public void undo() {
        if (!annotationShapes.isEmpty()) {
            undoneAnnotationShapes.add(annotationShapes.pollLast());
            if (undoneAnnotationShapes.size() > undoCacheSize) {
                undoneAnnotationShapes.pollFirst();
            }
        }
        MSImagizer.instance.UpdateImage();
    }

    public void redo() {
        if (!undoneAnnotationShapes.isEmpty()) {
            annotationShapes.addLast(undoneAnnotationShapes.pollLast());
        }
        MSImagizer.instance.UpdateImage();
    }

    public void SetHighlightStart(Point point) {
        this.startingPoint = point;
    }

    public void setHighLightEnd(Point point) {
        this.endingPoint = point;
    }

    public MSiFrame getHoveredFrame(Point e) {
        if (MSImagizer.MSI_IMAGE == null) {
            return null;
        }
        if (!(MSImagizer.MSI_IMAGE instanceof MultiMSiImage)) {
            return MSImagizer.MSI_IMAGE.getFrame();
        } else {
            MultiMSiImage img = (MultiMSiImage) MSImagizer.MSI_IMAGE;
            for (MSiFrame frame : img.getFrames()) {
                if (frame.getRect().contains(e) || frame.getRect().contains(e.getLocation())) {
                    return frame;
                }
            }
        }
        return null;
    }

    public void createAnnotation(Point endPoint) {
        if (MSImagizer.MSI_IMAGE == null) {
            return;
        }

        MSiFrame frame = getHoveredFrame(endPoint);
        if (frame != null) {
            //this all works on scale 1 so we need to divide everything to the scale ?

            int scale = MSImagizer.instance.getCurrentScale();
            int x = endPoint.x / scale;
            int y = endPoint.y / scale;
            int xRel = x % frame.getWidth();
            int yRel = y % frame.getHeight();
            System.out.println(x + "," + y + " => " + xRel + "," + yRel + " = " + frame.getName());

            int width = (x - (startingPoint.x / scale));
            int height = (y - (startingPoint.y / scale));

            switch (MSImagizer.instance.getCurrentAnnotationShapeType()) {
                case ARC:
                    addAnnotationShape(new AnnotationCircle(xRel - width, yRel - height, width, height, MSImagizer.instance.getCurrentAnnotationColor()));
                    break;
                case RECTANGLE:
                    addAnnotationShape(new AnnotationRect(xRel - width, yRel - height, width, height, MSImagizer.instance.getCurrentAnnotationColor()));
                    break;
            }
        }
    }

    public boolean isMouseDown() {
        return mouseDown;
    }

    public void setMouseDown(boolean mouseDown) {
        this.mouseDown = mouseDown;
    }

    @Override
    public void paint(Graphics g) {
        if (getIcon() != null) {
            setSize(getIcon().getIconWidth(), getIcon().getIconHeight());
        }
        super.paint(g);

        g.setColor(Color.cyan);
        g.drawRect(getX() + 1, getY() + 1, getWidth() - 2, getHeight() - 2);

        //   g.fillRect(0, 0, getWidth(), getHeight());
        if (MSImagizer.instance.IsAnnotationMode()) {
            if (mouseDown && startingPoint != null && endingPoint != null) {
                if (startingPoint.x < endingPoint.x && endingPoint.y > startingPoint.y) {
                    g.setColor(MSImagizer.instance.getCurrentAnnotationColor());
                    switch (MSImagizer.instance.getCurrentAnnotationShapeType()) {
                        case ARC:
                            g.drawArc(startingPoint.x, startingPoint.y, endingPoint.x - startingPoint.x, endingPoint.y - startingPoint.y, 0, 360);
                            break;
                        case RECTANGLE:
                            g.drawRect(startingPoint.x, startingPoint.y, endingPoint.x - startingPoint.x, endingPoint.y - startingPoint.y);
                            break;
                    }
                }
            }
        }

    }

    public void clear() {
        annotationShapes.clear();
    }

}
