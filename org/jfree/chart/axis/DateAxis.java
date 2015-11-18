package org.jfree.chart.axis;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.ValueAxisPlot;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.Range;
import org.jfree.data.time.DateRange;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.Month;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.Year;
import org.jfree.data.xy.NormalizedMatrixSeries;
import org.jfree.ui.Align;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;
import org.jfree.util.LogTarget;
import org.jfree.util.ObjectUtilities;

public class DateAxis extends ValueAxis implements Cloneable, Serializable {
    public static final Date DEFAULT_ANCHOR_DATE;
    public static final double DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS = 2.0d;
    public static final DateRange DEFAULT_DATE_RANGE;
    public static final DateTickUnit DEFAULT_DATE_TICK_UNIT;
    private static final Timeline DEFAULT_TIMELINE;
    private static final long serialVersionUID = -1013460999649007604L;
    private DateFormat dateFormatOverride;
    private Locale locale;
    private DateTickMarkPosition tickMarkPosition;
    private DateTickUnit tickUnit;
    private TimeZone timeZone;
    private Timeline timeline;

    private static class DefaultTimeline implements Timeline, Serializable {
        private DefaultTimeline() {
        }

        public long toTimelineValue(long millisecond) {
            return millisecond;
        }

        public long toTimelineValue(Date date) {
            return date.getTime();
        }

        public long toMillisecond(long value) {
            return value;
        }

        public boolean containsDomainValue(long millisecond) {
            return true;
        }

        public boolean containsDomainValue(Date date) {
            return true;
        }

        public boolean containsDomainRange(long from, long to) {
            return true;
        }

        public boolean containsDomainRange(Date from, Date to) {
            return true;
        }

        public boolean equals(Object object) {
            if (object == null) {
                return false;
            }
            if (object == this) {
                return true;
            }
            if (object instanceof DefaultTimeline) {
                return true;
            }
            return false;
        }
    }

    static {
        DEFAULT_DATE_RANGE = new DateRange();
        DEFAULT_DATE_TICK_UNIT = new DateTickUnit(DateTickUnitType.DAY, 1, new SimpleDateFormat());
        DEFAULT_ANCHOR_DATE = new Date();
        DEFAULT_TIMELINE = new DefaultTimeline();
    }

    public DateAxis() {
        this(null);
    }

    public DateAxis(String label) {
        this(label, TimeZone.getDefault());
    }

    public DateAxis(String label, TimeZone zone) {
        this(label, zone, Locale.getDefault());
    }

    public DateAxis(String label, TimeZone zone, Locale locale) {
        super(label, createStandardDateTickUnits(zone, locale));
        this.tickMarkPosition = DateTickMarkPosition.START;
        this.tickUnit = new DateTickUnit(DateTickUnitType.DAY, 1, new SimpleDateFormat());
        setAutoRangeMinimumSize(DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
        setRange(DEFAULT_DATE_RANGE, false, false);
        this.dateFormatOverride = null;
        this.timeZone = zone;
        this.locale = locale;
        this.timeline = DEFAULT_TIMELINE;
    }

    public TimeZone getTimeZone() {
        return this.timeZone;
    }

    public void setTimeZone(TimeZone zone) {
        ParamChecks.nullNotPermitted(zone, "zone");
        this.timeZone = zone;
        setStandardTickUnits(createStandardDateTickUnits(zone, this.locale));
        fireChangeEvent();
    }

    public Locale getLocale() {
        return this.locale;
    }

    public void setLocale(Locale locale) {
        ParamChecks.nullNotPermitted(locale, "locale");
        this.locale = locale;
        setStandardTickUnits(createStandardDateTickUnits(this.timeZone, this.locale));
        fireChangeEvent();
    }

    public Timeline getTimeline() {
        return this.timeline;
    }

    public void setTimeline(Timeline timeline) {
        if (this.timeline != timeline) {
            this.timeline = timeline;
            fireChangeEvent();
        }
    }

    public DateTickUnit getTickUnit() {
        return this.tickUnit;
    }

    public void setTickUnit(DateTickUnit unit) {
        setTickUnit(unit, true, true);
    }

    public void setTickUnit(DateTickUnit unit, boolean notify, boolean turnOffAutoSelection) {
        this.tickUnit = unit;
        if (turnOffAutoSelection) {
            setAutoTickUnitSelection(false, false);
        }
        if (notify) {
            fireChangeEvent();
        }
    }

    public DateFormat getDateFormatOverride() {
        return this.dateFormatOverride;
    }

    public void setDateFormatOverride(DateFormat formatter) {
        this.dateFormatOverride = formatter;
        fireChangeEvent();
    }

    public void setRange(Range range) {
        setRange(range, true, true);
    }

    public void setRange(Range range, boolean turnOffAutoRange, boolean notify) {
        ParamChecks.nullNotPermitted(range, "range");
        if (!(range instanceof DateRange)) {
            range = new DateRange(range);
        }
        super.setRange(range, turnOffAutoRange, notify);
    }

    public void setRange(Date lower, Date upper) {
        if (lower.getTime() >= upper.getTime()) {
            throw new IllegalArgumentException("Requires 'lower' < 'upper'.");
        }
        setRange(new DateRange(lower, upper));
    }

    public void setRange(double lower, double upper) {
        if (lower >= upper) {
            throw new IllegalArgumentException("Requires 'lower' < 'upper'.");
        }
        setRange(new DateRange(lower, upper));
    }

    public Date getMinimumDate() {
        Range range = getRange();
        if (range instanceof DateRange) {
            return ((DateRange) range).getLowerDate();
        }
        return new Date((long) range.getLowerBound());
    }

    public void setMinimumDate(Date date) {
        ParamChecks.nullNotPermitted(date, "date");
        Date maxDate = getMaximumDate();
        long maxMillis = maxDate.getTime();
        long newMinMillis = date.getTime();
        if (maxMillis <= newMinMillis) {
            maxDate = new Date(newMinMillis + (maxMillis - getMinimumDate().getTime()));
        }
        setRange(new DateRange(date, maxDate), true, false);
        fireChangeEvent();
    }

    public Date getMaximumDate() {
        Range range = getRange();
        if (range instanceof DateRange) {
            return ((DateRange) range).getUpperDate();
        }
        return new Date((long) range.getUpperBound());
    }

    public void setMaximumDate(Date maximumDate) {
        ParamChecks.nullNotPermitted(maximumDate, "maximumDate");
        Date minDate = getMinimumDate();
        long minMillis = minDate.getTime();
        long newMaxMillis = maximumDate.getTime();
        if (minMillis >= newMaxMillis) {
            minDate = new Date(newMaxMillis - (getMaximumDate().getTime() - minMillis));
        }
        setRange(new DateRange(minDate, maximumDate), true, false);
        fireChangeEvent();
    }

    public DateTickMarkPosition getTickMarkPosition() {
        return this.tickMarkPosition;
    }

    public void setTickMarkPosition(DateTickMarkPosition position) {
        ParamChecks.nullNotPermitted(position, "position");
        this.tickMarkPosition = position;
        fireChangeEvent();
    }

    public void configure() {
        if (isAutoRange()) {
            autoAdjustRange();
        }
    }

    public boolean isHiddenValue(long millis) {
        return !this.timeline.containsDomainValue(new Date(millis));
    }

    public double valueToJava2D(double value, Rectangle2D area, RectangleEdge edge) {
        value = (double) this.timeline.toTimelineValue((long) value);
        DateRange range = (DateRange) getRange();
        double axisMin = (double) this.timeline.toTimelineValue(range.getLowerMillis());
        double axisMax = (double) this.timeline.toTimelineValue(range.getUpperMillis());
        if (RectangleEdge.isTopOrBottom(edge)) {
            double minX = area.getX();
            double maxX = area.getMaxX();
            if (isInverted()) {
                return maxX + (((value - axisMin) / (axisMax - axisMin)) * (minX - maxX));
            }
            return minX + (((value - axisMin) / (axisMax - axisMin)) * (maxX - minX));
        } else if (!RectangleEdge.isLeftOrRight(edge)) {
            return 0.0d;
        } else {
            double minY = area.getMinY();
            double maxY = area.getMaxY();
            if (isInverted()) {
                return minY + (((value - axisMin) / (axisMax - axisMin)) * (maxY - minY));
            }
            return maxY - (((value - axisMin) / (axisMax - axisMin)) * (maxY - minY));
        }
    }

    public double dateToJava2D(Date date, Rectangle2D area, RectangleEdge edge) {
        return valueToJava2D((double) date.getTime(), area, edge);
    }

    public double java2DToValue(double java2DValue, Rectangle2D area, RectangleEdge edge) {
        double result;
        DateRange range = (DateRange) getRange();
        double axisMin = (double) this.timeline.toTimelineValue(range.getLowerMillis());
        double axisMax = (double) this.timeline.toTimelineValue(range.getUpperMillis());
        double min = 0.0d;
        double max = 0.0d;
        if (RectangleEdge.isTopOrBottom(edge)) {
            min = area.getX();
            max = area.getMaxX();
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            min = area.getMaxY();
            max = area.getY();
        }
        if (isInverted()) {
            result = axisMax - (((java2DValue - min) / (max - min)) * (axisMax - axisMin));
        } else {
            result = axisMin + (((java2DValue - min) / (max - min)) * (axisMax - axisMin));
        }
        return (double) this.timeline.toMillisecond((long) result);
    }

    public Date calculateLowestVisibleTickValue(DateTickUnit unit) {
        return nextStandardDate(getMinimumDate(), unit);
    }

    public Date calculateHighestVisibleTickValue(DateTickUnit unit) {
        return previousStandardDate(getMaximumDate(), unit);
    }

    protected Date previousStandardDate(Date date, DateTickUnit unit) {
        Calendar calendar = Calendar.getInstance(this.timeZone, this.locale);
        calendar.setTime(date);
        int count = unit.getCount();
        int value = count * (calendar.get(unit.getCalendarField()) / count);
        int months;
        int days;
        int years;
        int hours;
        int minutes;
        int seconds;
        switch (unit.getUnit()) {
            case LogTarget.ERROR /*0*/:
                if (this.tickMarkPosition == DateTickMarkPosition.START) {
                    months = 0;
                    days = 1;
                } else if (this.tickMarkPosition == DateTickMarkPosition.MIDDLE) {
                    months = 6;
                    days = 1;
                } else {
                    months = 11;
                    days = 31;
                }
                calendar.clear(14);
                calendar.set(value, months, days, 0, 0, 0);
                Date d3 = calendar.getTime();
                if (d3.getTime() >= date.getTime()) {
                    calendar.set(1, value - 1);
                    d3 = calendar.getTime();
                }
                return d3;
            case LogTarget.WARN /*1*/:
                years = calendar.get(1);
                calendar.clear(14);
                calendar.set(years, value, 1, 0, 0, 0);
                RegularTimePeriod month = new Month(calendar.getTime(), this.timeZone, this.locale);
                Date standardDate = calculateDateForPosition(month, this.tickMarkPosition);
                if (standardDate.getTime() >= date.getTime()) {
                    RegularTimePeriod month2 = (Month) month.previous();
                    month2.peg(Calendar.getInstance(this.timeZone));
                    standardDate = calculateDateForPosition(month2, this.tickMarkPosition);
                }
                return standardDate;
            case LogTarget.INFO /*2*/:
                years = calendar.get(1);
                months = calendar.get(2);
                if (this.tickMarkPosition == DateTickMarkPosition.START) {
                    hours = 0;
                } else if (this.tickMarkPosition == DateTickMarkPosition.MIDDLE) {
                    hours = 12;
                } else {
                    hours = 23;
                }
                calendar.clear(14);
                calendar.set(years, months, value, hours, 0, 0);
                Date d2 = calendar.getTime();
                if (d2.getTime() >= date.getTime()) {
                    calendar.set(5, value - 1);
                    d2 = calendar.getTime();
                }
                return d2;
            case LogTarget.DEBUG /*3*/:
                years = calendar.get(1);
                months = calendar.get(2);
                days = calendar.get(5);
                if (this.tickMarkPosition == DateTickMarkPosition.START) {
                    minutes = 0;
                    seconds = 0;
                } else if (this.tickMarkPosition == DateTickMarkPosition.MIDDLE) {
                    minutes = 30;
                    seconds = 0;
                } else {
                    minutes = 59;
                    seconds = 59;
                }
                calendar.clear(14);
                calendar.set(years, months, days, value, minutes, seconds);
                Date d1 = calendar.getTime();
                if (d1.getTime() >= date.getTime()) {
                    calendar.set(11, value - 1);
                    d1 = calendar.getTime();
                }
                return d1;
            case Align.WEST /*4*/:
                years = calendar.get(1);
                months = calendar.get(2);
                days = calendar.get(5);
                hours = calendar.get(11);
                if (this.tickMarkPosition == DateTickMarkPosition.START) {
                    seconds = 0;
                } else if (this.tickMarkPosition == DateTickMarkPosition.MIDDLE) {
                    seconds = 30;
                } else {
                    seconds = 59;
                }
                calendar.clear(14);
                calendar.set(years, months, days, hours, value, seconds);
                Date d0 = calendar.getTime();
                if (d0.getTime() >= date.getTime()) {
                    calendar.set(12, value - 1);
                    d0 = calendar.getTime();
                }
                return d0;
            case Align.TOP_LEFT /*5*/:
                int milliseconds;
                years = calendar.get(1);
                months = calendar.get(2);
                days = calendar.get(5);
                hours = calendar.get(11);
                minutes = calendar.get(12);
                if (this.tickMarkPosition == DateTickMarkPosition.START) {
                    milliseconds = 0;
                } else if (this.tickMarkPosition == DateTickMarkPosition.MIDDLE) {
                    milliseconds = ValueAxis.MAXIMUM_TICK_COUNT;
                } else {
                    milliseconds = Millisecond.LAST_MILLISECOND_IN_SECOND;
                }
                calendar.set(14, milliseconds);
                calendar.set(years, months, days, hours, minutes, value);
                Date dd = calendar.getTime();
                if (dd.getTime() >= date.getTime()) {
                    calendar.set(13, value - 1);
                    dd = calendar.getTime();
                }
                return dd;
            case Align.SOUTH_WEST /*6*/:
                calendar.set(calendar.get(1), calendar.get(2), calendar.get(5), calendar.get(11), calendar.get(12), calendar.get(13));
                calendar.set(14, value);
                Date mm = calendar.getTime();
                if (mm.getTime() < date.getTime()) {
                    return mm;
                }
                calendar.set(14, value - 1);
                return calendar.getTime();
            default:
                return null;
        }
    }

    private Date calculateDateForPosition(RegularTimePeriod period, DateTickMarkPosition position) {
        ParamChecks.nullNotPermitted(period, "period");
        if (position == DateTickMarkPosition.START) {
            return new Date(period.getFirstMillisecond());
        }
        if (position == DateTickMarkPosition.MIDDLE) {
            return new Date(period.getMiddleMillisecond());
        }
        if (position == DateTickMarkPosition.END) {
            return new Date(period.getLastMillisecond());
        }
        return null;
    }

    protected Date nextStandardDate(Date date, DateTickUnit unit) {
        Date previous = previousStandardDate(date, unit);
        Calendar calendar = Calendar.getInstance(this.timeZone, this.locale);
        calendar.setTime(previous);
        calendar.add(unit.getCalendarField(), unit.getMultiple());
        return calendar.getTime();
    }

    public static TickUnitSource createStandardDateTickUnits() {
        return createStandardDateTickUnits(TimeZone.getDefault(), Locale.getDefault());
    }

    public static TickUnitSource createStandardDateTickUnits(TimeZone zone, Locale locale) {
        ParamChecks.nullNotPermitted(zone, "zone");
        ParamChecks.nullNotPermitted(locale, "locale");
        TickUnits units = new TickUnits();
        DateFormat f1 = new SimpleDateFormat("HH:mm:ss.SSS", locale);
        DateFormat f2 = new SimpleDateFormat("HH:mm:ss", locale);
        DateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", locale);
        simpleDateFormat = new SimpleDateFormat("d-MMM, HH:mm", locale);
        simpleDateFormat = new SimpleDateFormat("d-MMM", locale);
        simpleDateFormat = new SimpleDateFormat("MMM-yyyy", locale);
        simpleDateFormat = new SimpleDateFormat("yyyy", locale);
        f1.setTimeZone(zone);
        f2.setTimeZone(zone);
        simpleDateFormat.setTimeZone(zone);
        simpleDateFormat.setTimeZone(zone);
        simpleDateFormat.setTimeZone(zone);
        simpleDateFormat.setTimeZone(zone);
        simpleDateFormat.setTimeZone(zone);
        units.add(new DateTickUnit(DateTickUnitType.MILLISECOND, 1, f1));
        units.add(new DateTickUnit(DateTickUnitType.MILLISECOND, 5, DateTickUnitType.MILLISECOND, 1, f1));
        units.add(new DateTickUnit(DateTickUnitType.MILLISECOND, 10, DateTickUnitType.MILLISECOND, 1, f1));
        units.add(new DateTickUnit(DateTickUnitType.MILLISECOND, 25, DateTickUnitType.MILLISECOND, 5, f1));
        units.add(new DateTickUnit(DateTickUnitType.MILLISECOND, 50, DateTickUnitType.MILLISECOND, 10, f1));
        units.add(new DateTickUnit(DateTickUnitType.MILLISECOND, 100, DateTickUnitType.MILLISECOND, 10, f1));
        units.add(new DateTickUnit(DateTickUnitType.MILLISECOND, 250, DateTickUnitType.MILLISECOND, 10, f1));
        units.add(new DateTickUnit(DateTickUnitType.MILLISECOND, (int) ValueAxis.MAXIMUM_TICK_COUNT, DateTickUnitType.MILLISECOND, 50, f1));
        units.add(new DateTickUnit(DateTickUnitType.SECOND, 1, DateTickUnitType.MILLISECOND, 50, f2));
        units.add(new DateTickUnit(DateTickUnitType.SECOND, 5, DateTickUnitType.SECOND, 1, f2));
        units.add(new DateTickUnit(DateTickUnitType.SECOND, 10, DateTickUnitType.SECOND, 1, f2));
        units.add(new DateTickUnit(DateTickUnitType.SECOND, 30, DateTickUnitType.SECOND, 5, f2));
        units.add(new DateTickUnit(DateTickUnitType.MINUTE, 1, DateTickUnitType.SECOND, 5, simpleDateFormat));
        units.add(new DateTickUnit(DateTickUnitType.MINUTE, 2, DateTickUnitType.SECOND, 10, simpleDateFormat));
        units.add(new DateTickUnit(DateTickUnitType.MINUTE, 5, DateTickUnitType.MINUTE, 1, simpleDateFormat));
        units.add(new DateTickUnit(DateTickUnitType.MINUTE, 10, DateTickUnitType.MINUTE, 1, simpleDateFormat));
        units.add(new DateTickUnit(DateTickUnitType.MINUTE, 15, DateTickUnitType.MINUTE, 5, simpleDateFormat));
        units.add(new DateTickUnit(DateTickUnitType.MINUTE, 20, DateTickUnitType.MINUTE, 5, simpleDateFormat));
        units.add(new DateTickUnit(DateTickUnitType.MINUTE, 30, DateTickUnitType.MINUTE, 5, simpleDateFormat));
        units.add(new DateTickUnit(DateTickUnitType.HOUR, 1, DateTickUnitType.MINUTE, 5, simpleDateFormat));
        units.add(new DateTickUnit(DateTickUnitType.HOUR, 2, DateTickUnitType.MINUTE, 10, simpleDateFormat));
        units.add(new DateTickUnit(DateTickUnitType.HOUR, 4, DateTickUnitType.MINUTE, 30, simpleDateFormat));
        units.add(new DateTickUnit(DateTickUnitType.HOUR, 6, DateTickUnitType.HOUR, 1, simpleDateFormat));
        units.add(new DateTickUnit(DateTickUnitType.HOUR, 12, DateTickUnitType.HOUR, 1, simpleDateFormat));
        units.add(new DateTickUnit(DateTickUnitType.DAY, 1, DateTickUnitType.HOUR, 1, simpleDateFormat));
        units.add(new DateTickUnit(DateTickUnitType.DAY, 2, DateTickUnitType.HOUR, 1, simpleDateFormat));
        units.add(new DateTickUnit(DateTickUnitType.DAY, 7, DateTickUnitType.DAY, 1, simpleDateFormat));
        units.add(new DateTickUnit(DateTickUnitType.DAY, 15, DateTickUnitType.DAY, 1, simpleDateFormat));
        units.add(new DateTickUnit(DateTickUnitType.MONTH, 1, DateTickUnitType.DAY, 1, simpleDateFormat));
        units.add(new DateTickUnit(DateTickUnitType.MONTH, 2, DateTickUnitType.DAY, 1, simpleDateFormat));
        units.add(new DateTickUnit(DateTickUnitType.MONTH, 3, DateTickUnitType.MONTH, 1, simpleDateFormat));
        units.add(new DateTickUnit(DateTickUnitType.MONTH, 4, DateTickUnitType.MONTH, 1, simpleDateFormat));
        units.add(new DateTickUnit(DateTickUnitType.MONTH, 6, DateTickUnitType.MONTH, 1, simpleDateFormat));
        units.add(new DateTickUnit(DateTickUnitType.YEAR, 1, DateTickUnitType.MONTH, 1, simpleDateFormat));
        units.add(new DateTickUnit(DateTickUnitType.YEAR, 2, DateTickUnitType.MONTH, 3, simpleDateFormat));
        units.add(new DateTickUnit(DateTickUnitType.YEAR, 5, DateTickUnitType.YEAR, 1, simpleDateFormat));
        units.add(new DateTickUnit(DateTickUnitType.YEAR, 10, DateTickUnitType.YEAR, 1, simpleDateFormat));
        units.add(new DateTickUnit(DateTickUnitType.YEAR, 25, DateTickUnitType.YEAR, 5, simpleDateFormat));
        units.add(new DateTickUnit(DateTickUnitType.YEAR, 50, DateTickUnitType.YEAR, 10, simpleDateFormat));
        units.add(new DateTickUnit(DateTickUnitType.YEAR, 100, DateTickUnitType.YEAR, 20, simpleDateFormat));
        return units;
    }

    protected void autoAdjustRange() {
        Plot plot = getPlot();
        if (plot != null && (plot instanceof ValueAxisPlot)) {
            long lower;
            Range r = ((ValueAxisPlot) plot).getDataRange(this);
            if (r == null) {
                if (this.timeline instanceof SegmentedTimeline) {
                    r = new DateRange((double) ((SegmentedTimeline) this.timeline).getStartTime(), (double) (((SegmentedTimeline) this.timeline).getStartTime() + 1));
                } else {
                    r = new DateRange();
                }
            }
            long upper = this.timeline.toTimelineValue((long) r.getUpperBound());
            long fixedAutoRange = (long) getFixedAutoRange();
            if (((double) fixedAutoRange) > 0.0d) {
                lower = upper - fixedAutoRange;
            } else {
                lower = this.timeline.toTimelineValue((long) r.getLowerBound());
                double range = (double) (upper - lower);
                long minRange = (long) getAutoRangeMinimumSize();
                if (range < ((double) minRange)) {
                    long expand = ((long) (((double) minRange) - range)) / 2;
                    upper += expand;
                    lower -= expand;
                }
                upper += (long) (getUpperMargin() * range);
                lower -= (long) (getLowerMargin() * range);
            }
            upper = this.timeline.toMillisecond(upper);
            setRange(new DateRange(new Date(this.timeline.toMillisecond(lower)), new Date(upper)), false, false);
        }
    }

    protected void selectAutoTickUnit(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        if (RectangleEdge.isTopOrBottom(edge)) {
            selectHorizontalAutoTickUnit(g2, dataArea, edge);
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            selectVerticalAutoTickUnit(g2, dataArea, edge);
        }
    }

    protected void selectHorizontalAutoTickUnit(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        long shift = 0;
        if (this.timeline instanceof SegmentedTimeline) {
            shift = ((SegmentedTimeline) this.timeline).getStartTime();
        }
        double zero = valueToJava2D(((double) shift) + 0.0d, dataArea, edge);
        double tickLabelWidth = estimateMaximumTickLabelWidth(g2, getTickUnit());
        TickUnitSource tickUnits = getStandardTickUnits();
        TickUnit unit1 = tickUnits.getCeilingTickUnit(getTickUnit());
        DateTickUnit unit2 = (DateTickUnit) tickUnits.getCeilingTickUnit((tickLabelWidth / Math.abs(valueToJava2D(((double) shift) + unit1.getSize(), dataArea, edge) - zero)) * unit1.getSize());
        if (estimateMaximumTickLabelWidth(g2, unit2) > Math.abs(valueToJava2D(((double) shift) + unit2.getSize(), dataArea, edge) - zero)) {
            unit2 = (DateTickUnit) tickUnits.getLargerTickUnit(unit2);
        }
        setTickUnit(unit2, false, false);
    }

    protected void selectVerticalAutoTickUnit(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        DateTickUnit finalUnit;
        TickUnitSource tickUnits = getStandardTickUnits();
        double zero = valueToJava2D(0.0d, dataArea, edge);
        DateTickUnit candidate1 = (DateTickUnit) tickUnits.getCeilingTickUnit(getRange().getLength() / XYPointerAnnotation.DEFAULT_TIP_RADIUS);
        DateTickUnit candidate2 = (DateTickUnit) tickUnits.getCeilingTickUnit((estimateMaximumTickLabelHeight(g2, candidate1) / Math.abs(valueToJava2D(candidate1.getSize(), dataArea, edge) - zero)) * candidate1.getSize());
        if (estimateMaximumTickLabelHeight(g2, candidate2) < Math.abs(valueToJava2D(candidate2.getSize(), dataArea, edge) - zero)) {
            finalUnit = candidate2;
        } else {
            finalUnit = (DateTickUnit) tickUnits.getLargerTickUnit(candidate2);
        }
        setTickUnit(finalUnit, false, false);
    }

    private double estimateMaximumTickLabelWidth(Graphics2D g2, DateTickUnit unit) {
        RectangleInsets tickLabelInsets = getTickLabelInsets();
        double result = tickLabelInsets.getLeft() + tickLabelInsets.getRight();
        Font tickLabelFont = getTickLabelFont();
        LineMetrics lm = tickLabelFont.getLineMetrics("ABCxyz", g2.getFontRenderContext());
        if (isVerticalTickLabels()) {
            return result + ((double) lm.getHeight());
        }
        String lowerStr;
        String upperStr;
        DateRange range = (DateRange) getRange();
        Date lower = range.getLowerDate();
        Date upper = range.getUpperDate();
        DateFormat formatter = getDateFormatOverride();
        if (formatter != null) {
            lowerStr = formatter.format(lower);
            upperStr = formatter.format(upper);
        } else {
            lowerStr = unit.dateToString(lower);
            upperStr = unit.dateToString(upper);
        }
        FontMetrics fm = g2.getFontMetrics(tickLabelFont);
        return result + Math.max((double) fm.stringWidth(lowerStr), (double) fm.stringWidth(upperStr));
    }

    private double estimateMaximumTickLabelHeight(Graphics2D g2, DateTickUnit unit) {
        RectangleInsets tickLabelInsets = getTickLabelInsets();
        double result = tickLabelInsets.getTop() + tickLabelInsets.getBottom();
        Font tickLabelFont = getTickLabelFont();
        LineMetrics lm = tickLabelFont.getLineMetrics("ABCxyz", g2.getFontRenderContext());
        if (isVerticalTickLabels()) {
            String lowerStr;
            String upperStr;
            DateRange range = (DateRange) getRange();
            Date lower = range.getLowerDate();
            Date upper = range.getUpperDate();
            DateFormat formatter = getDateFormatOverride();
            if (formatter != null) {
                lowerStr = formatter.format(lower);
                upperStr = formatter.format(upper);
            } else {
                lowerStr = unit.dateToString(lower);
                upperStr = unit.dateToString(upper);
            }
            FontMetrics fm = g2.getFontMetrics(tickLabelFont);
            return result + Math.max((double) fm.stringWidth(lowerStr), (double) fm.stringWidth(upperStr));
        }
        return result + ((double) lm.getHeight());
    }

    public List refreshTicks(Graphics2D g2, AxisState state, Rectangle2D dataArea, RectangleEdge edge) {
        if (RectangleEdge.isTopOrBottom(edge)) {
            return refreshTicksHorizontal(g2, dataArea, edge);
        }
        if (RectangleEdge.isLeftOrRight(edge)) {
            return refreshTicksVertical(g2, dataArea, edge);
        }
        return null;
    }

    private Date correctTickDateForPosition(Date time, DateTickUnit unit, DateTickMarkPosition position) {
        Date result = time;
        switch (unit.getUnit()) {
            case LogTarget.ERROR /*0*/:
                return calculateDateForPosition(new Year(time, this.timeZone, this.locale), position);
            case LogTarget.WARN /*1*/:
                return calculateDateForPosition(new Month(time, this.timeZone, this.locale), position);
            default:
                return result;
        }
    }

    protected List refreshTicksHorizontal(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        List result = new ArrayList();
        g2.setFont(getTickLabelFont());
        if (isAutoTickUnitSelection()) {
            selectAutoTickUnit(g2, dataArea, edge);
        }
        DateTickUnit unit = getTickUnit();
        Date tickDate = calculateLowestVisibleTickValue(unit);
        Date upperDate = getMaximumDate();
        boolean hasRolled = false;
        while (tickDate.before(upperDate)) {
            int minorTick;
            if (!hasRolled) {
                tickDate = correctTickDateForPosition(tickDate, unit, this.tickMarkPosition);
            }
            long lowestTickTime = tickDate.getTime();
            long distance = unit.addToDate(tickDate, this.timeZone).getTime() - lowestTickTime;
            int minorTickSpaces = getMinorTickCount();
            if (minorTickSpaces <= 0) {
                minorTickSpaces = unit.getMinorTickCount();
            }
            for (minorTick = 1; minorTick < minorTickSpaces; minorTick++) {
                long minorTickTime = lowestTickTime - ((((long) minorTick) * distance) / ((long) minorTickSpaces));
                if (minorTickTime > 0 && getRange().contains((double) minorTickTime) && !isHiddenValue(minorTickTime)) {
                    result.add(new DateTick(TickType.MINOR, new Date(minorTickTime), "", TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0d));
                }
            }
            if (isHiddenValue(tickDate.getTime())) {
                tickDate = unit.rollDate(tickDate, this.timeZone);
                hasRolled = true;
            } else {
                String tickLabel;
                TextAnchor anchor;
                TextAnchor rotationAnchor;
                DateFormat formatter = getDateFormatOverride();
                if (formatter != null) {
                    tickLabel = formatter.format(tickDate);
                } else {
                    tickLabel = this.tickUnit.dateToString(tickDate);
                }
                double angle = 0.0d;
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
                result.add(new DateTick(tickDate, tickLabel, anchor, rotationAnchor, angle));
                hasRolled = false;
                long currentTickTime = tickDate.getTime();
                tickDate = unit.addToDate(tickDate, this.timeZone);
                long nextTickTime = tickDate.getTime();
                for (minorTick = 1; minorTick < minorTickSpaces; minorTick++) {
                    minorTickTime = currentTickTime + (((nextTickTime - currentTickTime) * ((long) minorTick)) / ((long) minorTickSpaces));
                    if (getRange().contains((double) minorTickTime) && !isHiddenValue(minorTickTime)) {
                        result.add(new DateTick(TickType.MINOR, new Date(minorTickTime), "", TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0d));
                    }
                }
            }
        }
        return result;
    }

    protected List refreshTicksVertical(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        List result = new ArrayList();
        g2.setFont(getTickLabelFont());
        if (isAutoTickUnitSelection()) {
            selectAutoTickUnit(g2, dataArea, edge);
        }
        DateTickUnit unit = getTickUnit();
        Date tickDate = calculateLowestVisibleTickValue(unit);
        Date upperDate = getMaximumDate();
        boolean hasRolled = false;
        while (tickDate.before(upperDate)) {
            int minorTick;
            if (!hasRolled) {
                tickDate = correctTickDateForPosition(tickDate, unit, this.tickMarkPosition);
            }
            long lowestTickTime = tickDate.getTime();
            long distance = unit.addToDate(tickDate, this.timeZone).getTime() - lowestTickTime;
            int minorTickSpaces = getMinorTickCount();
            if (minorTickSpaces <= 0) {
                minorTickSpaces = unit.getMinorTickCount();
            }
            for (minorTick = 1; minorTick < minorTickSpaces; minorTick++) {
                long minorTickTime = lowestTickTime - ((((long) minorTick) * distance) / ((long) minorTickSpaces));
                if (minorTickTime > 0 && getRange().contains((double) minorTickTime) && !isHiddenValue(minorTickTime)) {
                    result.add(new DateTick(TickType.MINOR, new Date(minorTickTime), "", TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0d));
                }
            }
            if (isHiddenValue(tickDate.getTime())) {
                tickDate = unit.rollDate(tickDate, this.timeZone);
                hasRolled = true;
            } else {
                String tickLabel;
                TextAnchor anchor;
                TextAnchor rotationAnchor;
                DateFormat formatter = getDateFormatOverride();
                if (formatter != null) {
                    tickLabel = formatter.format(tickDate);
                } else {
                    tickLabel = this.tickUnit.dateToString(tickDate);
                }
                double angle = 0.0d;
                if (isVerticalTickLabels()) {
                    anchor = TextAnchor.BOTTOM_CENTER;
                    rotationAnchor = TextAnchor.BOTTOM_CENTER;
                    angle = edge == RectangleEdge.LEFT ? -1.5707963267948966d : 1.5707963267948966d;
                } else if (edge == RectangleEdge.LEFT) {
                    anchor = TextAnchor.CENTER_RIGHT;
                    rotationAnchor = TextAnchor.CENTER_RIGHT;
                } else {
                    anchor = TextAnchor.CENTER_LEFT;
                    rotationAnchor = TextAnchor.CENTER_LEFT;
                }
                result.add(new DateTick(tickDate, tickLabel, anchor, rotationAnchor, angle));
                hasRolled = false;
                long currentTickTime = tickDate.getTime();
                tickDate = unit.addToDate(tickDate, this.timeZone);
                long nextTickTime = tickDate.getTime();
                for (minorTick = 1; minorTick < minorTickSpaces; minorTick++) {
                    minorTickTime = currentTickTime + (((nextTickTime - currentTickTime) * ((long) minorTick)) / ((long) minorTickSpaces));
                    if (getRange().contains((double) minorTickTime) && !isHiddenValue(minorTickTime)) {
                        result.add(new DateTick(TickType.MINOR, new Date(minorTickTime), "", TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0d));
                    }
                }
            }
        }
        return result;
    }

    public AxisState draw(Graphics2D g2, double cursor, Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge, PlotRenderingInfo plotState) {
        AxisState state;
        if (isVisible()) {
            state = drawTickMarksAndLabels(g2, cursor, plotArea, dataArea, edge);
            if (getAttributedLabel() != null) {
                state = drawAttributedLabel(getAttributedLabel(), g2, plotArea, dataArea, edge, state);
            } else {
                state = drawLabel(getLabel(), g2, plotArea, dataArea, edge, state);
            }
            createAndAddEntity(cursor, state, dataArea, edge, plotState);
            return state;
        }
        state = new AxisState(cursor);
        state.setTicks(refreshTicks(g2, state, dataArea, edge));
        return state;
    }

    public void zoomRange(double lowerPercent, double upperPercent) {
        long adjStart;
        long adjEnd;
        double start = (double) this.timeline.toTimelineValue((long) getRange().getLowerBound());
        double length = ((double) this.timeline.toTimelineValue((long) getRange().getUpperBound())) - start;
        if (isInverted()) {
            adjStart = (long) (((NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR - upperPercent) * length) + start);
            adjEnd = (long) (((NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR - lowerPercent) * length) + start);
        } else {
            adjStart = (long) ((length * lowerPercent) + start);
            adjEnd = (long) ((length * upperPercent) + start);
        }
        if (adjEnd <= adjStart) {
            adjEnd = adjStart + 1;
        }
        setRange(new DateRange((double) this.timeline.toMillisecond(adjStart), (double) this.timeline.toMillisecond(adjEnd)));
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DateAxis)) {
            return false;
        }
        DateAxis that = (DateAxis) obj;
        if (ObjectUtilities.equal(this.timeZone, that.timeZone) && ObjectUtilities.equal(this.locale, that.locale) && ObjectUtilities.equal(this.tickUnit, that.tickUnit) && ObjectUtilities.equal(this.dateFormatOverride, that.dateFormatOverride) && ObjectUtilities.equal(this.tickMarkPosition, that.tickMarkPosition) && ObjectUtilities.equal(this.timeline, that.timeline)) {
            return super.equals(obj);
        }
        return false;
    }

    public int hashCode() {
        return super.hashCode();
    }

    public Object clone() throws CloneNotSupportedException {
        DateAxis clone = (DateAxis) super.clone();
        if (this.dateFormatOverride != null) {
            clone.dateFormatOverride = (DateFormat) this.dateFormatOverride.clone();
        }
        return clone;
    }

    public static TickUnitSource createStandardDateTickUnits(TimeZone zone) {
        return createStandardDateTickUnits(zone, Locale.getDefault());
    }
}
