package org.jfree.data.category;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import org.jfree.chart.util.ParamChecks;
import org.jfree.chart.util.ResourceBundleWrapper;
import org.jfree.data.DataUtilities;
import org.jfree.data.UnknownKeyException;
import org.jfree.data.general.AbstractSeriesDataset;

public class DefaultIntervalCategoryDataset extends AbstractSeriesDataset implements IntervalCategoryDataset {
    private Comparable[] categoryKeys;
    private Number[][] endData;
    private Comparable[] seriesKeys;
    private Number[][] startData;

    public DefaultIntervalCategoryDataset(double[][] starts, double[][] ends) {
        this(DataUtilities.createNumberArray2D(starts), DataUtilities.createNumberArray2D(ends));
    }

    public DefaultIntervalCategoryDataset(Number[][] starts, Number[][] ends) {
        this(null, null, starts, ends);
    }

    public DefaultIntervalCategoryDataset(String[] seriesNames, Number[][] starts, Number[][] ends) {
        this(seriesNames, null, starts, ends);
    }

    public DefaultIntervalCategoryDataset(Comparable[] seriesKeys, Comparable[] categoryKeys, Number[][] starts, Number[][] ends) {
        this.startData = starts;
        this.endData = ends;
        if (starts != null && ends != null) {
            ResourceBundle resources = ResourceBundleWrapper.getBundle("org.jfree.data.resources.DataPackageResources");
            int seriesCount = starts.length;
            if (seriesCount != ends.length) {
                throw new IllegalArgumentException("DefaultIntervalCategoryDataset: the number of series in the start value dataset does not match the number of series in the end value dataset.");
            } else if (seriesCount > 0) {
                if (seriesKeys == null) {
                    this.seriesKeys = generateKeys(seriesCount, resources.getString("series.default-prefix") + " ");
                } else if (seriesKeys.length != seriesCount) {
                    throw new IllegalArgumentException("The number of series keys does not match the number of series in the data.");
                } else {
                    this.seriesKeys = seriesKeys;
                }
                int categoryCount = starts[0].length;
                if (categoryCount != ends[0].length) {
                    throw new IllegalArgumentException("DefaultIntervalCategoryDataset: the number of categories in the start value dataset does not match the number of categories in the end value dataset.");
                } else if (categoryKeys == null) {
                    this.categoryKeys = generateKeys(categoryCount, resources.getString("categories.default-prefix") + " ");
                } else if (categoryKeys.length != categoryCount) {
                    throw new IllegalArgumentException("The number of category keys does not match the number of categories in the data.");
                } else {
                    this.categoryKeys = categoryKeys;
                }
            } else {
                this.seriesKeys = new Comparable[0];
                this.categoryKeys = new Comparable[0];
            }
        }
    }

    public int getSeriesCount() {
        if (this.startData != null) {
            return this.startData.length;
        }
        return 0;
    }

    public int getSeriesIndex(Comparable seriesKey) {
        for (int i = 0; i < this.seriesKeys.length; i++) {
            if (seriesKey.equals(this.seriesKeys[i])) {
                return i;
            }
        }
        return -1;
    }

    public Comparable getSeriesKey(int series) {
        if (series < getSeriesCount() && series >= 0) {
            return this.seriesKeys[series];
        }
        throw new IllegalArgumentException("No such series : " + series);
    }

    public void setSeriesKeys(Comparable[] seriesKeys) {
        ParamChecks.nullNotPermitted(seriesKeys, "seriesKeys");
        if (seriesKeys.length != getSeriesCount()) {
            throw new IllegalArgumentException("The number of series keys does not match the data.");
        }
        this.seriesKeys = seriesKeys;
        fireDatasetChanged();
    }

    public int getCategoryCount() {
        if (this.startData == null || getSeriesCount() <= 0) {
            return 0;
        }
        return this.startData[0].length;
    }

    public List getColumnKeys() {
        if (this.categoryKeys == null) {
            return new ArrayList();
        }
        return Collections.unmodifiableList(Arrays.asList(this.categoryKeys));
    }

    public void setCategoryKeys(Comparable[] categoryKeys) {
        ParamChecks.nullNotPermitted(categoryKeys, "categoryKeys");
        if (categoryKeys.length != getCategoryCount()) {
            throw new IllegalArgumentException("The number of categories does not match the data.");
        }
        for (Comparable comparable : categoryKeys) {
            if (comparable == null) {
                throw new IllegalArgumentException("DefaultIntervalCategoryDataset.setCategoryKeys(): null category not permitted.");
            }
        }
        this.categoryKeys = categoryKeys;
        fireDatasetChanged();
    }

    public Number getValue(Comparable series, Comparable category) {
        int seriesIndex = getSeriesIndex(series);
        if (seriesIndex < 0) {
            throw new UnknownKeyException("Unknown 'series' key.");
        }
        int itemIndex = getColumnIndex(category);
        if (itemIndex >= 0) {
            return getValue(seriesIndex, itemIndex);
        }
        throw new UnknownKeyException("Unknown 'category' key.");
    }

    public Number getValue(int series, int category) {
        return getEndValue(series, category);
    }

    public Number getStartValue(Comparable series, Comparable category) {
        int seriesIndex = getSeriesIndex(series);
        if (seriesIndex < 0) {
            throw new UnknownKeyException("Unknown 'series' key.");
        }
        int itemIndex = getColumnIndex(category);
        if (itemIndex >= 0) {
            return getStartValue(seriesIndex, itemIndex);
        }
        throw new UnknownKeyException("Unknown 'category' key.");
    }

    public Number getStartValue(int series, int category) {
        if (series < 0 || series >= getSeriesCount()) {
            throw new IllegalArgumentException("DefaultIntervalCategoryDataset.getValue(): series index out of range.");
        } else if (category >= 0 && category < getCategoryCount()) {
            return this.startData[series][category];
        } else {
            throw new IllegalArgumentException("DefaultIntervalCategoryDataset.getValue(): category index out of range.");
        }
    }

    public Number getEndValue(Comparable series, Comparable category) {
        int seriesIndex = getSeriesIndex(series);
        if (seriesIndex < 0) {
            throw new UnknownKeyException("Unknown 'series' key.");
        }
        int itemIndex = getColumnIndex(category);
        if (itemIndex >= 0) {
            return getEndValue(seriesIndex, itemIndex);
        }
        throw new UnknownKeyException("Unknown 'category' key.");
    }

    public Number getEndValue(int series, int category) {
        if (series < 0 || series >= getSeriesCount()) {
            throw new IllegalArgumentException("DefaultIntervalCategoryDataset.getValue(): series index out of range.");
        } else if (category >= 0 && category < getCategoryCount()) {
            return this.endData[series][category];
        } else {
            throw new IllegalArgumentException("DefaultIntervalCategoryDataset.getValue(): category index out of range.");
        }
    }

    public void setStartValue(int series, Comparable category, Number value) {
        if (series < 0 || series > getSeriesCount() - 1) {
            throw new IllegalArgumentException("DefaultIntervalCategoryDataset.setValue: series outside valid range.");
        }
        int categoryIndex = getCategoryIndex(category);
        if (categoryIndex < 0) {
            throw new IllegalArgumentException("DefaultIntervalCategoryDataset.setValue: unrecognised category.");
        }
        this.startData[series][categoryIndex] = value;
        fireDatasetChanged();
    }

    public void setEndValue(int series, Comparable category, Number value) {
        if (series < 0 || series > getSeriesCount() - 1) {
            throw new IllegalArgumentException("DefaultIntervalCategoryDataset.setValue: series outside valid range.");
        }
        int categoryIndex = getCategoryIndex(category);
        if (categoryIndex < 0) {
            throw new IllegalArgumentException("DefaultIntervalCategoryDataset.setValue: unrecognised category.");
        }
        this.endData[series][categoryIndex] = value;
        fireDatasetChanged();
    }

    public int getCategoryIndex(Comparable category) {
        for (int i = 0; i < this.categoryKeys.length; i++) {
            if (category.equals(this.categoryKeys[i])) {
                return i;
            }
        }
        return -1;
    }

    private Comparable[] generateKeys(int count, String prefix) {
        Comparable[] result = new Comparable[count];
        for (int i = 0; i < count; i++) {
            result[i] = prefix + (i + 1);
        }
        return result;
    }

    public Comparable getColumnKey(int column) {
        return this.categoryKeys[column];
    }

    public int getColumnIndex(Comparable columnKey) {
        ParamChecks.nullNotPermitted(columnKey, "columnKey");
        return getCategoryIndex(columnKey);
    }

    public int getRowIndex(Comparable rowKey) {
        return getSeriesIndex(rowKey);
    }

    public List getRowKeys() {
        if (this.seriesKeys == null) {
            return new ArrayList();
        }
        return Collections.unmodifiableList(Arrays.asList(this.seriesKeys));
    }

    public Comparable getRowKey(int row) {
        if (row < getRowCount() && row >= 0) {
            return this.seriesKeys[row];
        }
        throw new IllegalArgumentException("The 'row' argument is out of bounds.");
    }

    public int getColumnCount() {
        return this.categoryKeys.length;
    }

    public int getRowCount() {
        return this.seriesKeys.length;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DefaultIntervalCategoryDataset)) {
            return false;
        }
        DefaultIntervalCategoryDataset that = (DefaultIntervalCategoryDataset) obj;
        if (!Arrays.equals(this.seriesKeys, that.seriesKeys)) {
            return false;
        }
        if (!Arrays.equals(this.categoryKeys, that.categoryKeys)) {
            return false;
        }
        if (!equal(this.startData, that.startData)) {
            return false;
        }
        if (equal(this.endData, that.endData)) {
            return true;
        }
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        DefaultIntervalCategoryDataset clone = (DefaultIntervalCategoryDataset) super.clone();
        clone.categoryKeys = (Comparable[]) this.categoryKeys.clone();
        clone.seriesKeys = (Comparable[]) this.seriesKeys.clone();
        clone.startData = clone(this.startData);
        clone.endData = clone(this.endData);
        return clone;
    }

    private static boolean equal(Number[][] array1, Number[][] array2) {
        boolean z = true;
        if (array1 == null) {
            if (array2 != null) {
                z = false;
            }
            return z;
        } else if (array2 == null || array1.length != array2.length) {
            return false;
        } else {
            for (int i = 0; i < array1.length; i++) {
                if (!Arrays.equals(array1[i], array2[i])) {
                    return false;
                }
            }
            return true;
        }
    }

    private static Number[][] clone(Number[][] array) {
        ParamChecks.nullNotPermitted(array, "array");
        Number[][] result = new Number[array.length][];
        for (int i = 0; i < array.length; i++) {
            Number[] child = array[i];
            Number[] copychild = new Number[child.length];
            System.arraycopy(child, 0, copychild, 0, child.length);
            result[i] = copychild;
        }
        return result;
    }

    public List getSeries() {
        if (this.seriesKeys == null) {
            return new ArrayList();
        }
        return Collections.unmodifiableList(Arrays.asList(this.seriesKeys));
    }

    public List getCategories() {
        return getColumnKeys();
    }

    public int getItemCount() {
        return this.categoryKeys.length;
    }
}
