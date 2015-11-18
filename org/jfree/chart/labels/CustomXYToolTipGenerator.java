package org.jfree.chart.labels;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.jfree.data.xy.XYDataset;
import org.jfree.util.PublicCloneable;

public class CustomXYToolTipGenerator implements XYToolTipGenerator, Cloneable, PublicCloneable, Serializable {
    private static final long serialVersionUID = 8636030004670141362L;
    private List toolTipSeries;

    public CustomXYToolTipGenerator() {
        this.toolTipSeries = new ArrayList();
    }

    public int getListCount() {
        return this.toolTipSeries.size();
    }

    public int getToolTipCount(int list) {
        List tooltips = (List) this.toolTipSeries.get(list);
        if (tooltips != null) {
            return tooltips.size();
        }
        return 0;
    }

    public String getToolTipText(int series, int item) {
        if (series >= getListCount()) {
            return null;
        }
        List tooltips = (List) this.toolTipSeries.get(series);
        if (tooltips == null || item >= tooltips.size()) {
            return null;
        }
        return (String) tooltips.get(item);
    }

    public void addToolTipSeries(List toolTips) {
        this.toolTipSeries.add(toolTips);
    }

    public String generateToolTip(XYDataset data, int series, int item) {
        return getToolTipText(series, item);
    }

    public Object clone() throws CloneNotSupportedException {
        CustomXYToolTipGenerator clone = (CustomXYToolTipGenerator) super.clone();
        if (this.toolTipSeries != null) {
            clone.toolTipSeries = new ArrayList(this.toolTipSeries);
        }
        return clone;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CustomXYToolTipGenerator)) {
            return false;
        }
        CustomXYToolTipGenerator generator = (CustomXYToolTipGenerator) obj;
        boolean result = true;
        for (int series = 0; series < getListCount(); series++) {
            for (int item = 0; item < getToolTipCount(series); item++) {
                String t1 = getToolTipText(series, item);
                String t2 = generator.getToolTipText(series, item);
                if (t1 == null) {
                    result = result && t2 == null;
                } else if (result && t1.equals(t2)) {
                    result = true;
                } else {
                    result = false;
                }
            }
        }
        return result;
    }
}
