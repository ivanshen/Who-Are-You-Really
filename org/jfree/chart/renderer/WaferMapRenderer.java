package org.jfree.chart.renderer;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Rectangle2D.Double;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.plot.DrawingSupplier;
import org.jfree.chart.plot.WaferMapPlot;
import org.jfree.data.general.WaferMapDataset;
import org.jfree.data.xy.NormalizedMatrixSeries;

public class WaferMapRenderer extends AbstractRenderer {
    private static final int DEFAULT_PAINT_LIMIT = 35;
    public static final int POSITION_INDEX = 0;
    public static final int VALUE_INDEX = 1;
    private Map paintIndex;
    private int paintIndexMethod;
    private int paintLimit;
    private WaferMapPlot plot;

    public WaferMapRenderer() {
        this(null, null);
    }

    public WaferMapRenderer(int paintLimit, int paintIndexMethod) {
        this(new Integer(paintLimit), new Integer(paintIndexMethod));
    }

    public WaferMapRenderer(Integer paintLimit, Integer paintIndexMethod) {
        this.paintIndex = new HashMap();
        if (paintLimit == null) {
            this.paintLimit = DEFAULT_PAINT_LIMIT;
        } else {
            this.paintLimit = paintLimit.intValue();
        }
        this.paintIndexMethod = VALUE_INDEX;
        if (paintIndexMethod != null && isMethodValid(paintIndexMethod.intValue())) {
            this.paintIndexMethod = paintIndexMethod.intValue();
        }
    }

    private boolean isMethodValid(int method) {
        switch (method) {
            case POSITION_INDEX /*0*/:
            case VALUE_INDEX /*1*/:
                return true;
            default:
                return false;
        }
    }

    public DrawingSupplier getDrawingSupplier() {
        WaferMapPlot p = getPlot();
        if (p != null) {
            return p.getDrawingSupplier();
        }
        return null;
    }

    public WaferMapPlot getPlot() {
        return this.plot;
    }

    public void setPlot(WaferMapPlot plot) {
        this.plot = plot;
        makePaintIndex();
    }

    public Paint getChipColor(Number value) {
        return getSeriesPaint(getPaintIndex(value));
    }

    private int getPaintIndex(Number value) {
        return ((Integer) this.paintIndex.get(value)).intValue();
    }

    private void makePaintIndex() {
        if (this.plot != null) {
            WaferMapDataset data = this.plot.getDataset();
            Number dataMin = data.getMinValue();
            Number dataMax = data.getMaxValue();
            Set<Object> uniqueValues = data.getUniqueValues();
            if (uniqueValues.size() <= this.paintLimit) {
                int count = POSITION_INDEX;
                for (Object put : uniqueValues) {
                    int count2 = count + VALUE_INDEX;
                    this.paintIndex.put(put, new Integer(count));
                    count = count2;
                }
                return;
            }
            switch (this.paintIndexMethod) {
                case POSITION_INDEX /*0*/:
                    makePositionIndex(uniqueValues);
                case VALUE_INDEX /*1*/:
                    makeValueIndex(dataMax, dataMin, uniqueValues);
                default:
            }
        }
    }

    private void makePositionIndex(Set uniqueValues) {
        int valuesPerColor = (int) Math.ceil(((double) uniqueValues.size()) / ((double) this.paintLimit));
        int count = POSITION_INDEX;
        int paint = POSITION_INDEX;
        for (Object put : uniqueValues) {
            this.paintIndex.put(put, new Integer(paint));
            count += VALUE_INDEX;
            if (count % valuesPerColor == 0) {
                paint += VALUE_INDEX;
            }
            if (paint > this.paintLimit) {
                paint = this.paintLimit;
            }
        }
    }

    private void makeValueIndex(Number max, Number min, Set uniqueValues) {
        double valueStep = (max.doubleValue() - min.doubleValue()) / ((double) this.paintLimit);
        int paint = POSITION_INDEX;
        double cutPoint = min.doubleValue() + valueStep;
        for (Number value : uniqueValues) {
            while (value.doubleValue() > cutPoint) {
                cutPoint += valueStep;
                paint += VALUE_INDEX;
                if (paint > this.paintLimit) {
                    paint = this.paintLimit;
                }
            }
            this.paintIndex.put(value, new Integer(paint));
        }
    }

    public LegendItemCollection getLegendCollection() {
        LegendItemCollection result = new LegendItemCollection();
        if (this.paintIndex != null && this.paintIndex.size() > 0) {
            String str;
            String str2;
            Shape shape;
            LegendItemCollection legendItemCollection;
            if (this.paintIndex.size() <= this.paintLimit) {
                for (Entry entry : this.paintIndex.entrySet()) {
                    String label = entry.getKey().toString();
                    String description = label;
                    Shape shape2 = new Double(NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR, NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR, NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR, NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR);
                    str = label;
                    str2 = description;
                    shape = shape2;
                    legendItemCollection = result;
                    legendItemCollection.add(new LegendItem(str, str2, null, null, shape, lookupSeriesPaint(((Integer) entry.getValue()).intValue()), DEFAULT_STROKE, Color.black));
                }
            } else {
                Set unique = new HashSet();
                for (Entry entry2 : this.paintIndex.entrySet()) {
                    if (unique.add(entry2.getValue())) {
                        str = getMinPaintValue((Integer) entry2.getValue()).toString() + " - " + getMaxPaintValue((Integer) entry2.getValue()).toString();
                        str2 = str;
                        Double doubleR = new Double(NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR, NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR, NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR, NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR);
                        shape = doubleR;
                        legendItemCollection = result;
                        legendItemCollection.add(new LegendItem(str, str2, null, null, shape, getSeriesPaint(((Integer) entry2.getValue()).intValue()), DEFAULT_STROKE, Color.black));
                    }
                }
            }
        }
        return result;
    }

    private Number getMinPaintValue(Integer index) {
        double minValue = Double.POSITIVE_INFINITY;
        for (Entry entry : this.paintIndex.entrySet()) {
            if (((Integer) entry.getValue()).equals(index) && ((Number) entry.getKey()).doubleValue() < minValue) {
                minValue = ((Number) entry.getKey()).doubleValue();
            }
        }
        return new Double(minValue);
    }

    private Number getMaxPaintValue(Integer index) {
        double maxValue = Double.NEGATIVE_INFINITY;
        for (Entry entry : this.paintIndex.entrySet()) {
            if (((Integer) entry.getValue()).equals(index) && ((Number) entry.getKey()).doubleValue() > maxValue) {
                maxValue = ((Number) entry.getKey()).doubleValue();
            }
        }
        return new Double(maxValue);
    }
}
