package org.jfree.chart.axis;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class DateTickMarkPosition implements Serializable {
    public static final DateTickMarkPosition END;
    public static final DateTickMarkPosition MIDDLE;
    public static final DateTickMarkPosition START;
    private static final long serialVersionUID = 2540750672764537240L;
    private String name;

    static {
        START = new DateTickMarkPosition("DateTickMarkPosition.START");
        MIDDLE = new DateTickMarkPosition("DateTickMarkPosition.MIDDLE");
        END = new DateTickMarkPosition("DateTickMarkPosition.END");
    }

    private DateTickMarkPosition(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DateTickMarkPosition)) {
            return false;
        }
        if (this.name.equals(((DateTickMarkPosition) obj).toString())) {
            return true;
        }
        return false;
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
