package org.jfree.chart.labels;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.jfree.data.contour.ContourDataset;

public class StandardContourToolTipGenerator implements ContourToolTipGenerator, Serializable {
    private static final long serialVersionUID = -1881659351247502711L;
    private DecimalFormat valueForm;

    public StandardContourToolTipGenerator() {
        this.valueForm = new DecimalFormat("##.###");
    }

    public String generateToolTip(ContourDataset data, int item) {
        String xString;
        double x = data.getXValue(0, item);
        double y = data.getYValue(0, item);
        double z = data.getZValue(0, item);
        if (data.isDateAxis(0)) {
            xString = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss").format(new Date((long) x), new StringBuffer(), new FieldPosition(0)).toString();
        } else {
            xString = this.valueForm.format(x);
        }
        if (Double.isNaN(z)) {
            return "X: " + xString + ", Y: " + this.valueForm.format(y) + ", Z: no data";
        }
        return "X: " + xString + ", Y: " + this.valueForm.format(y) + ", Z: " + this.valueForm.format(z);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StandardContourToolTipGenerator)) {
            return false;
        }
        StandardContourToolTipGenerator that = (StandardContourToolTipGenerator) obj;
        if (this.valueForm != null) {
            return this.valueForm.equals(that.valueForm);
        }
        return false;
    }
}
