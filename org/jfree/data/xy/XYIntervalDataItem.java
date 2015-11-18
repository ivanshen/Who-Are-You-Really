package org.jfree.data.xy;

import org.jfree.data.ComparableObjectItem;

public class XYIntervalDataItem extends ComparableObjectItem {
    public XYIntervalDataItem(double x, double xLow, double xHigh, double y, double yLow, double yHigh) {
        super(new Double(x), new XYInterval(xLow, xHigh, y, yLow, yHigh));
    }

    public Double getX() {
        return (Double) getComparable();
    }

    public double getYValue() {
        XYInterval interval = (XYInterval) getObject();
        if (interval != null) {
            return interval.getY();
        }
        return Double.NaN;
    }

    public double getXLowValue() {
        XYInterval interval = (XYInterval) getObject();
        if (interval != null) {
            return interval.getXLow();
        }
        return Double.NaN;
    }

    public double getXHighValue() {
        XYInterval interval = (XYInterval) getObject();
        if (interval != null) {
            return interval.getXHigh();
        }
        return Double.NaN;
    }

    public double getYLowValue() {
        XYInterval interval = (XYInterval) getObject();
        if (interval != null) {
            return interval.getYLow();
        }
        return Double.NaN;
    }

    public double getYHighValue() {
        XYInterval interval = (XYInterval) getObject();
        if (interval != null) {
            return interval.getYHigh();
        }
        return Double.NaN;
    }
}
