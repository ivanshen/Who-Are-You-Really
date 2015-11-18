package org.jfree.data.time;

import java.util.Date;

public interface TimePeriod extends Comparable {
    Date getEnd();

    Date getStart();
}
