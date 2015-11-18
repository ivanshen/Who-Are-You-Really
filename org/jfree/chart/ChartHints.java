package org.jfree.chart;

import java.util.Map;
import org.jfree.util.LogTarget;

public final class ChartHints {
    public static final Key KEY_BEGIN_ELEMENT;
    public static final Key KEY_END_ELEMENT;

    public static class Key extends java.awt.RenderingHints.Key {
        public Key(int privateKey) {
            super(privateKey);
        }

        public boolean isCompatibleValue(Object val) {
            switch (intKey()) {
                case LogTarget.ERROR /*0*/:
                    if (val == null || (val instanceof String) || (val instanceof Map)) {
                        return true;
                    }
                    return false;
                case LogTarget.WARN /*1*/:
                    return val == null || (val instanceof Object);
                default:
                    throw new RuntimeException("Not possible!");
            }
        }
    }

    private ChartHints() {
    }

    static {
        KEY_BEGIN_ELEMENT = new Key(0);
        KEY_END_ELEMENT = new Key(1);
    }
}
