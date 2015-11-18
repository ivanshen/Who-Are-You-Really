package org.jfree.chart.axis;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.chart.plot.ValueAxisPlot;
import org.jfree.data.Range;
import org.jfree.data.xy.NormalizedMatrixSeries;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;

public class LogarithmicAxis extends NumberAxis {
    public static final double LOG10_VALUE;
    public static final double SMALL_LOG_VALUE = 1.0E-100d;
    private static final long serialVersionUID = 2502918599004103054L;
    protected boolean allowNegativesFlag;
    protected boolean autoRangeNextLogFlag;
    protected boolean expTickLabelsFlag;
    protected boolean log10TickLabelsFlag;
    protected final NumberFormat numberFormatterObj;
    protected boolean smallLogFlag;
    protected boolean strictValuesFlag;

    static {
        LOG10_VALUE = Math.log(XYPointerAnnotation.DEFAULT_TIP_RADIUS);
    }

    public LogarithmicAxis(String label) {
        super(label);
        this.allowNegativesFlag = false;
        this.strictValuesFlag = true;
        this.numberFormatterObj = NumberFormat.getInstance();
        this.expTickLabelsFlag = false;
        this.log10TickLabelsFlag = false;
        this.autoRangeNextLogFlag = false;
        this.smallLogFlag = false;
        setupNumberFmtObj();
    }

    public void setAllowNegativesFlag(boolean flgVal) {
        this.allowNegativesFlag = flgVal;
    }

    public boolean getAllowNegativesFlag() {
        return this.allowNegativesFlag;
    }

    public void setStrictValuesFlag(boolean flgVal) {
        this.strictValuesFlag = flgVal;
    }

    public boolean getStrictValuesFlag() {
        return this.strictValuesFlag;
    }

    public void setExpTickLabelsFlag(boolean flgVal) {
        this.expTickLabelsFlag = flgVal;
        setupNumberFmtObj();
    }

    public boolean getExpTickLabelsFlag() {
        return this.expTickLabelsFlag;
    }

    public void setLog10TickLabelsFlag(boolean flag) {
        this.log10TickLabelsFlag = flag;
    }

    public boolean getLog10TickLabelsFlag() {
        return this.log10TickLabelsFlag;
    }

    public void setAutoRangeNextLogFlag(boolean flag) {
        this.autoRangeNextLogFlag = flag;
    }

    public boolean getAutoRangeNextLogFlag() {
        return this.autoRangeNextLogFlag;
    }

    public void setRange(Range range) {
        super.setRange(range);
        setupSmallLogFlag();
    }

    protected void setupSmallLogFlag() {
        double lowerVal = getRange().getLowerBound();
        boolean z = !this.allowNegativesFlag && lowerVal < XYPointerAnnotation.DEFAULT_TIP_RADIUS && lowerVal > LOG10_VALUE;
        this.smallLogFlag = z;
    }

    protected void setupNumberFmtObj() {
        if (this.numberFormatterObj instanceof DecimalFormat) {
            ((DecimalFormat) this.numberFormatterObj).applyPattern(this.expTickLabelsFlag ? "0E0" : "0.###");
        }
    }

    protected double switchedLog10(double val) {
        if (this.smallLogFlag) {
            return Math.log(val) / LOG10_VALUE;
        }
        return adjustedLog10(val);
    }

    public double switchedPow10(double val) {
        return this.smallLogFlag ? Math.pow(XYPointerAnnotation.DEFAULT_TIP_RADIUS, val) : adjustedPow10(val);
    }

    public double adjustedLog10(double val) {
        boolean negFlag = val < LOG10_VALUE;
        if (negFlag) {
            val = -val;
        }
        if (val < XYPointerAnnotation.DEFAULT_TIP_RADIUS) {
            val += (XYPointerAnnotation.DEFAULT_TIP_RADIUS - val) / XYPointerAnnotation.DEFAULT_TIP_RADIUS;
        }
        double res = Math.log(val) / LOG10_VALUE;
        return negFlag ? -res : res;
    }

    public double adjustedPow10(double val) {
        double res;
        boolean negFlag = val < LOG10_VALUE;
        if (negFlag) {
            val = -val;
        }
        if (val < NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR) {
            res = (Math.pow(XYPointerAnnotation.DEFAULT_TIP_RADIUS, val + NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR) - XYPointerAnnotation.DEFAULT_TIP_RADIUS) / 9.0d;
        } else {
            res = Math.pow(XYPointerAnnotation.DEFAULT_TIP_RADIUS, val);
        }
        return negFlag ? -res : res;
    }

    protected double computeLogFloor(double lower) {
        if (this.allowNegativesFlag) {
            if (lower > XYPointerAnnotation.DEFAULT_TIP_RADIUS) {
                return Math.pow(XYPointerAnnotation.DEFAULT_TIP_RADIUS, Math.floor(Math.log(lower) / LOG10_VALUE));
            }
            if (lower < -10.0d) {
                return -Math.pow(XYPointerAnnotation.DEFAULT_TIP_RADIUS, -Math.floor(-(Math.log(-lower) / LOG10_VALUE)));
            }
            return Math.floor(lower);
        } else if (lower > LOG10_VALUE) {
            return Math.pow(XYPointerAnnotation.DEFAULT_TIP_RADIUS, Math.floor(Math.log(lower) / LOG10_VALUE));
        } else {
            return Math.floor(lower);
        }
    }

    protected double computeLogCeil(double upper) {
        if (this.allowNegativesFlag) {
            if (upper > XYPointerAnnotation.DEFAULT_TIP_RADIUS) {
                return Math.pow(XYPointerAnnotation.DEFAULT_TIP_RADIUS, Math.ceil(Math.log(upper) / LOG10_VALUE));
            }
            if (upper < -10.0d) {
                return -Math.pow(XYPointerAnnotation.DEFAULT_TIP_RADIUS, -Math.ceil(-(Math.log(-upper) / LOG10_VALUE)));
            }
            return Math.ceil(upper);
        } else if (upper > LOG10_VALUE) {
            return Math.pow(XYPointerAnnotation.DEFAULT_TIP_RADIUS, Math.ceil(Math.log(upper) / LOG10_VALUE));
        } else {
            return Math.ceil(upper);
        }
    }

    public void autoAdjustRange() {
        Plot plot = getPlot();
        if (plot != null && (plot instanceof ValueAxisPlot)) {
            double lower;
            double logAbs;
            Range r = ((ValueAxisPlot) plot).getDataRange(this);
            if (r == null) {
                r = getDefaultAutoRange();
                lower = r.getLowerBound();
            } else {
                lower = r.getLowerBound();
                if (this.strictValuesFlag && !this.allowNegativesFlag && lower <= LOG10_VALUE) {
                    throw new RuntimeException("Values less than or equal to zero not allowed with logarithmic axis");
                }
            }
            if (lower > LOG10_VALUE) {
                double lowerMargin = getLowerMargin();
                if (lowerMargin > LOG10_VALUE) {
                    double logLower = Math.log(lower) / LOG10_VALUE;
                    logAbs = Math.abs(logLower);
                    if (logAbs < NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR) {
                        logAbs = NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR;
                    }
                    lower = Math.pow(XYPointerAnnotation.DEFAULT_TIP_RADIUS, logLower - (logAbs * lowerMargin));
                }
            }
            if (this.autoRangeNextLogFlag) {
                lower = computeLogFloor(lower);
            }
            if (!this.allowNegativesFlag && lower >= LOG10_VALUE && lower < SMALL_LOG_VALUE) {
                lower = r.getLowerBound();
            }
            double upper = r.getUpperBound();
            if (upper > LOG10_VALUE) {
                double upperMargin = getUpperMargin();
                if (upperMargin > LOG10_VALUE) {
                    double logUpper = Math.log(upper) / LOG10_VALUE;
                    logAbs = Math.abs(logUpper);
                    if (logAbs < NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR) {
                        logAbs = NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR;
                    }
                    upper = Math.pow(XYPointerAnnotation.DEFAULT_TIP_RADIUS, (logAbs * upperMargin) + logUpper);
                }
            }
            if (!this.allowNegativesFlag && upper < NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR && upper > LOG10_VALUE && lower > LOG10_VALUE) {
                double expVal = Math.pow(XYPointerAnnotation.DEFAULT_TIP_RADIUS, Math.ceil((-(Math.log(upper) / LOG10_VALUE)) + 0.001d));
                if (expVal > LOG10_VALUE) {
                    upper = Math.ceil(upper * expVal) / expVal;
                } else {
                    upper = Math.ceil(upper);
                }
            } else if (this.autoRangeNextLogFlag) {
                upper = computeLogCeil(upper);
            } else {
                upper = Math.ceil(upper);
            }
            double minRange = getAutoRangeMinimumSize();
            if (upper - lower < minRange) {
                upper = ((upper + lower) + minRange) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
                lower = ((upper + lower) - minRange) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
                if (upper - lower < minRange) {
                    double absUpper = Math.abs(upper);
                    double adjVal = absUpper > SMALL_LOG_VALUE ? absUpper / 100.0d : SpiderWebPlot.DEFAULT_HEAD;
                    upper = ((upper + lower) + adjVal) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
                    lower = ((upper + lower) - adjVal) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
                }
            }
            setRange(new Range(lower, upper), false, false);
            setupSmallLogFlag();
        }
    }

    public double valueToJava2D(double value, Rectangle2D plotArea, RectangleEdge edge) {
        Range range = getRange();
        double axisMin = switchedLog10(range.getLowerBound());
        double axisMax = switchedLog10(range.getUpperBound());
        double min = LOG10_VALUE;
        double max = LOG10_VALUE;
        if (RectangleEdge.isTopOrBottom(edge)) {
            min = plotArea.getMinX();
            max = plotArea.getMaxX();
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            min = plotArea.getMaxY();
            max = plotArea.getMinY();
        }
        value = switchedLog10(value);
        if (isInverted()) {
            return max - (((value - axisMin) / (axisMax - axisMin)) * (max - min));
        }
        return (((value - axisMin) / (axisMax - axisMin)) * (max - min)) + min;
    }

    public double java2DToValue(double java2DValue, Rectangle2D plotArea, RectangleEdge edge) {
        Range range = getRange();
        double axisMin = switchedLog10(range.getLowerBound());
        double axisMax = switchedLog10(range.getUpperBound());
        double plotMin = LOG10_VALUE;
        double plotMax = LOG10_VALUE;
        if (RectangleEdge.isTopOrBottom(edge)) {
            plotMin = plotArea.getX();
            plotMax = plotArea.getMaxX();
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            plotMin = plotArea.getMaxY();
            plotMax = plotArea.getMinY();
        }
        if (isInverted()) {
            return switchedPow10(axisMax - (((java2DValue - plotMin) / (plotMax - plotMin)) * (axisMax - axisMin)));
        }
        return switchedPow10((((java2DValue - plotMin) / (plotMax - plotMin)) * (axisMax - axisMin)) + axisMin);
    }

    public void zoomRange(double lowerPercent, double upperPercent) {
        Range adjusted;
        double startLog = switchedLog10(getRange().getLowerBound());
        double lengthLog = switchedLog10(getRange().getUpperBound()) - startLog;
        if (isInverted()) {
            adjusted = new Range(switchedPow10(((NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR - upperPercent) * lengthLog) + startLog), switchedPow10(((NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR - lowerPercent) * lengthLog) + startLog));
        } else {
            adjusted = new Range(switchedPow10((lengthLog * lowerPercent) + startLog), switchedPow10((lengthLog * upperPercent) + startLog));
        }
        setRange(adjusted);
    }

    protected List refreshTicksHorizontal(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        List ticks = new ArrayList();
        Range range = getRange();
        double lowerBoundVal = range.getLowerBound();
        if (this.smallLogFlag && lowerBoundVal < SMALL_LOG_VALUE) {
            lowerBoundVal = SMALL_LOG_VALUE;
        }
        double upperBoundVal = range.getUpperBound();
        int iBegCount = (int) Math.rint(switchedLog10(lowerBoundVal));
        int iEndCount = (int) Math.rint(switchedLog10(upperBoundVal));
        if (iBegCount == iEndCount && iBegCount > 0) {
            if (Math.pow(XYPointerAnnotation.DEFAULT_TIP_RADIUS, (double) iBegCount) > lowerBoundVal) {
                iBegCount--;
            }
        }
        boolean zeroTickFlag = false;
        int i = iBegCount;
        loop0:
        while (i <= iEndCount) {
            int j = 0;
            while (j < 10) {
                double currentTickValue;
                String tickLabel;
                if (this.smallLogFlag) {
                    currentTickValue = Math.pow(XYPointerAnnotation.DEFAULT_TIP_RADIUS, (double) i) + (Math.pow(XYPointerAnnotation.DEFAULT_TIP_RADIUS, (double) i) * ((double) j));
                    if (!this.expTickLabelsFlag && (i >= 0 || currentTickValue <= LOG10_VALUE || currentTickValue >= NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR)) {
                        tickLabel = (j < 1 || ((i < 1 && j < 5) || j < 4 - i || currentTickValue >= upperBoundVal)) ? makeTickLabel(currentTickValue) : "";
                    } else if (j == 0 || ((i > -4 && j < 2) || currentTickValue >= upperBoundVal)) {
                        this.numberFormatterObj.setMaximumFractionDigits(-i);
                        tickLabel = makeTickLabel(currentTickValue, true);
                    } else {
                        tickLabel = "";
                    }
                } else {
                    if (zeroTickFlag) {
                        j--;
                    }
                    if (i >= 0) {
                        currentTickValue = Math.pow(XYPointerAnnotation.DEFAULT_TIP_RADIUS, (double) i) + (Math.pow(XYPointerAnnotation.DEFAULT_TIP_RADIUS, (double) i) * ((double) j));
                    } else {
                        currentTickValue = -(Math.pow(XYPointerAnnotation.DEFAULT_TIP_RADIUS, (double) (-i)) - (Math.pow(XYPointerAnnotation.DEFAULT_TIP_RADIUS, (double) ((-i) - 1)) * ((double) j)));
                    }
                    if (zeroTickFlag) {
                        zeroTickFlag = false;
                    } else if (Math.abs(currentTickValue - NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR) < 1.0E-4d && lowerBoundVal <= LOG10_VALUE && upperBoundVal >= LOG10_VALUE) {
                        currentTickValue = LOG10_VALUE;
                        zeroTickFlag = true;
                    }
                    tickLabel = ((!this.expTickLabelsFlag || j >= 2) && j >= 1 && ((i >= 1 || j >= 5) && j >= 4 - i && currentTickValue < upperBoundVal)) ? "" : makeTickLabel(currentTickValue);
                }
                if (currentTickValue > upperBoundVal) {
                    break loop0;
                }
                if (currentTickValue >= lowerBoundVal - SMALL_LOG_VALUE) {
                    TextAnchor anchor;
                    TextAnchor rotationAnchor;
                    double angle = LOG10_VALUE;
                    if (isVerticalTickLabels()) {
                        anchor = TextAnchor.CENTER_RIGHT;
                        rotationAnchor = TextAnchor.CENTER_RIGHT;
                        angle = edge == RectangleEdge.TOP ? 1.5707963267948966d : -1.5707963267948966d;
                    } else if (edge == RectangleEdge.TOP) {
                        anchor = TextAnchor.BOTTOM_CENTER;
                        rotationAnchor = TextAnchor.BOTTOM_CENTER;
                    } else {
                        anchor = TextAnchor.TOP_CENTER;
                        rotationAnchor = TextAnchor.TOP_CENTER;
                    }
                    ticks.add(new NumberTick(new Double(currentTickValue), tickLabel, anchor, rotationAnchor, angle));
                }
                j++;
            }
            i++;
        }
        return ticks;
    }

    protected List refreshTicksVertical(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        List ticks = new ArrayList();
        double lowerBoundVal = getRange().getLowerBound();
        if (this.smallLogFlag && lowerBoundVal < SMALL_LOG_VALUE) {
            lowerBoundVal = SMALL_LOG_VALUE;
        }
        double upperBoundVal = getRange().getUpperBound();
        int iBegCount = (int) Math.rint(switchedLog10(lowerBoundVal));
        int iEndCount = (int) Math.rint(switchedLog10(upperBoundVal));
        if (iBegCount == iEndCount && iBegCount > 0) {
            if (Math.pow(XYPointerAnnotation.DEFAULT_TIP_RADIUS, (double) iBegCount) > lowerBoundVal) {
                iBegCount--;
            }
        }
        boolean zeroTickFlag = false;
        int i = iBegCount;
        loop0:
        while (i <= iEndCount) {
            int jEndCount = 10;
            if (i == iEndCount) {
                jEndCount = 1;
            }
            int j = 0;
            while (j < jEndCount) {
                double tickVal;
                String tickLabel;
                NumberFormat format;
                if (this.smallLogFlag) {
                    tickVal = Math.pow(XYPointerAnnotation.DEFAULT_TIP_RADIUS, (double) i) + (Math.pow(XYPointerAnnotation.DEFAULT_TIP_RADIUS, (double) i) * ((double) j));
                    if (j != 0) {
                        tickLabel = "";
                    } else if (this.log10TickLabelsFlag) {
                        tickLabel = "10^" + i;
                    } else if (this.expTickLabelsFlag) {
                        tickLabel = "1e" + i;
                    } else if (i >= 0) {
                        format = getNumberFormatOverride();
                        if (format != null) {
                            tickLabel = format.format(tickVal);
                        } else {
                            tickLabel = Long.toString((long) Math.rint(tickVal));
                        }
                    } else {
                        this.numberFormatterObj.setMaximumFractionDigits(-i);
                        tickLabel = this.numberFormatterObj.format(tickVal);
                    }
                } else {
                    if (zeroTickFlag) {
                        j--;
                    }
                    if (i >= 0) {
                        tickVal = Math.pow(XYPointerAnnotation.DEFAULT_TIP_RADIUS, (double) i) + (Math.pow(XYPointerAnnotation.DEFAULT_TIP_RADIUS, (double) i) * ((double) j));
                    } else {
                        tickVal = -(Math.pow(XYPointerAnnotation.DEFAULT_TIP_RADIUS, (double) (-i)) - (Math.pow(XYPointerAnnotation.DEFAULT_TIP_RADIUS, (double) ((-i) - 1)) * ((double) j)));
                    }
                    if (j != 0) {
                        tickLabel = "";
                        zeroTickFlag = false;
                    } else if (zeroTickFlag) {
                        tickLabel = "";
                        zeroTickFlag = false;
                    } else if (i > iBegCount && i < iEndCount && Math.abs(tickVal - NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR) < 1.0E-4d) {
                        tickVal = LOG10_VALUE;
                        zeroTickFlag = true;
                        tickLabel = "0";
                    } else if (this.log10TickLabelsFlag) {
                        tickLabel = (i < 0 ? "-" : "") + "10^" + Math.abs(i);
                    } else if (this.expTickLabelsFlag) {
                        tickLabel = (i < 0 ? "-" : "") + "1e" + Math.abs(i);
                    } else {
                        format = getNumberFormatOverride();
                        if (format != null) {
                            tickLabel = format.format(tickVal);
                        } else {
                            tickLabel = Long.toString((long) Math.rint(tickVal));
                        }
                    }
                }
                if (tickVal > upperBoundVal) {
                    break loop0;
                }
                if (tickVal >= lowerBoundVal - SMALL_LOG_VALUE) {
                    TextAnchor anchor;
                    TextAnchor rotationAnchor;
                    double angle = LOG10_VALUE;
                    if (isVerticalTickLabels()) {
                        if (edge == RectangleEdge.LEFT) {
                            anchor = TextAnchor.BOTTOM_CENTER;
                            rotationAnchor = TextAnchor.BOTTOM_CENTER;
                            angle = -1.5707963267948966d;
                        } else {
                            anchor = TextAnchor.BOTTOM_CENTER;
                            rotationAnchor = TextAnchor.BOTTOM_CENTER;
                            angle = 1.5707963267948966d;
                        }
                    } else if (edge == RectangleEdge.LEFT) {
                        anchor = TextAnchor.CENTER_RIGHT;
                        rotationAnchor = TextAnchor.CENTER_RIGHT;
                    } else {
                        anchor = TextAnchor.CENTER_LEFT;
                        rotationAnchor = TextAnchor.CENTER_LEFT;
                    }
                    ticks.add(new NumberTick(new Double(tickVal), tickLabel, anchor, rotationAnchor, angle));
                }
                j++;
            }
            i++;
        }
        return ticks;
    }

    protected String makeTickLabel(double val, boolean forceFmtFlag) {
        if (this.expTickLabelsFlag || forceFmtFlag) {
            return this.numberFormatterObj.format(val).toLowerCase();
        }
        return getTickUnit().valueToString(val);
    }

    protected String makeTickLabel(double val) {
        return makeTickLabel(val, false);
    }
}
