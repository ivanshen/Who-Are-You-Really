package org.jfree.chart.labels;

import java.text.AttributedString;
import org.jfree.data.general.PieDataset;

public interface PieSectionLabelGenerator {
    AttributedString generateAttributedSectionLabel(PieDataset pieDataset, Comparable comparable);

    String generateSectionLabel(PieDataset pieDataset, Comparable comparable);
}
