package org.jfree.chart.axis;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.text.TextUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.ObjectUtilities;

public class MarkerAxisBand implements Serializable {
    private static final long serialVersionUID = -1729482413886398919L;
    private NumberAxis axis;
    private double bottomInnerGap;
    private double bottomOuterGap;
    private Font font;
    private List markers;
    private double topInnerGap;
    private double topOuterGap;

    public MarkerAxisBand(NumberAxis axis, double topOuterGap, double topInnerGap, double bottomOuterGap, double bottomInnerGap, Font font) {
        this.axis = axis;
        this.topOuterGap = topOuterGap;
        this.topInnerGap = topInnerGap;
        this.bottomOuterGap = bottomOuterGap;
        this.bottomInnerGap = bottomInnerGap;
        this.font = font;
        this.markers = new ArrayList();
    }

    public void addMarker(IntervalMarker marker) {
        this.markers.add(marker);
    }

    public double getHeight(Graphics2D g2) {
        if (this.markers.size() <= 0) {
            return 0.0d;
        }
        return (((this.topOuterGap + this.topInnerGap) + ((double) this.font.getLineMetrics("123g", g2.getFontRenderContext()).getHeight())) + this.bottomInnerGap) + this.bottomOuterGap;
    }

    private void drawStringInRect(Graphics2D g2, Rectangle2D bounds, Font font, String text) {
        g2.setFont(font);
        Rectangle2D r = TextUtilities.getTextBounds(text, g2, g2.getFontMetrics(font));
        double x = bounds.getX();
        if (r.getWidth() < bounds.getWidth()) {
            x += (bounds.getWidth() - r.getWidth()) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
        }
        g2.drawString(text, (float) x, (float) ((bounds.getMaxY() - this.bottomInnerGap) - ((double) font.getLineMetrics(text, g2.getFontRenderContext()).getDescent())));
    }

    public void draw(Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea, double x, double y) {
        double h = getHeight(g2);
        for (IntervalMarker marker : this.markers) {
            double start = Math.max(marker.getStartValue(), this.axis.getRange().getLowerBound());
            double end = Math.min(marker.getEndValue(), this.axis.getRange().getUpperBound());
            double s = this.axis.valueToJava2D(start, dataArea, RectangleEdge.BOTTOM);
            Rectangle2D r = new Double(s, this.topOuterGap + y, this.axis.valueToJava2D(end, dataArea, RectangleEdge.BOTTOM) - s, (h - this.topOuterGap) - this.bottomOuterGap);
            Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(3, marker.getAlpha()));
            g2.setPaint(marker.getPaint());
            g2.fill(r);
            g2.setPaint(marker.getOutlinePaint());
            g2.draw(r);
            g2.setComposite(originalComposite);
            g2.setPaint(Color.black);
            drawStringInRect(g2, r, this.font, marker.getLabel());
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof MarkerAxisBand)) {
            return false;
        }
        MarkerAxisBand that = (MarkerAxisBand) obj;
        if (this.topOuterGap != that.topOuterGap) {
            return false;
        }
        if (this.topInnerGap != that.topInnerGap) {
            return false;
        }
        if (this.bottomInnerGap != that.bottomInnerGap) {
            return false;
        }
        if (this.bottomOuterGap != that.bottomOuterGap) {
            return false;
        }
        if (!ObjectUtilities.equal(this.font, that.font)) {
            return false;
        }
        if (ObjectUtilities.equal(this.markers, that.markers)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return ((this.font.hashCode() + 703) * 19) + this.markers.hashCode();
    }
}
