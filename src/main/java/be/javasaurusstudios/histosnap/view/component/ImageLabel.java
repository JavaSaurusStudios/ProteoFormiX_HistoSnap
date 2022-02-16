package be.javasaurusstudios.histosnap.view.component;

import static be.javasaurusstudios.histosnap.view.MSImagizer.CACHE;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import javax.swing.JLabel;

/**
 *
 * @author Dr. Kenneth Verheggen <kenneth.verheggen@proteoformix.com>
 */
public class ImageLabel extends JLabel {

    //The starting point, where the dragging started
    private Point startingPoint;
    //The ending point, where dragging ended
    private Point endingPoint;
    //boolean indicating if the mouse is held down
    private boolean isMouseDown = false;

    public ImageLabel() {
        super();
    }

    public void setMouseDown(boolean mouseDown) {
        this.isMouseDown = mouseDown;
    }

    public void SetHighlightStart(Point point) {
        this.startingPoint = point;
    }

    public void setHighLightEnd(Point point) {
        this.endingPoint = point;
    }

    public Point getStartingPoint() {
        return startingPoint;
    }

    public Point getEndingPoint() {
        return endingPoint;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (CACHE.getImageList().size() > 0) {
            g.setColor(Color.red);
            if (startingPoint != null && endingPoint != null) {

                if (startingPoint.x > endingPoint.x) {
                    Point tmp = endingPoint;
                    endingPoint = startingPoint;
                    startingPoint = tmp;
                }

                g.drawRect(startingPoint.x, startingPoint.y, endingPoint.x - startingPoint.x, endingPoint.y - startingPoint.y);
            }
        }
    }

}
