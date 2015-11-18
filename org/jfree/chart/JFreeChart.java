package org.jfree.chart;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.swing.UIManager;
import javax.swing.event.EventListenerList;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.block.BlockParams;
import org.jfree.chart.block.EntityBlockResult;
import org.jfree.chart.block.LengthConstraintType;
import org.jfree.chart.block.LineBorder;
import org.jfree.chart.block.RectangleConstraint;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.JFreeChartEntity;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.chart.event.ChartProgressEvent;
import org.jfree.chart.event.ChartProgressListener;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.event.PlotChangeListener;
import org.jfree.chart.event.TitleChangeEvent;
import org.jfree.chart.event.TitleChangeListener;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.title.Title;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.Range;
import org.jfree.data.xy.NormalizedMatrixSeries;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.Align;
import org.jfree.ui.Drawable;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.Size2D;
import org.jfree.ui.VerticalAlignment;
import org.jfree.ui.about.ProjectInfo;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;

public class JFreeChart implements Drawable, TitleChangeListener, PlotChangeListener, Serializable, Cloneable {
    public static final Image DEFAULT_BACKGROUND_IMAGE;
    public static final int DEFAULT_BACKGROUND_IMAGE_ALIGNMENT = 15;
    public static final float DEFAULT_BACKGROUND_IMAGE_ALPHA = 0.5f;
    public static final Paint DEFAULT_BACKGROUND_PAINT;
    public static final Font DEFAULT_TITLE_FONT;
    public static final ProjectInfo INFO;
    public static final Key KEY_SUPPRESS_SHADOW_GENERATION;
    private static final long serialVersionUID = -3470703747817429120L;
    private transient Image backgroundImage;
    private int backgroundImageAlignment;
    private float backgroundImageAlpha;
    private transient Paint backgroundPaint;
    private transient Paint borderPaint;
    private transient Stroke borderStroke;
    private boolean borderVisible;
    private transient EventListenerList changeListeners;
    private boolean notify;
    private RectangleInsets padding;
    private Plot plot;
    private transient EventListenerList progressListeners;
    private transient RenderingHints renderingHints;
    private List subtitles;
    private TextTitle title;

    static class 1 extends Key {
        1(int x0) {
            super(x0);
        }

        public boolean isCompatibleValue(Object val) {
            return val instanceof Boolean;
        }
    }

    static {
        INFO = new JFreeChartInfo();
        DEFAULT_TITLE_FONT = new Font("SansSerif", 1, 18);
        DEFAULT_BACKGROUND_PAINT = UIManager.getColor("Panel.background");
        DEFAULT_BACKGROUND_IMAGE = null;
        KEY_SUPPRESS_SHADOW_GENERATION = new 1(0);
    }

    public JFreeChart(Plot plot) {
        this(null, null, plot, true);
    }

    public JFreeChart(String title, Plot plot) {
        this(title, DEFAULT_TITLE_FONT, plot, true);
    }

    public JFreeChart(String title, Font titleFont, Plot plot, boolean createLegend) {
        this.backgroundImageAlignment = DEFAULT_BACKGROUND_IMAGE_ALIGNMENT;
        this.backgroundImageAlpha = DEFAULT_BACKGROUND_IMAGE_ALPHA;
        ParamChecks.nullNotPermitted(plot, "plot");
        this.progressListeners = new EventListenerList();
        this.changeListeners = new EventListenerList();
        this.notify = true;
        this.renderingHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        this.renderingHints.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        this.borderVisible = false;
        this.borderStroke = new BasicStroke(Plot.DEFAULT_FOREGROUND_ALPHA);
        this.borderPaint = Color.black;
        this.padding = RectangleInsets.ZERO_INSETS;
        this.plot = plot;
        plot.addChangeListener(this);
        this.subtitles = new ArrayList();
        if (createLegend) {
            LegendTitle legend = new LegendTitle(this.plot);
            legend.setMargin(new RectangleInsets(NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR, NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR, NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR, NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR));
            legend.setFrame(new LineBorder());
            legend.setBackgroundPaint(Color.white);
            legend.setPosition(RectangleEdge.BOTTOM);
            this.subtitles.add(legend);
            legend.addChangeListener(this);
        }
        if (title != null) {
            if (titleFont == null) {
                titleFont = DEFAULT_TITLE_FONT;
            }
            this.title = new TextTitle(title, titleFont);
            this.title.addChangeListener(this);
        }
        this.backgroundPaint = DEFAULT_BACKGROUND_PAINT;
        this.backgroundImage = DEFAULT_BACKGROUND_IMAGE;
        this.backgroundImageAlignment = DEFAULT_BACKGROUND_IMAGE_ALIGNMENT;
        this.backgroundImageAlpha = DEFAULT_BACKGROUND_IMAGE_ALPHA;
    }

    public RenderingHints getRenderingHints() {
        return this.renderingHints;
    }

    public void setRenderingHints(RenderingHints renderingHints) {
        ParamChecks.nullNotPermitted(renderingHints, "renderingHints");
        this.renderingHints = renderingHints;
        fireChartChanged();
    }

    public boolean isBorderVisible() {
        return this.borderVisible;
    }

    public void setBorderVisible(boolean visible) {
        this.borderVisible = visible;
        fireChartChanged();
    }

    public Stroke getBorderStroke() {
        return this.borderStroke;
    }

    public void setBorderStroke(Stroke stroke) {
        this.borderStroke = stroke;
        fireChartChanged();
    }

    public Paint getBorderPaint() {
        return this.borderPaint;
    }

    public void setBorderPaint(Paint paint) {
        this.borderPaint = paint;
        fireChartChanged();
    }

    public RectangleInsets getPadding() {
        return this.padding;
    }

    public void setPadding(RectangleInsets padding) {
        ParamChecks.nullNotPermitted(padding, "padding");
        this.padding = padding;
        notifyListeners(new ChartChangeEvent(this));
    }

    public TextTitle getTitle() {
        return this.title;
    }

    public void setTitle(TextTitle title) {
        if (this.title != null) {
            this.title.removeChangeListener(this);
        }
        this.title = title;
        if (title != null) {
            title.addChangeListener(this);
        }
        fireChartChanged();
    }

    public void setTitle(String text) {
        if (text == null) {
            setTitle((TextTitle) null);
        } else if (this.title == null) {
            setTitle(new TextTitle(text, DEFAULT_TITLE_FONT));
        } else {
            this.title.setText(text);
        }
    }

    public void addLegend(LegendTitle legend) {
        addSubtitle(legend);
    }

    public LegendTitle getLegend() {
        return getLegend(0);
    }

    public LegendTitle getLegend(int index) {
        int seen = 0;
        for (Title subtitle : this.subtitles) {
            if (subtitle instanceof LegendTitle) {
                if (seen == index) {
                    return (LegendTitle) subtitle;
                }
                seen++;
            }
        }
        return null;
    }

    public void removeLegend() {
        removeSubtitle(getLegend());
    }

    public List getSubtitles() {
        return new ArrayList(this.subtitles);
    }

    public void setSubtitles(List subtitles) {
        if (subtitles == null) {
            throw new NullPointerException("Null 'subtitles' argument.");
        }
        setNotify(false);
        clearSubtitles();
        for (Title t : subtitles) {
            if (t != null) {
                addSubtitle(t);
            }
        }
        setNotify(true);
    }

    public int getSubtitleCount() {
        return this.subtitles.size();
    }

    public Title getSubtitle(int index) {
        if (index >= 0 && index < getSubtitleCount()) {
            return (Title) this.subtitles.get(index);
        }
        throw new IllegalArgumentException("Index out of range.");
    }

    public void addSubtitle(Title subtitle) {
        ParamChecks.nullNotPermitted(subtitle, "subtitle");
        this.subtitles.add(subtitle);
        subtitle.addChangeListener(this);
        fireChartChanged();
    }

    public void addSubtitle(int index, Title subtitle) {
        if (index < 0 || index > getSubtitleCount()) {
            throw new IllegalArgumentException("The 'index' argument is out of range.");
        }
        ParamChecks.nullNotPermitted(subtitle, "subtitle");
        this.subtitles.add(index, subtitle);
        subtitle.addChangeListener(this);
        fireChartChanged();
    }

    public void clearSubtitles() {
        for (Title t : this.subtitles) {
            t.removeChangeListener(this);
        }
        this.subtitles.clear();
        fireChartChanged();
    }

    public void removeSubtitle(Title title) {
        this.subtitles.remove(title);
        fireChartChanged();
    }

    public Plot getPlot() {
        return this.plot;
    }

    public CategoryPlot getCategoryPlot() {
        return (CategoryPlot) this.plot;
    }

    public XYPlot getXYPlot() {
        return (XYPlot) this.plot;
    }

    public boolean getAntiAlias() {
        return RenderingHints.VALUE_ANTIALIAS_ON.equals(this.renderingHints.get(RenderingHints.KEY_ANTIALIASING));
    }

    public void setAntiAlias(boolean flag) {
        this.renderingHints.put(RenderingHints.KEY_ANTIALIASING, flag ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
        fireChartChanged();
    }

    public Object getTextAntiAlias() {
        return this.renderingHints.get(RenderingHints.KEY_TEXT_ANTIALIASING);
    }

    public void setTextAntiAlias(boolean flag) {
        if (flag) {
            setTextAntiAlias(RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        } else {
            setTextAntiAlias(RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        }
    }

    public void setTextAntiAlias(Object val) {
        this.renderingHints.put(RenderingHints.KEY_TEXT_ANTIALIASING, val);
        notifyListeners(new ChartChangeEvent(this));
    }

    public Paint getBackgroundPaint() {
        return this.backgroundPaint;
    }

    public void setBackgroundPaint(Paint paint) {
        if (this.backgroundPaint != null) {
            if (!this.backgroundPaint.equals(paint)) {
                this.backgroundPaint = paint;
                fireChartChanged();
            }
        } else if (paint != null) {
            this.backgroundPaint = paint;
            fireChartChanged();
        }
    }

    public Image getBackgroundImage() {
        return this.backgroundImage;
    }

    public void setBackgroundImage(Image image) {
        if (this.backgroundImage != null) {
            if (!this.backgroundImage.equals(image)) {
                this.backgroundImage = image;
                fireChartChanged();
            }
        } else if (image != null) {
            this.backgroundImage = image;
            fireChartChanged();
        }
    }

    public int getBackgroundImageAlignment() {
        return this.backgroundImageAlignment;
    }

    public void setBackgroundImageAlignment(int alignment) {
        if (this.backgroundImageAlignment != alignment) {
            this.backgroundImageAlignment = alignment;
            fireChartChanged();
        }
    }

    public float getBackgroundImageAlpha() {
        return this.backgroundImageAlpha;
    }

    public void setBackgroundImageAlpha(float alpha) {
        if (this.backgroundImageAlpha != alpha) {
            this.backgroundImageAlpha = alpha;
            fireChartChanged();
        }
    }

    public boolean isNotify() {
        return this.notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
        if (notify) {
            notifyListeners(new ChartChangeEvent(this));
        }
    }

    public void draw(Graphics2D g2, Rectangle2D area) {
        draw(g2, area, null, null);
    }

    public void draw(Graphics2D g2, Rectangle2D area, ChartRenderingInfo info) {
        draw(g2, area, null, info);
    }

    public void draw(Graphics2D g2, Rectangle2D chartArea, Point2D anchor, ChartRenderingInfo info) {
        EntityCollection e;
        notifyListeners(new ChartProgressEvent(this, this, 1, 0));
        EntityCollection entities = null;
        if (info != null) {
            info.clear();
            info.setChartArea(chartArea);
            entities = info.getEntityCollection();
        }
        if (entities != null) {
            entities.add(new JFreeChartEntity((Rectangle2D) chartArea.clone(), this));
        }
        Shape savedClip = g2.getClip();
        g2.clip(chartArea);
        g2.addRenderingHints(this.renderingHints);
        if (this.backgroundPaint != null) {
            g2.setPaint(this.backgroundPaint);
            g2.fill(chartArea);
        }
        if (this.backgroundImage != null) {
            Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(3, this.backgroundImageAlpha));
            Rectangle2D dest = new Double(0.0d, 0.0d, (double) this.backgroundImage.getWidth(null), (double) this.backgroundImage.getHeight(null));
            Align.align(dest, chartArea, this.backgroundImageAlignment);
            g2.drawImage(this.backgroundImage, (int) dest.getX(), (int) dest.getY(), (int) dest.getWidth(), (int) dest.getHeight(), null);
            g2.setComposite(originalComposite);
        }
        if (isBorderVisible()) {
            Paint paint = getBorderPaint();
            Stroke stroke = getBorderStroke();
            if (!(paint == null || stroke == null)) {
                Rectangle2D borderArea = new Double(chartArea.getX(), chartArea.getY(), chartArea.getWidth() - NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR, chartArea.getHeight() - NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR);
                g2.setPaint(paint);
                g2.setStroke(stroke);
                g2.draw(borderArea);
            }
        }
        Rectangle2D nonTitleArea = new Double();
        nonTitleArea.setRect(chartArea);
        this.padding.trim(nonTitleArea);
        if (this.title != null && this.title.isVisible()) {
            e = drawTitle(this.title, g2, nonTitleArea, entities != null);
            if (!(e == null || entities == null)) {
                entities.addAll(e);
            }
        }
        for (Title currentTitle : this.subtitles) {
            if (currentTitle.isVisible()) {
                e = drawTitle(currentTitle, g2, nonTitleArea, entities != null);
                if (!(e == null || entities == null)) {
                    entities.addAll(e);
                }
            }
        }
        Rectangle2D plotArea = nonTitleArea;
        PlotRenderingInfo plotInfo = null;
        if (info != null) {
            plotInfo = info.getPlotInfo();
        }
        this.plot.draw(g2, plotArea, anchor, null, plotInfo);
        g2.setClip(savedClip);
        notifyListeners(new ChartProgressEvent(this, this, 2, 100));
    }

    private Rectangle2D createAlignedRectangle2D(Size2D dimensions, Rectangle2D frame, HorizontalAlignment hAlign, VerticalAlignment vAlign) {
        double x = Double.NaN;
        double y = Double.NaN;
        if (hAlign == HorizontalAlignment.LEFT) {
            x = frame.getX();
        } else if (hAlign == HorizontalAlignment.CENTER) {
            x = frame.getCenterX() - (dimensions.width / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
        } else if (hAlign == HorizontalAlignment.RIGHT) {
            x = frame.getMaxX() - dimensions.width;
        }
        if (vAlign == VerticalAlignment.TOP) {
            y = frame.getY();
        } else if (vAlign == VerticalAlignment.CENTER) {
            y = frame.getCenterY() - (dimensions.height / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
        } else if (vAlign == VerticalAlignment.BOTTOM) {
            y = frame.getMaxY() - dimensions.height;
        }
        return new Double(x, y, dimensions.width, dimensions.height);
    }

    protected EntityCollection drawTitle(Title t, Graphics2D g2, Rectangle2D area, boolean entities) {
        ParamChecks.nullNotPermitted(t, "t");
        ParamChecks.nullNotPermitted(area, "area");
        RectangleEdge position = t.getPosition();
        double ww = area.getWidth();
        if (ww <= 0.0d) {
            return null;
        }
        double hh = area.getHeight();
        if (hh <= 0.0d) {
            return null;
        }
        EntityBlockResult retValue;
        RectangleConstraint constraint = new RectangleConstraint(ww, new Range(0.0d, ww), LengthConstraintType.RANGE, hh, new Range(0.0d, hh), LengthConstraintType.RANGE);
        BlockParams p = new BlockParams();
        p.setGenerateEntities(entities);
        Size2D size;
        if (position == RectangleEdge.TOP) {
            size = t.arrange(g2, constraint);
            retValue = t.draw(g2, createAlignedRectangle2D(size, area, t.getHorizontalAlignment(), VerticalAlignment.TOP), p);
            area.setRect(area.getX(), Math.min(area.getY() + size.height, area.getMaxY()), area.getWidth(), Math.max(area.getHeight() - size.height, 0.0d));
        } else if (position == RectangleEdge.BOTTOM) {
            size = t.arrange(g2, constraint);
            retValue = t.draw(g2, createAlignedRectangle2D(size, area, t.getHorizontalAlignment(), VerticalAlignment.BOTTOM), p);
            area.setRect(area.getX(), area.getY(), area.getWidth(), area.getHeight() - size.height);
        } else if (position == RectangleEdge.RIGHT) {
            size = t.arrange(g2, constraint);
            retValue = t.draw(g2, createAlignedRectangle2D(size, area, HorizontalAlignment.RIGHT, t.getVerticalAlignment()), p);
            area.setRect(area.getX(), area.getY(), area.getWidth() - size.width, area.getHeight());
        } else if (position == RectangleEdge.LEFT) {
            size = t.arrange(g2, constraint);
            retValue = t.draw(g2, createAlignedRectangle2D(size, area, HorizontalAlignment.LEFT, t.getVerticalAlignment()), p);
            area.setRect(area.getX() + size.width, area.getY(), area.getWidth() - size.width, area.getHeight());
        } else {
            throw new RuntimeException("Unrecognised title position.");
        }
        if (retValue instanceof EntityBlockResult) {
            return retValue.getEntityCollection();
        }
        return null;
    }

    public BufferedImage createBufferedImage(int width, int height) {
        return createBufferedImage(width, height, null);
    }

    public BufferedImage createBufferedImage(int width, int height, ChartRenderingInfo info) {
        return createBufferedImage(width, height, 2, info);
    }

    public BufferedImage createBufferedImage(int width, int height, int imageType, ChartRenderingInfo info) {
        BufferedImage image = new BufferedImage(width, height, imageType);
        Graphics2D g2 = image.createGraphics();
        draw(g2, new Double(0.0d, 0.0d, (double) width, (double) height), null, info);
        g2.dispose();
        return image;
    }

    public BufferedImage createBufferedImage(int imageWidth, int imageHeight, double drawWidth, double drawHeight, ChartRenderingInfo info) {
        BufferedImage image = new BufferedImage(imageWidth, imageHeight, 2);
        Graphics2D g2 = image.createGraphics();
        g2.transform(AffineTransform.getScaleInstance(((double) imageWidth) / drawWidth, ((double) imageHeight) / drawHeight));
        draw(g2, new Double(0.0d, 0.0d, drawWidth, drawHeight), null, info);
        g2.dispose();
        return image;
    }

    public void handleClick(int x, int y, ChartRenderingInfo info) {
        this.plot.handleClick(x, y, info.getPlotInfo());
    }

    public void addChangeListener(ChartChangeListener listener) {
        ParamChecks.nullNotPermitted(listener, "listener");
        this.changeListeners.add(ChartChangeListener.class, listener);
    }

    public void removeChangeListener(ChartChangeListener listener) {
        ParamChecks.nullNotPermitted(listener, "listener");
        this.changeListeners.remove(ChartChangeListener.class, listener);
    }

    public void fireChartChanged() {
        notifyListeners(new ChartChangeEvent(this));
    }

    protected void notifyListeners(ChartChangeEvent event) {
        if (this.notify) {
            Object[] listeners = this.changeListeners.getListenerList();
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == ChartChangeListener.class) {
                    ((ChartChangeListener) listeners[i + 1]).chartChanged(event);
                }
            }
        }
    }

    public void addProgressListener(ChartProgressListener listener) {
        this.progressListeners.add(ChartProgressListener.class, listener);
    }

    public void removeProgressListener(ChartProgressListener listener) {
        this.progressListeners.remove(ChartProgressListener.class, listener);
    }

    protected void notifyListeners(ChartProgressEvent event) {
        Object[] listeners = this.progressListeners.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ChartProgressListener.class) {
                ((ChartProgressListener) listeners[i + 1]).chartProgress(event);
            }
        }
    }

    public void titleChanged(TitleChangeEvent event) {
        event.setChart(this);
        notifyListeners((ChartChangeEvent) event);
    }

    public void plotChanged(PlotChangeEvent event) {
        event.setChart(this);
        notifyListeners((ChartChangeEvent) event);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof JFreeChart)) {
            return false;
        }
        JFreeChart that = (JFreeChart) obj;
        if (!this.renderingHints.equals(that.renderingHints)) {
            return false;
        }
        if (this.borderVisible != that.borderVisible) {
            return false;
        }
        if (!ObjectUtilities.equal(this.borderStroke, that.borderStroke)) {
            return false;
        }
        if (!PaintUtilities.equal(this.borderPaint, that.borderPaint)) {
            return false;
        }
        if (!this.padding.equals(that.padding)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.title, that.title)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.subtitles, that.subtitles)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.plot, that.plot)) {
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
        if (this.notify != that.notify) {
            return false;
        }
        return true;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeStroke(this.borderStroke, stream);
        SerialUtilities.writePaint(this.borderPaint, stream);
        SerialUtilities.writePaint(this.backgroundPaint, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.borderStroke = SerialUtilities.readStroke(stream);
        this.borderPaint = SerialUtilities.readPaint(stream);
        this.backgroundPaint = SerialUtilities.readPaint(stream);
        this.progressListeners = new EventListenerList();
        this.changeListeners = new EventListenerList();
        this.renderingHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        this.renderingHints.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        if (this.title != null) {
            this.title.addChangeListener(this);
        }
        for (int i = 0; i < getSubtitleCount(); i++) {
            getSubtitle(i).addChangeListener(this);
        }
        this.plot.addChangeListener(this);
    }

    public static void main(String[] args) {
        System.out.println(INFO.toString());
    }

    public Object clone() throws CloneNotSupportedException {
        JFreeChart chart = (JFreeChart) super.clone();
        chart.renderingHints = (RenderingHints) this.renderingHints.clone();
        if (this.title != null) {
            chart.title = (TextTitle) this.title.clone();
            chart.title.addChangeListener(chart);
        }
        chart.subtitles = new ArrayList();
        for (int i = 0; i < getSubtitleCount(); i++) {
            Title subtitle = (Title) getSubtitle(i).clone();
            chart.subtitles.add(subtitle);
            subtitle.addChangeListener(chart);
        }
        if (this.plot != null) {
            chart.plot = (Plot) this.plot.clone();
            chart.plot.addChangeListener(chart);
        }
        chart.progressListeners = new EventListenerList();
        chart.changeListeners = new EventListenerList();
        return chart;
    }
}
