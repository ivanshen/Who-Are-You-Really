package org.jfree.chart.renderer.xy;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D.Double;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.LookupPaintScale;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYZDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.util.PublicCloneable;
import org.jfree.util.ShapeUtilities;

public class XYShapeRenderer extends AbstractXYItemRenderer implements XYItemRenderer, Cloneable, Serializable {
    private static final long serialVersionUID = 8320552104211173221L;
    private boolean drawOutlines;
    private transient Paint guideLinePaint;
    private transient Stroke guideLineStroke;
    private boolean guideLinesVisible;
    private PaintScale paintScale;
    private boolean useFillPaint;
    private boolean useOutlinePaint;

    public XYShapeRenderer() {
        this.paintScale = new LookupPaintScale();
        this.useFillPaint = false;
        this.drawOutlines = false;
        this.useOutlinePaint = true;
        this.guideLinesVisible = false;
        this.guideLinePaint = Color.darkGray;
        this.guideLineStroke = new BasicStroke();
        setBaseShape(new Double(-5.0d, -5.0d, XYPointerAnnotation.DEFAULT_TIP_RADIUS, XYPointerAnnotation.DEFAULT_TIP_RADIUS));
        setAutoPopulateSeriesShape(false);
    }

    public PaintScale getPaintScale() {
        return this.paintScale;
    }

    public void setPaintScale(PaintScale scale) {
        ParamChecks.nullNotPermitted(scale, "scale");
        this.paintScale = scale;
        notifyListeners(new RendererChangeEvent(this));
    }

    public boolean getDrawOutlines() {
        return this.drawOutlines;
    }

    public void setDrawOutlines(boolean flag) {
        this.drawOutlines = flag;
        fireChangeEvent();
    }

    public boolean getUseFillPaint() {
        return this.useFillPaint;
    }

    public void setUseFillPaint(boolean flag) {
        this.useFillPaint = flag;
        fireChangeEvent();
    }

    public boolean getUseOutlinePaint() {
        return this.useOutlinePaint;
    }

    public void setUseOutlinePaint(boolean use) {
        this.useOutlinePaint = use;
        fireChangeEvent();
    }

    public boolean isGuideLinesVisible() {
        return this.guideLinesVisible;
    }

    public void setGuideLinesVisible(boolean visible) {
        this.guideLinesVisible = visible;
        fireChangeEvent();
    }

    public Paint getGuideLinePaint() {
        return this.guideLinePaint;
    }

    public void setGuideLinePaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.guideLinePaint = paint;
        fireChangeEvent();
    }

    public Stroke getGuideLineStroke() {
        return this.guideLineStroke;
    }

    public void setGuideLineStroke(Stroke stroke) {
        ParamChecks.nullNotPermitted(stroke, "stroke");
        this.guideLineStroke = stroke;
        fireChangeEvent();
    }

    public Range findDomainBounds(XYDataset dataset) {
        if (dataset == null) {
            return null;
        }
        Range r = DatasetUtilities.findDomainBounds(dataset, false);
        if (r != null) {
            return new Range(r.getLowerBound() + 0.0d, r.getUpperBound() + 0.0d);
        }
        return null;
    }

    public Range findRangeBounds(XYDataset dataset) {
        if (dataset == null) {
            return null;
        }
        Range r = DatasetUtilities.findRangeBounds(dataset, false);
        if (r != null) {
            return new Range(r.getLowerBound() + 0.0d, r.getUpperBound() + 0.0d);
        }
        return null;
    }

    public Range findZBounds(XYZDataset dataset) {
        if (dataset != null) {
            return DatasetUtilities.findZBounds(dataset);
        }
        return null;
    }

    public int getPassCount() {
        return 2;
    }

    public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass) {
        EntityCollection entities = null;
        if (info != null) {
            entities = info.getOwner().getEntityCollection();
        }
        double x = dataset.getXValue(series, item);
        double y = dataset.getYValue(series, item);
        if (!Double.isNaN(x) && !Double.isNaN(y)) {
            double transX = domainAxis.valueToJava2D(x, dataArea, plot.getDomainAxisEdge());
            double transY = rangeAxis.valueToJava2D(y, dataArea, plot.getRangeAxisEdge());
            PlotOrientation orientation = plot.getOrientation();
            if (pass == 0 && this.guideLinesVisible) {
                g2.setStroke(this.guideLineStroke);
                g2.setPaint(this.guideLinePaint);
                Graphics2D graphics2D;
                if (orientation == PlotOrientation.HORIZONTAL) {
                    graphics2D = g2;
                    graphics2D.draw(new Line2D.Double(transY, dataArea.getMinY(), transY, dataArea.getMaxY()));
                    graphics2D = g2;
                    graphics2D.draw(new Line2D.Double(dataArea.getMinX(), transX, dataArea.getMaxX(), transX));
                    return;
                }
                graphics2D = g2;
                graphics2D.draw(new Line2D.Double(transX, dataArea.getMinY(), transX, dataArea.getMaxY()));
                graphics2D = g2;
                graphics2D.draw(new Line2D.Double(dataArea.getMinX(), transY, dataArea.getMaxX(), transY));
            } else if (pass == 1) {
                Shape shape = getItemShape(series, item);
                if (orientation == PlotOrientation.HORIZONTAL) {
                    shape = ShapeUtilities.createTranslatedShape(shape, transY, transX);
                } else if (orientation == PlotOrientation.VERTICAL) {
                    shape = ShapeUtilities.createTranslatedShape(shape, transX, transY);
                }
                Shape hotspot = shape;
                if (shape.intersects(dataArea)) {
                    g2.setPaint(getPaint(dataset, series, item));
                    g2.fill(shape);
                    if (this.drawOutlines) {
                        if (getUseOutlinePaint()) {
                            g2.setPaint(getItemOutlinePaint(series, item));
                        } else {
                            g2.setPaint(getItemPaint(series, item));
                        }
                        g2.setStroke(getItemOutlineStroke(series, item));
                        g2.draw(shape);
                    }
                }
                if (entities != null) {
                    addEntity(entities, hotspot, dataset, series, item, transX, transY);
                }
            }
        }
    }

    protected Paint getPaint(XYDataset dataset, int series, int item) {
        if (dataset instanceof XYZDataset) {
            return this.paintScale.getPaint(((XYZDataset) dataset).getZValue(series, item));
        } else if (this.useFillPaint) {
            return getItemFillPaint(series, item);
        } else {
            return getItemPaint(series, item);
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYShapeRenderer)) {
            return false;
        }
        XYShapeRenderer that = (XYShapeRenderer) obj;
        if (this.paintScale.equals(that.paintScale) && this.drawOutlines == that.drawOutlines && this.useOutlinePaint == that.useOutlinePaint && this.useFillPaint == that.useFillPaint && this.guideLinesVisible == that.guideLinesVisible && this.guideLinePaint.equals(that.guideLinePaint) && this.guideLineStroke.equals(that.guideLineStroke)) {
            return super.equals(obj);
        }
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        XYShapeRenderer clone = (XYShapeRenderer) super.clone();
        if (this.paintScale instanceof PublicCloneable) {
            clone.paintScale = (PaintScale) this.paintScale.clone();
        }
        return clone;
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.guideLinePaint = SerialUtilities.readPaint(stream);
        this.guideLineStroke = SerialUtilities.readStroke(stream);
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.guideLinePaint, stream);
        SerialUtilities.writeStroke(this.guideLineStroke, stream);
    }
}
