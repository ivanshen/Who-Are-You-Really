package org.jfree.chart.annotations;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;

public interface CategoryAnnotation extends Annotation {
    void draw(Graphics2D graphics2D, CategoryPlot categoryPlot, Rectangle2D rectangle2D, CategoryAxis categoryAxis, ValueAxis valueAxis);
}
