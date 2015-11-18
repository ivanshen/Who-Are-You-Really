package org.jfree.chart.renderer;

import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.List;

public class OutlierList {
    private Outlier averagedOutlier;
    private boolean multiple;
    private List outliers;

    public OutlierList(Outlier outlier) {
        this.multiple = false;
        this.outliers = new ArrayList();
        setAveragedOutlier(outlier);
    }

    public boolean add(Outlier outlier) {
        return this.outliers.add(outlier);
    }

    public int getItemCount() {
        return this.outliers.size();
    }

    public Outlier getAveragedOutlier() {
        return this.averagedOutlier;
    }

    public void setAveragedOutlier(Outlier averagedOutlier) {
        this.averagedOutlier = averagedOutlier;
    }

    public boolean isMultiple() {
        return this.multiple;
    }

    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }

    public boolean isOverlapped(Outlier other) {
        if (other == null) {
            return false;
        }
        return other.overlaps(getAveragedOutlier());
    }

    public void updateAveragedOutlier() {
        double totalXCoords = 0.0d;
        double totalYCoords = 0.0d;
        int size = getItemCount();
        for (Outlier o : this.outliers) {
            totalXCoords += o.getX();
            totalYCoords += o.getY();
        }
        getAveragedOutlier().getPoint().setLocation(new Double(totalXCoords / ((double) size), totalYCoords / ((double) size)));
    }
}
