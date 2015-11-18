package org.jfree.data.statistics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.util.ParamChecks;

public abstract class BoxAndWhiskerCalculator {
    public static BoxAndWhiskerItem calculateBoxAndWhiskerStatistics(List values) {
        return calculateBoxAndWhiskerStatistics(values, true);
    }

    public static BoxAndWhiskerItem calculateBoxAndWhiskerStatistics(List values, boolean stripNullAndNaNItems) {
        Iterator iterator;
        ParamChecks.nullNotPermitted(values, "values");
        if (stripNullAndNaNItems) {
            List arrayList = new ArrayList(values.size());
            iterator = values.listIterator();
            while (iterator.hasNext()) {
                Number obj = iterator.next();
                if (obj instanceof Number) {
                    Number n = obj;
                    if (!Double.isNaN(n.doubleValue())) {
                        arrayList.add(n);
                    }
                }
            }
        } else {
            List vlist = values;
        }
        Collections.sort(vlist);
        double mean = Statistics.calculateMean((Collection) vlist, false);
        double median = Statistics.calculateMedian(vlist, false);
        double q1 = calculateQ1(vlist);
        double q3 = calculateQ3(vlist);
        double interQuartileRange = q3 - q1;
        double upperOutlierThreshold = q3 + (1.5d * interQuartileRange);
        double lowerOutlierThreshold = q1 - (1.5d * interQuartileRange);
        double upperFaroutThreshold = q3 + (DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS * interQuartileRange);
        double lowerFaroutThreshold = q1 - (DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS * interQuartileRange);
        double minRegularValue = Double.POSITIVE_INFINITY;
        double maxRegularValue = Double.NEGATIVE_INFINITY;
        double minOutlier = Double.POSITIVE_INFINITY;
        double maxOutlier = Double.NEGATIVE_INFINITY;
        List outliers = new ArrayList();
        for (Number number : vlist) {
            double value = number.doubleValue();
            if (value > upperOutlierThreshold) {
                outliers.add(number);
                if (value > maxOutlier && value <= upperFaroutThreshold) {
                    maxOutlier = value;
                }
            } else if (value < lowerOutlierThreshold) {
                outliers.add(number);
                if (value < minOutlier && value >= lowerFaroutThreshold) {
                    minOutlier = value;
                }
            } else {
                minRegularValue = Math.min(minRegularValue, value);
                maxRegularValue = Math.max(maxRegularValue, value);
            }
            minOutlier = Math.min(minOutlier, minRegularValue);
            maxOutlier = Math.max(maxOutlier, maxRegularValue);
        }
        return new BoxAndWhiskerItem(new Double(mean), new Double(median), new Double(q1), new Double(q3), new Double(minRegularValue), new Double(maxRegularValue), new Double(minOutlier), new Double(maxOutlier), outliers);
    }

    public static double calculateQ1(List values) {
        ParamChecks.nullNotPermitted(values, "values");
        int count = values.size();
        if (count <= 0) {
            return Double.NaN;
        }
        if (count % 2 != 1) {
            return Statistics.calculateMedian(values, 0, (count / 2) - 1);
        }
        if (count > 1) {
            return Statistics.calculateMedian(values, 0, count / 2);
        }
        return Statistics.calculateMedian(values, 0, 0);
    }

    public static double calculateQ3(List values) {
        ParamChecks.nullNotPermitted(values, "values");
        int count = values.size();
        if (count <= 0) {
            return Double.NaN;
        }
        if (count % 2 != 1) {
            return Statistics.calculateMedian(values, count / 2, count - 1);
        }
        if (count > 1) {
            return Statistics.calculateMedian(values, count / 2, count - 1);
        }
        return Statistics.calculateMedian(values, 0, 0);
    }
}
