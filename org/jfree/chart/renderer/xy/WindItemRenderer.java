package org.jfree.chart.renderer.xy;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Line2D.Double;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.WindDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PublicCloneable;

public class WindItemRenderer extends AbstractXYItemRenderer implements XYItemRenderer, Cloneable, PublicCloneable, Serializable {
    private static final long serialVersionUID = 8078914101916976844L;

    public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D plotArea, PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass) {
        WindDataset windData = (WindDataset) dataset;
        Paint seriesPaint = getItemPaint(series, item);
        Stroke seriesStroke = getItemStroke(series, item);
        g2.setPaint(seriesPaint);
        g2.setStroke(seriesStroke);
        Number x = windData.getX(series, item);
        Number windDir = windData.getWindDirection(series, item);
        Number wforce = windData.getWindForce(series, item);
        double windForce = wforce.doubleValue();
        double wdirt = Math.toRadians((windDir.doubleValue() * -30.0d) - SpiderWebPlot.DEFAULT_START_ANGLE);
        RectangleEdge domainAxisLocation = plot.getDomainAxisEdge();
        RectangleEdge rangeAxisLocation = plot.getRangeAxisEdge();
        double ax1 = domainAxis.valueToJava2D(x.doubleValue(), plotArea, domainAxisLocation);
        double ay1 = rangeAxis.valueToJava2D(0.0d, plotArea, rangeAxisLocation);
        double ray2 = windForce * Math.sin(wdirt);
        double ax2 = domainAxis.valueToJava2D(x.doubleValue() + ((Math.cos(wdirt) * windForce) * 8000000.0d), plotArea, domainAxisLocation);
        double ay2 = rangeAxis.valueToJava2D(ray2, plotArea, rangeAxisLocation);
        int diri = windDir.intValue();
        int forcei = wforce.intValue();
        String dirforce = diri + "-" + forcei;
        g2.draw(new Double(ax1, ay1, ax2, ay2));
        g2.setPaint(Color.blue);
        g2.setFont(new Font("Dialog", 1, 9));
        g2.drawString(dirforce, (float) ax1, (float) ay1);
        g2.setPaint(seriesPaint);
        g2.setStroke(seriesStroke);
        double aldir = Math.toRadians(((windDir.doubleValue() * -30.0d) - SpiderWebPlot.DEFAULT_START_ANGLE) - XYPointerAnnotation.DEFAULT_ARROW_LENGTH);
        Graphics2D graphics2D = g2;
        graphics2D.draw(new Double(domainAxis.valueToJava2D((((wforce.doubleValue() * Math.cos(aldir)) * 8000000.0d) * 0.8d) + x.doubleValue(), plotArea, domainAxisLocation), rangeAxis.valueToJava2D((wforce.doubleValue() * Math.sin(aldir)) * 0.8d, plotArea, rangeAxisLocation), ax2, ay2));
        double ardir = Math.toRadians(((windDir.doubleValue() * -30.0d) - SpiderWebPlot.DEFAULT_START_ANGLE) + XYPointerAnnotation.DEFAULT_ARROW_LENGTH);
        graphics2D = g2;
        graphics2D.draw(new Double(domainAxis.valueToJava2D((((wforce.doubleValue() * Math.cos(ardir)) * 8000000.0d) * 0.8d) + x.doubleValue(), plotArea, domainAxisLocation), rangeAxis.valueToJava2D((wforce.doubleValue() * Math.sin(ardir)) * 0.8d, plotArea, rangeAxisLocation), ax2, ay2));
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
