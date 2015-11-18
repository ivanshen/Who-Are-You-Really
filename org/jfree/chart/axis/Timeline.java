package org.jfree.chart.axis;

import java.util.Date;

public interface Timeline {
    boolean containsDomainRange(long j, long j2);

    boolean containsDomainRange(Date date, Date date2);

    boolean containsDomainValue(long j);

    boolean containsDomainValue(Date date);

    long toMillisecond(long j);

    long toTimelineValue(long j);

    long toTimelineValue(Date date);
}
