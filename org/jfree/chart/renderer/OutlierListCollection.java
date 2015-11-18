package org.jfree.chart.renderer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OutlierListCollection {
    private boolean highFarOut;
    private boolean lowFarOut;
    private List outlierLists;

    public OutlierListCollection() {
        this.highFarOut = false;
        this.lowFarOut = false;
        this.outlierLists = new ArrayList();
    }

    public boolean isHighFarOut() {
        return this.highFarOut;
    }

    public void setHighFarOut(boolean farOut) {
        this.highFarOut = farOut;
    }

    public boolean isLowFarOut() {
        return this.lowFarOut;
    }

    public void setLowFarOut(boolean farOut) {
        this.lowFarOut = farOut;
    }

    public boolean add(Outlier outlier) {
        if (this.outlierLists.isEmpty()) {
            return this.outlierLists.add(new OutlierList(outlier));
        }
        boolean updated = false;
        for (OutlierList list : this.outlierLists) {
            if (list.isOverlapped(outlier)) {
                updated = updateOutlierList(list, outlier);
            }
        }
        if (updated) {
            return updated;
        }
        return this.outlierLists.add(new OutlierList(outlier));
    }

    public Iterator iterator() {
        return this.outlierLists.iterator();
    }

    private boolean updateOutlierList(OutlierList list, Outlier outlier) {
        boolean result = list.add(outlier);
        list.updateAveragedOutlier();
        list.setMultiple(true);
        return result;
    }
}
