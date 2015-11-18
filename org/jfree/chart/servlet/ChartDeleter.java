package org.jfree.chart.servlet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

public class ChartDeleter implements HttpSessionBindingListener, Serializable {
    private List chartNames;

    public ChartDeleter() {
        this.chartNames = new ArrayList();
    }

    public void addChart(String filename) {
        this.chartNames.add(filename);
    }

    public boolean isChartAvailable(String filename) {
        return this.chartNames.contains(filename);
    }

    public void valueBound(HttpSessionBindingEvent event) {
    }

    public void valueUnbound(HttpSessionBindingEvent event) {
        Iterator iter = this.chartNames.listIterator();
        while (iter.hasNext()) {
            File file = new File(System.getProperty("java.io.tmpdir"), (String) iter.next());
            if (file.exists()) {
                file.delete();
            }
        }
    }
}
