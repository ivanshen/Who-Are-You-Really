package org.jfree.data.time;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class FixedMillisecond extends RegularTimePeriod implements Serializable {
    private static final long serialVersionUID = 7867521484545646931L;
    private long time;

    public FixedMillisecond() {
        this(new Date());
    }

    public FixedMillisecond(long millisecond) {
        this(new Date(millisecond));
    }

    public FixedMillisecond(Date time) {
        this.time = time.getTime();
    }

    public Date getTime() {
        return new Date(this.time);
    }

    public void peg(Calendar calendar) {
    }

    public RegularTimePeriod previous() {
        long t = this.time;
        if (t != Long.MIN_VALUE) {
            return new FixedMillisecond(t - 1);
        }
        return null;
    }

    public RegularTimePeriod next() {
        long t = this.time;
        if (t != Long.MAX_VALUE) {
            return new FixedMillisecond(1 + t);
        }
        return null;
    }

    public boolean equals(Object object) {
        if (!(object instanceof FixedMillisecond)) {
            return false;
        }
        if (this.time == ((FixedMillisecond) object).getFirstMillisecond()) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return (int) this.time;
    }

    public int compareTo(Object o1) {
        if (o1 instanceof FixedMillisecond) {
            long difference = this.time - ((FixedMillisecond) o1).time;
            if (difference > 0) {
                return 1;
            }
            if (difference < 0) {
                return -1;
            }
            return 0;
        } else if (o1 instanceof RegularTimePeriod) {
            return 0;
        } else {
            return 1;
        }
    }

    public long getFirstMillisecond() {
        return this.time;
    }

    public long getFirstMillisecond(Calendar calendar) {
        return this.time;
    }

    public long getLastMillisecond() {
        return this.time;
    }

    public long getLastMillisecond(Calendar calendar) {
        return this.time;
    }

    public long getMiddleMillisecond() {
        return this.time;
    }

    public long getMiddleMillisecond(Calendar calendar) {
        return this.time;
    }

    public long getSerialIndex() {
        return this.time;
    }
}
