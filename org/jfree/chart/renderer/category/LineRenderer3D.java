package org.jfree.chart.renderer.category;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.jfree.chart.Effect3D;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.category.CategoryDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.util.PaintUtilities;
import org.jfree.util.ShapeUtilities;

public class LineRenderer3D extends LineAndShapeRenderer implements Effect3D, Serializable {
    public static final Paint DEFAULT_WALL_PAINT;
    public static final double DEFAULT_X_OFFSET = 12.0d;
    public static final double DEFAULT_Y_OFFSET = 8.0d;
    private static final long serialVersionUID = 5467931468380928736L;
    private transient Paint wallPaint;
    private double xOffset;
    private double yOffset;

    static {
        DEFAULT_WALL_PAINT = new Color(221, 221, 221);
    }

    public LineRenderer3D() {
        super(true, false);
        this.xOffset = DEFAULT_X_OFFSET;
        this.yOffset = DEFAULT_Y_OFFSET;
        this.wallPaint = DEFAULT_WALL_PAINT;
    }

    public double getXOffset() {
        return this.xOffset;
    }

    public double getYOffset() {
        return this.yOffset;
    }

    public void setXOffset(double xOffset) {
        this.xOffset = xOffset;
        fireChangeEvent();
    }

    public void setYOffset(double yOffset) {
        this.yOffset = yOffset;
        fireChangeEvent();
    }

    public Paint getWallPaint() {
        return this.wallPaint;
    }

    public void setWallPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.wallPaint = paint;
        fireChangeEvent();
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
        Line2D corner = new Double((double) x0, (double) y0, (double) x1, (double) y1);
        g2.draw(corner);
        corner.setLine((double) x1, (double) y1, (double) x1, (double) y3);
        g2.draw(corner);
        corner.setLine((double) x1, (double) y1, (double) x3, (double) y1);
        g2.draw(corner);
        if (plot.getBackgroundImage() != null) {
            plot.drawBackgroundImage(g2, new Rectangle2D.Double(dataArea.getX() + getXOffset(), dataArea.getY(), dataArea.getWidth() - getXOffset(), dataArea.getHeight() - getYOffset()));
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
            line1 = new Double(x0, y0, x1, y1);
            line2 = new Double(x1, y1, x2, y1);
        } else if (orientation == PlotOrientation.VERTICAL) {
            x0 = value;
            x1 = value + getXOffset();
            y0 = dataArea.getMaxY();
            y1 = y0 - getYOffset();
            double y2 = dataArea.getMinY();
            line1 = new Double(x0, y0, x1, y1);
            Double doubleR = new Double(x1, y1, x1, y2);
        }
        g2.setPaint(plot.getDomainGridlinePaint());
        g2.setStroke(plot.getDomainGridlineStroke());
        g2.draw(line1);
        g2.draw(line2);
    }

    public void drawRangeGridline(Graphics2D g2, CategoryPlot plot, ValueAxis axis, Rectangle2D dataArea, double value) {
        if (axis.getRange().contains(value)) {
            Rectangle2D adjusted = new Rectangle2D.Double(dataArea.getX(), dataArea.getY() + getYOffset(), dataArea.getWidth() - getXOffset(), dataArea.getHeight() - getYOffset());
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
                line1 = new Double(x0, y0, x1, y1);
                line2 = new Double(x1, y1, x1, y2);
            } else if (orientation == PlotOrientation.VERTICAL) {
                y0 = axis.valueToJava2D(value, adjusted, plot.getRangeAxisEdge());
                y1 = y0 - getYOffset();
                x0 = dataArea.getMinX();
                x1 = x0 + getXOffset();
                double x2 = dataArea.getMaxX();
                line1 = new Double(x0, y0, x1, y1);
                Double doubleR = new Double(x1, y1, x2, y1);
            }
            g2.setPaint(plot.getRangeGridlinePaint());
            g2.setStroke(plot.getRangeGridlineStroke());
            g2.draw(line1);
            g2.draw(line2);
        }
    }

    public void drawRangeMarker(Graphics2D g2, CategoryPlot plot, ValueAxis axis, Marker marker, Rectangle2D dataArea) {
        Rectangle2D adjusted = new Rectangle2D.Double(dataArea.getX(), dataArea.getY() + getYOffset(), dataArea.getWidth() - getXOffset(), dataArea.getHeight() - getYOffset());
        if (marker instanceof ValueMarker) {
            double value = ((ValueMarker) marker).getValue();
            if (axis.getRange().contains(value)) {
                GeneralPath path = null;
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
                }
                g2.setPaint(marker.getPaint());
                g2.fill(path);
                g2.setPaint(marker.getOutlinePaint());
                g2.draw(path);
                return;
            }
            return;
        }
        super.drawRangeMarker(g2, plot, axis, marker, adjusted);
    }

    public void drawItem(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryDataset dataset, int row, int column, int pass) {
        if (getItemVisible(row, column)) {
            Number v = dataset.getValue(row, column);
            if (v != null) {
                Rectangle2D adjusted = new Rectangle2D.Double(dataArea.getX(), dataArea.getY() + getYOffset(), dataArea.getWidth() - getXOffset(), dataArea.getHeight() - getYOffset());
                PlotOrientation orientation = plot.getOrientation();
                double x1 = domainAxis.getCategoryMiddle(column, getColumnCount(), adjusted, plot.getDomainAxisEdge());
                double value = v.doubleValue();
                double y1 = rangeAxis.valueToJava2D(value, adjusted, plot.getRangeAxisEdge());
                Shape shape = getItemShape(row, column);
                if (orientation == PlotOrientation.HORIZONTAL) {
                    shape = ShapeUtilities.createTranslatedShape(shape, y1, x1);
                } else if (orientation == PlotOrientation.VERTICAL) {
                    shape = ShapeUtilities.createTranslatedShape(shape, x1, y1);
                }
                if (pass == 0 && getItemLineVisible(row, column) && column != 0) {
                    Number previousValue = dataset.getValue(row, column - 1);
                    if (previousValue != null) {
                        double previous = previousValue.doubleValue();
                        double x0 = domainAxis.getCategoryMiddle(column - 1, getColumnCount(), adjusted, plot.getDomainAxisEdge());
                        double y0 = rangeAxis.valueToJava2D(previous, adjusted, plot.getRangeAxisEdge());
                        double x2 = x0 + getXOffset();
                        double y2 = y0 - getYOffset();
                        double x3 = x1 + getXOffset();
                        double y3 = y1 - getYOffset();
                        GeneralPath clip = new GeneralPath();
                        if (orientation == PlotOrientation.HORIZONTAL) {
                            clip.moveTo((float) y0, (float) x0);
                            clip.lineTo((float) y1, (float) x1);
                            clip.lineTo((float) y3, (float) x3);
                            clip.lineTo((float) y2, (float) x2);
                            clip.lineTo((float) y0, (float) x0);
                            clip.closePath();
                        } else if (orientation == PlotOrientation.VERTICAL) {
                            clip.moveTo((float) x0, (float) y0);
                            clip.lineTo((float) x1, (float) y1);
                            clip.lineTo((float) x3, (float) y3);
                            clip.lineTo((float) x2, (float) y2);
                            clip.lineTo((float) x0, (float) y0);
                            clip.closePath();
                        }
                        g2.setPaint(getItemPaint(row, column));
                        g2.fill(clip);
                        g2.setStroke(getItemOutlineStroke(row, column));
                        g2.setPaint(getItemOutlinePaint(row, column));
                        g2.draw(clip);
                    }
                }
                if (pass == 1 && isItemLabelVisible(row, column)) {
                    if (orientation == PlotOrientation.HORIZONTAL) {
                        drawItemLabel(g2, orientation, dataset, row, column, y1, x1, value < 0.0d);
                    } else if (orientation == PlotOrientation.VERTICAL) {
                        drawItemLabel(g2, orientation, dataset, row, column, x1, y1, value < 0.0d);
                    }
                }
                EntityCollection entities = state.getEntityCollection();
                if (entities != null) {
                    addItemEntity(entities, dataset, row, column, shape);
                }
            }
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof LineRenderer3D)) {
            return false;
        }
        LineRenderer3D that = (LineRenderer3D) obj;
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
