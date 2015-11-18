package net.miginfocom.layout;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;

public final class LC implements Externalizable {
    private UnitValue alignX;
    private UnitValue alignY;
    private int debugMillis;
    private boolean fillX;
    private boolean fillY;
    private boolean flowX;
    private BoundSize gridGapX;
    private BoundSize gridGapY;
    private BoundSize height;
    private int hideMode;
    private UnitValue[] insets;
    private Boolean leftToRight;
    private boolean noCache;
    private boolean noGrid;
    private BoundSize packH;
    private BoundSize packW;
    private float phAlign;
    private float pwAlign;
    private boolean topToBottom;
    private boolean visualPadding;
    private BoundSize width;
    private int wrapAfter;

    public LC() {
        this.wrapAfter = 2097051;
        this.leftToRight = null;
        this.insets = null;
        this.alignX = null;
        this.alignY = null;
        this.gridGapX = null;
        this.gridGapY = null;
        this.width = BoundSize.NULL_SIZE;
        this.height = BoundSize.NULL_SIZE;
        this.packW = BoundSize.NULL_SIZE;
        this.packH = BoundSize.NULL_SIZE;
        this.pwAlign = JFreeChart.DEFAULT_BACKGROUND_IMAGE_ALPHA;
        this.phAlign = Plot.DEFAULT_FOREGROUND_ALPHA;
        this.debugMillis = 0;
        this.hideMode = 0;
        this.noCache = false;
        this.flowX = true;
        this.fillX = false;
        this.fillY = false;
        this.topToBottom = true;
        this.noGrid = false;
        this.visualPadding = true;
    }

    private Object readResolve() throws ObjectStreamException {
        return LayoutUtil.getSerializedObject(this);
    }

    public final LC align(String str, String str2) {
        if (str != null) {
            alignX(str);
        }
        if (str2 != null) {
            alignY(str2);
        }
        return this;
    }

    public final LC alignX(String str) {
        setAlignX(ConstraintParser.parseUnitValueOrAlign(str, true, null));
        return this;
    }

    public final LC alignY(String str) {
        setAlignY(ConstraintParser.parseUnitValueOrAlign(str, false, null));
        return this;
    }

    public final LC bottomToTop() {
        setTopToBottom(false);
        return this;
    }

    public final LC debug(int i) {
        setDebugMillis(i);
        return this;
    }

    public final LC fill() {
        setFillX(true);
        setFillY(true);
        return this;
    }

    public final LC fillX() {
        setFillX(true);
        return this;
    }

    public final LC fillY() {
        setFillY(true);
        return this;
    }

    public final LC flowX() {
        setFlowX(true);
        return this;
    }

    public final LC flowY() {
        setFlowX(false);
        return this;
    }

    public final UnitValue getAlignX() {
        return this.alignX;
    }

    public final UnitValue getAlignY() {
        return this.alignY;
    }

    public final int getDebugMillis() {
        return this.debugMillis;
    }

    public final BoundSize getGridGapX() {
        return this.gridGapX;
    }

    public final BoundSize getGridGapY() {
        return this.gridGapY;
    }

    public final BoundSize getHeight() {
        return this.height;
    }

    public final int getHideMode() {
        return this.hideMode;
    }

    public final UnitValue[] getInsets() {
        if (this.insets == null) {
            return null;
        }
        return new UnitValue[]{this.insets[0], this.insets[1], this.insets[2], this.insets[3]};
    }

    public final Boolean getLeftToRight() {
        return this.leftToRight;
    }

    public final BoundSize getPackHeight() {
        return this.packH;
    }

    public final float getPackHeightAlign() {
        return this.phAlign;
    }

    public final BoundSize getPackWidth() {
        return this.packW;
    }

    public final float getPackWidthAlign() {
        return this.pwAlign;
    }

    public final BoundSize getWidth() {
        return this.width;
    }

    public final int getWrapAfter() {
        return this.wrapAfter;
    }

    public final LC gridGap(String str, String str2) {
        if (str != null) {
            gridGapX(str);
        }
        if (str2 != null) {
            gridGapY(str2);
        }
        return this;
    }

    public final LC gridGapX(String str) {
        setGridGapX(ConstraintParser.parseBoundSize(str, true, true));
        return this;
    }

    public final LC gridGapY(String str) {
        setGridGapY(ConstraintParser.parseBoundSize(str, true, false));
        return this;
    }

    public final LC height(String str) {
        setHeight(ConstraintParser.parseBoundSize(str, false, false));
        return this;
    }

    public final LC hideMode(int i) {
        setHideMode(i);
        return this;
    }

    public final LC insets(String str) {
        this.insets = ConstraintParser.parseInsets(str, true);
        return this;
    }

    public final LC insets(String str, String str2, String str3, String str4) {
        this.insets = new UnitValue[]{ConstraintParser.parseUnitValue(str, false), ConstraintParser.parseUnitValue(str2, true), ConstraintParser.parseUnitValue(str3, false), ConstraintParser.parseUnitValue(str4, true)};
        return this;
    }

    public final LC insetsAll(String str) {
        UnitValue parseUnitValue = ConstraintParser.parseUnitValue(str, true);
        UnitValue parseUnitValue2 = ConstraintParser.parseUnitValue(str, false);
        this.insets = new UnitValue[]{parseUnitValue2, parseUnitValue, parseUnitValue2, parseUnitValue};
        return this;
    }

    public final boolean isFillX() {
        return this.fillX;
    }

    public final boolean isFillY() {
        return this.fillY;
    }

    public final boolean isFlowX() {
        return this.flowX;
    }

    public boolean isNoCache() {
        return this.noCache;
    }

    public final boolean isNoGrid() {
        return this.noGrid;
    }

    public final boolean isTopToBottom() {
        return this.topToBottom;
    }

    public final boolean isVisualPadding() {
        return this.visualPadding;
    }

    public final LC leftToRight(boolean z) {
        setLeftToRight(z ? Boolean.TRUE : Boolean.FALSE);
        return this;
    }

    public final LC maxHeight(String str) {
        setHeight(LayoutUtil.derive(getHeight(), null, null, ConstraintParser.parseUnitValue(str, false)));
        return this;
    }

    public final LC maxWidth(String str) {
        setWidth(LayoutUtil.derive(getWidth(), null, null, ConstraintParser.parseUnitValue(str, true)));
        return this;
    }

    public final LC minHeight(String str) {
        setHeight(LayoutUtil.derive(getHeight(), ConstraintParser.parseUnitValue(str, false), null, null));
        return this;
    }

    public final LC minWidth(String str) {
        setWidth(LayoutUtil.derive(getWidth(), ConstraintParser.parseUnitValue(str, true), null, null));
        return this;
    }

    public final LC noCache() {
        setNoCache(true);
        return this;
    }

    public final LC noGrid() {
        setNoGrid(true);
        return this;
    }

    public final LC noVisualPadding() {
        setVisualPadding(false);
        return this;
    }

    public final LC pack() {
        return pack("pref", "pref");
    }

    public final LC pack(String str, String str2) {
        setPackWidth(str != null ? ConstraintParser.parseBoundSize(str, false, false) : BoundSize.NULL_SIZE);
        setPackHeight(str2 != null ? ConstraintParser.parseBoundSize(str2, false, false) : BoundSize.NULL_SIZE);
        return this;
    }

    public final LC packAlign(float f, float f2) {
        setPackWidthAlign(f);
        setPackHeightAlign(f2);
        return this;
    }

    public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
        LayoutUtil.setSerializedObject(this, LayoutUtil.readAsXML(objectInput));
    }

    public final LC rightToLeft() {
        setLeftToRight(Boolean.FALSE);
        return this;
    }

    public final void setAlignX(UnitValue unitValue) {
        this.alignX = unitValue;
    }

    public final void setAlignY(UnitValue unitValue) {
        this.alignY = unitValue;
    }

    public final void setDebugMillis(int i) {
        this.debugMillis = i;
    }

    public final void setFillX(boolean z) {
        this.fillX = z;
    }

    public final void setFillY(boolean z) {
        this.fillY = z;
    }

    public final void setFlowX(boolean z) {
        this.flowX = z;
    }

    public final void setGridGapX(BoundSize boundSize) {
        this.gridGapX = boundSize;
    }

    public final void setGridGapY(BoundSize boundSize) {
        this.gridGapY = boundSize;
    }

    public final void setHeight(BoundSize boundSize) {
        if (boundSize == null) {
            boundSize = BoundSize.NULL_SIZE;
        }
        this.height = boundSize;
    }

    public final void setHideMode(int i) {
        if (i < 0 || i > 3) {
            throw new IllegalArgumentException("Wrong hideMode: " + i);
        }
        this.hideMode = i;
    }

    public final void setInsets(UnitValue[] unitValueArr) {
        this.insets = unitValueArr != null ? new UnitValue[]{unitValueArr[0], unitValueArr[1], unitValueArr[2], unitValueArr[3]} : null;
    }

    public final void setLeftToRight(Boolean bool) {
        this.leftToRight = bool;
    }

    public void setNoCache(boolean z) {
        this.noCache = z;
    }

    public final void setNoGrid(boolean z) {
        this.noGrid = z;
    }

    public final void setPackHeight(BoundSize boundSize) {
        if (boundSize == null) {
            boundSize = BoundSize.NULL_SIZE;
        }
        this.packH = boundSize;
    }

    public final void setPackHeightAlign(float f) {
        this.phAlign = Math.max(0.0f, Math.min(Plot.DEFAULT_FOREGROUND_ALPHA, f));
    }

    public final void setPackWidth(BoundSize boundSize) {
        if (boundSize == null) {
            boundSize = BoundSize.NULL_SIZE;
        }
        this.packW = boundSize;
    }

    public final void setPackWidthAlign(float f) {
        this.pwAlign = Math.max(0.0f, Math.min(Plot.DEFAULT_FOREGROUND_ALPHA, f));
    }

    public final void setTopToBottom(boolean z) {
        this.topToBottom = z;
    }

    public final void setVisualPadding(boolean z) {
        this.visualPadding = z;
    }

    public final void setWidth(BoundSize boundSize) {
        if (boundSize == null) {
            boundSize = BoundSize.NULL_SIZE;
        }
        this.width = boundSize;
    }

    public final void setWrapAfter(int i) {
        this.wrapAfter = i;
    }

    public final LC topToBottom() {
        setTopToBottom(true);
        return this;
    }

    public final LC width(String str) {
        setWidth(ConstraintParser.parseBoundSize(str, false, true));
        return this;
    }

    public final LC wrap() {
        setWrapAfter(0);
        return this;
    }

    public final LC wrapAfter(int i) {
        setWrapAfter(i);
        return this;
    }

    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        if (getClass() == LC.class) {
            LayoutUtil.writeAsXML(objectOutput, this);
        }
    }
}
