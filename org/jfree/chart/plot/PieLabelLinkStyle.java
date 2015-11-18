package org.jfree.chart.plot;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class PieLabelLinkStyle implements Serializable {
    public static final PieLabelLinkStyle CUBIC_CURVE;
    public static final PieLabelLinkStyle QUAD_CURVE;
    public static final PieLabelLinkStyle STANDARD;
    private String name;

    static {
        STANDARD = new PieLabelLinkStyle("PieLabelLinkStyle.STANDARD");
        QUAD_CURVE = new PieLabelLinkStyle("PieLabelLinkStyle.QUAD_CURVE");
        CUBIC_CURVE = new PieLabelLinkStyle("PieLabelLinkStyle.CUBIC_CURVE");
    }

    private PieLabelLinkStyle(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof PieLabelLinkStyle)) {
            return false;
        }
        if (this.name.equals(((PieLabelLinkStyle) obj).toString())) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    private Object readResolve() throws ObjectStreamException {
        if (equals(STANDARD)) {
            return STANDARD;
        }
        if (equals(QUAD_CURVE)) {
            return QUAD_CURVE;
        }
        if (equals(CUBIC_CURVE)) {
            return CUBIC_CURVE;
        }
        return null;
    }
}
