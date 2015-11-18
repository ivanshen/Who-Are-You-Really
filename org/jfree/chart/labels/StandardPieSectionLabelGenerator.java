package org.jfree.chart.labels;

import java.io.Serializable;
import java.text.AttributedString;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.jfree.data.general.PieDataset;
import org.jfree.util.PublicCloneable;

public class StandardPieSectionLabelGenerator extends AbstractPieItemLabelGenerator implements PieSectionLabelGenerator, Cloneable, PublicCloneable, Serializable {
    public static final String DEFAULT_SECTION_LABEL_FORMAT = "{0}";
    private static final long serialVersionUID = 3064190563760203668L;
    private Map attributedLabels;

    public StandardPieSectionLabelGenerator() {
        this(DEFAULT_SECTION_LABEL_FORMAT, NumberFormat.getNumberInstance(), NumberFormat.getPercentInstance());
    }

    public StandardPieSectionLabelGenerator(Locale locale) {
        this(DEFAULT_SECTION_LABEL_FORMAT, locale);
    }

    public StandardPieSectionLabelGenerator(String labelFormat) {
        this(labelFormat, NumberFormat.getNumberInstance(), NumberFormat.getPercentInstance());
    }

    public StandardPieSectionLabelGenerator(String labelFormat, Locale locale) {
        this(labelFormat, NumberFormat.getNumberInstance(locale), NumberFormat.getPercentInstance(locale));
    }

    public StandardPieSectionLabelGenerator(String labelFormat, NumberFormat numberFormat, NumberFormat percentFormat) {
        super(labelFormat, numberFormat, percentFormat);
        this.attributedLabels = new HashMap();
    }

    public AttributedString getAttributedLabel(int section) {
        return (AttributedString) this.attributedLabels.get(Integer.valueOf(section));
    }

    public void setAttributedLabel(int section, AttributedString label) {
        this.attributedLabels.put(Integer.valueOf(section), label);
    }

    public String generateSectionLabel(PieDataset dataset, Comparable key) {
        return super.generateSectionLabel(dataset, key);
    }

    public AttributedString generateAttributedSectionLabel(PieDataset dataset, Comparable key) {
        return getAttributedLabel(dataset.getIndex(key));
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StandardPieSectionLabelGenerator)) {
            return false;
        }
        if (this.attributedLabels.equals(((StandardPieSectionLabelGenerator) obj).attributedLabels)) {
            return super.equals(obj);
        }
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        StandardPieSectionLabelGenerator clone = (StandardPieSectionLabelGenerator) super.clone();
        clone.attributedLabels = new HashMap();
        clone.attributedLabels.putAll(this.attributedLabels);
        return clone;
    }
}
