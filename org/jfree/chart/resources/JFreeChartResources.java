package org.jfree.chart.resources;

import java.util.ListResourceBundle;

public class JFreeChartResources extends ListResourceBundle {
    private static final Object[][] CONTENTS;

    public Object[][] getContents() {
        return CONTENTS;
    }

    static {
        r0 = new Object[4][];
        r0[0] = new Object[]{"project.name", "JFreeChart"};
        r0[1] = new Object[]{"project.version", "1.0.18"};
        r0[2] = new Object[]{"project.info", "http://www.jfree.org/jfreechart/index.html"};
        r0[3] = new Object[]{"project.copyright", "(C)opyright 2000-2013, by Object Refinery Limited and Contributors"};
        CONTENTS = r0;
    }
}
