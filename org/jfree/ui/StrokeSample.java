package org.jfree.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D.Double;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.axis.DateAxis;

public class StrokeSample extends JComponent implements ListCellRenderer {
    private Dimension preferredSize;
    private Stroke stroke;

    public StrokeSample(Stroke stroke) {
        this.stroke = stroke;
        this.preferredSize = new Dimension(80, 18);
        setPreferredSize(this.preferredSize);
    }

    public Stroke getStroke() {
        return this.stroke;
    }

    public void setStroke(Stroke stroke) {
        this.stroke = stroke;
        repaint();
    }

    public Dimension getPreferredSize() {
        return this.preferredSize;
    }

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Dimension size = getSize();
        Insets insets = getInsets();
        double xx = (double) insets.left;
        double yy = (double) insets.top;
        double ww = (size.getWidth() - ((double) insets.left)) - ((double) insets.right);
        double hh = (size.getHeight() - ((double) insets.top)) - ((double) insets.bottom);
        Double doubleR = new Double(6.0d + xx, (hh / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS) + yy);
        doubleR = new Double((xx + ww) - 6.0d, (hh / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS) + yy);
        Ellipse2D circle1 = new Ellipse2D.Double(doubleR.getX() - XYPointerAnnotation.DEFAULT_ARROW_LENGTH, doubleR.getY() - XYPointerAnnotation.DEFAULT_ARROW_LENGTH, XYPointerAnnotation.DEFAULT_TIP_RADIUS, XYPointerAnnotation.DEFAULT_TIP_RADIUS);
        Ellipse2D circle2 = new Ellipse2D.Double(doubleR.getX() - 6.0d, doubleR.getY() - XYPointerAnnotation.DEFAULT_ARROW_LENGTH, XYPointerAnnotation.DEFAULT_TIP_RADIUS, XYPointerAnnotation.DEFAULT_TIP_RADIUS);
        g2.draw(circle1);
        g2.fill(circle1);
        g2.draw(circle2);
        g2.fill(circle2);
        Line2D.Double doubleR2 = new Line2D.Double(doubleR, doubleR);
        if (this.stroke != null) {
            g2.setStroke(this.stroke);
            g2.draw(doubleR2);
        }
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value instanceof Stroke) {
            setStroke((Stroke) value);
        } else {
            setStroke(null);
        }
        return this;
    }
}
