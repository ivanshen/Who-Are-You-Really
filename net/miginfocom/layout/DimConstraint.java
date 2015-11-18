package net.miginfocom.layout;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;

public final class DimConstraint implements Externalizable {
    private UnitValue align;
    private String endGroup;
    private boolean fill;
    private BoundSize gapAfter;
    private BoundSize gapBefore;
    private boolean noGrid;
    final ResizeConstraint resize;
    private BoundSize size;
    private String sizeGroup;

    public DimConstraint() {
        this.resize = new ResizeConstraint();
        this.sizeGroup = null;
        this.size = BoundSize.NULL_SIZE;
        this.gapBefore = null;
        this.gapAfter = null;
        this.align = null;
        this.endGroup = null;
        this.fill = false;
        this.noGrid = false;
    }

    private Object readResolve() throws ObjectStreamException {
        return LayoutUtil.getSerializedObject(this);
    }

    public UnitValue getAlign() {
        return this.align;
    }

    public UnitValue getAlignOrDefault(boolean z) {
        return this.align != null ? this.align : z ? UnitValue.LEADING : (this.fill || !PlatformDefaults.getDefaultRowAlignmentBaseline()) ? UnitValue.CENTER : UnitValue.BASELINE_IDENTITY;
    }

    int[] getComponentGaps(ContainerWrapper containerWrapper, ComponentWrapper componentWrapper, BoundSize boundSize, ComponentWrapper componentWrapper2, String str, int i, int i2, boolean z) {
        BoundSize boundSize2 = i2 < 2 ? this.gapBefore : this.gapAfter;
        Object obj = (boundSize2 == null || !boundSize2.getGapPush()) ? null : 1;
        BoundSize defaultComponentGap = ((boundSize2 == null || boundSize2.isUnset()) && ((boundSize == null || boundSize.isUnset()) && componentWrapper != null)) ? PlatformDefaults.getDefaultComponentGap(componentWrapper, componentWrapper2, i2 + 1, str, z) : boundSize2;
        if (defaultComponentGap == null) {
            return obj != null ? new int[]{0, 0, -2147471302} : null;
        } else {
            int[] iArr = new int[3];
            for (int i3 = 0; i3 <= 2; i3++) {
                UnitValue size = defaultComponentGap.getSize(i3);
                iArr[i3] = size != null ? size.getPixels((float) i, containerWrapper, null) : -2147471302;
            }
            return iArr;
        }
    }

    public String getEndGroup() {
        return this.endGroup;
    }

    public BoundSize getGapAfter() {
        return this.gapAfter;
    }

    public BoundSize getGapBefore() {
        return this.gapBefore;
    }

    public Float getGrow() {
        return this.resize.grow;
    }

    public int getGrowPriority() {
        return this.resize.growPrio;
    }

    int[] getRowGaps(ContainerWrapper containerWrapper, BoundSize boundSize, int i, boolean z) {
        BoundSize boundSize2 = z ? this.gapBefore : this.gapAfter;
        if (!(boundSize2 == null || boundSize2.isUnset())) {
            boundSize = boundSize2;
        }
        if (boundSize == null || boundSize.isUnset()) {
            return null;
        }
        int[] iArr = new int[3];
        for (int i2 = 0; i2 <= 2; i2++) {
            UnitValue size = boundSize.getSize(i2);
            iArr[i2] = size != null ? size.getPixels((float) i, containerWrapper, null) : -2147471302;
        }
        return iArr;
    }

    public Float getShrink() {
        return this.resize.shrink;
    }

    public int getShrinkPriority() {
        return this.resize.shrinkPrio;
    }

    public BoundSize getSize() {
        return this.size;
    }

    public String getSizeGroup() {
        return this.sizeGroup;
    }

    boolean hasGapAfter() {
        return (this.gapAfter == null || this.gapAfter.isUnset()) ? false : true;
    }

    boolean hasGapBefore() {
        return (this.gapBefore == null || this.gapBefore.isUnset()) ? false : true;
    }

    public boolean isFill() {
        return this.fill;
    }

    boolean isGapAfterPush() {
        return this.gapAfter != null && this.gapAfter.getGapPush();
    }

    boolean isGapBeforePush() {
        return this.gapBefore != null && this.gapBefore.getGapPush();
    }

    public boolean isNoGrid() {
        return this.noGrid;
    }

    public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
        LayoutUtil.setSerializedObject(this, LayoutUtil.readAsXML(objectInput));
    }

    public void setAlign(UnitValue unitValue) {
        this.align = unitValue;
    }

    public void setEndGroup(String str) {
        this.endGroup = str;
    }

    public void setFill(boolean z) {
        this.fill = z;
    }

    public void setGapAfter(BoundSize boundSize) {
        this.gapAfter = boundSize;
    }

    public void setGapBefore(BoundSize boundSize) {
        this.gapBefore = boundSize;
    }

    public void setGrow(Float f) {
        this.resize.grow = f;
    }

    public void setGrowPriority(int i) {
        this.resize.growPrio = i;
    }

    public void setNoGrid(boolean z) {
        this.noGrid = z;
    }

    public void setShrink(Float f) {
        this.resize.shrink = f;
    }

    public void setShrinkPriority(int i) {
        this.resize.shrinkPrio = i;
    }

    public void setSize(BoundSize boundSize) {
        if (boundSize != null) {
            boundSize.checkNotLinked();
        }
        this.size = boundSize;
    }

    public void setSizeGroup(String str) {
        this.sizeGroup = str;
    }

    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        if (getClass() == DimConstraint.class) {
            LayoutUtil.writeAsXML(objectOutput, this);
        }
    }
}
