package org.jfree.chart.renderer.category;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D.Double;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.swing.Icon;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYLine3DRenderer;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.category.CategoryDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.util.PaintUtilities;

public class MinMaxCategoryRenderer extends AbstractCategoryItemRenderer {
    private static final long serialVersionUID = 2935615937671064911L;
    private transient Paint groupPaint;
    private transient Stroke groupStroke;
    private int lastCategory;
    private double max;
    private transient Icon maxIcon;
    private double min;
    private transient Icon minIcon;
    private transient Icon objectIcon;
    private boolean plotLines;

    class 1 implements Icon {
        final /* synthetic */ Paint val$fillPaint;
        final /* synthetic */ int val$height;
        final /* synthetic */ Paint val$outlinePaint;
        final /* synthetic */ GeneralPath val$path;
        final /* synthetic */ int val$width;

        1(GeneralPath generalPath, Paint paint, Paint paint2, int i, int i2) {
            this.val$path = generalPath;
            this.val$fillPaint = paint;
            this.val$outlinePaint = paint2;
            this.val$width = i;
            this.val$height = i2;
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g;
            this.val$path.transform(AffineTransform.getTranslateInstance((double) x, (double) y));
            if (this.val$fillPaint != null) {
                g2.setPaint(this.val$fillPaint);
                g2.fill(this.val$path);
            }
            if (this.val$outlinePaint != null) {
                g2.setPaint(this.val$outlinePaint);
                g2.draw(this.val$path);
            }
            this.val$path.transform(AffineTransform.getTranslateInstance((double) (-x), (double) (-y)));
        }

        public int getIconWidth() {
            return this.val$width;
        }

        public int getIconHeight() {
            return this.val$height;
        }
    }

    class 2 implements Icon {
        final /* synthetic */ boolean val$fill;
        final /* synthetic */ int val$height;
        final /* synthetic */ boolean val$outline;
        final /* synthetic */ GeneralPath val$path;
        final /* synthetic */ int val$width;

        2(GeneralPath generalPath, boolean z, boolean z2, int i, int i2) {
            this.val$path = generalPath;
            this.val$fill = z;
            this.val$outline = z2;
            this.val$width = i;
            this.val$height = i2;
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g;
            this.val$path.transform(AffineTransform.getTranslateInstance((double) x, (double) y));
            if (this.val$fill) {
                g2.fill(this.val$path);
            }
            if (this.val$outline) {
                g2.draw(this.val$path);
            }
            this.val$path.transform(AffineTransform.getTranslateInstance((double) (-x), (double) (-y)));
        }

        public int getIconWidth() {
            return this.val$width;
        }

        public int getIconHeight() {
            return this.val$height;
        }
    }

    public MinMaxCategoryRenderer() {
        this.plotLines = false;
        this.groupPaint = Color.black;
        this.groupStroke = new BasicStroke(Plot.DEFAULT_FOREGROUND_ALPHA);
        this.minIcon = getIcon(new Double(-4.0d, -4.0d, XYLine3DRenderer.DEFAULT_Y_OFFSET, XYLine3DRenderer.DEFAULT_Y_OFFSET, 0.0d, 360.0d, 0), null, Color.black);
        this.maxIcon = getIcon(new Double(-4.0d, -4.0d, XYLine3DRenderer.DEFAULT_Y_OFFSET, XYLine3DRenderer.DEFAULT_Y_OFFSET, 0.0d, 360.0d, 0), null, Color.black);
        this.objectIcon = getIcon(new Line2D.Double(-4.0d, 0.0d, 4.0d, 0.0d), false, true);
        this.lastCategory = -1;
    }

    public boolean isDrawLines() {
        return this.plotLines;
    }

    public void setDrawLines(boolean draw) {
        if (this.plotLines != draw) {
            this.plotLines = draw;
            fireChangeEvent();
        }
    }

    public Paint getGroupPaint() {
        return this.groupPaint;
    }

    public void setGroupPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.groupPaint = paint;
        fireChangeEvent();
    }

    public Stroke getGroupStroke() {
        return this.groupStroke;
    }

    public void setGroupStroke(Stroke stroke) {
        ParamChecks.nullNotPermitted(stroke, "stroke");
        this.groupStroke = stroke;
        fireChangeEvent();
    }

    public Icon getObjectIcon() {
        return this.objectIcon;
    }

    public void setObjectIcon(Icon icon) {
        ParamChecks.nullNotPermitted(icon, "icon");
        this.objectIcon = icon;
        fireChangeEvent();
    }

    public Icon getMaxIcon() {
        return this.maxIcon;
    }

    public void setMaxIcon(Icon icon) {
        ParamChecks.nullNotPermitted(icon, "icon");
        this.maxIcon = icon;
        fireChangeEvent();
    }

    public Icon getMinIcon() {
        return this.minIcon;
    }

    public void setMinIcon(Icon icon) {
        ParamChecks.nullNotPermitted(icon, "icon");
        this.minIcon = icon;
        fireChangeEvent();
    }

    public void drawItem(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryDataset dataset, int row, int column, int pass) {
        Number value = dataset.getValue(row, column);
        if (value != null) {
            double x1 = domainAxis.getCategoryMiddle(column, getColumnCount(), dataArea, plot.getDomainAxisEdge());
            double y1 = rangeAxis.valueToJava2D(value.doubleValue(), dataArea, plot.getRangeAxisEdge());
            Shape hotspot = new Rectangle2D.Double(x1 - 4.0d, y1 - 4.0d, XYLine3DRenderer.DEFAULT_Y_OFFSET, XYLine3DRenderer.DEFAULT_Y_OFFSET);
            g2.setPaint(getItemPaint(row, column));
            g2.setStroke(getItemStroke(row, column));
            PlotOrientation orient = plot.getOrientation();
            if (orient == PlotOrientation.VERTICAL) {
                this.objectIcon.paintIcon(null, g2, (int) x1, (int) y1);
            } else {
                this.objectIcon.paintIcon(null, g2, (int) y1, (int) x1);
            }
            if (this.lastCategory == column) {
                if (this.min > value.doubleValue()) {
                    this.min = value.doubleValue();
                }
                if (this.max < value.doubleValue()) {
                    this.max = value.doubleValue();
                }
                if (dataset.getRowCount() - 1 == row) {
                    g2.setPaint(this.groupPaint);
                    g2.setStroke(this.groupStroke);
                    double minY = rangeAxis.valueToJava2D(this.min, dataArea, plot.getRangeAxisEdge());
                    double maxY = rangeAxis.valueToJava2D(this.max, dataArea, plot.getRangeAxisEdge());
                    if (orient == PlotOrientation.VERTICAL) {
                        g2.draw(new Line2D.Double(x1, minY, x1, maxY));
                        this.minIcon.paintIcon(null, g2, (int) x1, (int) minY);
                        this.maxIcon.paintIcon(null, g2, (int) x1, (int) maxY);
                    } else {
                        g2.draw(new Line2D.Double(minY, x1, maxY, x1));
                        Graphics graphics = g2;
                        this.minIcon.paintIcon(null, graphics, (int) minY, (int) x1);
                        graphics = g2;
                        this.maxIcon.paintIcon(null, graphics, (int) maxY, (int) x1);
                    }
                }
            } else {
                this.lastCategory = column;
                this.min = value.doubleValue();
                this.max = value.doubleValue();
            }
            if (this.plotLines && column != 0) {
                Number previousValue = dataset.getValue(row, column - 1);
                if (previousValue != null) {
                    Line2D line;
                    double previous = previousValue.doubleValue();
                    double x0 = domainAxis.getCategoryMiddle(column - 1, getColumnCount(), dataArea, plot.getDomainAxisEdge());
                    double y0 = rangeAxis.valueToJava2D(previous, dataArea, plot.getRangeAxisEdge());
                    g2.setPaint(getItemPaint(row, column));
                    g2.setStroke(getItemStroke(row, column));
                    if (orient == PlotOrientation.VERTICAL) {
                        line = new Line2D.Double(x0, y0, x1, y1);
                    } else {
                        Line2D.Double doubleR = new Line2D.Double(y0, x0, y1, x1);
                    }
                    g2.draw(line);
                }
            }
            EntityCollection entities = state.getEntityCollection();
            if (entities != null) {
                addItemEntity(entities, dataset, row, column, hotspot);
            }
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof MinMaxCategoryRenderer)) {
            return false;
        }
        MinMaxCategoryRenderer that = (MinMaxCategoryRenderer) obj;
        if (this.plotLines == that.plotLines && PaintUtilities.equal(this.groupPaint, that.groupPaint) && this.groupStroke.equals(that.groupStroke)) {
            return super.equals(obj);
        }
        return false;
    }

    private Icon getIcon(Shape shape, Paint fillPaint, Paint outlinePaint) {
        return new 1(new GeneralPath(shape), fillPaint, outlinePaint, shape.getBounds().width, shape.getBounds().height);
    }

    private Icon getIcon(Shape shape, boolean fill, boolean outline) {
        return new 2(new GeneralPath(shape), fill, outline, shape.getBounds().width, shape.getBounds().height);
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeStroke(this.groupStroke, stream);
        SerialUtilities.writePaint(this.groupPaint, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.groupStroke = SerialUtilities.readStroke(stream);
        this.groupPaint = SerialUtilities.readPaint(stream);
        this.minIcon = getIcon(new Double(-4.0d, -4.0d, XYLine3DRenderer.DEFAULT_Y_OFFSET, XYLine3DRenderer.DEFAULT_Y_OFFSET, 0.0d, 360.0d, 0), null, Color.black);
        this.maxIcon = getIcon(new Double(-4.0d, -4.0d, XYLine3DRenderer.DEFAULT_Y_OFFSET, XYLine3DRenderer.DEFAULT_Y_OFFSET, 0.0d, 360.0d, 0), null, Color.black);
        this.objectIcon = getIcon(new Line2D.Double(-4.0d, 0.0d, 4.0d, 0.0d), false, true);
    }
}
