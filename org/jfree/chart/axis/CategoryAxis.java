package org.jfree.chart.axis;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.jfree.chart.entity.CategoryLabelEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.xy.NormalizedMatrixSeries;
import org.jfree.io.SerialUtilities;
import org.jfree.text.G2TextMeasurer;
import org.jfree.text.TextBlock;
import org.jfree.text.TextUtilities;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.Size2D;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;
import org.jfree.util.ShapeUtilities;

public class CategoryAxis extends Axis implements Cloneable, Serializable {
    public static final double DEFAULT_AXIS_MARGIN = 0.05d;
    public static final double DEFAULT_CATEGORY_MARGIN = 0.2d;
    private static final long serialVersionUID = 5886554608114265863L;
    private int categoryLabelPositionOffset;
    private CategoryLabelPositions categoryLabelPositions;
    private Map categoryLabelToolTips;
    private Map categoryLabelURLs;
    private double categoryMargin;
    private double lowerMargin;
    private int maximumCategoryLabelLines;
    private float maximumCategoryLabelWidthRatio;
    private Map tickLabelFontMap;
    private transient Map tickLabelPaintMap;
    private double upperMargin;

    public CategoryAxis() {
        this(null);
    }

    public CategoryAxis(String label) {
        super(label);
        this.lowerMargin = DEFAULT_AXIS_MARGIN;
        this.upperMargin = DEFAULT_AXIS_MARGIN;
        this.categoryMargin = DEFAULT_CATEGORY_MARGIN;
        this.maximumCategoryLabelLines = 1;
        this.maximumCategoryLabelWidthRatio = 0.0f;
        this.categoryLabelPositionOffset = 4;
        this.categoryLabelPositions = CategoryLabelPositions.STANDARD;
        this.tickLabelFontMap = new HashMap();
        this.tickLabelPaintMap = new HashMap();
        this.categoryLabelToolTips = new HashMap();
        this.categoryLabelURLs = new HashMap();
    }

    public double getLowerMargin() {
        return this.lowerMargin;
    }

    public void setLowerMargin(double margin) {
        this.lowerMargin = margin;
        fireChangeEvent();
    }

    public double getUpperMargin() {
        return this.upperMargin;
    }

    public void setUpperMargin(double margin) {
        this.upperMargin = margin;
        fireChangeEvent();
    }

    public double getCategoryMargin() {
        return this.categoryMargin;
    }

    public void setCategoryMargin(double margin) {
        this.categoryMargin = margin;
        fireChangeEvent();
    }

    public int getMaximumCategoryLabelLines() {
        return this.maximumCategoryLabelLines;
    }

    public void setMaximumCategoryLabelLines(int lines) {
        this.maximumCategoryLabelLines = lines;
        fireChangeEvent();
    }

    public float getMaximumCategoryLabelWidthRatio() {
        return this.maximumCategoryLabelWidthRatio;
    }

    public void setMaximumCategoryLabelWidthRatio(float ratio) {
        this.maximumCategoryLabelWidthRatio = ratio;
        fireChangeEvent();
    }

    public int getCategoryLabelPositionOffset() {
        return this.categoryLabelPositionOffset;
    }

    public void setCategoryLabelPositionOffset(int offset) {
        this.categoryLabelPositionOffset = offset;
        fireChangeEvent();
    }

    public CategoryLabelPositions getCategoryLabelPositions() {
        return this.categoryLabelPositions;
    }

    public void setCategoryLabelPositions(CategoryLabelPositions positions) {
        ParamChecks.nullNotPermitted(positions, "positions");
        this.categoryLabelPositions = positions;
        fireChangeEvent();
    }

    public Font getTickLabelFont(Comparable category) {
        ParamChecks.nullNotPermitted(category, "category");
        Font result = (Font) this.tickLabelFontMap.get(category);
        if (result == null) {
            return getTickLabelFont();
        }
        return result;
    }

    public void setTickLabelFont(Comparable category, Font font) {
        ParamChecks.nullNotPermitted(category, "category");
        if (font == null) {
            this.tickLabelFontMap.remove(category);
        } else {
            this.tickLabelFontMap.put(category, font);
        }
        fireChangeEvent();
    }

    public Paint getTickLabelPaint(Comparable category) {
        ParamChecks.nullNotPermitted(category, "category");
        Paint result = (Paint) this.tickLabelPaintMap.get(category);
        if (result == null) {
            return getTickLabelPaint();
        }
        return result;
    }

    public void setTickLabelPaint(Comparable category, Paint paint) {
        ParamChecks.nullNotPermitted(category, "category");
        if (paint == null) {
            this.tickLabelPaintMap.remove(category);
        } else {
            this.tickLabelPaintMap.put(category, paint);
        }
        fireChangeEvent();
    }

    public void addCategoryLabelToolTip(Comparable category, String tooltip) {
        ParamChecks.nullNotPermitted(category, "category");
        this.categoryLabelToolTips.put(category, tooltip);
        fireChangeEvent();
    }

    public String getCategoryLabelToolTip(Comparable category) {
        ParamChecks.nullNotPermitted(category, "category");
        return (String) this.categoryLabelToolTips.get(category);
    }

    public void removeCategoryLabelToolTip(Comparable category) {
        ParamChecks.nullNotPermitted(category, "category");
        if (this.categoryLabelToolTips.remove(category) != null) {
            fireChangeEvent();
        }
    }

    public void clearCategoryLabelToolTips() {
        this.categoryLabelToolTips.clear();
        fireChangeEvent();
    }

    public void addCategoryLabelURL(Comparable category, String url) {
        ParamChecks.nullNotPermitted(category, "category");
        this.categoryLabelURLs.put(category, url);
        fireChangeEvent();
    }

    public String getCategoryLabelURL(Comparable category) {
        ParamChecks.nullNotPermitted(category, "category");
        return (String) this.categoryLabelURLs.get(category);
    }

    public void removeCategoryLabelURL(Comparable category) {
        ParamChecks.nullNotPermitted(category, "category");
        if (this.categoryLabelURLs.remove(category) != null) {
            fireChangeEvent();
        }
    }

    public void clearCategoryLabelURLs() {
        this.categoryLabelURLs.clear();
        fireChangeEvent();
    }

    public double getCategoryJava2DCoordinate(CategoryAnchor anchor, int category, int categoryCount, Rectangle2D area, RectangleEdge edge) {
        if (anchor == CategoryAnchor.START) {
            return getCategoryStart(category, categoryCount, area, edge);
        }
        if (anchor == CategoryAnchor.MIDDLE) {
            return getCategoryMiddle(category, categoryCount, area, edge);
        }
        if (anchor == CategoryAnchor.END) {
            return getCategoryEnd(category, categoryCount, area, edge);
        }
        return 0.0d;
    }

    public double getCategoryStart(int category, int categoryCount, Rectangle2D area, RectangleEdge edge) {
        double result = 0.0d;
        if (edge == RectangleEdge.TOP || edge == RectangleEdge.BOTTOM) {
            result = area.getX() + (area.getWidth() * getLowerMargin());
        } else if (edge == RectangleEdge.LEFT || edge == RectangleEdge.RIGHT) {
            result = area.getMinY() + (area.getHeight() * getLowerMargin());
        }
        return result + (((double) category) * (calculateCategorySize(categoryCount, area, edge) + calculateCategoryGapSize(categoryCount, area, edge)));
    }

    public double getCategoryMiddle(int category, int categoryCount, Rectangle2D area, RectangleEdge edge) {
        if (category >= 0 && category < categoryCount) {
            return getCategoryStart(category, categoryCount, area, edge) + (calculateCategorySize(categoryCount, area, edge) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
        }
        throw new IllegalArgumentException("Invalid category index: " + category);
    }

    public double getCategoryEnd(int category, int categoryCount, Rectangle2D area, RectangleEdge edge) {
        return getCategoryStart(category, categoryCount, area, edge) + calculateCategorySize(categoryCount, area, edge);
    }

    public double getCategoryMiddle(Comparable category, List categories, Rectangle2D area, RectangleEdge edge) {
        ParamChecks.nullNotPermitted(categories, "categories");
        return getCategoryMiddle(categories.indexOf(category), categories.size(), area, edge);
    }

    public double getCategorySeriesMiddle(Comparable category, Comparable seriesKey, CategoryDataset dataset, double itemMargin, Rectangle2D area, RectangleEdge edge) {
        int categoryIndex = dataset.getColumnIndex(category);
        int categoryCount = dataset.getColumnCount();
        int seriesIndex = dataset.getRowIndex(seriesKey);
        int seriesCount = dataset.getRowCount();
        double start = getCategoryStart(categoryIndex, categoryCount, area, edge);
        double width = getCategoryEnd(categoryIndex, categoryCount, area, edge) - start;
        if (seriesCount == 1) {
            return (width / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS) + start;
        }
        double ww = ((NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR - itemMargin) * width) / ((double) seriesCount);
        return ((((double) seriesIndex) * (ww + ((width * itemMargin) / ((double) (seriesCount - 1))))) + start) + (ww / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
    }

    public double getCategorySeriesMiddle(int categoryIndex, int categoryCount, int seriesIndex, int seriesCount, double itemMargin, Rectangle2D area, RectangleEdge edge) {
        double start = getCategoryStart(categoryIndex, categoryCount, area, edge);
        double width = getCategoryEnd(categoryIndex, categoryCount, area, edge) - start;
        if (seriesCount == 1) {
            return (width / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS) + start;
        }
        double ww = ((NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR - itemMargin) * width) / ((double) seriesCount);
        return ((((double) seriesIndex) * (ww + ((width * itemMargin) / ((double) (seriesCount - 1))))) + start) + (ww / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
    }

    protected double calculateCategorySize(int categoryCount, Rectangle2D area, RectangleEdge edge) {
        double available = 0.0d;
        if (edge == RectangleEdge.TOP || edge == RectangleEdge.BOTTOM) {
            available = area.getWidth();
        } else if (edge == RectangleEdge.LEFT || edge == RectangleEdge.RIGHT) {
            available = area.getHeight();
        }
        if (categoryCount > 1) {
            return (available * (((NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR - getLowerMargin()) - getUpperMargin()) - getCategoryMargin())) / ((double) categoryCount);
        }
        return available * ((NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR - getLowerMargin()) - getUpperMargin());
    }

    protected double calculateCategoryGapSize(int categoryCount, Rectangle2D area, RectangleEdge edge) {
        double available = 0.0d;
        if (edge == RectangleEdge.TOP || edge == RectangleEdge.BOTTOM) {
            available = area.getWidth();
        } else if (edge == RectangleEdge.LEFT || edge == RectangleEdge.RIGHT) {
            available = area.getHeight();
        }
        if (categoryCount > 1) {
            return (getCategoryMargin() * available) / ((double) (categoryCount - 1));
        }
        return 0.0d;
    }

    public AxisSpace reserveSpace(Graphics2D g2, Plot plot, Rectangle2D plotArea, RectangleEdge edge, AxisSpace space) {
        if (space == null) {
            space = new AxisSpace();
        }
        if (isVisible()) {
            double tickLabelHeight = 0.0d;
            double tickLabelWidth = 0.0d;
            if (isTickLabelsVisible()) {
                g2.setFont(getTickLabelFont());
                AxisState state = new AxisState();
                refreshTicks(g2, state, plotArea, edge);
                if (edge == RectangleEdge.TOP) {
                    tickLabelHeight = state.getMax();
                } else if (edge == RectangleEdge.BOTTOM) {
                    tickLabelHeight = state.getMax();
                } else if (edge == RectangleEdge.LEFT) {
                    tickLabelWidth = state.getMax();
                } else if (edge == RectangleEdge.RIGHT) {
                    tickLabelWidth = state.getMax();
                }
            }
            Rectangle2D labelEnclosure = getLabelEnclosure(g2, edge);
            AxisSpace axisSpace;
            if (RectangleEdge.isTopOrBottom(edge)) {
                axisSpace = space;
                axisSpace.add((labelEnclosure.getHeight() + tickLabelHeight) + ((double) this.categoryLabelPositionOffset), edge);
            } else if (RectangleEdge.isLeftOrRight(edge)) {
                axisSpace = space;
                axisSpace.add((labelEnclosure.getWidth() + tickLabelWidth) + ((double) this.categoryLabelPositionOffset), edge);
            }
        }
        return space;
    }

    public void configure() {
    }

    public AxisState draw(Graphics2D g2, double cursor, Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge, PlotRenderingInfo plotState) {
        if (!isVisible()) {
            return new AxisState(cursor);
        }
        if (isAxisLineVisible()) {
            drawAxisLine(g2, cursor, dataArea, edge);
        }
        AxisState state = new AxisState(cursor);
        if (isTickMarksVisible()) {
            drawTickMarks(g2, cursor, dataArea, edge, state);
        }
        createAndAddEntity(cursor, state, dataArea, edge, plotState);
        state = drawCategoryLabels(g2, plotArea, dataArea, edge, state, plotState);
        if (getAttributedLabel() != null) {
            return drawAttributedLabel(getAttributedLabel(), g2, plotArea, dataArea, edge, state);
        }
        return drawLabel(getLabel(), g2, plotArea, dataArea, edge, state);
    }

    protected AxisState drawCategoryLabels(Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge, AxisState state, PlotRenderingInfo plotState) {
        ParamChecks.nullNotPermitted(state, "state");
        if (isTickLabelsVisible()) {
            List<CategoryTick> ticks = refreshTicks(g2, state, plotArea, edge);
            state.setTicks(ticks);
            int categoryIndex = 0;
            for (CategoryTick tick : ticks) {
                g2.setFont(getTickLabelFont(tick.getCategory()));
                g2.setPaint(getTickLabelPaint(tick.getCategory()));
                CategoryLabelPosition position = this.categoryLabelPositions.getLabelPosition(edge);
                double x0 = 0.0d;
                double x1 = 0.0d;
                double y0 = 0.0d;
                double y1 = 0.0d;
                if (edge == RectangleEdge.TOP) {
                    x0 = getCategoryStart(categoryIndex, ticks.size(), dataArea, edge);
                    x1 = getCategoryEnd(categoryIndex, ticks.size(), dataArea, edge);
                    y1 = state.getCursor() - ((double) this.categoryLabelPositionOffset);
                    y0 = y1 - state.getMax();
                } else if (edge == RectangleEdge.BOTTOM) {
                    x0 = getCategoryStart(categoryIndex, ticks.size(), dataArea, edge);
                    x1 = getCategoryEnd(categoryIndex, ticks.size(), dataArea, edge);
                    y0 = state.getCursor() + ((double) this.categoryLabelPositionOffset);
                    y1 = y0 + state.getMax();
                } else if (edge == RectangleEdge.LEFT) {
                    y0 = getCategoryStart(categoryIndex, ticks.size(), dataArea, edge);
                    y1 = getCategoryEnd(categoryIndex, ticks.size(), dataArea, edge);
                    x1 = state.getCursor() - ((double) this.categoryLabelPositionOffset);
                    x0 = x1 - state.getMax();
                } else if (edge == RectangleEdge.RIGHT) {
                    y0 = getCategoryStart(categoryIndex, ticks.size(), dataArea, edge);
                    y1 = getCategoryEnd(categoryIndex, ticks.size(), dataArea, edge);
                    x0 = state.getCursor() + ((double) this.categoryLabelPositionOffset);
                    x1 = x0 - state.getMax();
                }
                Point2D anchorPoint = RectangleAnchor.coordinates(new Double(x0, y0, x1 - x0, y1 - y0), position.getCategoryAnchor());
                TextBlock block = tick.getLabel();
                Graphics2D graphics2D = g2;
                block.draw(graphics2D, (float) anchorPoint.getX(), (float) anchorPoint.getY(), position.getLabelAnchor(), (float) anchorPoint.getX(), (float) anchorPoint.getY(), position.getAngle());
                graphics2D = g2;
                Shape bounds = block.calculateBounds(graphics2D, (float) anchorPoint.getX(), (float) anchorPoint.getY(), position.getLabelAnchor(), (float) anchorPoint.getX(), (float) anchorPoint.getY(), position.getAngle());
                if (!(plotState == null || plotState.getOwner() == null)) {
                    EntityCollection entities = plotState.getOwner().getEntityCollection();
                    if (entities != null) {
                        entities.add(new CategoryLabelEntity(tick.getCategory(), bounds, getCategoryLabelToolTip(tick.getCategory()), getCategoryLabelURL(tick.getCategory())));
                    }
                }
                categoryIndex++;
            }
            if (edge.equals(RectangleEdge.TOP)) {
                state.cursorUp(state.getMax() + ((double) this.categoryLabelPositionOffset));
            } else {
                if (edge.equals(RectangleEdge.BOTTOM)) {
                    state.cursorDown(state.getMax() + ((double) this.categoryLabelPositionOffset));
                } else if (edge == RectangleEdge.LEFT) {
                    state.cursorLeft(state.getMax() + ((double) this.categoryLabelPositionOffset));
                } else if (edge == RectangleEdge.RIGHT) {
                    state.cursorRight(state.getMax() + ((double) this.categoryLabelPositionOffset));
                }
            }
        }
        return state;
    }

    public List refreshTicks(Graphics2D g2, AxisState state, Rectangle2D dataArea, RectangleEdge edge) {
        List ticks = new ArrayList();
        if (dataArea.getHeight() > 0.0d && dataArea.getWidth() >= 0.0d) {
            List<Comparable> categories = ((CategoryPlot) getPlot()).getCategoriesForAxis(this);
            double max = 0.0d;
            if (categories != null) {
                float l;
                CategoryLabelPosition position = this.categoryLabelPositions.getLabelPosition(edge);
                float r = this.maximumCategoryLabelWidthRatio;
                if (((double) r) <= 0.0d) {
                    r = position.getWidthRatio();
                }
                if (position.getWidthType() == CategoryLabelWidthType.CATEGORY) {
                    l = (float) calculateCategorySize(categories.size(), dataArea, edge);
                } else if (RectangleEdge.isLeftOrRight(edge)) {
                    l = (float) dataArea.getWidth();
                } else {
                    l = (float) dataArea.getHeight();
                }
                int categoryIndex = 0;
                for (Comparable category : categories) {
                    g2.setFont(getTickLabelFont(category));
                    TextBlock label = createLabel(category, l * r, edge, g2);
                    if (edge == RectangleEdge.TOP || edge == RectangleEdge.BOTTOM) {
                        max = Math.max(max, calculateTextBlockHeight(label, position, g2));
                    } else if (edge == RectangleEdge.LEFT || edge == RectangleEdge.RIGHT) {
                        max = Math.max(max, calculateTextBlockWidth(label, position, g2));
                    }
                    ticks.add(new CategoryTick(category, label, position.getLabelAnchor(), position.getRotationAnchor(), position.getAngle()));
                    categoryIndex++;
                }
            }
            state.setMax(max);
        }
        return ticks;
    }

    public void drawTickMarks(Graphics2D g2, double cursor, Rectangle2D dataArea, RectangleEdge edge, AxisState state) {
        Plot p = getPlot();
        if (p != null) {
            CategoryPlot plot = (CategoryPlot) p;
            double il = (double) getTickMarkInsideLength();
            double ol = (double) getTickMarkOutsideLength();
            Line2D line = new Line2D.Double();
            List<Comparable> categories = plot.getCategoriesForAxis(this);
            g2.setPaint(getTickMarkPaint());
            g2.setStroke(getTickMarkStroke());
            Object saved = g2.getRenderingHint(RenderingHints.KEY_STROKE_CONTROL);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
            double x;
            if (edge.equals(RectangleEdge.TOP)) {
                for (Comparable categoryMiddle : categories) {
                    x = getCategoryMiddle(categoryMiddle, (List) categories, dataArea, edge);
                    line.setLine(x, cursor, x, cursor + il);
                    g2.draw(line);
                    line.setLine(x, cursor, x, cursor - ol);
                    g2.draw(line);
                }
                state.cursorUp(ol);
            } else {
                if (edge.equals(RectangleEdge.BOTTOM)) {
                    for (Comparable categoryMiddle2 : categories) {
                        x = getCategoryMiddle(categoryMiddle2, (List) categories, dataArea, edge);
                        line.setLine(x, cursor, x, cursor - il);
                        g2.draw(line);
                        line.setLine(x, cursor, x, cursor + ol);
                        g2.draw(line);
                    }
                    state.cursorDown(ol);
                } else {
                    double y;
                    if (edge.equals(RectangleEdge.LEFT)) {
                        for (Comparable categoryMiddle22 : categories) {
                            y = getCategoryMiddle(categoryMiddle22, (List) categories, dataArea, edge);
                            line.setLine(cursor, y, cursor + il, y);
                            g2.draw(line);
                            line.setLine(cursor, y, cursor - ol, y);
                            g2.draw(line);
                        }
                        state.cursorLeft(ol);
                    } else {
                        if (edge.equals(RectangleEdge.RIGHT)) {
                            for (Comparable categoryMiddle222 : categories) {
                                y = getCategoryMiddle(categoryMiddle222, (List) categories, dataArea, edge);
                                line.setLine(cursor, y, cursor - il, y);
                                g2.draw(line);
                                line.setLine(cursor, y, cursor + ol, y);
                                g2.draw(line);
                            }
                            state.cursorRight(ol);
                        }
                    }
                }
            }
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, saved);
        }
    }

    protected TextBlock createLabel(Comparable category, float width, RectangleEdge edge, Graphics2D g2) {
        return TextUtilities.createTextBlock(category.toString(), getTickLabelFont(category), getTickLabelPaint(category), width, this.maximumCategoryLabelLines, new G2TextMeasurer(g2));
    }

    protected double calculateTextBlockWidth(TextBlock block, CategoryLabelPosition position, Graphics2D g2) {
        RectangleInsets insets = getTickLabelInsets();
        Size2D size = block.calculateDimensions(g2);
        return (ShapeUtilities.rotateShape(new Double(0.0d, 0.0d, size.getWidth(), size.getHeight()), position.getAngle(), 0.0f, 0.0f).getBounds2D().getWidth() + insets.getLeft()) + insets.getRight();
    }

    protected double calculateTextBlockHeight(TextBlock block, CategoryLabelPosition position, Graphics2D g2) {
        RectangleInsets insets = getTickLabelInsets();
        Size2D size = block.calculateDimensions(g2);
        return (ShapeUtilities.rotateShape(new Double(0.0d, 0.0d, size.getWidth(), size.getHeight()), position.getAngle(), 0.0f, 0.0f).getBounds2D().getHeight() + insets.getTop()) + insets.getBottom();
    }

    public Object clone() throws CloneNotSupportedException {
        CategoryAxis clone = (CategoryAxis) super.clone();
        clone.tickLabelFontMap = new HashMap(this.tickLabelFontMap);
        clone.tickLabelPaintMap = new HashMap(this.tickLabelPaintMap);
        clone.categoryLabelToolTips = new HashMap(this.categoryLabelToolTips);
        clone.categoryLabelURLs = new HashMap(this.categoryLabelToolTips);
        return clone;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CategoryAxis)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        CategoryAxis that = (CategoryAxis) obj;
        if (that.lowerMargin != this.lowerMargin) {
            return false;
        }
        if (that.upperMargin != this.upperMargin) {
            return false;
        }
        if (that.categoryMargin != this.categoryMargin) {
            return false;
        }
        if (that.maximumCategoryLabelWidthRatio != this.maximumCategoryLabelWidthRatio) {
            return false;
        }
        if (that.categoryLabelPositionOffset != this.categoryLabelPositionOffset) {
            return false;
        }
        if (!ObjectUtilities.equal(that.categoryLabelPositions, this.categoryLabelPositions)) {
            return false;
        }
        if (!ObjectUtilities.equal(that.categoryLabelToolTips, this.categoryLabelToolTips)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.categoryLabelURLs, that.categoryLabelURLs)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.tickLabelFontMap, that.tickLabelFontMap)) {
            return false;
        }
        if (equalPaintMaps(this.tickLabelPaintMap, that.tickLabelPaintMap)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return super.hashCode();
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        writePaintMap(this.tickLabelPaintMap, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.tickLabelPaintMap = readPaintMap(stream);
    }

    private Map readPaintMap(ObjectInputStream in) throws IOException, ClassNotFoundException {
        if (in.readBoolean()) {
            return null;
        }
        Map result = new HashMap();
        int count = in.readInt();
        for (int i = 0; i < count; i++) {
            result.put((Comparable) in.readObject(), SerialUtilities.readPaint(in));
        }
        return result;
    }

    private void writePaintMap(Map map, ObjectOutputStream out) throws IOException {
        if (map == null) {
            out.writeBoolean(true);
            return;
        }
        out.writeBoolean(false);
        Set<Comparable> keys = map.keySet();
        out.writeInt(keys.size());
        for (Comparable key : keys) {
            out.writeObject(key);
            SerialUtilities.writePaint((Paint) map.get(key), out);
        }
    }

    private boolean equalPaintMaps(Map map1, Map map2) {
        if (map1.size() != map2.size()) {
            return false;
        }
        for (Entry entry : map1.entrySet()) {
            if (!PaintUtilities.equal((Paint) entry.getValue(), (Paint) map2.get(entry.getKey()))) {
                return false;
            }
        }
        return true;
    }

    protected AxisState drawCategoryLabels(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge, AxisState state, PlotRenderingInfo plotState) {
        return drawCategoryLabels(g2, dataArea, dataArea, edge, state, plotState);
    }
}
