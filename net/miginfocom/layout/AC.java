package net.miginfocom.layout;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;
import java.util.ArrayList;
import org.jfree.chart.plot.Plot;

public final class AC implements Externalizable {
    private final ArrayList<DimConstraint> cList;
    private transient int curIx;

    public AC() {
        this.cList = new ArrayList(8);
        this.curIx = 0;
        this.cList.add(new DimConstraint());
    }

    private void makeSize(int i) {
        if (this.cList.size() <= i) {
            this.cList.ensureCapacity(i);
            for (int size = this.cList.size(); size <= i; size++) {
                this.cList.add(new DimConstraint());
            }
        }
    }

    private Object readResolve() throws ObjectStreamException {
        return LayoutUtil.getSerializedObject(this);
    }

    public final AC align(String str) {
        return align(str, this.curIx);
    }

    public final AC align(String str, int... iArr) {
        UnitValue parseAlignKeywords = ConstraintParser.parseAlignKeywords(str, true);
        UnitValue parseAlignKeywords2 = parseAlignKeywords == null ? ConstraintParser.parseAlignKeywords(str, false) : parseAlignKeywords;
        for (int length = iArr.length - 1; length >= 0; length--) {
            int i = iArr[length];
            makeSize(i);
            ((DimConstraint) this.cList.get(i)).setAlign(parseAlignKeywords2);
        }
        return this;
    }

    public final AC count(int i) {
        makeSize(i);
        return this;
    }

    public final AC fill() {
        return fill(this.curIx);
    }

    public final AC fill(int... iArr) {
        for (int length = iArr.length - 1; length >= 0; length--) {
            int i = iArr[length];
            makeSize(i);
            ((DimConstraint) this.cList.get(i)).setFill(true);
        }
        return this;
    }

    public final AC gap() {
        this.curIx++;
        return this;
    }

    public final AC gap(String str) {
        int[] iArr = new int[1];
        int i = this.curIx;
        this.curIx = i + 1;
        iArr[0] = i;
        return gap(str, iArr);
    }

    public final AC gap(String str, int... iArr) {
        BoundSize parseBoundSize = str != null ? ConstraintParser.parseBoundSize(str, true, true) : null;
        for (int length = iArr.length - 1; length >= 0; length--) {
            int i = iArr[length];
            makeSize(i);
            if (parseBoundSize != null) {
                ((DimConstraint) this.cList.get(i)).setGapAfter(parseBoundSize);
            }
        }
        return this;
    }

    public final DimConstraint[] getConstaints() {
        return (DimConstraint[]) this.cList.toArray(new DimConstraint[this.cList.size()]);
    }

    public int getCount() {
        return this.cList.size();
    }

    public final AC grow() {
        return grow(Plot.DEFAULT_FOREGROUND_ALPHA, this.curIx);
    }

    public final AC grow(float f) {
        return grow(f, this.curIx);
    }

    public final AC grow(float f, int... iArr) {
        Float f2 = new Float(f);
        for (int length = iArr.length - 1; length >= 0; length--) {
            int i = iArr[length];
            makeSize(i);
            ((DimConstraint) this.cList.get(i)).setGrow(f2);
        }
        return this;
    }

    public final AC growPrio(int i) {
        return growPrio(i, this.curIx);
    }

    public final AC growPrio(int i, int... iArr) {
        for (int length = iArr.length - 1; length >= 0; length--) {
            int i2 = iArr[length];
            makeSize(i2);
            ((DimConstraint) this.cList.get(i2)).setGrowPriority(i);
        }
        return this;
    }

    public final AC index(int i) {
        makeSize(i);
        this.curIx = i;
        return this;
    }

    public final AC noGrid() {
        return noGrid(this.curIx);
    }

    public final AC noGrid(int... iArr) {
        for (int length = iArr.length - 1; length >= 0; length--) {
            int i = iArr[length];
            makeSize(i);
            ((DimConstraint) this.cList.get(i)).setNoGrid(true);
        }
        return this;
    }

    public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
        LayoutUtil.setSerializedObject(this, LayoutUtil.readAsXML(objectInput));
    }

    public final void setConstaints(DimConstraint[] dimConstraintArr) {
        int i = 0;
        if (dimConstraintArr == null || dimConstraintArr.length < 1) {
            dimConstraintArr = new DimConstraint[]{new DimConstraint()};
        }
        this.cList.clear();
        this.cList.ensureCapacity(dimConstraintArr.length);
        while (i < dimConstraintArr.length) {
            this.cList.add(dimConstraintArr[i]);
            i++;
        }
    }

    public final AC shrink() {
        return shrink(100.0f, this.curIx);
    }

    public final AC shrink(float f) {
        return shrink(f, this.curIx);
    }

    public final AC shrink(float f, int... iArr) {
        Float f2 = new Float(f);
        for (int length = iArr.length - 1; length >= 0; length--) {
            int i = iArr[length];
            makeSize(i);
            ((DimConstraint) this.cList.get(i)).setShrink(f2);
        }
        return this;
    }

    public final AC shrinkPrio(int i) {
        return shrinkPrio(i, this.curIx);
    }

    public final AC shrinkPrio(int i, int... iArr) {
        for (int length = iArr.length - 1; length >= 0; length--) {
            int i2 = iArr[length];
            makeSize(i2);
            ((DimConstraint) this.cList.get(i2)).setShrinkPriority(i);
        }
        return this;
    }

    public final AC shrinkWeight(float f) {
        return shrink(f);
    }

    public final AC shrinkWeight(float f, int... iArr) {
        return shrink(f, iArr);
    }

    public final AC size(String str) {
        return size(str, this.curIx);
    }

    public final AC size(String str, int... iArr) {
        BoundSize parseBoundSize = ConstraintParser.parseBoundSize(str, false, true);
        for (int length = iArr.length - 1; length >= 0; length--) {
            int i = iArr[length];
            makeSize(i);
            ((DimConstraint) this.cList.get(i)).setSize(parseBoundSize);
        }
        return this;
    }

    public final AC sizeGroup() {
        return sizeGroup("", this.curIx);
    }

    public final AC sizeGroup(String str) {
        return sizeGroup(str, this.curIx);
    }

    public final AC sizeGroup(String str, int... iArr) {
        for (int length = iArr.length - 1; length >= 0; length--) {
            int i = iArr[length];
            makeSize(i);
            ((DimConstraint) this.cList.get(i)).setSizeGroup(str);
        }
        return this;
    }

    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        if (getClass() == AC.class) {
            LayoutUtil.writeAsXML(objectOutput, this);
        }
    }
}
