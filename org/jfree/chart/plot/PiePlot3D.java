package org.jfree.chart.plot;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.PieSectionEntity;
import org.jfree.chart.labels.PieToolTipGenerator;
import org.jfree.chart.util.PaintAlpha;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.general.PieDataset;
import org.jfree.data.xy.NormalizedMatrixSeries;

public class PiePlot3D extends PiePlot implements Serializable {
    private static final long serialVersionUID = 3408984188945161432L;
    private boolean darkerSides;
    private double depthFactor;

    public PiePlot3D() {
        this(null);
    }

    public PiePlot3D(PieDataset dataset) {
        super(dataset);
        this.depthFactor = 0.12d;
        this.darkerSides = false;
        setCircular(false, false);
    }

    public double getDepthFactor() {
        return this.depthFactor;
    }

    public void setDepthFactor(double factor) {
        this.depthFactor = factor;
        fireChangeEvent();
    }

    public boolean getDarkerSides() {
        return this.darkerSides;
    }

    public void setDarkerSides(boolean darker) {
        this.darkerSides = darker;
        fireChangeEvent();
    }

    public void draw(Graphics2D g2, Rectangle2D plotArea, Point2D anchor, PlotState parentState, PlotRenderingInfo info) {
        getInsets().trim(plotArea);
        Rectangle2D originalPlotArea = (Rectangle2D) plotArea.clone();
        if (info != null) {
            info.setPlotArea(plotArea);
            info.setDataArea(plotArea);
        }
        drawBackground(g2, plotArea);
        Shape savedClip = g2.getClip();
        g2.clip(plotArea);
        Graphics2D savedG2 = g2;
        BufferedImage dataImage = null;
        if (getShadowGenerator() != null) {
            BufferedImage bufferedImage = new BufferedImage((int) plotArea.getWidth(), (int) plotArea.getHeight(), 2);
            g2 = bufferedImage.createGraphics();
            g2.translate(-plotArea.getX(), -plotArea.getY());
            g2.setRenderingHints(savedG2.getRenderingHints());
            originalPlotArea = (Rectangle2D) plotArea.clone();
        }
        double gapPercent = getInteriorGap();
        double labelPercent = 0.0d;
        if (getLabelGenerator() != null) {
            labelPercent = getLabelGap() + getMaximumLabelWidth();
        }
        double gapHorizontal = (plotArea.getWidth() * (gapPercent + labelPercent)) * DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
        double gapVertical = (plotArea.getHeight() * gapPercent) * DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
        double linkX = plotArea.getX() + (gapHorizontal / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
        double linkY = plotArea.getY() + (gapVertical / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
        double linkW = plotArea.getWidth() - gapHorizontal;
        double linkH = plotArea.getHeight() - gapVertical;
        if (isCircular()) {
            double min = Math.min(linkW, linkH) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
            linkX = (((linkX + linkX) + linkW) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS) - min;
            linkY = (((linkY + linkY) + linkH) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS) - min;
            linkW = DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS * min;
            linkH = DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS * min;
        }
        PiePlotState state = initialise(g2, plotArea, this, null, info);
        PiePlotState piePlotState = state;
        piePlotState.setLinkArea(new Double(linkX, linkY, linkW, linkH * (NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR - this.depthFactor)));
        double hh = linkW * getLabelLinkMargin();
        double vv = linkH * getLabelLinkMargin();
        Rectangle2D explodeArea = new Double(linkX + (hh / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS), linkY + (vv / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS), linkW - hh, linkH - vv);
        state.setExplodedPieArea(explodeArea);
        double maximumExplodePercent = getMaximumExplodePercent();
        double percent = maximumExplodePercent / (NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR + maximumExplodePercent);
        double h1 = explodeArea.getWidth() * percent;
        double v1 = explodeArea.getHeight() * percent;
        Rectangle2D pieArea = new Double(explodeArea.getX() + (h1 / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS), explodeArea.getY() + (v1 / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS), explodeArea.getWidth() - h1, explodeArea.getHeight() - v1);
        int depth = (int) (pieArea.getHeight() * this.depthFactor);
        Rectangle2D linkArea = new Double(linkX, linkY, linkW, linkH - ((double) depth));
        state.setLinkArea(linkArea);
        state.setPieArea(pieArea);
        state.setPieCenterX(pieArea.getCenterX());
        state.setPieCenterY(pieArea.getCenterY() - (((double) depth) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS));
        state.setPieWRadius(pieArea.getWidth() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
        state.setPieHRadius((pieArea.getHeight() - ((double) depth)) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
        PieDataset dataset = getDataset();
        if (DatasetUtilities.isEmptyOrNull(getDataset())) {
            drawNoDataMessage(g2, plotArea);
            g2.setClip(savedClip);
            drawOutline(g2, plotArea);
        } else if (((double) dataset.getKeys().size()) > plotArea.getWidth()) {
            String text = localizationResources.getString("Too_many_elements");
            Font font = new Font("dialog", 1, 10);
            g2.setFont(font);
            Graphics2D graphics2D = g2;
            String str = text;
            graphics2D.drawString(str, (int) (plotArea.getX() + ((plotArea.getWidth() - ((double) g2.getFontMetrics(font).stringWidth(text))) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS)), (int) (plotArea.getY() + (plotArea.getHeight() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS)));
        } else {
            if (isCircular()) {
                min = Math.min(plotArea.getWidth(), plotArea.getHeight()) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
                plotArea = new Double(plotArea.getCenterX() - min, plotArea.getCenterY() - min, DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS * min, DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS * min);
            }
            List<Comparable> sectionKeys = dataset.getKeys();
            if (!sectionKeys.isEmpty()) {
                double arcX = pieArea.getX();
                double arcY = pieArea.getY();
                Composite originalComposite = g2.getComposite();
                g2.setComposite(AlphaComposite.getInstance(3, getForegroundAlpha()));
                double totalValue = DatasetUtilities.calculatePieDatasetTotal(dataset);
                double runningTotal = 0.0d;
                if (depth >= 0) {
                    Arc2D.Double arc;
                    Comparable key;
                    Paint paint;
                    Paint outlinePaint;
                    Stroke outlineStroke;
                    Arc2D segment;
                    ArrayList arcList = new ArrayList();
                    for (Comparable value : sectionKeys) {
                        Number dataValue = dataset.getValue(value);
                        if (dataValue == null) {
                            arcList.add(null);
                        } else {
                            double value2 = dataValue.doubleValue();
                            if (value2 <= 0.0d) {
                                arcList.add(null);
                            } else {
                                double startAngle = getStartAngle();
                                double direction = getDirection().getFactor();
                                double angle1 = startAngle + (((360.0d * runningTotal) * direction) / totalValue);
                                double angle2 = startAngle + ((((runningTotal + value2) * direction) * 360.0d) / totalValue);
                                if (Math.abs(angle2 - angle1) > getMinimumArcAngleToDraw()) {
                                    arcList.add(new Arc2D.Double(arcX, arcY + ((double) depth), pieArea.getWidth(), pieArea.getHeight() - ((double) depth), angle1, angle2 - angle1, 2));
                                } else {
                                    arcList.add(null);
                                }
                                runningTotal += value2;
                            }
                        }
                    }
                    Shape oldClip = g2.getClip();
                    Ellipse2D top = new Ellipse2D.Double(pieArea.getX(), pieArea.getY(), pieArea.getWidth(), pieArea.getHeight() - ((double) depth));
                    Ellipse2D bottom = new Ellipse2D.Double(pieArea.getX(), pieArea.getY() + ((double) depth), pieArea.getWidth(), pieArea.getHeight() - ((double) depth));
                    Rectangle2D lower = new Double(top.getX(), top.getCenterY(), pieArea.getWidth(), bottom.getMaxY() - top.getCenterY());
                    Rectangle2D upper = new Double(pieArea.getX(), top.getY(), pieArea.getWidth(), bottom.getCenterY() - top.getY());
                    Area area = new Area(top);
                    area.add(new Area(lower));
                    area = new Area(bottom);
                    area.add(new Area(upper));
                    area = new Area(area);
                    area.intersect(area);
                    area = new Area(area);
                    area.subtract(new Area(top));
                    area = new Area(area);
                    area.subtract(new Area(bottom));
                    int categoryCount = arcList.size();
                    for (int categoryIndex = 0; categoryIndex < categoryCount; categoryIndex++) {
                        arc = (Arc2D.Double) arcList.get(categoryIndex);
                        if (arc != null) {
                            key = getSectionKey(categoryIndex);
                            paint = lookupSectionPaint(key);
                            outlinePaint = lookupSectionOutlinePaint(key);
                            outlineStroke = lookupSectionOutlineStroke(key);
                            g2.setPaint(paint);
                            g2.fill(arc);
                            g2.setPaint(outlinePaint);
                            g2.setStroke(outlineStroke);
                            g2.draw(arc);
                            g2.setPaint(paint);
                            Point2D p1 = arc.getStartPoint();
                            Polygon polygon = new Polygon(new int[]{(int) arc.getCenterX(), (int) arc.getCenterX(), (int) p1.getX(), (int) p1.getX()}, new int[]{(int) arc.getCenterY(), ((int) arc.getCenterY()) - depth, ((int) p1.getY()) - depth, (int) p1.getY()}, 4);
                            g2.setPaint(Color.lightGray);
                            g2.fill(polygon);
                            g2.setPaint(outlinePaint);
                            g2.setStroke(outlineStroke);
                            g2.draw(polygon);
                            g2.setPaint(paint);
                        }
                    }
                    g2.setPaint(Color.gray);
                    g2.fill(area);
                    g2.fill(area);
                    int cat = 0;
                    Iterator iterator = arcList.iterator();
                    while (iterator.hasNext()) {
                        segment = (Arc2D) iterator.next();
                        if (segment != null) {
                            key = getSectionKey(cat);
                            drawSide(g2, pieArea, segment, area, area, lookupSectionPaint(key), lookupSectionOutlinePaint(key), lookupSectionOutlineStroke(key), false, true);
                        }
                        cat++;
                    }
                    cat = 0;
                    iterator = arcList.iterator();
                    while (iterator.hasNext()) {
                        segment = (Arc2D) iterator.next();
                        if (segment != null) {
                            key = getSectionKey(cat);
                            drawSide(g2, pieArea, segment, area, area, lookupSectionPaint(key), lookupSectionOutlinePaint(key), lookupSectionOutlineStroke(key), true, false);
                        }
                        cat++;
                    }
                    g2.setClip(oldClip);
                    for (int sectionIndex = 0; sectionIndex < categoryCount; sectionIndex++) {
                        arc = (Arc2D.Double) arcList.get(sectionIndex);
                        if (arc != null) {
                            Arc2D upperArc = new Arc2D.Double(arcX, arcY, pieArea.getWidth(), pieArea.getHeight() - ((double) depth), arc.getAngleStart(), arc.getAngleExtent(), 2);
                            Comparable currentKey = (Comparable) sectionKeys.get(sectionIndex);
                            paint = lookupSectionPaint(currentKey, true);
                            outlinePaint = lookupSectionOutlinePaint(currentKey);
                            outlineStroke = lookupSectionOutlineStroke(currentKey);
                            g2.setPaint(paint);
                            g2.fill(upperArc);
                            g2.setStroke(outlineStroke);
                            g2.setPaint(outlinePaint);
                            g2.draw(upperArc);
                            if (info != null) {
                                EntityCollection entities = info.getOwner().getEntityCollection();
                                if (entities != null) {
                                    String tip = null;
                                    PieToolTipGenerator tipster = getToolTipGenerator();
                                    if (tipster != null) {
                                        tip = tipster.generateToolTip(dataset, currentKey);
                                    }
                                    String url = null;
                                    if (getURLGenerator() != null) {
                                        url = getURLGenerator().generateURL(dataset, currentKey, getPieIndex());
                                    }
                                    entities.add(new PieSectionEntity(upperArc, dataset, getPieIndex(), sectionIndex, currentKey, tip, url));
                                }
                            }
                        }
                    }
                    List keys = dataset.getKeys();
                    Rectangle2D adjustedPlotArea = new Double(originalPlotArea.getX(), originalPlotArea.getY(), originalPlotArea.getWidth(), originalPlotArea.getHeight() - ((double) depth));
                    if (getSimpleLabels()) {
                        drawSimpleLabels(g2, keys, totalValue, adjustedPlotArea, linkArea, state);
                    } else {
                        drawLabels(g2, keys, totalValue, adjustedPlotArea, linkArea, state);
                    }
                    if (getShadowGenerator() != null) {
                        g2 = savedG2;
                        Image createDropShadow = getShadowGenerator().createDropShadow(dataImage);
                        g2.drawImage(shadowImage, ((int) plotArea.getX()) + getShadowGenerator().calculateOffsetX(), ((int) plotArea.getY()) + getShadowGenerator().calculateOffsetY(), null);
                        g2.drawImage(dataImage, (int) plotArea.getX(), (int) plotArea.getY(), null);
                    }
                    g2.setClip(savedClip);
                    g2.setComposite(originalComposite);
                    drawOutline(g2, originalPlotArea);
                }
            }
        }
    }

    protected void drawSide(Graphics2D g2, Rectangle2D plotArea, Arc2D arc, Area front, Area back, Paint paint, Paint outlinePaint, Stroke outlineStroke, boolean drawFront, boolean drawBack) {
        if (getDarkerSides()) {
            paint = PaintAlpha.darker(paint);
        }
        double start = arc.getAngleStart();
        double extent = arc.getAngleExtent();
        double end = start + extent;
        g2.setStroke(outlineStroke);
        Area area;
        Area side;
        if (extent < 0.0d) {
            if (isAngleAtFront(start)) {
                if (isAngleAtBack(end)) {
                    if (drawBack) {
                        area = new Area(new Double(plotArea.getX(), plotArea.getY(), arc.getEndPoint().getX() - plotArea.getX(), plotArea.getHeight()));
                        area.intersect(back);
                        g2.setPaint(paint);
                        g2.fill(area);
                        g2.setPaint(outlinePaint);
                        g2.draw(area);
                    }
                    if (drawFront) {
                        area = new Area(new Double(plotArea.getX(), plotArea.getY(), arc.getStartPoint().getX() - plotArea.getX(), plotArea.getHeight()));
                        area.intersect(front);
                        g2.setPaint(paint);
                        g2.fill(area);
                        g2.setPaint(outlinePaint);
                        g2.draw(area);
                    }
                } else if (extent <= -180.0d) {
                    area = new Area(new Double(plotArea.getX(), plotArea.getY(), arc.getStartPoint().getX() - plotArea.getX(), plotArea.getHeight()));
                    area.intersect(front);
                    area = new Area(new Double(arc.getEndPoint().getX(), plotArea.getY(), plotArea.getMaxX() - arc.getEndPoint().getX(), plotArea.getHeight()));
                    area.intersect(front);
                    g2.setPaint(paint);
                    if (drawFront) {
                        g2.fill(area);
                        g2.fill(area);
                    }
                    if (drawBack) {
                        g2.fill(back);
                    }
                    g2.setPaint(outlinePaint);
                    if (drawFront) {
                        g2.draw(area);
                        g2.draw(area);
                    }
                    if (drawBack) {
                        g2.draw(back);
                    }
                } else if (drawFront) {
                    side = new Area(new Double(arc.getEndPoint().getX(), plotArea.getY(), arc.getStartPoint().getX() - arc.getEndPoint().getX(), plotArea.getHeight()));
                    side.intersect(front);
                    g2.setPaint(paint);
                    g2.fill(side);
                    g2.setPaint(outlinePaint);
                    g2.draw(side);
                }
            } else if (isAngleAtFront(end)) {
                if (drawBack) {
                    area = new Area(new Double(arc.getStartPoint().getX(), plotArea.getY(), plotArea.getMaxX() - arc.getStartPoint().getX(), plotArea.getHeight()));
                    area.intersect(back);
                    g2.setPaint(paint);
                    g2.fill(area);
                    g2.setPaint(outlinePaint);
                    g2.draw(area);
                }
                if (drawFront) {
                    area = new Area(new Double(arc.getEndPoint().getX(), plotArea.getY(), plotArea.getMaxX() - arc.getEndPoint().getX(), plotArea.getHeight()));
                    area.intersect(front);
                    g2.setPaint(paint);
                    g2.fill(area);
                    g2.setPaint(outlinePaint);
                    g2.draw(area);
                }
            } else if (extent <= -180.0d) {
                area = new Area(new Double(arc.getStartPoint().getX(), plotArea.getY(), plotArea.getMaxX() - arc.getStartPoint().getX(), plotArea.getHeight()));
                area.intersect(back);
                area = new Area(new Double(plotArea.getX(), plotArea.getY(), arc.getEndPoint().getX() - plotArea.getX(), plotArea.getHeight()));
                area.intersect(back);
                g2.setPaint(paint);
                if (drawBack) {
                    g2.fill(area);
                    g2.fill(area);
                }
                if (drawFront) {
                    g2.fill(front);
                }
                g2.setPaint(outlinePaint);
                if (drawBack) {
                    g2.draw(area);
                    g2.draw(area);
                }
                if (drawFront) {
                    g2.draw(front);
                }
            } else if (drawBack) {
                side = new Area(new Double(arc.getStartPoint().getX(), plotArea.getY(), arc.getEndPoint().getX() - arc.getStartPoint().getX(), plotArea.getHeight()));
                side.intersect(back);
                g2.setPaint(paint);
                g2.fill(side);
                g2.setPaint(outlinePaint);
                g2.draw(side);
            }
        } else if (extent <= 0.0d) {
        } else {
            if (isAngleAtFront(start)) {
                if (isAngleAtBack(end)) {
                    if (drawBack) {
                        area = new Area(new Double(arc.getEndPoint().getX(), plotArea.getY(), plotArea.getMaxX() - arc.getEndPoint().getX(), plotArea.getHeight()));
                        area.intersect(back);
                        g2.setPaint(paint);
                        g2.fill(area);
                        g2.setPaint(outlinePaint);
                        g2.draw(area);
                    }
                    if (drawFront) {
                        area = new Area(new Double(arc.getStartPoint().getX(), plotArea.getY(), plotArea.getMaxX() - arc.getStartPoint().getX(), plotArea.getHeight()));
                        area.intersect(front);
                        g2.setPaint(paint);
                        g2.fill(area);
                        g2.setPaint(outlinePaint);
                        g2.draw(area);
                    }
                } else if (extent >= 180.0d) {
                    area = new Area(new Double(arc.getStartPoint().getX(), plotArea.getY(), plotArea.getMaxX() - arc.getStartPoint().getX(), plotArea.getHeight()));
                    area.intersect(front);
                    area = new Area(new Double(plotArea.getX(), plotArea.getY(), arc.getEndPoint().getX() - plotArea.getX(), plotArea.getHeight()));
                    area.intersect(front);
                    g2.setPaint(paint);
                    if (drawFront) {
                        g2.fill(area);
                        g2.fill(area);
                    }
                    if (drawBack) {
                        g2.fill(back);
                    }
                    g2.setPaint(outlinePaint);
                    if (drawFront) {
                        g2.draw(area);
                        g2.draw(area);
                    }
                    if (drawBack) {
                        g2.draw(back);
                    }
                } else if (drawFront) {
                    side = new Area(new Double(arc.getStartPoint().getX(), plotArea.getY(), arc.getEndPoint().getX() - arc.getStartPoint().getX(), plotArea.getHeight()));
                    side.intersect(front);
                    g2.setPaint(paint);
                    g2.fill(side);
                    g2.setPaint(outlinePaint);
                    g2.draw(side);
                }
            } else if (isAngleAtFront(end)) {
                if (drawBack) {
                    area = new Area(new Double(plotArea.getX(), plotArea.getY(), arc.getStartPoint().getX() - plotArea.getX(), plotArea.getHeight()));
                    area.intersect(back);
                    g2.setPaint(paint);
                    g2.fill(area);
                    g2.setPaint(outlinePaint);
                    g2.draw(area);
                }
                if (drawFront) {
                    area = new Area(new Double(plotArea.getX(), plotArea.getY(), arc.getEndPoint().getX() - plotArea.getX(), plotArea.getHeight()));
                    area.intersect(front);
                    g2.setPaint(paint);
                    g2.fill(area);
                    g2.setPaint(outlinePaint);
                    g2.draw(area);
                }
            } else if (extent >= 180.0d) {
                area = new Area(new Double(arc.getStartPoint().getX(), plotArea.getY(), plotArea.getX() - arc.getStartPoint().getX(), plotArea.getHeight()));
                area.intersect(back);
                area = new Area(new Double(arc.getEndPoint().getX(), plotArea.getY(), plotArea.getMaxX() - arc.getEndPoint().getX(), plotArea.getHeight()));
                area.intersect(back);
                g2.setPaint(paint);
                if (drawBack) {
                    g2.fill(area);
                    g2.fill(area);
                }
                if (drawFront) {
                    g2.fill(front);
                }
                g2.setPaint(outlinePaint);
                if (drawBack) {
                    g2.draw(area);
                    g2.draw(area);
                }
                if (drawFront) {
                    g2.draw(front);
                }
            } else if (drawBack) {
                side = new Area(new Double(arc.getEndPoint().getX(), plotArea.getY(), arc.getStartPoint().getX() - arc.getEndPoint().getX(), plotArea.getHeight()));
                side.intersect(back);
                g2.setPaint(paint);
                g2.fill(side);
                g2.setPaint(outlinePaint);
                g2.draw(side);
            }
        }
    }

    public String getPlotType() {
        return localizationResources.getString("Pie_3D_Plot");
    }

    private boolean isAngleAtFront(double angle) {
        return Math.sin(Math.toRadians(angle)) < 0.0d;
    }

    private boolean isAngleAtBack(double angle) {
        return Math.sin(Math.toRadians(angle)) > 0.0d;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PiePlot3D)) {
            return false;
        }
        PiePlot3D that = (PiePlot3D) obj;
        if (this.depthFactor == that.depthFactor && this.darkerSides == that.darkerSides) {
            return super.equals(obj);
        }
        return false;
    }
}
