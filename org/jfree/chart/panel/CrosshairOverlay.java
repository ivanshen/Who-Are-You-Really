package org.jfree.chart.panel;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Crosshair;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.util.ParamChecks;
import org.jfree.text.TextUtilities;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;

public class CrosshairOverlay extends AbstractOverlay implements Overlay, PropertyChangeListener, PublicCloneable, Cloneable, Serializable {
    private List xCrosshairs;
    private List yCrosshairs;

    public CrosshairOverlay() {
        this.xCrosshairs = new ArrayList();
        this.yCrosshairs = new ArrayList();
    }

    public void addDomainCrosshair(Crosshair crosshair) {
        ParamChecks.nullNotPermitted(crosshair, "crosshair");
        this.xCrosshairs.add(crosshair);
        crosshair.addPropertyChangeListener(this);
        fireOverlayChanged();
    }

    public void removeDomainCrosshair(Crosshair crosshair) {
        ParamChecks.nullNotPermitted(crosshair, "crosshair");
        if (this.xCrosshairs.remove(crosshair)) {
            crosshair.removePropertyChangeListener(this);
            fireOverlayChanged();
        }
    }

    public void clearDomainCrosshairs() {
        if (!this.xCrosshairs.isEmpty()) {
            List crosshairs = getDomainCrosshairs();
            for (int i = 0; i < crosshairs.size(); i++) {
                Crosshair c = (Crosshair) crosshairs.get(i);
                this.xCrosshairs.remove(c);
                c.removePropertyChangeListener(this);
            }
            fireOverlayChanged();
        }
    }

    public List getDomainCrosshairs() {
        return new ArrayList(this.xCrosshairs);
    }

    public void addRangeCrosshair(Crosshair crosshair) {
        ParamChecks.nullNotPermitted(crosshair, "crosshair");
        this.yCrosshairs.add(crosshair);
        crosshair.addPropertyChangeListener(this);
        fireOverlayChanged();
    }

    public void removeRangeCrosshair(Crosshair crosshair) {
        ParamChecks.nullNotPermitted(crosshair, "crosshair");
        if (this.yCrosshairs.remove(crosshair)) {
            crosshair.removePropertyChangeListener(this);
            fireOverlayChanged();
        }
    }

    public void clearRangeCrosshairs() {
        if (!this.yCrosshairs.isEmpty()) {
            List crosshairs = getRangeCrosshairs();
            for (int i = 0; i < crosshairs.size(); i++) {
                Crosshair c = (Crosshair) crosshairs.get(i);
                this.yCrosshairs.remove(c);
                c.removePropertyChangeListener(this);
            }
            fireOverlayChanged();
        }
    }

    public List getRangeCrosshairs() {
        return new ArrayList(this.yCrosshairs);
    }

    public void propertyChange(PropertyChangeEvent e) {
        fireOverlayChanged();
    }

    public void paintOverlay(Graphics2D g2, ChartPanel chartPanel) {
        Shape savedClip = g2.getClip();
        Rectangle2D dataArea = chartPanel.getScreenDataArea();
        g2.clip(dataArea);
        XYPlot plot = (XYPlot) chartPanel.getChart().getPlot();
        ValueAxis xAxis = plot.getDomainAxis();
        RectangleEdge xAxisEdge = plot.getDomainAxisEdge();
        for (Crosshair ch : this.xCrosshairs) {
            if (ch.isVisible()) {
                double xx = xAxis.valueToJava2D(ch.getValue(), dataArea, xAxisEdge);
                if (plot.getOrientation() == PlotOrientation.VERTICAL) {
                    drawVerticalCrosshair(g2, dataArea, xx, ch);
                } else {
                    drawHorizontalCrosshair(g2, dataArea, xx, ch);
                }
            }
        }
        ValueAxis yAxis = plot.getRangeAxis();
        RectangleEdge yAxisEdge = plot.getRangeAxisEdge();
        for (Crosshair ch2 : this.yCrosshairs) {
            if (ch2.isVisible()) {
                double yy = yAxis.valueToJava2D(ch2.getValue(), dataArea, yAxisEdge);
                if (plot.getOrientation() == PlotOrientation.VERTICAL) {
                    drawHorizontalCrosshair(g2, dataArea, yy, ch2);
                } else {
                    drawVerticalCrosshair(g2, dataArea, yy, ch2);
                }
            }
        }
        g2.setClip(savedClip);
    }

    protected void drawHorizontalCrosshair(Graphics2D g2, Rectangle2D dataArea, double y, Crosshair crosshair) {
        if (y >= dataArea.getMinY() && y <= dataArea.getMaxY()) {
            Line2D line = new Double(dataArea.getMinX(), y, dataArea.getMaxX(), y);
            Paint savedPaint = g2.getPaint();
            Stroke savedStroke = g2.getStroke();
            g2.setPaint(crosshair.getPaint());
            g2.setStroke(crosshair.getStroke());
            g2.draw(line);
            if (crosshair.isLabelVisible()) {
                String label = crosshair.getLabelGenerator().generateLabel(crosshair);
                RectangleAnchor anchor = crosshair.getLabelAnchor();
                Point2D pt = calculateLabelPoint(line, anchor, XYPointerAnnotation.DEFAULT_ARROW_LENGTH, XYPointerAnnotation.DEFAULT_ARROW_LENGTH);
                float xx = (float) pt.getX();
                float yy = (float) pt.getY();
                TextAnchor alignPt = textAlignPtForLabelAnchorH(anchor);
                Shape hotspot = TextUtilities.calculateRotatedStringBounds(label, g2, xx, yy, alignPt, 0.0d, TextAnchor.CENTER);
                if (!dataArea.contains(hotspot.getBounds2D())) {
                    anchor = flipAnchorV(anchor);
                    pt = calculateLabelPoint(line, anchor, XYPointerAnnotation.DEFAULT_ARROW_LENGTH, XYPointerAnnotation.DEFAULT_ARROW_LENGTH);
                    xx = (float) pt.getX();
                    yy = (float) pt.getY();
                    alignPt = textAlignPtForLabelAnchorH(anchor);
                    hotspot = TextUtilities.calculateRotatedStringBounds(label, g2, xx, yy, alignPt, 0.0d, TextAnchor.CENTER);
                }
                g2.setPaint(crosshair.getLabelBackgroundPaint());
                g2.fill(hotspot);
                g2.setPaint(crosshair.getLabelOutlinePaint());
                g2.draw(hotspot);
                TextUtilities.drawAlignedString(label, g2, xx, yy, alignPt);
            }
            g2.setPaint(savedPaint);
            g2.setStroke(savedStroke);
        }
    }

    protected void drawVerticalCrosshair(Graphics2D g2, Rectangle2D dataArea, double x, Crosshair crosshair) {
        if (x >= dataArea.getMinX() && x <= dataArea.getMaxX()) {
            Line2D line = new Double(x, dataArea.getMinY(), x, dataArea.getMaxY());
            Paint savedPaint = g2.getPaint();
            Stroke savedStroke = g2.getStroke();
            g2.setPaint(crosshair.getPaint());
            g2.setStroke(crosshair.getStroke());
            g2.draw(line);
            if (crosshair.isLabelVisible()) {
                String label = crosshair.getLabelGenerator().generateLabel(crosshair);
                RectangleAnchor anchor = crosshair.getLabelAnchor();
                Point2D pt = calculateLabelPoint(line, anchor, XYPointerAnnotation.DEFAULT_ARROW_LENGTH, XYPointerAnnotation.DEFAULT_ARROW_LENGTH);
                float xx = (float) pt.getX();
                float yy = (float) pt.getY();
                TextAnchor alignPt = textAlignPtForLabelAnchorV(anchor);
                Shape hotspot = TextUtilities.calculateRotatedStringBounds(label, g2, xx, yy, alignPt, 0.0d, TextAnchor.CENTER);
                if (!dataArea.contains(hotspot.getBounds2D())) {
                    anchor = flipAnchorH(anchor);
                    pt = calculateLabelPoint(line, anchor, XYPointerAnnotation.DEFAULT_ARROW_LENGTH, XYPointerAnnotation.DEFAULT_ARROW_LENGTH);
                    xx = (float) pt.getX();
                    yy = (float) pt.getY();
                    alignPt = textAlignPtForLabelAnchorV(anchor);
                    hotspot = TextUtilities.calculateRotatedStringBounds(label, g2, xx, yy, alignPt, 0.0d, TextAnchor.CENTER);
                }
                g2.setPaint(crosshair.getLabelBackgroundPaint());
                g2.fill(hotspot);
                g2.setPaint(crosshair.getLabelOutlinePaint());
                g2.draw(hotspot);
                TextUtilities.drawAlignedString(label, g2, xx, yy, alignPt);
            }
            g2.setPaint(savedPaint);
            g2.setStroke(savedStroke);
        }
    }

    private Point2D calculateLabelPoint(Line2D line, RectangleAnchor anchor, double deltaX, double deltaY) {
        double x;
        double y;
        boolean left = anchor == RectangleAnchor.BOTTOM_LEFT || anchor == RectangleAnchor.LEFT || anchor == RectangleAnchor.TOP_LEFT;
        boolean right = anchor == RectangleAnchor.BOTTOM_RIGHT || anchor == RectangleAnchor.RIGHT || anchor == RectangleAnchor.TOP_RIGHT;
        boolean top = anchor == RectangleAnchor.TOP_LEFT || anchor == RectangleAnchor.TOP || anchor == RectangleAnchor.TOP_RIGHT;
        boolean bottom = anchor == RectangleAnchor.BOTTOM_LEFT || anchor == RectangleAnchor.BOTTOM || anchor == RectangleAnchor.BOTTOM_RIGHT;
        Rectangle rect = line.getBounds();
        if (line.getX1() == line.getX2()) {
            x = line.getX1();
            y = (line.getY1() + line.getY2()) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
            if (left) {
                x -= deltaX;
            }
            if (right) {
                x += deltaX;
            }
            if (top) {
                y = Math.min(line.getY1(), line.getY2()) + deltaY;
            }
            if (bottom) {
                y = Math.max(line.getY1(), line.getY2()) - deltaY;
            }
        } else {
            x = (line.getX1() + line.getX2()) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
            y = line.getY1();
            if (left) {
                x = Math.min(line.getX1(), line.getX2()) + deltaX;
            }
            if (right) {
                x = Math.max(line.getX1(), line.getX2()) - deltaX;
            }
            if (top) {
                y -= deltaY;
            }
            if (bottom) {
                y += deltaY;
            }
        }
        return new Point2D.Double(x, y);
    }

    private TextAnchor textAlignPtForLabelAnchorV(RectangleAnchor anchor) {
        TextAnchor result = TextAnchor.CENTER;
        if (anchor.equals(RectangleAnchor.TOP_LEFT)) {
            return TextAnchor.TOP_RIGHT;
        }
        if (anchor.equals(RectangleAnchor.TOP)) {
            return TextAnchor.TOP_CENTER;
        }
        if (anchor.equals(RectangleAnchor.TOP_RIGHT)) {
            return TextAnchor.TOP_LEFT;
        }
        if (anchor.equals(RectangleAnchor.LEFT)) {
            return TextAnchor.HALF_ASCENT_RIGHT;
        }
        if (anchor.equals(RectangleAnchor.RIGHT)) {
            return TextAnchor.HALF_ASCENT_LEFT;
        }
        if (anchor.equals(RectangleAnchor.BOTTOM_LEFT)) {
            return TextAnchor.BOTTOM_RIGHT;
        }
        if (anchor.equals(RectangleAnchor.BOTTOM)) {
            return TextAnchor.BOTTOM_CENTER;
        }
        if (anchor.equals(RectangleAnchor.BOTTOM_RIGHT)) {
            return TextAnchor.BOTTOM_LEFT;
        }
        return result;
    }

    private TextAnchor textAlignPtForLabelAnchorH(RectangleAnchor anchor) {
        TextAnchor result = TextAnchor.CENTER;
        if (anchor.equals(RectangleAnchor.TOP_LEFT)) {
            return TextAnchor.BOTTOM_LEFT;
        }
        if (anchor.equals(RectangleAnchor.TOP)) {
            return TextAnchor.BOTTOM_CENTER;
        }
        if (anchor.equals(RectangleAnchor.TOP_RIGHT)) {
            return TextAnchor.BOTTOM_RIGHT;
        }
        if (anchor.equals(RectangleAnchor.LEFT)) {
            return TextAnchor.HALF_ASCENT_LEFT;
        }
        if (anchor.equals(RectangleAnchor.RIGHT)) {
            return TextAnchor.HALF_ASCENT_RIGHT;
        }
        if (anchor.equals(RectangleAnchor.BOTTOM_LEFT)) {
            return TextAnchor.TOP_LEFT;
        }
        if (anchor.equals(RectangleAnchor.BOTTOM)) {
            return TextAnchor.TOP_CENTER;
        }
        if (anchor.equals(RectangleAnchor.BOTTOM_RIGHT)) {
            return TextAnchor.TOP_RIGHT;
        }
        return result;
    }

    private RectangleAnchor flipAnchorH(RectangleAnchor anchor) {
        RectangleAnchor result = anchor;
        if (anchor.equals(RectangleAnchor.TOP_LEFT)) {
            return RectangleAnchor.TOP_RIGHT;
        }
        if (anchor.equals(RectangleAnchor.TOP_RIGHT)) {
            return RectangleAnchor.TOP_LEFT;
        }
        if (anchor.equals(RectangleAnchor.LEFT)) {
            return RectangleAnchor.RIGHT;
        }
        if (anchor.equals(RectangleAnchor.RIGHT)) {
            return RectangleAnchor.LEFT;
        }
        if (anchor.equals(RectangleAnchor.BOTTOM_LEFT)) {
            return RectangleAnchor.BOTTOM_RIGHT;
        }
        if (anchor.equals(RectangleAnchor.BOTTOM_RIGHT)) {
            return RectangleAnchor.BOTTOM_LEFT;
        }
        return result;
    }

    private RectangleAnchor flipAnchorV(RectangleAnchor anchor) {
        RectangleAnchor result = anchor;
        if (anchor.equals(RectangleAnchor.TOP_LEFT)) {
            return RectangleAnchor.BOTTOM_LEFT;
        }
        if (anchor.equals(RectangleAnchor.TOP_RIGHT)) {
            return RectangleAnchor.BOTTOM_RIGHT;
        }
        if (anchor.equals(RectangleAnchor.TOP)) {
            return RectangleAnchor.BOTTOM;
        }
        if (anchor.equals(RectangleAnchor.BOTTOM)) {
            return RectangleAnchor.TOP;
        }
        if (anchor.equals(RectangleAnchor.BOTTOM_LEFT)) {
            return RectangleAnchor.TOP_LEFT;
        }
        if (anchor.equals(RectangleAnchor.BOTTOM_RIGHT)) {
            return RectangleAnchor.TOP_RIGHT;
        }
        return result;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CrosshairOverlay)) {
            return false;
        }
        CrosshairOverlay that = (CrosshairOverlay) obj;
        if (!this.xCrosshairs.equals(that.xCrosshairs)) {
            return false;
        }
        if (this.yCrosshairs.equals(that.yCrosshairs)) {
            return true;
        }
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        CrosshairOverlay clone = (CrosshairOverlay) super.clone();
        clone.xCrosshairs = (List) ObjectUtilities.deepClone(this.xCrosshairs);
        clone.yCrosshairs = (List) ObjectUtilities.deepClone(this.yCrosshairs);
        return clone;
    }
}
