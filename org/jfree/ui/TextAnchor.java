package org.jfree.ui;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class TextAnchor implements Serializable {
    public static final TextAnchor BASELINE_CENTER;
    public static final TextAnchor BASELINE_LEFT;
    public static final TextAnchor BASELINE_RIGHT;
    public static final TextAnchor BOTTOM_CENTER;
    public static final TextAnchor BOTTOM_LEFT;
    public static final TextAnchor BOTTOM_RIGHT;
    public static final TextAnchor CENTER;
    public static final TextAnchor CENTER_LEFT;
    public static final TextAnchor CENTER_RIGHT;
    public static final TextAnchor HALF_ASCENT_CENTER;
    public static final TextAnchor HALF_ASCENT_LEFT;
    public static final TextAnchor HALF_ASCENT_RIGHT;
    public static final TextAnchor TOP_CENTER;
    public static final TextAnchor TOP_LEFT;
    public static final TextAnchor TOP_RIGHT;
    private static final long serialVersionUID = 8219158940496719660L;
    private String name;

    static {
        TOP_LEFT = new TextAnchor("TextAnchor.TOP_LEFT");
        TOP_CENTER = new TextAnchor("TextAnchor.TOP_CENTER");
        TOP_RIGHT = new TextAnchor("TextAnchor.TOP_RIGHT");
        HALF_ASCENT_LEFT = new TextAnchor("TextAnchor.HALF_ASCENT_LEFT");
        HALF_ASCENT_CENTER = new TextAnchor("TextAnchor.HALF_ASCENT_CENTER");
        HALF_ASCENT_RIGHT = new TextAnchor("TextAnchor.HALF_ASCENT_RIGHT");
        CENTER_LEFT = new TextAnchor("TextAnchor.CENTER_LEFT");
        CENTER = new TextAnchor("TextAnchor.CENTER");
        CENTER_RIGHT = new TextAnchor("TextAnchor.CENTER_RIGHT");
        BASELINE_LEFT = new TextAnchor("TextAnchor.BASELINE_LEFT");
        BASELINE_CENTER = new TextAnchor("TextAnchor.BASELINE_CENTER");
        BASELINE_RIGHT = new TextAnchor("TextAnchor.BASELINE_RIGHT");
        BOTTOM_LEFT = new TextAnchor("TextAnchor.BOTTOM_LEFT");
        BOTTOM_CENTER = new TextAnchor("TextAnchor.BOTTOM_CENTER");
        BOTTOM_RIGHT = new TextAnchor("TextAnchor.BOTTOM_RIGHT");
    }

    private TextAnchor(String name) {
        this.name = name;
    }

    public boolean isLeft() {
        return this == BASELINE_LEFT || this == BOTTOM_LEFT || this == CENTER_LEFT || this == HALF_ASCENT_LEFT || this == TOP_LEFT;
    }

    public boolean isRight() {
        return this == BASELINE_RIGHT || this == BOTTOM_RIGHT || this == CENTER_RIGHT || this == HALF_ASCENT_RIGHT || this == TOP_RIGHT;
    }

    public boolean isHorizontalCenter() {
        return this == BASELINE_CENTER || this == BOTTOM_CENTER || this == CENTER || this == HALF_ASCENT_CENTER || this == TOP_CENTER;
    }

    public boolean isTop() {
        return this == TOP_LEFT || this == TOP_CENTER || this == TOP_RIGHT;
    }

    public boolean isBottom() {
        return this == BOTTOM_LEFT || this == BOTTOM_CENTER || this == BOTTOM_RIGHT;
    }

    public boolean isBaseline() {
        return this == BASELINE_LEFT || this == BASELINE_CENTER || this == BASELINE_RIGHT;
    }

    public boolean isHalfAscent() {
        return this == HALF_ASCENT_LEFT || this == HALF_ASCENT_CENTER || this == HALF_ASCENT_RIGHT;
    }

    public boolean isVerticalCenter() {
        return this == CENTER_LEFT || this == CENTER || this == CENTER_RIGHT;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TextAnchor)) {
            return false;
        }
        if (this.name.equals(((TextAnchor) o).name)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    private Object readResolve() throws ObjectStreamException {
        if (equals(TOP_LEFT)) {
            return TOP_LEFT;
        }
        if (equals(TOP_CENTER)) {
            return TOP_CENTER;
        }
        if (equals(TOP_RIGHT)) {
            return TOP_RIGHT;
        }
        if (equals(BOTTOM_LEFT)) {
            return BOTTOM_LEFT;
        }
        if (equals(BOTTOM_CENTER)) {
            return BOTTOM_CENTER;
        }
        if (equals(BOTTOM_RIGHT)) {
            return BOTTOM_RIGHT;
        }
        if (equals(BASELINE_LEFT)) {
            return BASELINE_LEFT;
        }
        if (equals(BASELINE_CENTER)) {
            return BASELINE_CENTER;
        }
        if (equals(BASELINE_RIGHT)) {
            return BASELINE_RIGHT;
        }
        if (equals(CENTER_LEFT)) {
            return CENTER_LEFT;
        }
        if (equals(CENTER)) {
            return CENTER;
        }
        if (equals(CENTER_RIGHT)) {
            return CENTER_RIGHT;
        }
        if (equals(HALF_ASCENT_LEFT)) {
            return HALF_ASCENT_LEFT;
        }
        if (equals(HALF_ASCENT_CENTER)) {
            return HALF_ASCENT_CENTER;
        }
        if (equals(HALF_ASCENT_RIGHT)) {
            return HALF_ASCENT_RIGHT;
        }
        return null;
    }
}
