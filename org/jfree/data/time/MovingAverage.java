package org.jfree.data.time;

import org.jfree.chart.util.ParamChecks;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class MovingAverage {
    public static TimeSeriesCollection createMovingAverage(TimeSeriesCollection source, String suffix, int periodCount, int skip) {
        ParamChecks.nullNotPermitted(source, "source");
        if (periodCount < 1) {
            throw new IllegalArgumentException("periodCount must be greater than or equal to 1.");
        }
        TimeSeriesCollection result = new TimeSeriesCollection();
        for (int i = 0; i < source.getSeriesCount(); i++) {
            TimeSeries sourceSeries = source.getSeries(i);
            result.addSeries(createMovingAverage(sourceSeries, sourceSeries.getKey() + suffix, periodCount, skip));
        }
        return result;
    }

    public static TimeSeries createMovingAverage(TimeSeries source, String name, int periodCount, int skip) {
        ParamChecks.nullNotPermitted(source, "source");
        if (periodCount < 1) {
            throw new IllegalArgumentException("periodCount must be greater than or equal to 1.");
        }
        TimeSeries result = new TimeSeries(name);
        if (source.getItemCount() > 0) {
            long firstSerial = source.getTimePeriod(0).getSerialIndex() + ((long) skip);
            for (int i = source.getItemCount() - 1; i >= 0; i--) {
                RegularTimePeriod period = source.getTimePeriod(i);
                if (period.getSerialIndex() >= firstSerial) {
                    int n = 0;
                    double sum = 0.0d;
                    long serialLimit = period.getSerialIndex() - ((long) periodCount);
                    boolean finished = false;
                    for (int offset = 0; offset < periodCount && !finished; offset++) {
                        if (i - offset >= 0) {
                            TimeSeriesDataItem item = source.getRawDataItem(i - offset);
                            RegularTimePeriod p = item.getPeriod();
                            Number v = item.getValue();
                            if (p.getSerialIndex() <= serialLimit) {
                                finished = true;
                            } else if (v != null) {
                                sum += v.doubleValue();
                                n++;
                            }
                        }
                    }
                    if (n > 0) {
                        result.add(period, sum / ((double) n));
                    } else {
                        result.add(period, null);
                    }
                }
            }
        }
        return result;
    }

    public static TimeSeries createPointMovingAverage(TimeSeries source, String name, int pointCount) {
        ParamChecks.nullNotPermitted(source, "source");
        if (pointCount < 2) {
            throw new IllegalArgumentException("periodCount must be greater than or equal to 2.");
        }
        TimeSeries result = new TimeSeries(name);
        double rollingSumForPeriod = 0.0d;
        for (int i = 0; i < source.getItemCount(); i++) {
            TimeSeriesDataItem current = source.getRawDataItem(i);
            RegularTimePeriod period = current.getPeriod();
            rollingSumForPeriod += current.getValue().doubleValue();
            if (i > pointCount - 1) {
                rollingSumForPeriod -= source.getRawDataItem(i - pointCount).getValue().doubleValue();
                result.add(period, rollingSumForPeriod / ((double) pointCount));
            } else if (i == pointCount - 1) {
                result.add(period, rollingSumForPeriod / ((double) pointCount));
            }
        }
        return result;
    }

    public static XYDataset createMovingAverage(XYDataset source, String suffix, long period, long skip) {
        return createMovingAverage(source, suffix, (double) period, (double) skip);
    }

    public static XYDataset createMovingAverage(XYDataset source, String suffix, double period, double skip) {
        ParamChecks.nullNotPermitted(source, "source");
        XYSeriesCollection result = new XYSeriesCollection();
        for (int i = 0; i < source.getSeriesCount(); i++) {
            result.addSeries(createMovingAverage(source, i, source.getSeriesKey(i) + suffix, period, skip));
        }
        return result;
    }

    public static XYSeries createMovingAverage(XYDataset source, int series, String name, double period, double skip) {
        ParamChecks.nullNotPermitted(source, "source");
        if (period < Double.MIN_VALUE) {
            throw new IllegalArgumentException("period must be positive.");
        } else if (skip < 0.0d) {
            throw new IllegalArgumentException("skip must be >= 0.0.");
        } else {
            XYSeries result = new XYSeries(name);
            if (source.getItemCount(series) > 0) {
                double first = source.getXValue(series, 0) + skip;
                for (int i = source.getItemCount(series) - 1; i >= 0; i--) {
                    double x = source.getXValue(series, i);
                    if (x >= first) {
                        int n = 0;
                        double sum = 0.0d;
                        double limit = x - period;
                        int offset = 0;
                        boolean finished = false;
                        while (!finished) {
                            if (i - offset >= 0) {
                                double xx = source.getXValue(series, i - offset);
                                Number yy = source.getY(series, i - offset);
                                if (xx <= limit) {
                                    finished = true;
                                } else if (yy != null) {
                                    sum += yy.doubleValue();
                                    n++;
                                }
                            } else {
                                finished = true;
                            }
                            offset++;
                        }
                        if (n > 0) {
                            result.add(x, sum / ((double) n));
                        } else {
                            result.add(x, null);
                        }
                    }
                }
            }
            return result;
        }
    }
}
