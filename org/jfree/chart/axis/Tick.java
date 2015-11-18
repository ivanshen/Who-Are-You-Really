package org.jfree.chart.axis;

import java.io.Serializable;
import org.jfree.chart.util.ParamChecks;
import org.jfree.ui.TextAnchor;
import org.jfree.util.ObjectUtilities;

public abstract class Tick implements Serializable, Cloneable {
    private static final long serialVersionUID = 6668230383875149773L;
    private double angle;
    private TextAnchor rotationAnchor;
    private String text;
    private TextAnchor textAnchor;

    public Tick(String text, TextAnchor textAnchor, TextAnchor rotationAnchor, double angle) {
        ParamChecks.nullNotPermitted(textAnchor, "textAnchor");
        ParamChecks.nullNotPermitted(rotationAnchor, "rotationAnchor");
        this.text = text;
        this.textAnchor = textAnchor;
        this.rotationAnchor = rotationAnchor;
        this.angle = angle;
    }

    public String getText() {
        return this.text;
    }

    public TextAnchor getTextAnchor() {
        return this.textAnchor;
    }

    public TextAnchor getRotationAnchor() {
        return this.rotationAnchor;
    }

    public double getAngle() {
        return this.angle;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Tick)) {
            return false;
        }
        Tick t = (Tick) obj;
        if (!ObjectUtilities.equal(this.text, t.text)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.textAnchor, t.textAnchor)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.rotationAnchor, t.rotationAnchor)) {
            return false;
        }
        if (this.angle != t.angle) {
            return false;
        }
        return true;
    }

    public Object clone() throws CloneNotSupportedException {
        return (Tick) super.clone();
    }

    public String toString() {
        return this.text;
    }
}
