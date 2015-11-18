package org.jfree.chart.axis;

import java.text.AttributedString;
import org.jfree.ui.TextAnchor;

public class LogTick extends ValueTick {
    AttributedString attributedLabel;

    public LogTick(TickType type, double value, AttributedString label, TextAnchor textAnchor) {
        super(type, value, null, textAnchor, textAnchor, 0.0d);
        this.attributedLabel = label;
    }

    public AttributedString getAttributedLabel() {
        return this.attributedLabel;
    }
}
