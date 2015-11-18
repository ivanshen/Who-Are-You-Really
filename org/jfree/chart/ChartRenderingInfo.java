package org.jfree.chart;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.io.SerialUtilities;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;

public class ChartRenderingInfo implements Cloneable, Serializable {
    private static final long serialVersionUID = 2751952018173406822L;
    private transient Rectangle2D chartArea;
    private EntityCollection entities;
    private PlotRenderingInfo plotInfo;

    public ChartRenderingInfo() {
        this(new StandardEntityCollection());
    }

    public ChartRenderingInfo(EntityCollection entities) {
        this.chartArea = new Double();
        this.plotInfo = new PlotRenderingInfo(this);
        this.entities = entities;
    }

    public Rectangle2D getChartArea() {
        return this.chartArea;
    }

    public void setChartArea(Rectangle2D area) {
        this.chartArea.setRect(area);
    }

    public EntityCollection getEntityCollection() {
        return this.entities;
    }

    public void setEntityCollection(EntityCollection entities) {
        this.entities = entities;
    }

    public void clear() {
        this.chartArea.setRect(0.0d, 0.0d, 0.0d, 0.0d);
        this.plotInfo = new PlotRenderingInfo(this);
        if (this.entities != null) {
            this.entities.clear();
        }
    }

    public PlotRenderingInfo getPlotInfo() {
        return this.plotInfo;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ChartRenderingInfo)) {
            return false;
        }
        ChartRenderingInfo that = (ChartRenderingInfo) obj;
        if (!ObjectUtilities.equal(this.chartArea, that.chartArea)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.plotInfo, that.plotInfo)) {
            return false;
        }
        if (ObjectUtilities.equal(this.entities, that.entities)) {
            return true;
        }
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        ChartRenderingInfo clone = (ChartRenderingInfo) super.clone();
        if (this.chartArea != null) {
            clone.chartArea = (Rectangle2D) this.chartArea.clone();
        }
        if (this.entities instanceof PublicCloneable) {
            clone.entities = (EntityCollection) this.entities.clone();
        }
        return clone;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeShape(this.chartArea, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.chartArea = (Rectangle2D) SerialUtilities.readShape(stream);
    }
}
