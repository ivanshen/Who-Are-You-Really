package org.jfree.chart.axis;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.io.Serializable;
import org.jfree.chart.Effect3D;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.ui.RectangleEdge;

public class CategoryAxis3D extends CategoryAxis implements Cloneable, Serializable {
    private static final long serialVersionUID = 4114732251353700972L;

    public CategoryAxis3D() {
        this(null);
    }

    public CategoryAxis3D(String label) {
        super(label);
    }

    public AxisState draw(Graphics2D g2, double cursor, Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge, PlotRenderingInfo plotState) {
        if (!isVisible()) {
            return new AxisState(cursor);
        }
        CategoryPlot plot = (CategoryPlot) getPlot();
        Rectangle2D adjustedDataArea = new Double();
        if (plot.getRenderer() instanceof Effect3D) {
            Effect3D e3D = (Effect3D) plot.getRenderer();
            double adjustedX = dataArea.getMinX();
            double adjustedY = dataArea.getMinY();
            double adjustedW = dataArea.getWidth() - e3D.getXOffset();
            double adjustedH = dataArea.getHeight() - e3D.getYOffset();
            if (edge == RectangleEdge.LEFT || edge == RectangleEdge.BOTTOM) {
                adjustedY += e3D.getYOffset();
            } else if (edge == RectangleEdge.RIGHT || edge == RectangleEdge.TOP) {
                adjustedX += e3D.getXOffset();
            }
            adjustedDataArea.setRect(adjustedX, adjustedY, adjustedW, adjustedH);
        } else {
            adjustedDataArea.setRect(dataArea);
        }
        if (isAxisLineVisible()) {
            drawAxisLine(g2, cursor, adjustedDataArea, edge);
        }
        AxisState axisState = new AxisState(cursor);
        if (isTickMarksVisible()) {
            drawTickMarks(g2, cursor, adjustedDataArea, edge, axisState);
        }
        AxisState state = drawCategoryLabels(g2, plotArea, adjustedDataArea, edge, axisState, plotState);
        if (getAttributedLabel() != null) {
            return drawAttributedLabel(getAttributedLabel(), g2, plotArea, dataArea, edge, state);
        }
        return drawLabel(getLabel(), g2, plotArea, dataArea, edge, state);
    }

    public double getCategoryJava2DCoordinate(CategoryAnchor anchor, int category, int categoryCount, Rectangle2D area, RectangleEdge edge) {
        Rectangle2D adjustedArea = area;
        CategoryItemRenderer renderer = ((CategoryPlot) getPlot()).getRenderer();
        if (renderer instanceof Effect3D) {
            Effect3D e3D = (Effect3D) renderer;
            double adjustedX = area.getMinX();
            double adjustedY = area.getMinY();
            double adjustedW = area.getWidth() - e3D.getXOffset();
            double adjustedH = area.getHeight() - e3D.getYOffset();
            if (edge == RectangleEdge.LEFT || edge == RectangleEdge.BOTTOM) {
                adjustedY += e3D.getYOffset();
            } else if (edge == RectangleEdge.RIGHT || edge == RectangleEdge.TOP) {
                adjustedX += e3D.getXOffset();
            }
            adjustedArea = new Double(adjustedX, adjustedY, adjustedW, adjustedH);
        }
        if (anchor == CategoryAnchor.START) {
            return getCategoryStart(category, categoryCount, adjustedArea, edge);
        }
        if (anchor == CategoryAnchor.MIDDLE) {
            return getCategoryMiddle(category, categoryCount, adjustedArea, edge);
        }
        if (anchor == CategoryAnchor.END) {
            return getCategoryEnd(category, categoryCount, adjustedArea, edge);
        }
        return 0.0d;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
