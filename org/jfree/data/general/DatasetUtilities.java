package org.jfree.data.general;

import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.DomainInfo;
import org.jfree.data.DomainOrder;
import org.jfree.data.KeyToGroupMap;
import org.jfree.data.KeyedValues;
import org.jfree.data.Range;
import org.jfree.data.RangeInfo;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.CategoryRangeInfo;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.function.Function2D;
import org.jfree.data.statistics.BoxAndWhiskerCategoryDataset;
import org.jfree.data.statistics.BoxAndWhiskerXYDataset;
import org.jfree.data.statistics.MultiValueCategoryDataset;
import org.jfree.data.statistics.StatisticalCategoryDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.data.xy.TableXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYDomainInfo;
import org.jfree.data.xy.XYRangeInfo;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.XYZDataset;
import org.jfree.util.ArrayUtilities;

public final class DatasetUtilities {
    private DatasetUtilities() {
    }

    public static double calculatePieDatasetTotal(PieDataset dataset) {
        ParamChecks.nullNotPermitted(dataset, "dataset");
        double totalValue = 0.0d;
        for (Comparable current : dataset.getKeys()) {
            if (current != null) {
                Number value = dataset.getValue(current);
                double v = 0.0d;
                if (value != null) {
                    v = value.doubleValue();
                }
                if (v > 0.0d) {
                    totalValue += v;
                }
            }
        }
        return totalValue;
    }

    public static PieDataset createPieDatasetForRow(CategoryDataset dataset, Comparable rowKey) {
        return createPieDatasetForRow(dataset, dataset.getRowIndex(rowKey));
    }

    public static PieDataset createPieDatasetForRow(CategoryDataset dataset, int row) {
        DefaultPieDataset result = new DefaultPieDataset();
        int columnCount = dataset.getColumnCount();
        for (int current = 0; current < columnCount; current++) {
            result.setValue(dataset.getColumnKey(current), dataset.getValue(row, current));
        }
        return result;
    }

    public static PieDataset createPieDatasetForColumn(CategoryDataset dataset, Comparable columnKey) {
        return createPieDatasetForColumn(dataset, dataset.getColumnIndex(columnKey));
    }

    public static PieDataset createPieDatasetForColumn(CategoryDataset dataset, int column) {
        DefaultPieDataset result = new DefaultPieDataset();
        int rowCount = dataset.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            result.setValue(dataset.getRowKey(i), dataset.getValue(i, column));
        }
        return result;
    }

    public static PieDataset createConsolidatedPieDataset(PieDataset source, Comparable key, double minimumPercent) {
        return createConsolidatedPieDataset(source, key, minimumPercent, 2);
    }

    public static PieDataset createConsolidatedPieDataset(PieDataset source, Comparable key, double minimumPercent, int minItems) {
        DefaultPieDataset result = new DefaultPieDataset();
        double total = calculatePieDatasetTotal(source);
        List<Comparable> keys = source.getKeys();
        ArrayList otherKeys = new ArrayList();
        for (Comparable currentKey : keys) {
            Number dataValue = source.getValue(currentKey);
            if (dataValue != null && dataValue.doubleValue() / total < minimumPercent) {
                otherKeys.add(currentKey);
            }
        }
        double otherValue = 0.0d;
        for (Comparable currentKey2 : keys) {
            dataValue = source.getValue(currentKey2);
            if (dataValue != null) {
                if (!otherKeys.contains(currentKey2) || otherKeys.size() < minItems) {
                    result.setValue(currentKey2, dataValue);
                } else {
                    otherValue += dataValue.doubleValue();
                }
            }
        }
        if (otherKeys.size() >= minItems) {
            result.setValue(key, otherValue);
        }
        return result;
    }

    public static CategoryDataset createCategoryDataset(String rowKeyPrefix, String columnKeyPrefix, double[][] data) {
        DefaultCategoryDataset result = new DefaultCategoryDataset();
        for (int r = 0; r < data.length; r++) {
            Comparable rowKey = rowKeyPrefix + (r + 1);
            for (int c = 0; c < data[r].length; c++) {
                result.addValue(new Double(data[r][c]), rowKey, columnKeyPrefix + (c + 1));
            }
        }
        return result;
    }

    public static CategoryDataset createCategoryDataset(String rowKeyPrefix, String columnKeyPrefix, Number[][] data) {
        DefaultCategoryDataset result = new DefaultCategoryDataset();
        for (int r = 0; r < data.length; r++) {
            Comparable rowKey = rowKeyPrefix + (r + 1);
            for (int c = 0; c < data[r].length; c++) {
                result.addValue(data[r][c], rowKey, columnKeyPrefix + (c + 1));
            }
        }
        return result;
    }

    public static CategoryDataset createCategoryDataset(Comparable[] rowKeys, Comparable[] columnKeys, double[][] data) {
        ParamChecks.nullNotPermitted(rowKeys, "rowKeys");
        ParamChecks.nullNotPermitted(columnKeys, "columnKeys");
        if (ArrayUtilities.hasDuplicateItems(rowKeys)) {
            throw new IllegalArgumentException("Duplicate items in 'rowKeys'.");
        } else if (ArrayUtilities.hasDuplicateItems(columnKeys)) {
            throw new IllegalArgumentException("Duplicate items in 'columnKeys'.");
        } else if (rowKeys.length != data.length) {
            throw new IllegalArgumentException("The number of row keys does not match the number of rows in the data array.");
        } else {
            int r;
            int columnCount = 0;
            for (double[] length : data) {
                columnCount = Math.max(columnCount, length.length);
            }
            if (columnKeys.length != columnCount) {
                throw new IllegalArgumentException("The number of column keys does not match the number of columns in the data array.");
            }
            DefaultCategoryDataset result = new DefaultCategoryDataset();
            for (r = 0; r < data.length; r++) {
                Comparable rowKey = rowKeys[r];
                for (int c = 0; c < data[r].length; c++) {
                    result.addValue(new Double(data[r][c]), rowKey, columnKeys[c]);
                }
            }
            return result;
        }
    }

    public static CategoryDataset createCategoryDataset(Comparable rowKey, KeyedValues rowData) {
        ParamChecks.nullNotPermitted(rowKey, "rowKey");
        ParamChecks.nullNotPermitted(rowData, "rowData");
        DefaultCategoryDataset result = new DefaultCategoryDataset();
        for (int i = 0; i < rowData.getItemCount(); i++) {
            result.addValue(rowData.getValue(i), rowKey, rowData.getKey(i));
        }
        return result;
    }

    public static XYDataset sampleFunction2D(Function2D f, double start, double end, int samples, Comparable seriesKey) {
        return new XYSeriesCollection(sampleFunction2DToSeries(f, start, end, samples, seriesKey));
    }

    public static XYSeries sampleFunction2DToSeries(Function2D f, double start, double end, int samples, Comparable seriesKey) {
        ParamChecks.nullNotPermitted(f, "f");
        ParamChecks.nullNotPermitted(seriesKey, "seriesKey");
        if (start >= end) {
            throw new IllegalArgumentException("Requires 'start' < 'end'.");
        } else if (samples < 2) {
            throw new IllegalArgumentException("Requires 'samples' > 1");
        } else {
            XYSeries series = new XYSeries(seriesKey);
            double step = (end - start) / ((double) (samples - 1));
            for (int i = 0; i < samples; i++) {
                double x = start + (((double) i) * step);
                series.add(x, f.getValue(x));
            }
            return series;
        }
    }

    public static boolean isEmptyOrNull(PieDataset dataset) {
        if (dataset == null) {
            return true;
        }
        int itemCount = dataset.getItemCount();
        if (itemCount == 0) {
            return true;
        }
        for (int item = 0; item < itemCount; item++) {
            Number y = dataset.getValue(item);
            if (y != null && y.doubleValue() > 0.0d) {
                return false;
            }
        }
        return true;
    }

    public static boolean isEmptyOrNull(CategoryDataset dataset) {
        if (dataset == null) {
            return true;
        }
        int rowCount = dataset.getRowCount();
        int columnCount = dataset.getColumnCount();
        if (rowCount == 0 || columnCount == 0) {
            return true;
        }
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                if (dataset.getValue(r, c) != null) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isEmptyOrNull(XYDataset dataset) {
        if (dataset != null) {
            for (int s = 0; s < dataset.getSeriesCount(); s++) {
                if (dataset.getItemCount(s) > 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public static Range findDomainBounds(XYDataset dataset) {
        return findDomainBounds(dataset, true);
    }

    public static Range findDomainBounds(XYDataset dataset, boolean includeInterval) {
        ParamChecks.nullNotPermitted(dataset, "dataset");
        if (dataset instanceof DomainInfo) {
            return ((DomainInfo) dataset).getDomainBounds(includeInterval);
        }
        return iterateDomainBounds(dataset, includeInterval);
    }

    public static Range findDomainBounds(XYDataset dataset, List visibleSeriesKeys, boolean includeInterval) {
        ParamChecks.nullNotPermitted(dataset, "dataset");
        if (dataset instanceof XYDomainInfo) {
            return ((XYDomainInfo) dataset).getDomainBounds(visibleSeriesKeys, includeInterval);
        }
        return iterateToFindDomainBounds(dataset, visibleSeriesKeys, includeInterval);
    }

    public static Range iterateDomainBounds(XYDataset dataset) {
        return iterateDomainBounds(dataset, true);
    }

    public static Range iterateDomainBounds(XYDataset dataset, boolean includeInterval) {
        ParamChecks.nullNotPermitted(dataset, "dataset");
        double minimum = Double.POSITIVE_INFINITY;
        double maximum = Double.NEGATIVE_INFINITY;
        int seriesCount = dataset.getSeriesCount();
        int series;
        int itemCount;
        int item;
        double lvalue;
        double uvalue;
        if (includeInterval && (dataset instanceof IntervalXYDataset)) {
            IntervalXYDataset intervalXYData = (IntervalXYDataset) dataset;
            for (series = 0; series < seriesCount; series++) {
                itemCount = dataset.getItemCount(series);
                for (item = 0; item < itemCount; item++) {
                    double value = intervalXYData.getXValue(series, item);
                    lvalue = intervalXYData.getStartXValue(series, item);
                    uvalue = intervalXYData.getEndXValue(series, item);
                    if (!Double.isNaN(value)) {
                        minimum = Math.min(minimum, value);
                        maximum = Math.max(maximum, value);
                    }
                    if (!Double.isNaN(lvalue)) {
                        minimum = Math.min(minimum, lvalue);
                        maximum = Math.max(maximum, lvalue);
                    }
                    if (!Double.isNaN(uvalue)) {
                        minimum = Math.min(minimum, uvalue);
                        maximum = Math.max(maximum, uvalue);
                    }
                }
            }
        } else {
            for (series = 0; series < seriesCount; series++) {
                itemCount = dataset.getItemCount(series);
                for (item = 0; item < itemCount; item++) {
                    lvalue = dataset.getXValue(series, item);
                    uvalue = lvalue;
                    if (!Double.isNaN(lvalue)) {
                        minimum = Math.min(minimum, lvalue);
                        maximum = Math.max(maximum, uvalue);
                    }
                }
            }
        }
        if (minimum > maximum) {
            return null;
        }
        return new Range(minimum, maximum);
    }

    public static Range findRangeBounds(CategoryDataset dataset) {
        return findRangeBounds(dataset, true);
    }

    public static Range findRangeBounds(CategoryDataset dataset, boolean includeInterval) {
        ParamChecks.nullNotPermitted(dataset, "dataset");
        if (dataset instanceof RangeInfo) {
            return ((RangeInfo) dataset).getRangeBounds(includeInterval);
        }
        return iterateRangeBounds(dataset, includeInterval);
    }

    public static Range findRangeBounds(CategoryDataset dataset, List visibleSeriesKeys, boolean includeInterval) {
        ParamChecks.nullNotPermitted(dataset, "dataset");
        if (dataset instanceof CategoryRangeInfo) {
            return ((CategoryRangeInfo) dataset).getRangeBounds(visibleSeriesKeys, includeInterval);
        }
        return iterateToFindRangeBounds(dataset, visibleSeriesKeys, includeInterval);
    }

    public static Range findRangeBounds(XYDataset dataset) {
        return findRangeBounds(dataset, true);
    }

    public static Range findRangeBounds(XYDataset dataset, boolean includeInterval) {
        ParamChecks.nullNotPermitted(dataset, "dataset");
        if (dataset instanceof RangeInfo) {
            return ((RangeInfo) dataset).getRangeBounds(includeInterval);
        }
        return iterateRangeBounds(dataset, includeInterval);
    }

    public static Range findRangeBounds(XYDataset dataset, List visibleSeriesKeys, Range xRange, boolean includeInterval) {
        ParamChecks.nullNotPermitted(dataset, "dataset");
        if (dataset instanceof XYRangeInfo) {
            return ((XYRangeInfo) dataset).getRangeBounds(visibleSeriesKeys, xRange, includeInterval);
        }
        return iterateToFindRangeBounds(dataset, visibleSeriesKeys, xRange, includeInterval);
    }

    public static Range iterateCategoryRangeBounds(CategoryDataset dataset, boolean includeInterval) {
        return iterateRangeBounds(dataset, includeInterval);
    }

    public static Range iterateRangeBounds(CategoryDataset dataset) {
        return iterateRangeBounds(dataset, true);
    }

    public static Range iterateRangeBounds(CategoryDataset dataset, boolean includeInterval) {
        double minimum = Double.POSITIVE_INFINITY;
        double maximum = Double.NEGATIVE_INFINITY;
        int rowCount = dataset.getRowCount();
        int columnCount = dataset.getColumnCount();
        int row;
        int column;
        Number value;
        double v;
        if (includeInterval && (dataset instanceof IntervalCategoryDataset)) {
            IntervalCategoryDataset icd = (IntervalCategoryDataset) dataset;
            for (row = 0; row < rowCount; row++) {
                for (column = 0; column < columnCount; column++) {
                    value = icd.getValue(row, column);
                    if (value != null) {
                        v = value.doubleValue();
                        if (!Double.isNaN(v)) {
                            minimum = Math.min(v, minimum);
                            maximum = Math.max(v, maximum);
                        }
                    }
                    Number lvalue = icd.getStartValue(row, column);
                    if (lvalue != null) {
                        v = lvalue.doubleValue();
                        if (!Double.isNaN(v)) {
                            minimum = Math.min(v, minimum);
                            maximum = Math.max(v, maximum);
                        }
                    }
                    Number uvalue = icd.getEndValue(row, column);
                    if (uvalue != null) {
                        v = uvalue.doubleValue();
                        if (!Double.isNaN(v)) {
                            minimum = Math.min(v, minimum);
                            maximum = Math.max(v, maximum);
                        }
                    }
                }
            }
        } else {
            for (row = 0; row < rowCount; row++) {
                for (column = 0; column < columnCount; column++) {
                    value = dataset.getValue(row, column);
                    if (value != null) {
                        v = value.doubleValue();
                        if (!Double.isNaN(v)) {
                            minimum = Math.min(minimum, v);
                            maximum = Math.max(maximum, v);
                        }
                    }
                }
            }
        }
        if (minimum == Double.POSITIVE_INFINITY) {
            return null;
        }
        return new Range(minimum, maximum);
    }

    public static Range iterateToFindRangeBounds(CategoryDataset dataset, List visibleSeriesKeys, boolean includeInterval) {
        ParamChecks.nullNotPermitted(dataset, "dataset");
        ParamChecks.nullNotPermitted(visibleSeriesKeys, "visibleSeriesKeys");
        double minimum = Double.POSITIVE_INFINITY;
        double maximum = Double.NEGATIVE_INFINITY;
        int columnCount = dataset.getColumnCount();
        int series;
        Number lvalue;
        Number uvalue;
        if (includeInterval && (dataset instanceof BoxAndWhiskerCategoryDataset)) {
            BoxAndWhiskerCategoryDataset bx = (BoxAndWhiskerCategoryDataset) dataset;
            for (Comparable rowIndex : visibleSeriesKeys) {
                series = dataset.getRowIndex(rowIndex);
                int itemCount = dataset.getColumnCount();
                for (int item = 0; item < itemCount; item++) {
                    lvalue = bx.getMinRegularValue(series, item);
                    if (lvalue == null) {
                        lvalue = bx.getValue(series, item);
                    }
                    uvalue = bx.getMaxRegularValue(series, item);
                    if (uvalue == null) {
                        uvalue = bx.getValue(series, item);
                    }
                    if (lvalue != null) {
                        minimum = Math.min(minimum, lvalue.doubleValue());
                    }
                    if (uvalue != null) {
                        maximum = Math.max(maximum, uvalue.doubleValue());
                    }
                }
            }
        } else if (includeInterval && (dataset instanceof IntervalCategoryDataset)) {
            IntervalCategoryDataset icd = (IntervalCategoryDataset) dataset;
            for (Comparable rowIndex2 : visibleSeriesKeys) {
                series = dataset.getRowIndex(rowIndex2);
                for (column = 0; column < columnCount; column++) {
                    lvalue = icd.getStartValue(series, column);
                    uvalue = icd.getEndValue(series, column);
                    if (!(lvalue == null || Double.isNaN(lvalue.doubleValue()))) {
                        minimum = Math.min(minimum, lvalue.doubleValue());
                    }
                    if (!(uvalue == null || Double.isNaN(uvalue.doubleValue()))) {
                        maximum = Math.max(maximum, uvalue.doubleValue());
                    }
                }
            }
        } else if (includeInterval && (dataset instanceof MultiValueCategoryDataset)) {
            MultiValueCategoryDataset mvcd = (MultiValueCategoryDataset) dataset;
            for (Comparable rowIndex22 : visibleSeriesKeys) {
                series = dataset.getRowIndex(rowIndex22);
                for (column = 0; column < columnCount; column++) {
                    for (Object o : mvcd.getValues(series, column)) {
                        if (o instanceof Number) {
                            v = ((Number) o).doubleValue();
                            if (!Double.isNaN(v)) {
                                minimum = Math.min(minimum, v);
                                maximum = Math.max(maximum, v);
                            }
                        }
                    }
                }
            }
        } else if (includeInterval && (dataset instanceof StatisticalCategoryDataset)) {
            StatisticalCategoryDataset scd = (StatisticalCategoryDataset) dataset;
            for (Comparable rowIndex222 : visibleSeriesKeys) {
                series = dataset.getRowIndex(rowIndex222);
                for (column = 0; column < columnCount; column++) {
                    Number meanN = scd.getMeanValue(series, column);
                    if (meanN != null) {
                        double std = 0.0d;
                        Number stdN = scd.getStdDevValue(series, column);
                        if (stdN != null) {
                            std = stdN.doubleValue();
                            if (Double.isNaN(std)) {
                                std = 0.0d;
                            }
                        }
                        double mean = meanN.doubleValue();
                        if (!Double.isNaN(mean)) {
                            minimum = Math.min(minimum, mean - std);
                            maximum = Math.max(maximum, mean + std);
                        }
                    }
                }
            }
        } else {
            for (Comparable rowIndex2222 : visibleSeriesKeys) {
                series = dataset.getRowIndex(rowIndex2222);
                for (column = 0; column < columnCount; column++) {
                    Number value = dataset.getValue(series, column);
                    if (value != null) {
                        v = value.doubleValue();
                        if (!Double.isNaN(v)) {
                            minimum = Math.min(minimum, v);
                            maximum = Math.max(maximum, v);
                        }
                    }
                }
            }
        }
        if (minimum == Double.POSITIVE_INFINITY) {
            return null;
        }
        return new Range(minimum, maximum);
    }

    public static Range iterateXYRangeBounds(XYDataset dataset) {
        return iterateRangeBounds(dataset);
    }

    public static Range iterateRangeBounds(XYDataset dataset) {
        return iterateRangeBounds(dataset, true);
    }

    public static Range iterateRangeBounds(XYDataset dataset, boolean includeInterval) {
        double minimum = Double.POSITIVE_INFINITY;
        double maximum = Double.NEGATIVE_INFINITY;
        int seriesCount = dataset.getSeriesCount();
        int series;
        int itemCount;
        int item;
        double value;
        double lvalue;
        double uvalue;
        if (includeInterval && (dataset instanceof IntervalXYDataset)) {
            IntervalXYDataset ixyd = (IntervalXYDataset) dataset;
            for (series = 0; series < seriesCount; series++) {
                itemCount = dataset.getItemCount(series);
                for (item = 0; item < itemCount; item++) {
                    value = ixyd.getYValue(series, item);
                    lvalue = ixyd.getStartYValue(series, item);
                    uvalue = ixyd.getEndYValue(series, item);
                    if (!Double.isNaN(value)) {
                        minimum = Math.min(minimum, value);
                        maximum = Math.max(maximum, value);
                    }
                    if (!Double.isNaN(lvalue)) {
                        minimum = Math.min(minimum, lvalue);
                        maximum = Math.max(maximum, lvalue);
                    }
                    if (!Double.isNaN(uvalue)) {
                        minimum = Math.min(minimum, uvalue);
                        maximum = Math.max(maximum, uvalue);
                    }
                }
            }
        } else if (includeInterval && (dataset instanceof OHLCDataset)) {
            OHLCDataset ohlc = (OHLCDataset) dataset;
            for (series = 0; series < seriesCount; series++) {
                itemCount = dataset.getItemCount(series);
                for (item = 0; item < itemCount; item++) {
                    lvalue = ohlc.getLowValue(series, item);
                    uvalue = ohlc.getHighValue(series, item);
                    if (!Double.isNaN(lvalue)) {
                        minimum = Math.min(minimum, lvalue);
                    }
                    if (!Double.isNaN(uvalue)) {
                        maximum = Math.max(maximum, uvalue);
                    }
                }
            }
        } else {
            for (series = 0; series < seriesCount; series++) {
                itemCount = dataset.getItemCount(series);
                for (item = 0; item < itemCount; item++) {
                    value = dataset.getYValue(series, item);
                    if (!Double.isNaN(value)) {
                        minimum = Math.min(minimum, value);
                        maximum = Math.max(maximum, value);
                    }
                }
            }
        }
        if (minimum == Double.POSITIVE_INFINITY) {
            return null;
        }
        return new Range(minimum, maximum);
    }

    public static Range findZBounds(XYZDataset dataset) {
        return findZBounds(dataset, true);
    }

    public static Range findZBounds(XYZDataset dataset, boolean includeInterval) {
        ParamChecks.nullNotPermitted(dataset, "dataset");
        return iterateZBounds(dataset, includeInterval);
    }

    public static Range findZBounds(XYZDataset dataset, List visibleSeriesKeys, Range xRange, boolean includeInterval) {
        ParamChecks.nullNotPermitted(dataset, "dataset");
        return iterateToFindZBounds(dataset, visibleSeriesKeys, xRange, includeInterval);
    }

    public static Range iterateZBounds(XYZDataset dataset) {
        return iterateZBounds(dataset, true);
    }

    public static Range iterateZBounds(XYZDataset dataset, boolean includeInterval) {
        double minimum = Double.POSITIVE_INFINITY;
        double maximum = Double.NEGATIVE_INFINITY;
        int seriesCount = dataset.getSeriesCount();
        for (int series = 0; series < seriesCount; series++) {
            int itemCount = dataset.getItemCount(series);
            for (int item = 0; item < itemCount; item++) {
                double value = dataset.getZValue(series, item);
                if (!Double.isNaN(value)) {
                    minimum = Math.min(minimum, value);
                    maximum = Math.max(maximum, value);
                }
            }
        }
        if (minimum == Double.POSITIVE_INFINITY) {
            return null;
        }
        return new Range(minimum, maximum);
    }

    public static Range iterateToFindDomainBounds(XYDataset dataset, List visibleSeriesKeys, boolean includeInterval) {
        ParamChecks.nullNotPermitted(dataset, "dataset");
        ParamChecks.nullNotPermitted(visibleSeriesKeys, "visibleSeriesKeys");
        double minimum = Double.POSITIVE_INFINITY;
        double maximum = Double.NEGATIVE_INFINITY;
        int series;
        int itemCount;
        int item;
        if (includeInterval && (dataset instanceof IntervalXYDataset)) {
            IntervalXYDataset ixyd = (IntervalXYDataset) dataset;
            for (Comparable seriesKey : visibleSeriesKeys) {
                series = dataset.indexOf(seriesKey);
                itemCount = dataset.getItemCount(series);
                for (item = 0; item < itemCount; item++) {
                    double lvalue = ixyd.getStartXValue(series, item);
                    double uvalue = ixyd.getEndXValue(series, item);
                    if (!Double.isNaN(lvalue)) {
                        minimum = Math.min(minimum, lvalue);
                    }
                    if (!Double.isNaN(uvalue)) {
                        maximum = Math.max(maximum, uvalue);
                    }
                }
            }
        } else {
            for (Comparable seriesKey2 : visibleSeriesKeys) {
                series = dataset.indexOf(seriesKey2);
                itemCount = dataset.getItemCount(series);
                for (item = 0; item < itemCount; item++) {
                    double x = dataset.getXValue(series, item);
                    if (!Double.isNaN(x)) {
                        minimum = Math.min(minimum, x);
                        maximum = Math.max(maximum, x);
                    }
                }
            }
        }
        if (minimum == Double.POSITIVE_INFINITY) {
            return null;
        }
        return new Range(minimum, maximum);
    }

    public static Range iterateToFindRangeBounds(XYDataset dataset, List visibleSeriesKeys, Range xRange, boolean includeInterval) {
        ParamChecks.nullNotPermitted(dataset, "dataset");
        ParamChecks.nullNotPermitted(visibleSeriesKeys, "visibleSeriesKeys");
        ParamChecks.nullNotPermitted(xRange, "xRange");
        double minimum = Double.POSITIVE_INFINITY;
        double maximum = Double.NEGATIVE_INFINITY;
        int series;
        int itemCount;
        int item;
        double lvalue;
        double uvalue;
        if (includeInterval && (dataset instanceof OHLCDataset)) {
            OHLCDataset ohlc = (OHLCDataset) dataset;
            for (Comparable indexOf : visibleSeriesKeys) {
                series = dataset.indexOf(indexOf);
                itemCount = dataset.getItemCount(series);
                for (item = 0; item < itemCount; item++) {
                    if (xRange.contains(ohlc.getXValue(series, item))) {
                        lvalue = ohlc.getLowValue(series, item);
                        uvalue = ohlc.getHighValue(series, item);
                        if (!Double.isNaN(lvalue)) {
                            minimum = Math.min(minimum, lvalue);
                        }
                        if (!Double.isNaN(uvalue)) {
                            maximum = Math.max(maximum, uvalue);
                        }
                    }
                }
            }
        } else if (includeInterval && (dataset instanceof BoxAndWhiskerXYDataset)) {
            BoxAndWhiskerXYDataset bx = (BoxAndWhiskerXYDataset) dataset;
            for (Comparable indexOf2 : visibleSeriesKeys) {
                series = dataset.indexOf(indexOf2);
                itemCount = dataset.getItemCount(series);
                for (item = 0; item < itemCount; item++) {
                    if (xRange.contains(bx.getXValue(series, item))) {
                        Number lvalue2 = bx.getMinRegularValue(series, item);
                        Number uvalue2 = bx.getMaxRegularValue(series, item);
                        if (lvalue2 != null) {
                            minimum = Math.min(minimum, lvalue2.doubleValue());
                        }
                        if (uvalue2 != null) {
                            maximum = Math.max(maximum, uvalue2.doubleValue());
                        }
                    }
                }
            }
        } else if (includeInterval && (dataset instanceof IntervalXYDataset)) {
            IntervalXYDataset ixyd = (IntervalXYDataset) dataset;
            for (Comparable indexOf22 : visibleSeriesKeys) {
                series = dataset.indexOf(indexOf22);
                itemCount = dataset.getItemCount(series);
                for (item = 0; item < itemCount; item++) {
                    if (xRange.contains(ixyd.getXValue(series, item))) {
                        lvalue = ixyd.getStartYValue(series, item);
                        uvalue = ixyd.getEndYValue(series, item);
                        if (!Double.isNaN(lvalue)) {
                            minimum = Math.min(minimum, lvalue);
                        }
                        if (!Double.isNaN(uvalue)) {
                            maximum = Math.max(maximum, uvalue);
                        }
                    }
                }
            }
        } else {
            for (Comparable indexOf222 : visibleSeriesKeys) {
                series = dataset.indexOf(indexOf222);
                itemCount = dataset.getItemCount(series);
                for (item = 0; item < itemCount; item++) {
                    double x = dataset.getXValue(series, item);
                    double y = dataset.getYValue(series, item);
                    if (xRange.contains(x) && !Double.isNaN(y)) {
                        minimum = Math.min(minimum, y);
                        maximum = Math.max(maximum, y);
                    }
                }
            }
        }
        if (minimum == Double.POSITIVE_INFINITY) {
            return null;
        }
        return new Range(minimum, maximum);
    }

    public static Range iterateToFindZBounds(XYZDataset dataset, List visibleSeriesKeys, Range xRange, boolean includeInterval) {
        ParamChecks.nullNotPermitted(dataset, "dataset");
        ParamChecks.nullNotPermitted(visibleSeriesKeys, "visibleSeriesKeys");
        ParamChecks.nullNotPermitted(xRange, "xRange");
        double minimum = Double.POSITIVE_INFINITY;
        double maximum = Double.NEGATIVE_INFINITY;
        for (Comparable seriesKey : visibleSeriesKeys) {
            int series = dataset.indexOf(seriesKey);
            int itemCount = dataset.getItemCount(series);
            for (int item = 0; item < itemCount; item++) {
                double x = dataset.getXValue(series, item);
                double z = dataset.getZValue(series, item);
                if (xRange.contains(x) && !Double.isNaN(z)) {
                    minimum = Math.min(minimum, z);
                    maximum = Math.max(maximum, z);
                }
            }
        }
        if (minimum == Double.POSITIVE_INFINITY) {
            return null;
        }
        return new Range(minimum, maximum);
    }

    public static Number findMinimumDomainValue(XYDataset dataset) {
        ParamChecks.nullNotPermitted(dataset, "dataset");
        if (dataset instanceof DomainInfo) {
            return new Double(((DomainInfo) dataset).getDomainLowerBound(true));
        }
        double minimum = Double.POSITIVE_INFINITY;
        int seriesCount = dataset.getSeriesCount();
        for (int series = 0; series < seriesCount; series++) {
            int itemCount = dataset.getItemCount(series);
            for (int item = 0; item < itemCount; item++) {
                double value;
                if (dataset instanceof IntervalXYDataset) {
                    value = ((IntervalXYDataset) dataset).getStartXValue(series, item);
                } else {
                    value = dataset.getXValue(series, item);
                }
                if (!Double.isNaN(value)) {
                    minimum = Math.min(minimum, value);
                }
            }
        }
        if (minimum == Double.POSITIVE_INFINITY) {
            return null;
        }
        return new Double(minimum);
    }

    public static Number findMaximumDomainValue(XYDataset dataset) {
        ParamChecks.nullNotPermitted(dataset, "dataset");
        if (dataset instanceof DomainInfo) {
            return new Double(((DomainInfo) dataset).getDomainUpperBound(true));
        }
        double maximum = Double.NEGATIVE_INFINITY;
        int seriesCount = dataset.getSeriesCount();
        for (int series = 0; series < seriesCount; series++) {
            int itemCount = dataset.getItemCount(series);
            for (int item = 0; item < itemCount; item++) {
                double value;
                if (dataset instanceof IntervalXYDataset) {
                    value = ((IntervalXYDataset) dataset).getEndXValue(series, item);
                } else {
                    value = dataset.getXValue(series, item);
                }
                if (!Double.isNaN(value)) {
                    maximum = Math.max(maximum, value);
                }
            }
        }
        if (maximum == Double.NEGATIVE_INFINITY) {
            return null;
        }
        return new Double(maximum);
    }

    public static Number findMinimumRangeValue(CategoryDataset dataset) {
        ParamChecks.nullNotPermitted(dataset, "dataset");
        if (dataset instanceof RangeInfo) {
            return new Double(((RangeInfo) dataset).getRangeLowerBound(true));
        }
        double minimum = Double.POSITIVE_INFINITY;
        int seriesCount = dataset.getRowCount();
        int itemCount = dataset.getColumnCount();
        for (int series = 0; series < seriesCount; series++) {
            for (int item = 0; item < itemCount; item++) {
                Number value;
                if (dataset instanceof IntervalCategoryDataset) {
                    value = ((IntervalCategoryDataset) dataset).getStartValue(series, item);
                } else {
                    value = dataset.getValue(series, item);
                }
                if (value != null) {
                    minimum = Math.min(minimum, value.doubleValue());
                }
            }
        }
        if (minimum == Double.POSITIVE_INFINITY) {
            return null;
        }
        return new Double(minimum);
    }

    public static Number findMinimumRangeValue(XYDataset dataset) {
        ParamChecks.nullNotPermitted(dataset, "dataset");
        if (dataset instanceof RangeInfo) {
            return new Double(((RangeInfo) dataset).getRangeLowerBound(true));
        }
        double minimum = Double.POSITIVE_INFINITY;
        int seriesCount = dataset.getSeriesCount();
        for (int series = 0; series < seriesCount; series++) {
            int itemCount = dataset.getItemCount(series);
            for (int item = 0; item < itemCount; item++) {
                double value;
                if (dataset instanceof IntervalXYDataset) {
                    value = ((IntervalXYDataset) dataset).getStartYValue(series, item);
                } else if (dataset instanceof OHLCDataset) {
                    value = ((OHLCDataset) dataset).getLowValue(series, item);
                } else {
                    value = dataset.getYValue(series, item);
                }
                if (!Double.isNaN(value)) {
                    minimum = Math.min(minimum, value);
                }
            }
        }
        if (minimum == Double.POSITIVE_INFINITY) {
            return null;
        }
        return new Double(minimum);
    }

    public static Number findMaximumRangeValue(CategoryDataset dataset) {
        ParamChecks.nullNotPermitted(dataset, "dataset");
        if (dataset instanceof RangeInfo) {
            return new Double(((RangeInfo) dataset).getRangeUpperBound(true));
        }
        double maximum = Double.NEGATIVE_INFINITY;
        int seriesCount = dataset.getRowCount();
        int itemCount = dataset.getColumnCount();
        for (int series = 0; series < seriesCount; series++) {
            for (int item = 0; item < itemCount; item++) {
                Number value;
                if (dataset instanceof IntervalCategoryDataset) {
                    value = ((IntervalCategoryDataset) dataset).getEndValue(series, item);
                } else {
                    value = dataset.getValue(series, item);
                }
                if (value != null) {
                    maximum = Math.max(maximum, value.doubleValue());
                }
            }
        }
        if (maximum == Double.NEGATIVE_INFINITY) {
            return null;
        }
        return new Double(maximum);
    }

    public static Number findMaximumRangeValue(XYDataset dataset) {
        ParamChecks.nullNotPermitted(dataset, "dataset");
        if (dataset instanceof RangeInfo) {
            return new Double(((RangeInfo) dataset).getRangeUpperBound(true));
        }
        double maximum = Double.NEGATIVE_INFINITY;
        int seriesCount = dataset.getSeriesCount();
        for (int series = 0; series < seriesCount; series++) {
            int itemCount = dataset.getItemCount(series);
            for (int item = 0; item < itemCount; item++) {
                double value;
                if (dataset instanceof IntervalXYDataset) {
                    value = ((IntervalXYDataset) dataset).getEndYValue(series, item);
                } else if (dataset instanceof OHLCDataset) {
                    value = ((OHLCDataset) dataset).getHighValue(series, item);
                } else {
                    value = dataset.getYValue(series, item);
                }
                if (!Double.isNaN(value)) {
                    maximum = Math.max(maximum, value);
                }
            }
        }
        if (maximum == Double.NEGATIVE_INFINITY) {
            return null;
        }
        return new Double(maximum);
    }

    public static Range findStackedRangeBounds(CategoryDataset dataset) {
        return findStackedRangeBounds(dataset, 0.0d);
    }

    public static Range findStackedRangeBounds(CategoryDataset dataset, double base) {
        ParamChecks.nullNotPermitted(dataset, "dataset");
        double minimum = Double.POSITIVE_INFINITY;
        double maximum = Double.NEGATIVE_INFINITY;
        int categoryCount = dataset.getColumnCount();
        for (int item = 0; item < categoryCount; item++) {
            double positive = base;
            double negative = base;
            int seriesCount = dataset.getRowCount();
            for (int series = 0; series < seriesCount; series++) {
                Number number = dataset.getValue(series, item);
                if (number != null) {
                    double value = number.doubleValue();
                    if (value > 0.0d) {
                        positive += value;
                    }
                    if (value < 0.0d) {
                        negative += value;
                    }
                }
            }
            minimum = Math.min(minimum, negative);
            maximum = Math.max(maximum, positive);
        }
        if (minimum <= maximum) {
            return new Range(minimum, maximum);
        }
        return null;
    }

    public static Range findStackedRangeBounds(CategoryDataset dataset, KeyToGroupMap map) {
        ParamChecks.nullNotPermitted(dataset, "dataset");
        boolean hasValidData = false;
        Range result = null;
        int[] groupIndex = new int[dataset.getRowCount()];
        for (int i = 0; i < dataset.getRowCount(); i++) {
            groupIndex[i] = map.getGroupIndex(map.getGroup(dataset.getRowKey(i)));
        }
        int groupCount = map.getGroupCount();
        double[] minimum = new double[groupCount];
        double[] maximum = new double[groupCount];
        int categoryCount = dataset.getColumnCount();
        for (int item = 0; item < categoryCount; item++) {
            double[] positive = new double[groupCount];
            double[] negative = new double[groupCount];
            int seriesCount = dataset.getRowCount();
            for (int series = 0; series < seriesCount; series++) {
                Number number = dataset.getValue(series, item);
                if (number != null) {
                    hasValidData = true;
                    double value = number.doubleValue();
                    if (value > 0.0d) {
                        positive[groupIndex[series]] = positive[groupIndex[series]] + value;
                    }
                    if (value < 0.0d) {
                        negative[groupIndex[series]] = negative[groupIndex[series]] + value;
                    }
                }
            }
            for (int g = 0; g < groupCount; g++) {
                minimum[g] = Math.min(minimum[g], negative[g]);
                maximum[g] = Math.max(maximum[g], positive[g]);
            }
        }
        if (hasValidData) {
            for (int j = 0; j < groupCount; j++) {
                result = Range.combine(result, new Range(minimum[j], maximum[j]));
            }
        }
        return result;
    }

    public static Number findMinimumStackedRangeValue(CategoryDataset dataset) {
        ParamChecks.nullNotPermitted(dataset, "dataset");
        boolean hasValidData = false;
        double minimum = 0.0d;
        int categoryCount = dataset.getColumnCount();
        for (int item = 0; item < categoryCount; item++) {
            double total = 0.0d;
            int seriesCount = dataset.getRowCount();
            for (int series = 0; series < seriesCount; series++) {
                Number number = dataset.getValue(series, item);
                if (number != null) {
                    hasValidData = true;
                    double value = number.doubleValue();
                    if (value < 0.0d) {
                        total += value;
                    }
                }
            }
            minimum = Math.min(minimum, total);
        }
        if (hasValidData) {
            return new Double(minimum);
        }
        return null;
    }

    public static Number findMaximumStackedRangeValue(CategoryDataset dataset) {
        ParamChecks.nullNotPermitted(dataset, "dataset");
        boolean hasValidData = false;
        double maximum = 0.0d;
        int categoryCount = dataset.getColumnCount();
        for (int item = 0; item < categoryCount; item++) {
            double total = 0.0d;
            int seriesCount = dataset.getRowCount();
            for (int series = 0; series < seriesCount; series++) {
                Number number = dataset.getValue(series, item);
                if (number != null) {
                    hasValidData = true;
                    double value = number.doubleValue();
                    if (value > 0.0d) {
                        total += value;
                    }
                }
            }
            maximum = Math.max(maximum, total);
        }
        if (hasValidData) {
            return new Double(maximum);
        }
        return null;
    }

    public static Range findStackedRangeBounds(TableXYDataset dataset) {
        return findStackedRangeBounds(dataset, 0.0d);
    }

    public static Range findStackedRangeBounds(TableXYDataset dataset, double base) {
        ParamChecks.nullNotPermitted(dataset, "dataset");
        double minimum = base;
        double maximum = base;
        for (int itemNo = 0; itemNo < dataset.getItemCount(); itemNo++) {
            double positive = base;
            double negative = base;
            int seriesCount = dataset.getSeriesCount();
            for (int seriesNo = 0; seriesNo < seriesCount; seriesNo++) {
                double y = dataset.getYValue(seriesNo, itemNo);
                if (!Double.isNaN(y)) {
                    if (y > 0.0d) {
                        positive += y;
                    } else {
                        negative += y;
                    }
                }
            }
            if (positive > maximum) {
                maximum = positive;
            }
            if (negative < minimum) {
                minimum = negative;
            }
        }
        if (minimum <= maximum) {
            return new Range(minimum, maximum);
        }
        return null;
    }

    public static double calculateStackTotal(TableXYDataset dataset, int item) {
        double total = 0.0d;
        int seriesCount = dataset.getSeriesCount();
        for (int s = 0; s < seriesCount; s++) {
            double value = dataset.getYValue(s, item);
            if (!Double.isNaN(value)) {
                total += value;
            }
        }
        return total;
    }

    public static Range findCumulativeRangeBounds(CategoryDataset dataset) {
        ParamChecks.nullNotPermitted(dataset, "dataset");
        boolean allItemsNull = true;
        double minimum = 0.0d;
        double maximum = 0.0d;
        for (int row = 0; row < dataset.getRowCount(); row++) {
            double runningTotal = 0.0d;
            for (int column = 0; column <= dataset.getColumnCount() - 1; column++) {
                Number n = dataset.getValue(row, column);
                if (n != null) {
                    allItemsNull = false;
                    double value = n.doubleValue();
                    if (!Double.isNaN(value)) {
                        runningTotal += value;
                        minimum = Math.min(minimum, runningTotal);
                        maximum = Math.max(maximum, runningTotal);
                    }
                }
            }
        }
        if (allItemsNull) {
            return null;
        }
        return new Range(minimum, maximum);
    }

    public static double findYValue(XYDataset dataset, int series, double x) {
        int[] indices = findItemIndicesForX(dataset, series, x);
        if (indices[0] == -1) {
            return Double.NaN;
        }
        if (indices[0] == indices[1]) {
            return dataset.getYValue(series, indices[0]);
        }
        double x0 = dataset.getXValue(series, indices[0]);
        double x1 = dataset.getXValue(series, indices[1]);
        double y0 = dataset.getYValue(series, indices[0]);
        return (((dataset.getYValue(series, indices[1]) - y0) * (x - x0)) / (x1 - x0)) + y0;
    }

    public static int[] findItemIndicesForX(XYDataset dataset, int series, double x) {
        ParamChecks.nullNotPermitted(dataset, "dataset");
        int itemCount = dataset.getItemCount(series);
        if (itemCount == 0) {
            return new int[]{-1, -1};
        }
        if (itemCount == 1) {
            if (x == dataset.getXValue(series, 0)) {
                return new int[]{0, 0};
            }
            return new int[]{-1, -1};
        } else if (dataset.getDomainOrder() == DomainOrder.ASCENDING) {
            low = 0;
            high = itemCount - 1;
            double lowValue = dataset.getXValue(series, 0);
            if (lowValue > x) {
                return new int[]{-1, -1};
            }
            if (lowValue == x) {
                return new int[]{0, 0};
            }
            double highValue = dataset.getXValue(series, high);
            if (highValue < x) {
                return new int[]{-1, -1};
            }
            if (highValue == x) {
                return new int[]{high, high};
            }
            mid = (0 + high) / 2;
            while (high - low > 1) {
                midV = dataset.getXValue(series, mid);
                if (x == midV) {
                    return new int[]{mid, mid};
                }
                if (midV < x) {
                    low = mid;
                } else {
                    high = mid;
                }
                mid = (low + high) / 2;
            }
            return new int[]{low, high};
        } else if (dataset.getDomainOrder() == DomainOrder.DESCENDING) {
            high = 0;
            low = itemCount - 1;
            if (dataset.getXValue(series, low) > x) {
                return new int[]{-1, -1};
            }
            if (dataset.getXValue(series, 0) < x) {
                return new int[]{-1, -1};
            }
            mid = (low + 0) / 2;
            while (high - low > 1) {
                midV = dataset.getXValue(series, mid);
                if (x == midV) {
                    return new int[]{mid, mid};
                }
                if (midV < x) {
                    low = mid;
                } else {
                    high = mid;
                }
                mid = (low + high) / 2;
            }
            return new int[]{low, high};
        } else {
            double prev = dataset.getXValue(series, 0);
            if (x == prev) {
                return new int[]{0, 0};
            }
            int i = 1;
            while (i < itemCount) {
                double next = dataset.getXValue(series, i);
                if (x == next) {
                    return new int[]{i, i};
                } else if ((x <= prev || x >= next) && (x >= prev || x <= next)) {
                    i++;
                } else {
                    return new int[]{i - 1, i};
                }
            }
            return new int[]{-1, -1};
        }
    }
}
