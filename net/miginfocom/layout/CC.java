package net.miginfocom.layout;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;
import java.util.ArrayList;
import org.jfree.util.LogTarget;

public final class CC implements Externalizable {
    private static final BoundSize DEF_GAP;
    static final String[] DOCK_SIDES;
    private static final String[] EMPTY_ARR;
    private boolean boundsInGrid;
    private int cellX;
    private int cellY;
    private int dock;
    private boolean external;
    private Boolean flowX;
    private int hideMode;
    private DimConstraint hor;
    private String id;
    private transient String[] linkTargets;
    private BoundSize newline;
    private UnitValue[] padding;
    private UnitValue[] pos;
    private Float pushX;
    private Float pushY;
    private int skip;
    private int spanX;
    private int spanY;
    private int split;
    private String tag;
    private DimConstraint ver;
    private BoundSize wrap;

    static {
        DEF_GAP = BoundSize.NULL_SIZE;
        DOCK_SIDES = new String[]{"north", "west", "south", "east"};
        EMPTY_ARR = new String[0];
    }

    public CC() {
        this.dock = -1;
        this.pos = null;
        this.padding = null;
        this.flowX = null;
        this.skip = 0;
        this.split = 1;
        this.spanX = 1;
        this.spanY = 1;
        this.cellX = -1;
        this.cellY = 0;
        this.tag = null;
        this.id = null;
        this.hideMode = -1;
        this.hor = new DimConstraint();
        this.ver = new DimConstraint();
        this.newline = null;
        this.wrap = null;
        this.boundsInGrid = true;
        this.external = false;
        this.pushX = null;
        this.pushY = null;
        this.linkTargets = null;
    }

    private void addLinkTargetIDs(ArrayList<String> arrayList, UnitValue unitValue) {
        if (unitValue != null) {
            String linkTargetId = unitValue.getLinkTargetId();
            if (linkTargetId != null) {
                arrayList.add(linkTargetId);
                return;
            }
            for (int subUnitCount = unitValue.getSubUnitCount() - 1; subUnitCount >= 0; subUnitCount--) {
                UnitValue subUnitValue = unitValue.getSubUnitValue(subUnitCount);
                if (subUnitValue.isLinkedDeep()) {
                    addLinkTargetIDs(arrayList, subUnitValue);
                }
            }
        }
    }

    private final CC corrPos(String str, int i) {
        UnitValue[] pos = getPos();
        if (pos == null) {
            pos = new UnitValue[4];
        }
        pos[i] = ConstraintParser.parseUnitValue(str, i % 2 == 0);
        setPos(pos);
        setBoundsInGrid(true);
        return this;
    }

    private Object readResolve() throws ObjectStreamException {
        return LayoutUtil.getSerializedObject(this);
    }

    public final CC alignX(String str) {
        this.hor.setAlign(ConstraintParser.parseUnitValueOrAlign(str, true, null));
        return this;
    }

    public final CC alignY(String str) {
        this.ver.setAlign(ConstraintParser.parseUnitValueOrAlign(str, false, null));
        return this;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final net.miginfocom.layout.CC cell(int... r4) {
        /*
        r3 = this;
        r0 = r4.length;
        switch(r0) {
            case 1: goto L_0x0030;
            case 2: goto L_0x002a;
            case 3: goto L_0x0024;
            case 4: goto L_0x001e;
            default: goto L_0x0004;
        };
    L_0x0004:
        r0 = new java.lang.IllegalArgumentException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Illegal argument count: ";
        r1 = r1.append(r2);
        r2 = r4.length;
        r1 = r1.append(r2);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
    L_0x001e:
        r0 = 3;
        r0 = r4[r0];
        r3.setSpanY(r0);
    L_0x0024:
        r0 = 2;
        r0 = r4[r0];
        r3.setSpanX(r0);
    L_0x002a:
        r0 = 1;
        r0 = r4[r0];
        r3.setCellY(r0);
    L_0x0030:
        r0 = 0;
        r0 = r4[r0];
        r3.setCellX(r0);
        return r3;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.miginfocom.layout.CC.cell(int[]):net.miginfocom.layout.CC");
    }

    public final CC dockEast() {
        setDockSide(3);
        return this;
    }

    public final CC dockNorth() {
        setDockSide(0);
        return this;
    }

    public final CC dockSouth() {
        setDockSide(2);
        return this;
    }

    public final CC dockWest() {
        setDockSide(1);
        return this;
    }

    public final CC endGroup(String... strArr) {
        switch (strArr.length) {
            case LogTarget.WARN /*1*/:
                break;
            case LogTarget.INFO /*2*/:
                endGroupY(strArr[1]);
                break;
            default:
                throw new IllegalArgumentException("Illegal argument count: " + strArr.length);
        }
        endGroupX(strArr[0]);
        return this;
    }

    public final CC endGroupX(String str) {
        this.hor.setEndGroup(str);
        return this;
    }

    public final CC endGroupY(String str) {
        this.ver.setEndGroup(str);
        return this;
    }

    public final CC external() {
        setExternal(true);
        return this;
    }

    public final CC flowX() {
        setFlowX(Boolean.TRUE);
        return this;
    }

    public final CC flowY() {
        setFlowX(Boolean.FALSE);
        return this;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final net.miginfocom.layout.CC gap(java.lang.String... r4) {
        /*
        r3 = this;
        r0 = r4.length;
        switch(r0) {
            case 1: goto L_0x0030;
            case 2: goto L_0x002a;
            case 3: goto L_0x0024;
            case 4: goto L_0x001e;
            default: goto L_0x0004;
        };
    L_0x0004:
        r0 = new java.lang.IllegalArgumentException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Illegal argument count: ";
        r1 = r1.append(r2);
        r2 = r4.length;
        r1 = r1.append(r2);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
    L_0x001e:
        r0 = 3;
        r0 = r4[r0];
        r3.gapBottom(r0);
    L_0x0024:
        r0 = 2;
        r0 = r4[r0];
        r3.gapTop(r0);
    L_0x002a:
        r0 = 1;
        r0 = r4[r0];
        r3.gapRight(r0);
    L_0x0030:
        r0 = 0;
        r0 = r4[r0];
        r3.gapLeft(r0);
        return r3;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.miginfocom.layout.CC.gap(java.lang.String[]):net.miginfocom.layout.CC");
    }

    public final CC gapAfter(String str) {
        this.hor.setGapAfter(ConstraintParser.parseBoundSize(str, true, true));
        return this;
    }

    public final CC gapBefore(String str) {
        this.hor.setGapBefore(ConstraintParser.parseBoundSize(str, true, true));
        return this;
    }

    public final CC gapBottom(String str) {
        this.ver.setGapAfter(ConstraintParser.parseBoundSize(str, true, false));
        return this;
    }

    public final CC gapLeft(String str) {
        this.hor.setGapBefore(ConstraintParser.parseBoundSize(str, true, true));
        return this;
    }

    public final CC gapRight(String str) {
        this.hor.setGapAfter(ConstraintParser.parseBoundSize(str, true, true));
        return this;
    }

    public final CC gapTop(String str) {
        this.ver.setGapBefore(ConstraintParser.parseBoundSize(str, true, false));
        return this;
    }

    public final CC gapX(String str, String str2) {
        if (str != null) {
            this.hor.setGapBefore(ConstraintParser.parseBoundSize(str, true, true));
        }
        if (str2 != null) {
            this.hor.setGapAfter(ConstraintParser.parseBoundSize(str2, true, true));
        }
        return this;
    }

    public final CC gapY(String str, String str2) {
        if (str != null) {
            this.ver.setGapBefore(ConstraintParser.parseBoundSize(str, true, false));
        }
        if (str2 != null) {
            this.ver.setGapAfter(ConstraintParser.parseBoundSize(str2, true, false));
        }
        return this;
    }

    public int getCellX() {
        return this.cellX;
    }

    public int getCellY() {
        return this.cellX < 0 ? -1 : this.cellY;
    }

    public DimConstraint getDimConstraint(boolean z) {
        return z ? this.hor : this.ver;
    }

    public int getDockSide() {
        return this.dock;
    }

    public Boolean getFlowX() {
        return this.flowX;
    }

    public int getHideMode() {
        return this.hideMode;
    }

    public DimConstraint getHorizontal() {
        return this.hor;
    }

    public String getId() {
        return this.id;
    }

    String[] getLinkTargets() {
        if (this.linkTargets == null) {
            ArrayList arrayList = new ArrayList(2);
            if (this.pos != null) {
                for (UnitValue addLinkTargetIDs : this.pos) {
                    addLinkTargetIDs(arrayList, addLinkTargetIDs);
                }
            }
            this.linkTargets = arrayList.size() == 0 ? EMPTY_ARR : (String[]) arrayList.toArray(new String[arrayList.size()]);
        }
        return this.linkTargets;
    }

    public BoundSize getNewlineGapSize() {
        return this.newline == DEF_GAP ? null : this.newline;
    }

    public UnitValue[] getPadding() {
        if (this.padding == null) {
            return null;
        }
        return new UnitValue[]{this.padding[0], this.padding[1], this.padding[2], this.padding[3]};
    }

    public UnitValue[] getPos() {
        if (this.pos == null) {
            return null;
        }
        return new UnitValue[]{this.pos[0], this.pos[1], this.pos[2], this.pos[3]};
    }

    public Float getPushX() {
        return this.pushX;
    }

    public Float getPushY() {
        return this.pushY;
    }

    public int getSkip() {
        return this.skip;
    }

    public int getSpanX() {
        return this.spanX;
    }

    public int getSpanY() {
        return this.spanY;
    }

    public int getSplit() {
        return this.split;
    }

    public String getTag() {
        return this.tag;
    }

    public DimConstraint getVertical() {
        return this.ver;
    }

    public BoundSize getWrapGapSize() {
        return this.wrap == DEF_GAP ? null : this.wrap;
    }

    public final CC grow() {
        growX();
        growY();
        return this;
    }

    public final CC grow(float... fArr) {
        switch (fArr.length) {
            case LogTarget.WARN /*1*/:
                break;
            case LogTarget.INFO /*2*/:
                growY(Float.valueOf(fArr[1]));
                break;
            default:
                throw new IllegalArgumentException("Illegal argument count: " + fArr.length);
        }
        growX(fArr[0]);
        return this;
    }

    public final CC growPrio(int... iArr) {
        switch (iArr.length) {
            case LogTarget.WARN /*1*/:
                break;
            case LogTarget.INFO /*2*/:
                growPrioY(iArr[1]);
                break;
            default:
                throw new IllegalArgumentException("Illegal argument count: " + iArr.length);
        }
        growPrioX(iArr[0]);
        return this;
    }

    public final CC growPrioX(int i) {
        this.hor.setGrowPriority(i);
        return this;
    }

    public final CC growPrioY(int i) {
        this.ver.setGrowPriority(i);
        return this;
    }

    public final CC growX() {
        this.hor.setGrow(ResizeConstraint.WEIGHT_100);
        return this;
    }

    public final CC growX(float f) {
        this.hor.setGrow(new Float(f));
        return this;
    }

    public final CC growY() {
        this.ver.setGrow(ResizeConstraint.WEIGHT_100);
        return this;
    }

    public final CC growY(Float f) {
        this.ver.setGrow(f);
        return this;
    }

    public final CC height(String str) {
        this.ver.setSize(ConstraintParser.parseBoundSize(str, false, false));
        return this;
    }

    public final CC hideMode(int i) {
        setHideMode(i);
        return this;
    }

    public final CC id(String str) {
        setId(str);
        return this;
    }

    public boolean isBoundsInGrid() {
        return this.boundsInGrid;
    }

    public boolean isExternal() {
        return this.external;
    }

    public boolean isNewline() {
        return this.newline != null;
    }

    public boolean isWrap() {
        return this.wrap != null;
    }

    public final CC maxHeight(String str) {
        this.ver.setSize(LayoutUtil.derive(this.ver.getSize(), null, null, ConstraintParser.parseUnitValue(str, false)));
        return this;
    }

    public final CC maxWidth(String str) {
        this.hor.setSize(LayoutUtil.derive(this.hor.getSize(), null, null, ConstraintParser.parseUnitValue(str, true)));
        return this;
    }

    public final CC minHeight(String str) {
        this.ver.setSize(LayoutUtil.derive(this.ver.getSize(), ConstraintParser.parseUnitValue(str, false), null, null));
        return this;
    }

    public final CC minWidth(String str) {
        this.hor.setSize(LayoutUtil.derive(this.hor.getSize(), ConstraintParser.parseUnitValue(str, true), null, null));
        return this;
    }

    public final CC newline() {
        setNewline(true);
        return this;
    }

    public final CC newline(String str) {
        boolean z = (this.flowX == null || this.flowX.booleanValue()) ? false : true;
        BoundSize parseBoundSize = ConstraintParser.parseBoundSize(str, true, z);
        if (parseBoundSize != null) {
            setNewlineGapSize(parseBoundSize);
        } else {
            setNewline(true);
        }
        return this;
    }

    public final CC pad(int i, int i2, int i3, int i4) {
        setPadding(new UnitValue[]{new UnitValue((float) i), new UnitValue((float) i2), new UnitValue((float) i3), new UnitValue((float) i4)});
        return this;
    }

    public final CC pad(String str) {
        setPadding(str != null ? ConstraintParser.parseInsets(str, false) : null);
        return this;
    }

    public final CC pos(String str, String str2) {
        UnitValue[] pos = getPos();
        if (pos == null) {
            pos = new UnitValue[4];
        }
        pos[0] = ConstraintParser.parseUnitValue(str, true);
        pos[1] = ConstraintParser.parseUnitValue(str2, false);
        setPos(pos);
        setBoundsInGrid(false);
        return this;
    }

    public final CC pos(String str, String str2, String str3, String str4) {
        setPos(new UnitValue[]{ConstraintParser.parseUnitValue(str, true), ConstraintParser.parseUnitValue(str2, false), ConstraintParser.parseUnitValue(str3, true), ConstraintParser.parseUnitValue(str4, false)});
        setBoundsInGrid(false);
        return this;
    }

    public final CC push() {
        return pushX().pushY();
    }

    public final CC push(Float f, Float f2) {
        return pushX(f).pushY(f2);
    }

    public final CC pushX() {
        return pushX(ResizeConstraint.WEIGHT_100);
    }

    public final CC pushX(Float f) {
        setPushX(f);
        return this;
    }

    public final CC pushY() {
        return pushY(ResizeConstraint.WEIGHT_100);
    }

    public final CC pushY(Float f) {
        setPushY(f);
        return this;
    }

    public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
        LayoutUtil.setSerializedObject(this, LayoutUtil.readAsXML(objectInput));
    }

    void setBoundsInGrid(boolean z) {
        this.boundsInGrid = z;
    }

    public void setCellX(int i) {
        this.cellX = i;
    }

    public void setCellY(int i) {
        if (i < 0) {
            this.cellX = -1;
        }
        if (i < 0) {
            i = 0;
        }
        this.cellY = i;
    }

    public void setDockSide(int i) {
        if (i < -1 || i > 3) {
            throw new IllegalArgumentException("Illegal dock side: " + i);
        }
        this.dock = i;
    }

    public void setExternal(boolean z) {
        this.external = z;
    }

    public void setFlowX(Boolean bool) {
        this.flowX = bool;
    }

    public void setHideMode(int i) {
        if (i < -1 || i > 3) {
            throw new IllegalArgumentException("Wrong hideMode: " + i);
        }
        this.hideMode = i;
    }

    public void setHorizontal(DimConstraint dimConstraint) {
        if (dimConstraint == null) {
            dimConstraint = new DimConstraint();
        }
        this.hor = dimConstraint;
    }

    public void setId(String str) {
        this.id = str;
    }

    public void setNewline(boolean z) {
        BoundSize boundSize = z ? this.newline == null ? DEF_GAP : this.newline : null;
        this.newline = boundSize;
    }

    public void setNewlineGapSize(BoundSize boundSize) {
        if (boundSize == null) {
            boundSize = this.newline != null ? DEF_GAP : null;
        }
        this.newline = boundSize;
    }

    public void setPadding(UnitValue[] unitValueArr) {
        this.padding = unitValueArr != null ? new UnitValue[]{unitValueArr[0], unitValueArr[1], unitValueArr[2], unitValueArr[3]} : null;
    }

    public void setPos(UnitValue[] unitValueArr) {
        this.pos = unitValueArr != null ? new UnitValue[]{unitValueArr[0], unitValueArr[1], unitValueArr[2], unitValueArr[3]} : null;
        this.linkTargets = null;
    }

    public void setPushX(Float f) {
        this.pushX = f;
    }

    public void setPushY(Float f) {
        this.pushY = f;
    }

    public void setSkip(int i) {
        this.skip = i;
    }

    public void setSpanX(int i) {
        this.spanX = i;
    }

    public void setSpanY(int i) {
        this.spanY = i;
    }

    public void setSplit(int i) {
        this.split = i;
    }

    public void setTag(String str) {
        this.tag = str;
    }

    public void setVertical(DimConstraint dimConstraint) {
        if (dimConstraint == null) {
            dimConstraint = new DimConstraint();
        }
        this.ver = dimConstraint;
    }

    public void setWrap(boolean z) {
        BoundSize boundSize = z ? this.wrap == null ? DEF_GAP : this.wrap : null;
        this.wrap = boundSize;
    }

    public void setWrapGapSize(BoundSize boundSize) {
        if (boundSize == null) {
            boundSize = this.wrap != null ? DEF_GAP : null;
        }
        this.wrap = boundSize;
    }

    public final CC shrink(float... fArr) {
        switch (fArr.length) {
            case LogTarget.WARN /*1*/:
                break;
            case LogTarget.INFO /*2*/:
                shrinkY(fArr[1]);
                break;
            default:
                throw new IllegalArgumentException("Illegal argument count: " + fArr.length);
        }
        shrinkX(fArr[0]);
        return this;
    }

    public final CC shrinkPrio(int... iArr) {
        switch (iArr.length) {
            case LogTarget.WARN /*1*/:
                break;
            case LogTarget.INFO /*2*/:
                shrinkPrioY(iArr[1]);
                break;
            default:
                throw new IllegalArgumentException("Illegal argument count: " + iArr.length);
        }
        shrinkPrioX(iArr[0]);
        return this;
    }

    public final CC shrinkPrioX(int i) {
        this.hor.setShrinkPriority(i);
        return this;
    }

    public final CC shrinkPrioY(int i) {
        this.ver.setShrinkPriority(i);
        return this;
    }

    public final CC shrinkX(float f) {
        this.hor.setShrink(new Float(f));
        return this;
    }

    public final CC shrinkY(float f) {
        this.ver.setShrink(new Float(f));
        return this;
    }

    public final CC sizeGroup(String... strArr) {
        switch (strArr.length) {
            case LogTarget.WARN /*1*/:
                break;
            case LogTarget.INFO /*2*/:
                sizeGroupY(strArr[1]);
                break;
            default:
                throw new IllegalArgumentException("Illegal argument count: " + strArr.length);
        }
        sizeGroupX(strArr[0]);
        return this;
    }

    public final CC sizeGroupX(String str) {
        this.hor.setSizeGroup(str);
        return this;
    }

    public final CC sizeGroupY(String str) {
        this.ver.setSizeGroup(str);
        return this;
    }

    public final CC skip() {
        setSkip(1);
        return this;
    }

    public final CC skip(int i) {
        setSkip(i);
        return this;
    }

    public final CC span(int... iArr) {
        if (iArr == null || iArr.length == 0) {
            setSpanX(2097051);
            setSpanY(1);
        } else if (iArr.length == 1) {
            setSpanX(iArr[0]);
            setSpanY(1);
        } else {
            setSpanX(iArr[0]);
            setSpanY(iArr[1]);
        }
        return this;
    }

    public final CC spanX() {
        return spanX(2097051);
    }

    public final CC spanX(int i) {
        setSpanX(i);
        return this;
    }

    public final CC spanY() {
        return spanY(2097051);
    }

    public final CC spanY(int i) {
        setSpanY(i);
        return this;
    }

    public final CC split() {
        setSplit(2097051);
        return this;
    }

    public final CC split(int i) {
        setSplit(i);
        return this;
    }

    public final CC tag(String str) {
        setTag(str);
        return this;
    }

    public final CC width(String str) {
        this.hor.setSize(ConstraintParser.parseBoundSize(str, false, true));
        return this;
    }

    public final CC wrap() {
        setWrap(true);
        return this;
    }

    public final CC wrap(String str) {
        boolean z = (this.flowX == null || this.flowX.booleanValue()) ? false : true;
        BoundSize parseBoundSize = ConstraintParser.parseBoundSize(str, true, z);
        if (parseBoundSize != null) {
            setWrapGapSize(parseBoundSize);
        } else {
            setWrap(true);
        }
        return this;
    }

    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        if (getClass() == CC.class) {
            LayoutUtil.writeAsXML(objectOutput, this);
        }
    }

    public final CC x(String str) {
        return corrPos(str, 0);
    }

    public final CC x2(String str) {
        return corrPos(str, 2);
    }

    public final CC y(String str) {
        return corrPos(str, 1);
    }

    public final CC y2(String str) {
        return corrPos(str, 3);
    }
}
