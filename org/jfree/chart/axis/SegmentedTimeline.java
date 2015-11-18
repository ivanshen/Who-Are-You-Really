package org.jfree.chart.axis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import org.jfree.date.SerialDate;

public class SegmentedTimeline implements Timeline, Cloneable, Serializable {
    public static final long DAY_SEGMENT_SIZE = 86400000;
    public static TimeZone DEFAULT_TIME_ZONE = null;
    public static final long FIFTEEN_MINUTE_SEGMENT_SIZE = 900000;
    public static long FIRST_MONDAY_AFTER_1900 = 0;
    public static final long HOUR_SEGMENT_SIZE = 3600000;
    public static final long MINUTE_SEGMENT_SIZE = 60000;
    public static TimeZone NO_DST_TIME_ZONE = null;
    private static final long serialVersionUID = 1093779862539903110L;
    private boolean adjustForDaylightSaving;
    private SegmentedTimeline baseTimeline;
    private List exceptionSegments;
    private int groupSegmentCount;
    private long segmentSize;
    private int segmentsExcluded;
    private long segmentsExcludedSize;
    private long segmentsGroupSize;
    private int segmentsIncluded;
    private long segmentsIncludedSize;
    private long startTime;
    private Calendar workingCalendar;
    private Calendar workingCalendarNoDST;

    public class Segment implements Comparable, Cloneable, Serializable {
        protected long millisecond;
        protected long segmentEnd;
        protected long segmentNumber;
        protected long segmentStart;

        protected Segment() {
        }

        protected Segment(long millisecond) {
            this.segmentNumber = calculateSegmentNumber(millisecond);
            this.segmentStart = SegmentedTimeline.this.startTime + (this.segmentNumber * SegmentedTimeline.this.segmentSize);
            this.segmentEnd = (this.segmentStart + SegmentedTimeline.this.segmentSize) - 1;
            this.millisecond = millisecond;
        }

        public long calculateSegmentNumber(long millis) {
            if (millis >= SegmentedTimeline.this.startTime) {
                return (millis - SegmentedTimeline.this.startTime) / SegmentedTimeline.this.segmentSize;
            }
            return ((millis - SegmentedTimeline.this.startTime) / SegmentedTimeline.this.segmentSize) - 1;
        }

        public long getSegmentNumber() {
            return this.segmentNumber;
        }

        public long getSegmentCount() {
            return 1;
        }

        public long getSegmentStart() {
            return this.segmentStart;
        }

        public long getSegmentEnd() {
            return this.segmentEnd;
        }

        public long getMillisecond() {
            return this.millisecond;
        }

        public Date getDate() {
            return SegmentedTimeline.this.getDate(this.millisecond);
        }

        public boolean contains(long millis) {
            return this.segmentStart <= millis && millis <= this.segmentEnd;
        }

        public boolean contains(long from, long to) {
            return this.segmentStart <= from && to <= this.segmentEnd;
        }

        public boolean contains(Segment segment) {
            return contains(segment.getSegmentStart(), segment.getSegmentEnd());
        }

        public boolean contained(long from, long to) {
            return from <= this.segmentStart && this.segmentEnd <= to;
        }

        public Segment intersect(long from, long to) {
            return (from > this.segmentStart || this.segmentEnd > to) ? null : this;
        }

        public boolean before(Segment other) {
            return this.segmentEnd < other.getSegmentStart();
        }

        public boolean after(Segment other) {
            return this.segmentStart > other.getSegmentEnd();
        }

        public boolean equals(Object object) {
            if (!(object instanceof Segment)) {
                return false;
            }
            Segment other = (Segment) object;
            if (this.segmentNumber == other.getSegmentNumber() && this.segmentStart == other.getSegmentStart() && this.segmentEnd == other.getSegmentEnd() && this.millisecond == other.getMillisecond()) {
                return true;
            }
            return false;
        }

        public Segment copy() {
            try {
                return (Segment) clone();
            } catch (CloneNotSupportedException e) {
                return null;
            }
        }

        public int compareTo(Object object) {
            Segment other = (Segment) object;
            if (before(other)) {
                return -1;
            }
            if (after(other)) {
                return 1;
            }
            return 0;
        }

        public boolean inIncludeSegments() {
            if (getSegmentNumberRelativeToGroup() >= ((long) SegmentedTimeline.this.segmentsIncluded) || inExceptionSegments()) {
                return false;
            }
            return true;
        }

        public boolean inExcludeSegments() {
            return getSegmentNumberRelativeToGroup() >= ((long) SegmentedTimeline.this.segmentsIncluded);
        }

        private long getSegmentNumberRelativeToGroup() {
            long p = this.segmentNumber % ((long) SegmentedTimeline.this.groupSegmentCount);
            if (p < 0) {
                return p + ((long) SegmentedTimeline.this.groupSegmentCount);
            }
            return p;
        }

        public boolean inExceptionSegments() {
            return SegmentedTimeline.this.binarySearchExceptionSegments(this) >= 0;
        }

        public void inc(long n) {
            this.segmentNumber += n;
            long m = n * SegmentedTimeline.this.segmentSize;
            this.segmentStart += m;
            this.segmentEnd += m;
            this.millisecond += m;
        }

        public void inc() {
            inc(1);
        }

        public void dec(long n) {
            this.segmentNumber -= n;
            long m = n * SegmentedTimeline.this.segmentSize;
            this.segmentStart -= m;
            this.segmentEnd -= m;
            this.millisecond -= m;
        }

        public void dec() {
            dec(1);
        }

        public void moveIndexToStart() {
            this.millisecond = this.segmentStart;
        }

        public void moveIndexToEnd() {
            this.millisecond = this.segmentEnd;
        }
    }

    protected class SegmentRange extends Segment {
        private long segmentCount;

        public SegmentRange(long fromMillisecond, long toMillisecond) {
            super();
            Segment start = SegmentedTimeline.this.getSegment(fromMillisecond);
            Segment end = SegmentedTimeline.this.getSegment(toMillisecond);
            this.millisecond = fromMillisecond;
            this.segmentNumber = calculateSegmentNumber(fromMillisecond);
            this.segmentStart = start.segmentStart;
            this.segmentEnd = end.segmentEnd;
            this.segmentCount = (end.getSegmentNumber() - start.getSegmentNumber()) + 1;
        }

        public long getSegmentCount() {
            return this.segmentCount;
        }

        public Segment intersect(long from, long to) {
            long start = Math.max(from, this.segmentStart);
            long end = Math.min(to, this.segmentEnd);
            if (start <= end) {
                return new SegmentRange(start, end);
            }
            return null;
        }

        public boolean inIncludeSegments() {
            Segment segment = SegmentedTimeline.this.getSegment(this.segmentStart);
            while (segment.getSegmentStart() < this.segmentEnd) {
                if (!segment.inIncludeSegments()) {
                    return false;
                }
                segment.inc();
            }
            return true;
        }

        public boolean inExcludeSegments() {
            Segment segment = SegmentedTimeline.this.getSegment(this.segmentStart);
            while (segment.getSegmentStart() < this.segmentEnd) {
                if (!segment.inExceptionSegments()) {
                    return false;
                }
                segment.inc();
            }
            return true;
        }

        public void inc(long n) {
            throw new IllegalArgumentException("Not implemented in SegmentRange");
        }
    }

    protected class BaseTimelineSegmentRange extends SegmentRange {
        public BaseTimelineSegmentRange(long fromDomainValue, long toDomainValue) {
            super(fromDomainValue, toDomainValue);
        }
    }

    static {
        DEFAULT_TIME_ZONE = TimeZone.getDefault();
        int offset = TimeZone.getDefault().getRawOffset();
        NO_DST_TIME_ZONE = new SimpleTimeZone(offset, "UTC-" + offset);
        Calendar cal = new GregorianCalendar(NO_DST_TIME_ZONE);
        cal.set(SerialDate.MINIMUM_YEAR_SUPPORTED, 0, 1, 0, 0, 0);
        cal.set(14, 0);
        while (cal.get(7) != 2) {
            cal.add(5, 1);
        }
        FIRST_MONDAY_AFTER_1900 = cal.getTime().getTime();
    }

    public SegmentedTimeline(long segmentSize, int segmentsIncluded, int segmentsExcluded) {
        this.workingCalendar = Calendar.getInstance();
        this.exceptionSegments = new ArrayList();
        this.adjustForDaylightSaving = false;
        this.segmentSize = segmentSize;
        this.segmentsIncluded = segmentsIncluded;
        this.segmentsExcluded = segmentsExcluded;
        this.groupSegmentCount = this.segmentsIncluded + this.segmentsExcluded;
        this.segmentsIncludedSize = ((long) this.segmentsIncluded) * this.segmentSize;
        this.segmentsExcludedSize = ((long) this.segmentsExcluded) * this.segmentSize;
        this.segmentsGroupSize = this.segmentsIncludedSize + this.segmentsExcludedSize;
        int offset = TimeZone.getDefault().getRawOffset();
        this.workingCalendarNoDST = new GregorianCalendar(new SimpleTimeZone(offset, "UTC-" + offset), Locale.getDefault());
    }

    public static long firstMondayAfter1900() {
        int offset = TimeZone.getDefault().getRawOffset();
        Calendar cal = new GregorianCalendar(new SimpleTimeZone(offset, "UTC-" + offset));
        cal.set(SerialDate.MINIMUM_YEAR_SUPPORTED, 0, 1, 0, 0, 0);
        cal.set(14, 0);
        while (cal.get(7) != 2) {
            cal.add(5, 1);
        }
        return cal.getTime().getTime();
    }

    public static SegmentedTimeline newMondayThroughFridayTimeline() {
        SegmentedTimeline timeline = new SegmentedTimeline(DAY_SEGMENT_SIZE, 5, 2);
        timeline.setStartTime(firstMondayAfter1900());
        return timeline;
    }

    public static SegmentedTimeline newFifteenMinuteTimeline() {
        SegmentedTimeline timeline = new SegmentedTimeline(FIFTEEN_MINUTE_SEGMENT_SIZE, 28, 68);
        timeline.setStartTime(firstMondayAfter1900() + (36 * timeline.getSegmentSize()));
        timeline.setBaseTimeline(newMondayThroughFridayTimeline());
        return timeline;
    }

    public boolean getAdjustForDaylightSaving() {
        return this.adjustForDaylightSaving;
    }

    public void setAdjustForDaylightSaving(boolean adjust) {
        this.adjustForDaylightSaving = adjust;
    }

    public void setStartTime(long millisecond) {
        this.startTime = millisecond;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public int getSegmentsExcluded() {
        return this.segmentsExcluded;
    }

    public long getSegmentsExcludedSize() {
        return this.segmentsExcludedSize;
    }

    public int getGroupSegmentCount() {
        return this.groupSegmentCount;
    }

    public long getSegmentsGroupSize() {
        return this.segmentsGroupSize;
    }

    public int getSegmentsIncluded() {
        return this.segmentsIncluded;
    }

    public long getSegmentsIncludedSize() {
        return this.segmentsIncludedSize;
    }

    public long getSegmentSize() {
        return this.segmentSize;
    }

    public List getExceptionSegments() {
        return Collections.unmodifiableList(this.exceptionSegments);
    }

    public void setExceptionSegments(List exceptionSegments) {
        this.exceptionSegments = exceptionSegments;
    }

    public SegmentedTimeline getBaseTimeline() {
        return this.baseTimeline;
    }

    public void setBaseTimeline(SegmentedTimeline baseTimeline) {
        if (baseTimeline != null) {
            if (baseTimeline.getSegmentSize() < this.segmentSize) {
                throw new IllegalArgumentException("baseTimeline.getSegmentSize() is smaller than segmentSize");
            } else if (baseTimeline.getStartTime() > this.startTime) {
                throw new IllegalArgumentException("baseTimeline.getStartTime() is after startTime");
            } else if (baseTimeline.getSegmentSize() % this.segmentSize != 0) {
                throw new IllegalArgumentException("baseTimeline.getSegmentSize() is not multiple of segmentSize");
            } else if ((this.startTime - baseTimeline.getStartTime()) % this.segmentSize != 0) {
                throw new IllegalArgumentException("baseTimeline is not aligned");
            }
        }
        this.baseTimeline = baseTimeline;
    }

    public long toTimelineValue(long millisecond) {
        long rawMilliseconds = millisecond - this.startTime;
        long groupIndex = rawMilliseconds / this.segmentsGroupSize;
        if (rawMilliseconds % this.segmentsGroupSize >= this.segmentsIncludedSize) {
            return toTimelineValue(this.startTime + (this.segmentsGroupSize * (1 + groupIndex)));
        }
        Segment segment = getSegment(millisecond);
        if (segment.inExceptionSegments()) {
            while (true) {
                int p = binarySearchExceptionSegments(segment);
                if (p < 0) {
                    return toTimelineValue(millisecond);
                }
                millisecond = ((Segment) this.exceptionSegments.get(p)).getSegmentEnd() + 1;
                segment = getSegment(millisecond);
            }
        } else {
            long shiftedSegmentedValue = millisecond - this.startTime;
            long x = shiftedSegmentedValue % this.segmentsGroupSize;
            long y = shiftedSegmentedValue / this.segmentsGroupSize;
            long wholeExceptionsBeforeDomainValue = getExceptionSegmentCount(this.startTime, millisecond - 1);
            if (x < this.segmentsIncludedSize) {
                return ((this.segmentsIncludedSize * y) + x) - (this.segmentSize * wholeExceptionsBeforeDomainValue);
            }
            return (this.segmentsIncludedSize * (1 + y)) - (this.segmentSize * wholeExceptionsBeforeDomainValue);
        }
    }

    public long toTimelineValue(Date date) {
        return toTimelineValue(getTime(date));
    }

    public long toMillisecond(long timelineValue) {
        Segment result = new Segment((this.startTime + timelineValue) + ((timelineValue / this.segmentsIncludedSize) * this.segmentsExcludedSize));
        long lastIndex = this.startTime;
        while (lastIndex <= result.segmentStart) {
            while (true) {
                long exceptionSegmentCount = getExceptionSegmentCount(lastIndex, ((result.millisecond / this.segmentSize) * this.segmentSize) - 1);
                if (exceptionSegmentCount <= 0) {
                    break;
                }
                lastIndex = result.segmentStart;
                for (int i = 0; ((long) i) < exceptionSegmentCount; i++) {
                    do {
                        result.inc();
                    } while (result.inExcludeSegments());
                }
            }
            lastIndex = result.segmentStart;
            while (true) {
                if (!result.inExceptionSegments() && !result.inExcludeSegments()) {
                    break;
                }
                result.inc();
                lastIndex += this.segmentSize;
            }
            lastIndex++;
        }
        return getTimeFromLong(result.millisecond);
    }

    public long getTimeFromLong(long date) {
        long result = date;
        if (!this.adjustForDaylightSaving) {
            return result;
        }
        this.workingCalendarNoDST.setTime(new Date(date));
        this.workingCalendar.set(this.workingCalendarNoDST.get(1), this.workingCalendarNoDST.get(2), this.workingCalendarNoDST.get(5), this.workingCalendarNoDST.get(11), this.workingCalendarNoDST.get(12), this.workingCalendarNoDST.get(13));
        this.workingCalendar.set(14, this.workingCalendarNoDST.get(14));
        return this.workingCalendar.getTime().getTime();
    }

    public boolean containsDomainValue(long millisecond) {
        return getSegment(millisecond).inIncludeSegments();
    }

    public boolean containsDomainValue(Date date) {
        return containsDomainValue(getTime(date));
    }

    public boolean containsDomainRange(long domainValueStart, long domainValueEnd) {
        if (domainValueEnd < domainValueStart) {
            throw new IllegalArgumentException("domainValueEnd (" + domainValueEnd + ") < domainValueStart (" + domainValueStart + ")");
        }
        boolean contains;
        Segment segment = getSegment(domainValueStart);
        do {
            contains = segment.inIncludeSegments();
            if (segment.contains(domainValueEnd)) {
                break;
            }
            segment.inc();
        } while (contains);
        return contains;
    }

    public boolean containsDomainRange(Date dateDomainValueStart, Date dateDomainValueEnd) {
        return containsDomainRange(getTime(dateDomainValueStart), getTime(dateDomainValueEnd));
    }

    public void addException(long millisecond) {
        addException(new Segment(millisecond));
    }

    public void addException(long fromDomainValue, long toDomainValue) {
        addException(new SegmentRange(fromDomainValue, toDomainValue));
    }

    public void addException(Date exceptionDate) {
        addException(getTime(exceptionDate));
    }

    public void addExceptions(List exceptionList) {
        for (Date addException : exceptionList) {
            addException(addException);
        }
    }

    private void addException(Segment segment) {
        if (segment.inIncludeSegments()) {
            this.exceptionSegments.add(-(binarySearchExceptionSegments(segment) + 1), segment);
        }
    }

    public void addBaseTimelineException(long domainValue) {
        Segment baseSegment = this.baseTimeline.getSegment(domainValue);
        if (baseSegment.inIncludeSegments()) {
            Segment segment = getSegment(baseSegment.getSegmentStart());
            while (segment.getSegmentStart() <= baseSegment.getSegmentEnd()) {
                if (segment.inIncludeSegments()) {
                    long toDomainValue;
                    long fromDomainValue = segment.getSegmentStart();
                    do {
                        toDomainValue = segment.getSegmentEnd();
                        segment.inc();
                    } while (segment.inIncludeSegments());
                    addException(fromDomainValue, toDomainValue);
                } else {
                    segment.inc();
                }
            }
        }
    }

    public void addBaseTimelineException(Date date) {
        addBaseTimelineException(getTime(date));
    }

    public void addBaseTimelineExclusions(long fromBaseDomainValue, long toBaseDomainValue) {
        Segment baseSegment = this.baseTimeline.getSegment(fromBaseDomainValue);
        while (baseSegment.getSegmentStart() <= toBaseDomainValue && !baseSegment.inExcludeSegments()) {
            baseSegment.inc();
        }
        while (baseSegment.getSegmentStart() <= toBaseDomainValue) {
            long baseExclusionRangeEnd = (baseSegment.getSegmentStart() + (((long) this.baseTimeline.getSegmentsExcluded()) * this.baseTimeline.getSegmentSize())) - 1;
            Segment segment = getSegment(baseSegment.getSegmentStart());
            while (segment.getSegmentStart() <= baseExclusionRangeEnd) {
                if (segment.inIncludeSegments()) {
                    long toDomainValue;
                    long fromDomainValue = segment.getSegmentStart();
                    do {
                        toDomainValue = segment.getSegmentEnd();
                        segment.inc();
                    } while (segment.inIncludeSegments());
                    addException(new BaseTimelineSegmentRange(fromDomainValue, toDomainValue));
                } else {
                    segment.inc();
                }
            }
            baseSegment.inc((long) this.baseTimeline.getGroupSegmentCount());
        }
    }

    public long getExceptionSegmentCount(long fromMillisecond, long toMillisecond) {
        if (toMillisecond < fromMillisecond) {
            return 0;
        }
        int n = 0;
        for (Segment segment : this.exceptionSegments) {
            Segment intersection = segment.intersect(fromMillisecond, toMillisecond);
            if (intersection != null) {
                n = (int) (((long) n) + intersection.getSegmentCount());
            }
        }
        return (long) n;
    }

    public Segment getSegment(long millisecond) {
        return new Segment(millisecond);
    }

    public Segment getSegment(Date date) {
        return getSegment(getTime(date));
    }

    private boolean equals(Object o, Object p) {
        return o == p || (o != null && o.equals(p));
    }

    public boolean equals(Object o) {
        if (!(o instanceof SegmentedTimeline)) {
            return false;
        }
        boolean b0;
        SegmentedTimeline other = (SegmentedTimeline) o;
        if (this.segmentSize == other.getSegmentSize()) {
            b0 = true;
        } else {
            b0 = false;
        }
        boolean b1;
        if (this.segmentsIncluded == other.getSegmentsIncluded()) {
            b1 = true;
        } else {
            b1 = false;
        }
        boolean b2;
        if (this.segmentsExcluded == other.getSegmentsExcluded()) {
            b2 = true;
        } else {
            b2 = false;
        }
        boolean b3;
        if (this.startTime == other.getStartTime()) {
            b3 = true;
        } else {
            b3 = false;
        }
        boolean b4 = equals(this.exceptionSegments, other.getExceptionSegments());
        if (b0 && b1 && b2 && b3 && b4) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return ((((int) (this.segmentSize ^ (this.segmentSize >>> 32))) + 703) * 37) + ((int) (this.startTime ^ (this.startTime >>> 32)));
    }

    private int binarySearchExceptionSegments(Segment segment) {
        int low = 0;
        int high = this.exceptionSegments.size() - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
            Segment midSegment = (Segment) this.exceptionSegments.get(mid);
            if (segment.contains(midSegment) || midSegment.contains(segment)) {
                return mid;
            }
            if (midSegment.before(segment)) {
                low = mid + 1;
            } else if (midSegment.after(segment)) {
                high = mid - 1;
            } else {
                throw new IllegalStateException("Invalid condition.");
            }
        }
        return -(low + 1);
    }

    public long getTime(Date date) {
        long result = date.getTime();
        if (!this.adjustForDaylightSaving) {
            return result;
        }
        this.workingCalendar.setTime(date);
        this.workingCalendarNoDST.set(this.workingCalendar.get(1), this.workingCalendar.get(2), this.workingCalendar.get(5), this.workingCalendar.get(11), this.workingCalendar.get(12), this.workingCalendar.get(13));
        this.workingCalendarNoDST.set(14, this.workingCalendar.get(14));
        return this.workingCalendarNoDST.getTime().getTime();
    }

    public Date getDate(long value) {
        this.workingCalendarNoDST.setTime(new Date(value));
        return this.workingCalendarNoDST.getTime();
    }

    public Object clone() throws CloneNotSupportedException {
        return (SegmentedTimeline) super.clone();
    }
}
