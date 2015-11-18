package org.jfree.chart.axis;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.category.CategoryDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.text.TextUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;

public class SubCategoryAxis extends CategoryAxis implements Cloneable, Serializable {
    private static final long serialVersionUID = -1279463299793228344L;
    private List subCategories;
    private Font subLabelFont;
    private transient Paint subLabelPaint;

    public SubCategoryAxis(String label) {
        super(label);
        this.subLabelFont = new Font("SansSerif", 0, 10);
        this.subLabelPaint = Color.black;
        this.subCategories = new ArrayList();
    }

    public void addSubCategory(Comparable subCategory) {
        ParamChecks.nullNotPermitted(subCategory, "subCategory");
        this.subCategories.add(subCategory);
        notifyListeners(new AxisChangeEvent(this));
    }

    public Font getSubLabelFont() {
        return this.subLabelFont;
    }

    public void setSubLabelFont(Font font) {
        ParamChecks.nullNotPermitted(font, "font");
        this.subLabelFont = font;
        notifyListeners(new AxisChangeEvent(this));
    }

    public Paint getSubLabelPaint() {
        return this.subLabelPaint;
    }

    public void setSubLabelPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.subLabelPaint = paint;
        notifyListeners(new AxisChangeEvent(this));
    }

    public AxisSpace reserveSpace(Graphics2D g2, Plot plot, Rectangle2D plotArea, RectangleEdge edge, AxisSpace space) {
        if (space == null) {
            space = new AxisSpace();
        }
        if (!isVisible()) {
            return space;
        }
        space = super.reserveSpace(g2, plot, plotArea, edge, space);
        double maxdim = getMaxDim(g2, edge);
        if (RectangleEdge.isTopOrBottom(edge)) {
            space.add(maxdim, edge);
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            space.add(maxdim, edge);
        }
        return space;
    }

    private double getMaxDim(Graphics2D g2, RectangleEdge edge) {
        double result = 0.0d;
        g2.setFont(this.subLabelFont);
        FontMetrics fm = g2.getFontMetrics();
        for (Comparable subcategory : this.subCategories) {
            double dim;
            Rectangle2D bounds = TextUtilities.getTextBounds(subcategory.toString(), g2, fm);
            if (RectangleEdge.isLeftOrRight(edge)) {
                dim = bounds.getWidth();
            } else {
                dim = bounds.getHeight();
            }
            result = Math.max(result, dim);
        }
        return result;
    }

    public AxisState draw(Graphics2D g2, double cursor, Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge, PlotRenderingInfo plotState) {
        if (!isVisible()) {
            return new AxisState(cursor);
        }
        if (isAxisLineVisible()) {
            drawAxisLine(g2, cursor, dataArea, edge);
        }
        AxisState state = drawCategoryLabels(g2, plotArea, dataArea, edge, drawSubCategoryLabels(g2, plotArea, dataArea, edge, new AxisState(cursor), plotState), plotState);
        if (getAttributedLabel() != null) {
            return drawAttributedLabel(getAttributedLabel(), g2, plotArea, dataArea, edge, state);
        }
        return drawLabel(getLabel(), g2, plotArea, dataArea, edge, state);
    }

    protected AxisState drawSubCategoryLabels(Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge, AxisState state, PlotRenderingInfo plotState) {
        ParamChecks.nullNotPermitted(state, "state");
        g2.setFont(this.subLabelFont);
        g2.setPaint(this.subLabelPaint);
        int categoryCount = 0;
        CategoryDataset dataset = ((CategoryPlot) getPlot()).getDataset();
        if (dataset != null) {
            categoryCount = dataset.getColumnCount();
        }
        double maxdim = getMaxDim(g2, edge);
        for (int categoryIndex = 0; categoryIndex < categoryCount; categoryIndex++) {
            double x0 = 0.0d;
            double x1 = 0.0d;
            double y0 = 0.0d;
            double y1 = 0.0d;
            if (edge == RectangleEdge.TOP) {
                x0 = getCategoryStart(categoryIndex, categoryCount, dataArea, edge);
                x1 = getCategoryEnd(categoryIndex, categoryCount, dataArea, edge);
                y1 = state.getCursor();
                y0 = y1 - maxdim;
            } else if (edge == RectangleEdge.BOTTOM) {
                x0 = getCategoryStart(categoryIndex, categoryCount, dataArea, edge);
                x1 = getCategoryEnd(categoryIndex, categoryCount, dataArea, edge);
                y0 = state.getCursor();
                y1 = y0 + maxdim;
            } else if (edge == RectangleEdge.LEFT) {
                y0 = getCategoryStart(categoryIndex, categoryCount, dataArea, edge);
                y1 = getCategoryEnd(categoryIndex, categoryCount, dataArea, edge);
                x1 = state.getCursor();
                x0 = x1 - maxdim;
            } else if (edge == RectangleEdge.RIGHT) {
                y0 = getCategoryStart(categoryIndex, categoryCount, dataArea, edge);
                y1 = getCategoryEnd(categoryIndex, categoryCount, dataArea, edge);
                x0 = state.getCursor();
                x1 = x0 + maxdim;
            }
            Rectangle2D area = new Double(x0, y0, x1 - x0, y1 - y0);
            int subCategoryCount = this.subCategories.size();
            float width = (float) ((x1 - x0) / ((double) subCategoryCount));
            float height = (float) ((y1 - y0) / ((double) subCategoryCount));
            for (int i = 0; i < subCategoryCount; i++) {
                float xx;
                float yy;
                double d;
                if (RectangleEdge.isTopOrBottom(edge)) {
                    d = (double) width;
                    xx = (float) (((((double) i) + 0.5d) * r0) + x0);
                    yy = (float) area.getCenterY();
                } else {
                    xx = (float) area.getCenterX();
                    d = (double) height;
                    yy = (float) (((((double) i) + 0.5d) * r0) + y0);
                }
                TextUtilities.drawRotatedString(this.subCategories.get(i).toString(), g2, xx, yy, TextAnchor.CENTER, 0.0d, TextAnchor.CENTER);
            }
        }
        if (edge.equals(RectangleEdge.TOP)) {
            state.cursorUp(maxdim);
        } else {
            if (edge.equals(RectangleEdge.BOTTOM)) {
                state.cursorDown(maxdim);
            } else if (edge == RectangleEdge.LEFT) {
                state.cursorLeft(maxdim);
            } else if (edge == RectangleEdge.RIGHT) {
                state.cursorRight(maxdim);
            }
        }
        return state;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof SubCategoryAxis) || !super.equals(obj)) {
            return false;
        }
        SubCategoryAxis axis = (SubCategoryAxis) obj;
        if (!this.subCategories.equals(axis.subCategories)) {
            return false;
        }
        if (!this.subLabelFont.equals(axis.subLabelFont)) {
            return false;
        }
        if (this.subLabelPaint.equals(axis.subLabelPaint)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return super.hashCode();
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.subLabelPaint, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.subLabelPaint = SerialUtilities.readPaint(stream);
    }
}
