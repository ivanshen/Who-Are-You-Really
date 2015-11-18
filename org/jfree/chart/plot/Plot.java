package org.jfree.chart.plot;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javax.swing.event.EventListenerList;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.LegendItemSource;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.PlotEntity;
import org.jfree.chart.event.AnnotationChangeEvent;
import org.jfree.chart.event.AnnotationChangeListener;
import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.event.AxisChangeListener;
import org.jfree.chart.event.ChartChangeEventType;
import org.jfree.chart.event.MarkerChangeEvent;
import org.jfree.chart.event.MarkerChangeListener;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.event.PlotChangeListener;
import org.jfree.chart.renderer.xy.XYLine3DRenderer;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.DatasetGroup;
import org.jfree.io.SerialUtilities;
import org.jfree.text.G2TextMeasurer;
import org.jfree.text.TextBlockAnchor;
import org.jfree.text.TextUtilities;
import org.jfree.ui.Align;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;

public abstract class Plot implements AxisChangeListener, DatasetChangeListener, AnnotationChangeListener, MarkerChangeListener, LegendItemSource, PublicCloneable, Cloneable, Serializable {
    public static final float DEFAULT_BACKGROUND_ALPHA = 1.0f;
    public static final Paint DEFAULT_BACKGROUND_PAINT;
    public static final float DEFAULT_FOREGROUND_ALPHA = 1.0f;
    public static final RectangleInsets DEFAULT_INSETS;
    public static final Shape DEFAULT_LEGEND_ITEM_BOX;
    public static final Shape DEFAULT_LEGEND_ITEM_CIRCLE;
    public static final Paint DEFAULT_OUTLINE_PAINT;
    public static final Stroke DEFAULT_OUTLINE_STROKE;
    public static final int MINIMUM_HEIGHT_TO_DRAW = 10;
    public static final int MINIMUM_WIDTH_TO_DRAW = 10;
    public static final Number ZERO;
    private static final long serialVersionUID = -8831571430103671324L;
    private float backgroundAlpha;
    private transient Image backgroundImage;
    private int backgroundImageAlignment;
    private float backgroundImageAlpha;
    private transient Paint backgroundPaint;
    private DatasetGroup datasetGroup;
    private DrawingSupplier drawingSupplier;
    private float foregroundAlpha;
    private RectangleInsets insets;
    private transient EventListenerList listenerList;
    private String noDataMessage;
    private Font noDataMessageFont;
    private transient Paint noDataMessagePaint;
    private boolean notify;
    private transient Paint outlinePaint;
    private transient Stroke outlineStroke;
    private boolean outlineVisible;
    private Plot parent;

    public abstract void draw(Graphics2D graphics2D, Rectangle2D rectangle2D, Point2D point2D, PlotState plotState, PlotRenderingInfo plotRenderingInfo);

    public abstract String getPlotType();

    static {
        ZERO = new Integer(0);
        DEFAULT_INSETS = new RectangleInsets(4.0d, XYLine3DRenderer.DEFAULT_Y_OFFSET, 4.0d, XYLine3DRenderer.DEFAULT_Y_OFFSET);
        DEFAULT_OUTLINE_STROKE = new BasicStroke(JFreeChart.DEFAULT_BACKGROUND_IMAGE_ALPHA, 1, 1);
        DEFAULT_OUTLINE_PAINT = Color.gray;
        DEFAULT_BACKGROUND_PAINT = Color.white;
        DEFAULT_LEGEND_ITEM_BOX = new Double(-4.0d, -4.0d, XYLine3DRenderer.DEFAULT_Y_OFFSET, XYLine3DRenderer.DEFAULT_Y_OFFSET);
        DEFAULT_LEGEND_ITEM_CIRCLE = new Ellipse2D.Double(-4.0d, -4.0d, XYLine3DRenderer.DEFAULT_Y_OFFSET, XYLine3DRenderer.DEFAULT_Y_OFFSET);
    }

    protected Plot() {
        this.backgroundImageAlignment = 15;
        this.backgroundImageAlpha = JFreeChart.DEFAULT_BACKGROUND_IMAGE_ALPHA;
        this.parent = null;
        this.insets = DEFAULT_INSETS;
        this.backgroundPaint = DEFAULT_BACKGROUND_PAINT;
        this.backgroundAlpha = DEFAULT_FOREGROUND_ALPHA;
        this.backgroundImage = null;
        this.outlineVisible = true;
        this.outlineStroke = DEFAULT_OUTLINE_STROKE;
        this.outlinePaint = DEFAULT_OUTLINE_PAINT;
        this.foregroundAlpha = DEFAULT_FOREGROUND_ALPHA;
        this.noDataMessage = null;
        this.noDataMessageFont = new Font("SansSerif", 0, 12);
        this.noDataMessagePaint = Color.black;
        this.drawingSupplier = new DefaultDrawingSupplier();
        this.notify = true;
        this.listenerList = new EventListenerList();
    }

    public DatasetGroup getDatasetGroup() {
        return this.datasetGroup;
    }

    protected void setDatasetGroup(DatasetGroup group) {
        this.datasetGroup = group;
    }

    public String getNoDataMessage() {
        return this.noDataMessage;
    }

    public void setNoDataMessage(String message) {
        this.noDataMessage = message;
        fireChangeEvent();
    }

    public Font getNoDataMessageFont() {
        return this.noDataMessageFont;
    }

    public void setNoDataMessageFont(Font font) {
        ParamChecks.nullNotPermitted(font, "font");
        this.noDataMessageFont = font;
        fireChangeEvent();
    }

    public Paint getNoDataMessagePaint() {
        return this.noDataMessagePaint;
    }

    public void setNoDataMessagePaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.noDataMessagePaint = paint;
        fireChangeEvent();
    }

    public Plot getParent() {
        return this.parent;
    }

    public void setParent(Plot parent) {
        this.parent = parent;
    }

    public Plot getRootPlot() {
        Plot p = getParent();
        return p == null ? this : p.getRootPlot();
    }

    public boolean isSubplot() {
        return getParent() != null;
    }

    public RectangleInsets getInsets() {
        return this.insets;
    }

    public void setInsets(RectangleInsets insets) {
        setInsets(insets, true);
    }

    public void setInsets(RectangleInsets insets, boolean notify) {
        ParamChecks.nullNotPermitted(insets, "insets");
        if (!this.insets.equals(insets)) {
            this.insets = insets;
            if (notify) {
                fireChangeEvent();
            }
        }
    }

    public Paint getBackgroundPaint() {
        return this.backgroundPaint;
    }

    public void setBackgroundPaint(Paint paint) {
        if (paint == null) {
            if (this.backgroundPaint != null) {
                this.backgroundPaint = null;
                fireChangeEvent();
            }
        } else if (this.backgroundPaint == null || !this.backgroundPaint.equals(paint)) {
            this.backgroundPaint = paint;
            fireChangeEvent();
        }
    }

    public float getBackgroundAlpha() {
        return this.backgroundAlpha;
    }

    public void setBackgroundAlpha(float alpha) {
        if (this.backgroundAlpha != alpha) {
            this.backgroundAlpha = alpha;
            fireChangeEvent();
        }
    }

    public DrawingSupplier getDrawingSupplier() {
        Plot p = getParent();
        if (p != null) {
            return p.getDrawingSupplier();
        }
        return this.drawingSupplier;
    }

    public void setDrawingSupplier(DrawingSupplier supplier) {
        this.drawingSupplier = supplier;
        fireChangeEvent();
    }

    public void setDrawingSupplier(DrawingSupplier supplier, boolean notify) {
        this.drawingSupplier = supplier;
        if (notify) {
            fireChangeEvent();
        }
    }

    public Image getBackgroundImage() {
        return this.backgroundImage;
    }

    public void setBackgroundImage(Image image) {
        this.backgroundImage = image;
        fireChangeEvent();
    }

    public int getBackgroundImageAlignment() {
        return this.backgroundImageAlignment;
    }

    public void setBackgroundImageAlignment(int alignment) {
        if (this.backgroundImageAlignment != alignment) {
            this.backgroundImageAlignment = alignment;
            fireChangeEvent();
        }
    }

    public float getBackgroundImageAlpha() {
        return this.backgroundImageAlpha;
    }

    public void setBackgroundImageAlpha(float alpha) {
        if (alpha < 0.0f || alpha > DEFAULT_FOREGROUND_ALPHA) {
            throw new IllegalArgumentException("The 'alpha' value must be in the range 0.0f to 1.0f.");
        } else if (this.backgroundImageAlpha != alpha) {
            this.backgroundImageAlpha = alpha;
            fireChangeEvent();
        }
    }

    public boolean isOutlineVisible() {
        return this.outlineVisible;
    }

    public void setOutlineVisible(boolean visible) {
        this.outlineVisible = visible;
        fireChangeEvent();
    }

    public Stroke getOutlineStroke() {
        return this.outlineStroke;
    }

    public void setOutlineStroke(Stroke stroke) {
        if (stroke == null) {
            if (this.outlineStroke != null) {
                this.outlineStroke = null;
                fireChangeEvent();
            }
        } else if (this.outlineStroke == null || !this.outlineStroke.equals(stroke)) {
            this.outlineStroke = stroke;
            fireChangeEvent();
        }
    }

    public Paint getOutlinePaint() {
        return this.outlinePaint;
    }

    public void setOutlinePaint(Paint paint) {
        if (paint == null) {
            if (this.outlinePaint != null) {
                this.outlinePaint = null;
                fireChangeEvent();
            }
        } else if (this.outlinePaint == null || !this.outlinePaint.equals(paint)) {
            this.outlinePaint = paint;
            fireChangeEvent();
        }
    }

    public float getForegroundAlpha() {
        return this.foregroundAlpha;
    }

    public void setForegroundAlpha(float alpha) {
        if (this.foregroundAlpha != alpha) {
            this.foregroundAlpha = alpha;
            fireChangeEvent();
        }
    }

    public LegendItemCollection getLegendItems() {
        return null;
    }

    public boolean isNotify() {
        return this.notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
        if (notify) {
            notifyListeners(new PlotChangeEvent(this));
        }
    }

    public void addChangeListener(PlotChangeListener listener) {
        this.listenerList.add(PlotChangeListener.class, listener);
    }

    public void removeChangeListener(PlotChangeListener listener) {
        this.listenerList.remove(PlotChangeListener.class, listener);
    }

    public void notifyListeners(PlotChangeEvent event) {
        if (this.notify) {
            Object[] listeners = this.listenerList.getListenerList();
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == PlotChangeListener.class) {
                    ((PlotChangeListener) listeners[i + 1]).plotChanged(event);
                }
            }
        }
    }

    protected void fireChangeEvent() {
        notifyListeners(new PlotChangeEvent(this));
    }

    public void drawBackground(Graphics2D g2, Rectangle2D area) {
        fillBackground(g2, area);
        drawBackgroundImage(g2, area);
    }

    protected void fillBackground(Graphics2D g2, Rectangle2D area) {
        fillBackground(g2, area, PlotOrientation.VERTICAL);
    }

    protected void fillBackground(Graphics2D g2, Rectangle2D area, PlotOrientation orientation) {
        ParamChecks.nullNotPermitted(orientation, "orientation");
        if (this.backgroundPaint != null) {
            Paint p = this.backgroundPaint;
            if (p instanceof GradientPaint) {
                GradientPaint gp = (GradientPaint) p;
                if (orientation == PlotOrientation.VERTICAL) {
                    p = new GradientPaint((float) area.getCenterX(), (float) area.getMaxY(), gp.getColor1(), (float) area.getCenterX(), (float) area.getMinY(), gp.getColor2());
                } else if (orientation == PlotOrientation.HORIZONTAL) {
                    p = new GradientPaint((float) area.getMinX(), (float) area.getCenterY(), gp.getColor1(), (float) area.getMaxX(), (float) area.getCenterY(), gp.getColor2());
                }
            }
            Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(3, this.backgroundAlpha));
            g2.setPaint(p);
            g2.fill(area);
            g2.setComposite(originalComposite);
        }
    }

    public void drawBackgroundImage(Graphics2D g2, Rectangle2D area) {
        if (this.backgroundImage != null) {
            Composite savedComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(3, this.backgroundImageAlpha));
            Rectangle2D dest = new Double(0.0d, 0.0d, (double) this.backgroundImage.getWidth(null), (double) this.backgroundImage.getHeight(null));
            Align.align(dest, area, this.backgroundImageAlignment);
            Shape savedClip = g2.getClip();
            g2.clip(area);
            g2.drawImage(this.backgroundImage, (int) dest.getX(), (int) dest.getY(), ((int) dest.getWidth()) + 1, ((int) dest.getHeight()) + 1, null);
            g2.setClip(savedClip);
            g2.setComposite(savedComposite);
        }
    }

    public void drawOutline(Graphics2D g2, Rectangle2D area) {
        if (this.outlineVisible && this.outlineStroke != null && this.outlinePaint != null) {
            g2.setStroke(this.outlineStroke);
            g2.setPaint(this.outlinePaint);
            Object saved = g2.getRenderingHint(RenderingHints.KEY_STROKE_CONTROL);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
            g2.draw(area);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, saved);
        }
    }

    protected void drawNoDataMessage(Graphics2D g2, Rectangle2D area) {
        Shape savedClip = g2.getClip();
        g2.clip(area);
        if (this.noDataMessage != null) {
            g2.setFont(this.noDataMessageFont);
            g2.setPaint(this.noDataMessagePaint);
            TextUtilities.createTextBlock(this.noDataMessage, this.noDataMessageFont, this.noDataMessagePaint, 0.9f * ((float) area.getWidth()), new G2TextMeasurer(g2)).draw(g2, (float) area.getCenterX(), (float) area.getCenterY(), TextBlockAnchor.CENTER);
        }
        g2.setClip(savedClip);
    }

    protected void createAndAddEntity(Rectangle2D dataArea, PlotRenderingInfo plotState, String toolTip, String urlText) {
        if (plotState != null && plotState.getOwner() != null) {
            EntityCollection e = plotState.getOwner().getEntityCollection();
            if (e != null) {
                e.add(new PlotEntity(dataArea, this, toolTip, urlText));
            }
        }
    }

    public void handleClick(int x, int y, PlotRenderingInfo info) {
    }

    public void zoom(double percent) {
    }

    public void annotationChanged(AnnotationChangeEvent event) {
        fireChangeEvent();
    }

    public void axisChanged(AxisChangeEvent event) {
        fireChangeEvent();
    }

    public void datasetChanged(DatasetChangeEvent event) {
        PlotChangeEvent newEvent = new PlotChangeEvent(this);
        newEvent.setType(ChartChangeEventType.DATASET_UPDATED);
        notifyListeners(newEvent);
    }

    public void markerChanged(MarkerChangeEvent event) {
        fireChangeEvent();
    }

    protected double getRectX(double x, double w1, double w2, RectangleEdge edge) {
        double result = x;
        if (edge == RectangleEdge.LEFT) {
            return result + w1;
        }
        if (edge == RectangleEdge.RIGHT) {
            return result + w2;
        }
        return result;
    }

    protected double getRectY(double y, double h1, double h2, RectangleEdge edge) {
        double result = y;
        if (edge == RectangleEdge.TOP) {
            return result + h1;
        }
        if (edge == RectangleEdge.BOTTOM) {
            return result + h2;
        }
        return result;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Plot)) {
            return false;
        }
        Plot that = (Plot) obj;
        if (!ObjectUtilities.equal(this.noDataMessage, that.noDataMessage)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.noDataMessageFont, that.noDataMessageFont)) {
            return false;
        }
        if (!PaintUtilities.equal(this.noDataMessagePaint, that.noDataMessagePaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.insets, that.insets)) {
            return false;
        }
        if (this.outlineVisible != that.outlineVisible) {
            return false;
        }
        if (!ObjectUtilities.equal(this.outlineStroke, that.outlineStroke)) {
            return false;
        }
        if (!PaintUtilities.equal(this.outlinePaint, that.outlinePaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.backgroundPaint, that.backgroundPaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.backgroundImage, that.backgroundImage)) {
            return false;
        }
        if (this.backgroundImageAlignment != that.backgroundImageAlignment) {
            return false;
        }
        if (this.backgroundImageAlpha != that.backgroundImageAlpha) {
            return false;
        }
        if (this.foregroundAlpha != that.foregroundAlpha) {
            return false;
        }
        if (this.backgroundAlpha != that.backgroundAlpha) {
            return false;
        }
        if (!this.drawingSupplier.equals(that.drawingSupplier)) {
            return false;
        }
        if (this.notify != that.notify) {
            return false;
        }
        return true;
    }

    public Object clone() throws CloneNotSupportedException {
        Plot clone = (Plot) super.clone();
        if (this.datasetGroup != null) {
            clone.datasetGroup = (DatasetGroup) ObjectUtilities.clone(this.datasetGroup);
        }
        clone.drawingSupplier = (DrawingSupplier) ObjectUtilities.clone(this.drawingSupplier);
        clone.listenerList = new EventListenerList();
        return clone;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.noDataMessagePaint, stream);
        SerialUtilities.writeStroke(this.outlineStroke, stream);
        SerialUtilities.writePaint(this.outlinePaint, stream);
        SerialUtilities.writePaint(this.backgroundPaint, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.noDataMessagePaint = SerialUtilities.readPaint(stream);
        this.outlineStroke = SerialUtilities.readStroke(stream);
        this.outlinePaint = SerialUtilities.readPaint(stream);
        this.backgroundPaint = SerialUtilities.readPaint(stream);
        this.listenerList = new EventListenerList();
    }

    public static RectangleEdge resolveDomainAxisLocation(AxisLocation location, PlotOrientation orientation) {
        ParamChecks.nullNotPermitted(location, "location");
        ParamChecks.nullNotPermitted(orientation, "orientation");
        RectangleEdge result = null;
        if (location == AxisLocation.TOP_OR_RIGHT) {
            if (orientation == PlotOrientation.HORIZONTAL) {
                result = RectangleEdge.RIGHT;
            } else if (orientation == PlotOrientation.VERTICAL) {
                result = RectangleEdge.TOP;
            }
        } else if (location == AxisLocation.TOP_OR_LEFT) {
            if (orientation == PlotOrientation.HORIZONTAL) {
                result = RectangleEdge.LEFT;
            } else if (orientation == PlotOrientation.VERTICAL) {
                result = RectangleEdge.TOP;
            }
        } else if (location == AxisLocation.BOTTOM_OR_RIGHT) {
            if (orientation == PlotOrientation.HORIZONTAL) {
                result = RectangleEdge.RIGHT;
            } else if (orientation == PlotOrientation.VERTICAL) {
                result = RectangleEdge.BOTTOM;
            }
        } else if (location == AxisLocation.BOTTOM_OR_LEFT) {
            if (orientation == PlotOrientation.HORIZONTAL) {
                result = RectangleEdge.LEFT;
            } else if (orientation == PlotOrientation.VERTICAL) {
                result = RectangleEdge.BOTTOM;
            }
        }
        if (result != null) {
            return result;
        }
        throw new IllegalStateException("resolveDomainAxisLocation()");
    }

    public static RectangleEdge resolveRangeAxisLocation(AxisLocation location, PlotOrientation orientation) {
        ParamChecks.nullNotPermitted(location, "location");
        ParamChecks.nullNotPermitted(orientation, "orientation");
        RectangleEdge result = null;
        if (location == AxisLocation.TOP_OR_RIGHT) {
            if (orientation == PlotOrientation.HORIZONTAL) {
                result = RectangleEdge.TOP;
            } else if (orientation == PlotOrientation.VERTICAL) {
                result = RectangleEdge.RIGHT;
            }
        } else if (location == AxisLocation.TOP_OR_LEFT) {
            if (orientation == PlotOrientation.HORIZONTAL) {
                result = RectangleEdge.TOP;
            } else if (orientation == PlotOrientation.VERTICAL) {
                result = RectangleEdge.LEFT;
            }
        } else if (location == AxisLocation.BOTTOM_OR_RIGHT) {
            if (orientation == PlotOrientation.HORIZONTAL) {
                result = RectangleEdge.BOTTOM;
            } else if (orientation == PlotOrientation.VERTICAL) {
                result = RectangleEdge.RIGHT;
            }
        } else if (location == AxisLocation.BOTTOM_OR_LEFT) {
            if (orientation == PlotOrientation.HORIZONTAL) {
                result = RectangleEdge.BOTTOM;
            } else if (orientation == PlotOrientation.VERTICAL) {
                result = RectangleEdge.LEFT;
            }
        }
        if (result != null) {
            return result;
        }
        throw new IllegalStateException("resolveRangeAxisLocation()");
    }
}
