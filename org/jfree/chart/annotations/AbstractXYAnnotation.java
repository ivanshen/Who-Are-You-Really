package org.jfree.chart.annotations;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.XYAnnotationEntity;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.util.ObjectUtilities;

public abstract class AbstractXYAnnotation extends AbstractAnnotation implements XYAnnotation {
    private String toolTipText;
    private String url;

    public abstract void draw(Graphics2D graphics2D, XYPlot xYPlot, Rectangle2D rectangle2D, ValueAxis valueAxis, ValueAxis valueAxis2, int i, PlotRenderingInfo plotRenderingInfo);

    protected AbstractXYAnnotation() {
        this.toolTipText = null;
        this.url = null;
    }

    public String getToolTipText() {
        return this.toolTipText;
    }

    public void setToolTipText(String text) {
        this.toolTipText = text;
    }

    public String getURL() {
        return this.url;
    }

    public void setURL(String url) {
        this.url = url;
    }

    protected void addEntity(PlotRenderingInfo info, Shape hotspot, int rendererIndex, String toolTipText, String urlText) {
        if (info != null) {
            EntityCollection entities = info.getOwner().getEntityCollection();
            if (entities != null) {
                entities.add(new XYAnnotationEntity(hotspot, rendererIndex, toolTipText, urlText));
            }
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AbstractXYAnnotation)) {
            return false;
        }
        AbstractXYAnnotation that = (AbstractXYAnnotation) obj;
        if (!ObjectUtilities.equal(this.toolTipText, that.toolTipText)) {
            return false;
        }
        if (ObjectUtilities.equal(this.url, that.url)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int result = 193;
        if (this.toolTipText != null) {
            result = this.toolTipText.hashCode() + 7141;
        }
        if (this.url != null) {
            return (result * 37) + this.url.hashCode();
        }
        return result;
    }
}
