package org.jfree.chart.entity;

import java.awt.Shape;
import java.io.Serializable;
import org.jfree.chart.HashUtilities;
import org.jfree.data.general.PieDataset;
import org.jfree.util.ObjectUtilities;

public class PieSectionEntity extends ChartEntity implements Serializable {
    private static final long serialVersionUID = 9199892576531984162L;
    private PieDataset dataset;
    private int pieIndex;
    private int sectionIndex;
    private Comparable sectionKey;

    public PieSectionEntity(Shape area, PieDataset dataset, int pieIndex, int sectionIndex, Comparable sectionKey, String toolTipText, String urlText) {
        super(area, toolTipText, urlText);
        this.dataset = dataset;
        this.pieIndex = pieIndex;
        this.sectionIndex = sectionIndex;
        this.sectionKey = sectionKey;
    }

    public PieDataset getDataset() {
        return this.dataset;
    }

    public void setDataset(PieDataset dataset) {
        this.dataset = dataset;
    }

    public int getPieIndex() {
        return this.pieIndex;
    }

    public void setPieIndex(int index) {
        this.pieIndex = index;
    }

    public int getSectionIndex() {
        return this.sectionIndex;
    }

    public void setSectionIndex(int index) {
        this.sectionIndex = index;
    }

    public Comparable getSectionKey() {
        return this.sectionKey;
    }

    public void setSectionKey(Comparable key) {
        this.sectionKey = key;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PieSectionEntity)) {
            return false;
        }
        PieSectionEntity that = (PieSectionEntity) obj;
        if (ObjectUtilities.equal(this.dataset, that.dataset) && this.pieIndex == that.pieIndex && this.sectionIndex == that.sectionIndex && ObjectUtilities.equal(this.sectionKey, that.sectionKey)) {
            return super.equals(obj);
        }
        return false;
    }

    public int hashCode() {
        return HashUtilities.hashCode(HashUtilities.hashCode(super.hashCode(), this.pieIndex), this.sectionIndex);
    }

    public String toString() {
        return "PieSection: " + this.pieIndex + ", " + this.sectionIndex + "(" + this.sectionKey.toString() + ")";
    }
}
