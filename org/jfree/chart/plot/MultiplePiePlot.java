package org.jfree.chart.plot;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D.Double;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.renderer.xy.XYLine3DRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.CategoryToPieDataset;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.general.PieDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;
import org.jfree.util.ShapeUtilities;
import org.jfree.util.TableOrder;

public class MultiplePiePlot extends Plot implements Cloneable, Serializable {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final long serialVersionUID = -355377800470807389L;
    private Comparable aggregatedItemsKey;
    private transient Paint aggregatedItemsPaint;
    private TableOrder dataExtractOrder;
    private CategoryDataset dataset;
    private transient Shape legendItemShape;
    private double limit;
    private JFreeChart pieChart;
    private transient Map sectionPaints;

    static {
        $assertionsDisabled = !MultiplePiePlot.class.desiredAssertionStatus() ? true : $assertionsDisabled;
    }

    public MultiplePiePlot() {
        this(null);
    }

    public MultiplePiePlot(CategoryDataset dataset) {
        this.limit = 0.0d;
        setDataset(dataset);
        PiePlot piePlot = new PiePlot(null);
        piePlot.setIgnoreNullValues(true);
        this.pieChart = new JFreeChart(piePlot);
        this.pieChart.removeLegend();
        this.dataExtractOrder = TableOrder.BY_COLUMN;
        this.pieChart.setBackgroundPaint(null);
        TextTitle seriesTitle = new TextTitle("Series Title", new Font("SansSerif", 1, 12));
        seriesTitle.setPosition(RectangleEdge.BOTTOM);
        this.pieChart.setTitle(seriesTitle);
        this.aggregatedItemsKey = "Other";
        this.aggregatedItemsPaint = Color.lightGray;
        this.sectionPaints = new HashMap();
        this.legendItemShape = new Double(-4.0d, -4.0d, XYLine3DRenderer.DEFAULT_Y_OFFSET, XYLine3DRenderer.DEFAULT_Y_OFFSET);
    }

    public CategoryDataset getDataset() {
        return this.dataset;
    }

    public void setDataset(CategoryDataset dataset) {
        if (this.dataset != null) {
            this.dataset.removeChangeListener(this);
        }
        this.dataset = dataset;
        if (dataset != null) {
            setDatasetGroup(dataset.getGroup());
            dataset.addChangeListener(this);
        }
        datasetChanged(new DatasetChangeEvent(this, dataset));
    }

    public JFreeChart getPieChart() {
        return this.pieChart;
    }

    public void setPieChart(JFreeChart pieChart) {
        ParamChecks.nullNotPermitted(pieChart, "pieChart");
        if (pieChart.getPlot() instanceof PiePlot) {
            this.pieChart = pieChart;
            fireChangeEvent();
            return;
        }
        throw new IllegalArgumentException("The 'pieChart' argument must be a chart based on a PiePlot.");
    }

    public TableOrder getDataExtractOrder() {
        return this.dataExtractOrder;
    }

    public void setDataExtractOrder(TableOrder order) {
        ParamChecks.nullNotPermitted(order, "order");
        this.dataExtractOrder = order;
        fireChangeEvent();
    }

    public double getLimit() {
        return this.limit;
    }

    public void setLimit(double limit) {
        this.limit = limit;
        fireChangeEvent();
    }

    public Comparable getAggregatedItemsKey() {
        return this.aggregatedItemsKey;
    }

    public void setAggregatedItemsKey(Comparable key) {
        ParamChecks.nullNotPermitted(key, "key");
        this.aggregatedItemsKey = key;
        fireChangeEvent();
    }

    public Paint getAggregatedItemsPaint() {
        return this.aggregatedItemsPaint;
    }

    public void setAggregatedItemsPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.aggregatedItemsPaint = paint;
        fireChangeEvent();
    }

    public String getPlotType() {
        return "Multiple Pie Plot";
    }

    public Shape getLegendItemShape() {
        return this.legendItemShape;
    }

    public void setLegendItemShape(Shape shape) {
        ParamChecks.nullNotPermitted(shape, "shape");
        this.legendItemShape = shape;
        fireChangeEvent();
    }

    public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor, PlotState parentState, PlotRenderingInfo info) {
        getInsets().trim(area);
        drawBackground(g2, area);
        drawOutline(g2, area);
        if (DatasetUtilities.isEmptyOrNull(this.dataset)) {
            drawNoDataMessage(g2, area);
            return;
        }
        int pieCount;
        if (this.dataExtractOrder == TableOrder.BY_ROW) {
            pieCount = this.dataset.getRowCount();
        } else {
            pieCount = this.dataset.getColumnCount();
        }
        int displayCols = (int) Math.ceil(Math.sqrt((double) pieCount));
        int displayRows = (int) Math.ceil(((double) pieCount) / ((double) displayCols));
        if (displayCols > displayRows && area.getWidth() < area.getHeight()) {
            int temp = displayCols;
            displayCols = displayRows;
            displayRows = temp;
        }
        prefetchSectionPaints();
        int x = (int) area.getX();
        int y = (int) area.getY();
        int width = ((int) area.getWidth()) / displayCols;
        int height = ((int) area.getHeight()) / displayRows;
        int row = 0;
        int column = 0;
        int diff = (displayRows * displayCols) - pieCount;
        int xoffset = 0;
        Rectangle rect = new Rectangle();
        for (int pieIndex = 0; pieIndex < pieCount; pieIndex++) {
            String title;
            PieDataset piedataset;
            rect.setBounds((x + xoffset) + (width * column), (height * row) + y, width, height);
            if (this.dataExtractOrder == TableOrder.BY_ROW) {
                title = this.dataset.getRowKey(pieIndex).toString();
            } else {
                title = this.dataset.getColumnKey(pieIndex).toString();
            }
            this.pieChart.setTitle(title);
            PieDataset dd = new CategoryToPieDataset(this.dataset, this.dataExtractOrder, pieIndex);
            if (this.limit > 0.0d) {
                piedataset = DatasetUtilities.createConsolidatedPieDataset(dd, this.aggregatedItemsKey, this.limit);
            } else {
                piedataset = dd;
            }
            PiePlot piePlot = (PiePlot) this.pieChart.getPlot();
            piePlot.setDataset(piedataset);
            piePlot.setPieIndex(pieIndex);
            for (int i = 0; i < piedataset.getItemCount(); i++) {
                Paint p;
                Comparable key = piedataset.getKey(i);
                if (key.equals(this.aggregatedItemsKey)) {
                    p = this.aggregatedItemsPaint;
                } else {
                    p = (Paint) this.sectionPaints.get(key);
                }
                piePlot.setSectionPaint(key, p);
            }
            ChartRenderingInfo subinfo = null;
            if (info != null) {
                subinfo = new ChartRenderingInfo();
            }
            this.pieChart.draw(g2, rect, subinfo);
            if (info != null) {
                if ($assertionsDisabled || subinfo != null) {
                    info.getOwner().getEntityCollection().addAll(subinfo.getEntityCollection());
                    info.addSubplotInfo(subinfo.getPlotInfo());
                } else {
                    throw new AssertionError();
                }
            }
            column++;
            if (column == displayCols) {
                column = 0;
                row++;
                if (row == displayRows - 1 && diff != 0) {
                    xoffset = (diff * width) / 2;
                }
            }
        }
    }

    private void prefetchSectionPaints() {
        PiePlot piePlot = (PiePlot) getPieChart().getPlot();
        Comparable key;
        Paint p;
        if (this.dataExtractOrder == TableOrder.BY_ROW) {
            for (int c = 0; c < this.dataset.getColumnCount(); c++) {
                key = this.dataset.getColumnKey(c);
                p = piePlot.getSectionPaint(key);
                if (p == null) {
                    p = (Paint) this.sectionPaints.get(key);
                    if (p == null) {
                        p = getDrawingSupplier().getNextPaint();
                    }
                }
                this.sectionPaints.put(key, p);
            }
            return;
        }
        for (int r = 0; r < this.dataset.getRowCount(); r++) {
            key = this.dataset.getRowKey(r);
            p = piePlot.getSectionPaint(key);
            if (p == null) {
                p = (Paint) this.sectionPaints.get(key);
                if (p == null) {
                    p = getDrawingSupplier().getNextPaint();
                }
            }
            this.sectionPaints.put(key, p);
        }
    }

    public LegendItemCollection getLegendItems() {
        LegendItemCollection result = new LegendItemCollection();
        if (this.dataset != null) {
            List keys = null;
            prefetchSectionPaints();
            if (this.dataExtractOrder == TableOrder.BY_ROW) {
                keys = this.dataset.getColumnKeys();
            } else if (this.dataExtractOrder == TableOrder.BY_COLUMN) {
                keys = this.dataset.getRowKeys();
            }
            if (keys != null) {
                int section = 0;
                for (Comparable key : keys) {
                    String label = key.toString();
                    Paint paint = (Paint) this.sectionPaints.get(key);
                    LegendItem item = new LegendItem(label, label, null, null, getLegendItemShape(), paint, Plot.DEFAULT_OUTLINE_STROKE, paint);
                    item.setSeriesKey(key);
                    item.setSeriesIndex(section);
                    item.setDataset(getDataset());
                    result.add(item);
                    section++;
                }
                if (this.limit > 0.0d) {
                    LegendItemCollection legendItemCollection = result;
                    legendItemCollection.add(new LegendItem(this.aggregatedItemsKey.toString(), this.aggregatedItemsKey.toString(), null, null, getLegendItemShape(), this.aggregatedItemsPaint, Plot.DEFAULT_OUTLINE_STROKE, this.aggregatedItemsPaint));
                }
            }
        }
        return result;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof MultiplePiePlot)) {
            return $assertionsDisabled;
        }
        MultiplePiePlot that = (MultiplePiePlot) obj;
        if (this.dataExtractOrder != that.dataExtractOrder) {
            return $assertionsDisabled;
        }
        if (this.limit != that.limit) {
            return $assertionsDisabled;
        }
        if (!this.aggregatedItemsKey.equals(that.aggregatedItemsKey)) {
            return $assertionsDisabled;
        }
        if (!PaintUtilities.equal(this.aggregatedItemsPaint, that.aggregatedItemsPaint)) {
            return $assertionsDisabled;
        }
        if (!ObjectUtilities.equal(this.pieChart, that.pieChart)) {
            return $assertionsDisabled;
        }
        if (!ShapeUtilities.equal(this.legendItemShape, that.legendItemShape)) {
            return $assertionsDisabled;
        }
        if (super.equals(obj)) {
            return true;
        }
        return $assertionsDisabled;
    }

    public Object clone() throws CloneNotSupportedException {
        MultiplePiePlot clone = (MultiplePiePlot) super.clone();
        clone.pieChart = (JFreeChart) this.pieChart.clone();
        clone.sectionPaints = new HashMap(this.sectionPaints);
        clone.legendItemShape = ShapeUtilities.clone(this.legendItemShape);
        return clone;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.aggregatedItemsPaint, stream);
        SerialUtilities.writeShape(this.legendItemShape, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.aggregatedItemsPaint = SerialUtilities.readPaint(stream);
        this.legendItemShape = SerialUtilities.readShape(stream);
        this.sectionPaints = new HashMap();
    }
}
