package org.jfree.chart.renderer.category;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.jfree.chart.Effect3D;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.util.PaintAlpha;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.category.CategoryDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.text.TextUtilities;
import org.jfree.ui.LengthAdjustmentType;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;

public class BarRenderer3D extends BarRenderer implements Effect3D, Cloneable, PublicCloneable, Serializable {
    public static final Paint DEFAULT_WALL_PAINT;
    public static final double DEFAULT_X_OFFSET = 12.0d;
    public static final double DEFAULT_Y_OFFSET = 8.0d;
    private static final long serialVersionUID = 7686976503536003636L;
    private transient Paint wallPaint;
    private double xOffset;
    private double yOffset;

    static {
        DEFAULT_WALL_PAINT = new Color(221, 221, 221);
    }

    public BarRenderer3D() {
        this(DEFAULT_X_OFFSET, DEFAULT_Y_OFFSET);
    }

    public BarRenderer3D(double xOffset, double yOffset) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.wallPaint = DEFAULT_WALL_PAINT;
        setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.INSIDE12, TextAnchor.TOP_CENTER));
        setBaseNegativeItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.INSIDE12, TextAnchor.TOP_CENTER));
    }

    public double getXOffset() {
        return this.xOffset;
    }

    public double getYOffset() {
        return this.yOffset;
    }

    public Paint getWallPaint() {
        return this.wallPaint;
    }

    public void setWallPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.wallPaint = paint;
        fireChangeEvent();
    }

    public CategoryItemRendererState initialise(Graphics2D g2, Rectangle2D dataArea, CategoryPlot plot, int rendererIndex, PlotRenderingInfo info) {
        return super.initialise(g2, new Double(dataArea.getX(), dataArea.getY() + getYOffset(), dataArea.getWidth() - getXOffset(), dataArea.getHeight() - getYOffset()), plot, rendererIndex, info);
    }

    public void drawBackground(Graphics2D g2, CategoryPlot plot, Rectangle2D dataArea) {
        float x0 = (float) dataArea.getX();
        float x1 = x0 + ((float) Math.abs(this.xOffset));
        float x3 = (float) dataArea.getMaxX();
        float x2 = x3 - ((float) Math.abs(this.xOffset));
        float y0 = (float) dataArea.getMaxY();
        float y1 = y0 - ((float) Math.abs(this.yOffset));
        float y3 = (float) dataArea.getMinY();
        float y2 = y3 + ((float) Math.abs(this.yOffset));
        GeneralPath clip = new GeneralPath();
        clip.moveTo(x0, y0);
        clip.lineTo(x0, y2);
        clip.lineTo(x1, y3);
        clip.lineTo(x3, y3);
        clip.lineTo(x3, y1);
        clip.lineTo(x2, y0);
        clip.closePath();
        Composite originalComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(3, plot.getBackgroundAlpha()));
        Paint backgroundPaint = plot.getBackgroundPaint();
        if (backgroundPaint != null) {
            g2.setPaint(backgroundPaint);
            g2.fill(clip);
        }
        GeneralPath leftWall = new GeneralPath();
        leftWall.moveTo(x0, y0);
        leftWall.lineTo(x0, y2);
        leftWall.lineTo(x1, y3);
        leftWall.lineTo(x1, y1);
        leftWall.closePath();
        g2.setPaint(getWallPaint());
        g2.fill(leftWall);
        GeneralPath bottomWall = new GeneralPath();
        bottomWall.moveTo(x0, y0);
        bottomWall.lineTo(x1, y1);
        bottomWall.lineTo(x3, y1);
        bottomWall.lineTo(x2, y0);
        bottomWall.closePath();
        g2.setPaint(getWallPaint());
        g2.fill(bottomWall);
        g2.setPaint(Color.lightGray);
        Line2D corner = new Line2D.Double((double) x0, (double) y0, (double) x1, (double) y1);
        g2.draw(corner);
        corner.setLine((double) x1, (double) y1, (double) x1, (double) y3);
        g2.draw(corner);
        corner.setLine((double) x1, (double) y1, (double) x3, (double) y1);
        g2.draw(corner);
        if (plot.getBackgroundImage() != null) {
            plot.drawBackgroundImage(g2, new Double(dataArea.getX() + getXOffset(), dataArea.getY(), dataArea.getWidth() - getXOffset(), dataArea.getHeight() - getYOffset()));
        }
        g2.setComposite(originalComposite);
    }

    public void drawOutline(Graphics2D g2, CategoryPlot plot, Rectangle2D dataArea) {
        float x0 = (float) dataArea.getX();
        float x1 = x0 + ((float) Math.abs(this.xOffset));
        float x3 = (float) dataArea.getMaxX();
        float x2 = x3 - ((float) Math.abs(this.xOffset));
        float y0 = (float) dataArea.getMaxY();
        float y1 = y0 - ((float) Math.abs(this.yOffset));
        float y3 = (float) dataArea.getMinY();
        float y2 = y3 + ((float) Math.abs(this.yOffset));
        GeneralPath clip = new GeneralPath();
        clip.moveTo(x0, y0);
        clip.lineTo(x0, y2);
        clip.lineTo(x1, y3);
        clip.lineTo(x3, y3);
        clip.lineTo(x3, y1);
        clip.lineTo(x2, y0);
        clip.closePath();
        Stroke outlineStroke = plot.getOutlineStroke();
        Paint outlinePaint = plot.getOutlinePaint();
        if (outlineStroke != null && outlinePaint != null) {
            g2.setStroke(outlineStroke);
            g2.setPaint(outlinePaint);
            g2.draw(clip);
        }
    }

    public void drawDomainGridline(Graphics2D g2, CategoryPlot plot, Rectangle2D dataArea, double value) {
        Line2D line1 = null;
        Line2D line2 = null;
        PlotOrientation orientation = plot.getOrientation();
        double y0;
        double y1;
        double x0;
        double x1;
        if (orientation == PlotOrientation.HORIZONTAL) {
            y0 = value;
            y1 = value - getYOffset();
            x0 = dataArea.getMinX();
            x1 = x0 + getXOffset();
            double x2 = dataArea.getMaxX();
            line1 = new Line2D.Double(x0, y0, x1, y1);
            line2 = new Line2D.Double(x1, y1, x2, y1);
        } else if (orientation == PlotOrientation.VERTICAL) {
            x0 = value;
            x1 = value + getXOffset();
            y0 = dataArea.getMaxY();
            y1 = y0 - getYOffset();
            double y2 = dataArea.getMinY();
            line1 = new Line2D.Double(x0, y0, x1, y1);
            Line2D.Double doubleR = new Line2D.Double(x1, y1, x1, y2);
        }
        Paint paint = plot.getDomainGridlinePaint();
        Stroke stroke = plot.getDomainGridlineStroke();
        if (paint == null) {
            paint = Plot.DEFAULT_OUTLINE_PAINT;
        }
        g2.setPaint(paint);
        if (stroke == null) {
            stroke = Plot.DEFAULT_OUTLINE_STROKE;
        }
        g2.setStroke(stroke);
        g2.draw(line1);
        g2.draw(line2);
    }

    public void drawRangeGridline(Graphics2D g2, CategoryPlot plot, ValueAxis axis, Rectangle2D dataArea, double value) {
        if (axis.getRange().contains(value)) {
            Rectangle2D adjusted = new Double(dataArea.getX(), dataArea.getY() + getYOffset(), dataArea.getWidth() - getXOffset(), dataArea.getHeight() - getYOffset());
            Line2D line1 = null;
            Line2D line2 = null;
            PlotOrientation orientation = plot.getOrientation();
            double x0;
            double x1;
            double y0;
            double y1;
            if (orientation == PlotOrientation.HORIZONTAL) {
                x0 = axis.valueToJava2D(value, adjusted, plot.getRangeAxisEdge());
                x1 = x0 + getXOffset();
                y0 = dataArea.getMaxY();
                y1 = y0 - getYOffset();
                double y2 = dataArea.getMinY();
                line1 = new Line2D.Double(x0, y0, x1, y1);
                line2 = new Line2D.Double(x1, y1, x1, y2);
            } else if (orientation == PlotOrientation.VERTICAL) {
                y0 = axis.valueToJava2D(value, adjusted, plot.getRangeAxisEdge());
                y1 = y0 - getYOffset();
                x0 = dataArea.getMinX();
                x1 = x0 + getXOffset();
                double x2 = dataArea.getMaxX();
                line1 = new Line2D.Double(x0, y0, x1, y1);
                Line2D.Double doubleR = new Line2D.Double(x1, y1, x2, y1);
            }
            Paint paint = plot.getRangeGridlinePaint();
            Stroke stroke = plot.getRangeGridlineStroke();
            if (paint == null) {
                paint = Plot.DEFAULT_OUTLINE_PAINT;
            }
            g2.setPaint(paint);
            if (stroke == null) {
                stroke = Plot.DEFAULT_OUTLINE_STROKE;
            }
            g2.setStroke(stroke);
            g2.draw(line1);
            g2.draw(line2);
        }
    }

    public void drawRangeLine(Graphics2D g2, CategoryPlot plot, ValueAxis axis, Rectangle2D dataArea, double value, Paint paint, Stroke stroke) {
        if (axis.getRange().contains(value)) {
            Rectangle2D adjusted = new Double(dataArea.getX(), dataArea.getY() + getYOffset(), dataArea.getWidth() - getXOffset(), dataArea.getHeight() - getYOffset());
            Line2D line1 = null;
            Line2D line2 = null;
            PlotOrientation orientation = plot.getOrientation();
            double x0;
            double x1;
            double y0;
            double y1;
            if (orientation == PlotOrientation.HORIZONTAL) {
                x0 = axis.valueToJava2D(value, adjusted, plot.getRangeAxisEdge());
                x1 = x0 + getXOffset();
                y0 = dataArea.getMaxY();
                y1 = y0 - getYOffset();
                double y2 = dataArea.getMinY();
                line1 = new Line2D.Double(x0, y0, x1, y1);
                line2 = new Line2D.Double(x1, y1, x1, y2);
            } else if (orientation == PlotOrientation.VERTICAL) {
                y0 = axis.valueToJava2D(value, adjusted, plot.getRangeAxisEdge());
                y1 = y0 - getYOffset();
                x0 = dataArea.getMinX();
                x1 = x0 + getXOffset();
                double x2 = dataArea.getMaxX();
                line1 = new Line2D.Double(x0, y0, x1, y1);
                Line2D.Double doubleR = new Line2D.Double(x1, y1, x2, y1);
            }
            g2.setPaint(paint);
            g2.setStroke(stroke);
            g2.draw(line1);
            g2.draw(line2);
        }
    }

    public void drawRangeMarker(Graphics2D g2, CategoryPlot plot, ValueAxis axis, Marker marker, Rectangle2D dataArea) {
        Rectangle2D adjusted = new Double(dataArea.getX(), dataArea.getY() + getYOffset(), dataArea.getWidth() - getXOffset(), dataArea.getHeight() - getYOffset());
        if (marker instanceof ValueMarker) {
            double value = ((ValueMarker) marker).getValue();
            if (axis.getRange().contains(value)) {
                GeneralPath path;
                PlotOrientation orientation = plot.getOrientation();
                float x;
                float y;
                if (orientation == PlotOrientation.HORIZONTAL) {
                    x = (float) axis.valueToJava2D(value, adjusted, plot.getRangeAxisEdge());
                    y = (float) adjusted.getMaxY();
                    path = new GeneralPath();
                    path.moveTo(x, y);
                    path.lineTo((float) (((double) x) + getXOffset()), y - ((float) getYOffset()));
                    path.lineTo((float) (((double) x) + getXOffset()), (float) (adjusted.getMinY() - getYOffset()));
                    path.lineTo(x, (float) adjusted.getMinY());
                    path.closePath();
                } else if (orientation == PlotOrientation.VERTICAL) {
                    y = (float) axis.valueToJava2D(value, adjusted, plot.getRangeAxisEdge());
                    x = (float) dataArea.getX();
                    path = new GeneralPath();
                    path.moveTo(x, y);
                    path.lineTo(((float) this.xOffset) + x, y - ((float) this.yOffset));
                    path.lineTo((float) (adjusted.getMaxX() + this.xOffset), y - ((float) this.yOffset));
                    path.lineTo((float) adjusted.getMaxX(), y);
                    path.closePath();
                } else {
                    throw new IllegalStateException();
                }
                g2.setPaint(marker.getPaint());
                g2.fill(path);
                g2.setPaint(marker.getOutlinePaint());
                g2.draw(path);
                String label = marker.getLabel();
                RectangleAnchor anchor = marker.getLabelAnchor();
                if (label != null) {
                    g2.setFont(marker.getLabelFont());
                    g2.setPaint(marker.getLabelPaint());
                    Point2D coordinates = calculateRangeMarkerTextAnchorPoint(g2, orientation, dataArea, path.getBounds2D(), marker.getLabelOffset(), LengthAdjustmentType.EXPAND, anchor);
                    TextUtilities.drawAlignedString(label, g2, (float) coordinates.getX(), (float) coordinates.getY(), marker.getLabelTextAnchor());
                    return;
                }
                return;
            }
            return;
        }
        super.drawRangeMarker(g2, plot, axis, marker, adjusted);
    }

    public void drawItem(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryDataset dataset, int row, int column, int pass) {
        int visibleRow = state.getVisibleSeriesIndex(row);
        if (visibleRow >= 0) {
            Number dataValue = dataset.getValue(row, column);
            if (dataValue != null) {
                double value = dataValue.doubleValue();
                Rectangle2D adjusted = new Double(dataArea.getX(), dataArea.getY() + getYOffset(), dataArea.getWidth() - getXOffset(), dataArea.getHeight() - getYOffset());
                PlotOrientation orientation = plot.getOrientation();
                double barW0 = calculateBarW0(plot, orientation, adjusted, domainAxis, state, visibleRow, column);
                double[] barL0L1 = calculateBarL0L1(value);
                if (barL0L1 != null) {
                    Rectangle2D bar;
                    RectangleEdge edge = plot.getRangeAxisEdge();
                    double transL0 = rangeAxis.valueToJava2D(barL0L1[0], adjusted, edge);
                    double transL1 = rangeAxis.valueToJava2D(barL0L1[1], adjusted, edge);
                    double barL0 = Math.min(transL0, transL1);
                    double barLength = Math.abs(transL1 - transL0);
                    if (orientation == PlotOrientation.HORIZONTAL) {
                        bar = new Double(barL0, barW0, barLength, state.getBarWidth());
                    } else {
                        Double doubleR = new Double(barW0, barL0, state.getBarWidth(), barLength);
                    }
                    Paint itemPaint = getItemPaint(row, column);
                    g2.setPaint(itemPaint);
                    g2.fill(bar);
                    double x0 = bar.getMinX();
                    double x1 = x0 + getXOffset();
                    double x2 = bar.getMaxX();
                    double x3 = x2 + getXOffset();
                    double y0 = bar.getMinY() - getYOffset();
                    double y1 = bar.getMinY();
                    double y2 = bar.getMaxY() - getYOffset();
                    double y3 = bar.getMaxY();
                    GeneralPath bar3dRight = null;
                    if (barLength > 0.0d) {
                        bar3dRight = new GeneralPath();
                        bar3dRight.moveTo((float) x2, (float) y3);
                        bar3dRight.lineTo((float) x2, (float) y1);
                        bar3dRight.lineTo((float) x3, (float) y0);
                        bar3dRight.lineTo((float) x3, (float) y2);
                        bar3dRight.closePath();
                        g2.setPaint(PaintAlpha.darker(itemPaint));
                        g2.fill(bar3dRight);
                    }
                    GeneralPath bar3dTop = new GeneralPath();
                    bar3dTop.moveTo((float) x0, (float) y1);
                    bar3dTop.lineTo((float) x1, (float) y0);
                    bar3dTop.lineTo((float) x3, (float) y0);
                    bar3dTop.lineTo((float) x2, (float) y1);
                    bar3dTop.closePath();
                    g2.fill(bar3dTop);
                    if (isDrawBarOutline() && state.getBarWidth() > BarRenderer.BAR_OUTLINE_WIDTH_THRESHOLD) {
                        g2.setStroke(getItemOutlineStroke(row, column));
                        g2.setPaint(getItemOutlinePaint(row, column));
                        g2.draw(bar);
                        if (bar3dRight != null) {
                            g2.draw(bar3dRight);
                        }
                        g2.draw(bar3dTop);
                    }
                    CategoryItemLabelGenerator generator = getItemLabelGenerator(row, column);
                    if (generator != null && isItemLabelVisible(row, column)) {
                        drawItemLabel(g2, dataset, row, column, plot, generator, bar, value < 0.0d);
                    }
                    EntityCollection entities = state.getEntityCollection();
                    if (entities != null) {
                        GeneralPath barOutline = new GeneralPath();
                        barOutline.moveTo((float) x0, (float) y3);
                        barOutline.lineTo((float) x0, (float) y1);
                        barOutline.lineTo((float) x1, (float) y0);
                        barOutline.lineTo((float) x3, (float) y0);
                        barOutline.lineTo((float) x3, (float) y2);
                        barOutline.lineTo((float) x2, (float) y3);
                        barOutline.closePath();
                        addItemEntity(entities, dataset, row, column, barOutline);
                    }
                }
            }
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof BarRenderer3D)) {
            return false;
        }
        BarRenderer3D that = (BarRenderer3D) obj;
        if (this.xOffset == that.xOffset && this.yOffset == that.yOffset && PaintUtilities.equal(this.wallPaint, that.wallPaint)) {
            return super.equals(obj);
        }
        return false;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.wallPaint, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.wallPaint = SerialUtilities.readPaint(stream);
    }
}
