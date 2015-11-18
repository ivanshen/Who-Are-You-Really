package org.jfree.data.time;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class TimePeriodAnchor implements Serializable {
    public static final TimePeriodAnchor END;
    public static final TimePeriodAnchor MIDDLE;
    public static final TimePeriodAnchor START;
    private static final long serialVersionUID = 2011955697457548862L;
    private String name;

    static {
        START = new TimePeriodAnchor("TimePeriodAnchor.START");
        MIDDLE = new TimePeriodAnchor("TimePeriodAnchor.MIDDLE");
        END = new TimePeriodAnchor("TimePeriodAnchor.END");
    }

    private TimePeriodAnchor(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TimePeriodAnchor)) {
            return false;
        }
        if (this.name.equals(((TimePeriodAnchor) obj).name)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    private Object readResolve() throws ObjectStreamException {
        if (equals(START)) {
            return START;
        }
        if (equals(MIDDLE)) {
            return MIDDLE;
        }
        if (equals(END)) {
            return END;
        }
        return null;
    }
}
