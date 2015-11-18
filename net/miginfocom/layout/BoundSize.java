package net.miginfocom.layout;

import java.beans.Encoder;
import java.beans.Expression;
import java.beans.PersistenceDelegate;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import org.jfree.util.LogTarget;

public class BoundSize implements Serializable {
    public static final BoundSize NULL_SIZE;
    public static final BoundSize ZERO_PIXEL;
    private static final long serialVersionUID = 1;
    private final transient boolean gapPush;
    private final transient UnitValue max;
    private final transient UnitValue min;
    private final transient UnitValue pref;

    static class 1 extends PersistenceDelegate {
        1() {
        }

        protected Expression instantiate(Object obj, Encoder encoder) {
            BoundSize boundSize = (BoundSize) obj;
            return new Expression(obj, BoundSize.class, "new", new Object[]{boundSize.getMin(), boundSize.getPreferred(), boundSize.getMax(), Boolean.valueOf(boundSize.getGapPush()), boundSize.getConstraintString()});
        }
    }

    static {
        NULL_SIZE = new BoundSize(null, null);
        ZERO_PIXEL = new BoundSize(UnitValue.ZERO, "0px");
        LayoutUtil.setDelegate(BoundSize.class, new 1());
    }

    public BoundSize(UnitValue unitValue, String str) {
        this(unitValue, unitValue, unitValue, str);
    }

    public BoundSize(UnitValue unitValue, UnitValue unitValue2, UnitValue unitValue3, String str) {
        this(unitValue, unitValue2, unitValue3, false, str);
    }

    public BoundSize(UnitValue unitValue, UnitValue unitValue2, UnitValue unitValue3, boolean z, String str) {
        this.min = unitValue;
        this.pref = unitValue2;
        this.max = unitValue3;
        this.gapPush = z;
        LayoutUtil.putCCString(this, str);
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        LayoutUtil.setSerializedObject(this, LayoutUtil.readAsXML(objectInputStream));
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        if (getClass() == BoundSize.class) {
            LayoutUtil.writeAsXML(objectOutputStream, this);
        }
    }

    void checkNotLinked() {
        if ((this.min != null && this.min.isLinkedDeep()) || ((this.pref != null && this.pref.isLinkedDeep()) || (this.max != null && this.max.isLinkedDeep()))) {
            throw new IllegalArgumentException("Size may not contain links");
        }
    }

    public int constrain(int i, float f, ContainerWrapper containerWrapper) {
        if (this.max != null) {
            i = Math.min(i, this.max.getPixels(f, containerWrapper, containerWrapper));
        }
        return this.min != null ? Math.max(i, this.min.getPixels(f, containerWrapper, containerWrapper)) : i;
    }

    String getConstraintString() {
        String cCString = LayoutUtil.getCCString(this);
        if (cCString != null) {
            return cCString;
        }
        if (this.min == this.pref && this.pref == this.max) {
            return this.min != null ? this.min.getConstraintString() + "!" : "null";
        } else {
            StringBuilder stringBuilder = new StringBuilder(16);
            if (this.min != null) {
                stringBuilder.append(this.min.getConstraintString()).append(':');
            }
            if (this.pref != null) {
                if (this.min == null && this.max != null) {
                    stringBuilder.append(":");
                }
                stringBuilder.append(this.pref.getConstraintString());
            } else if (this.min != null) {
                stringBuilder.append('n');
            }
            if (this.max != null) {
                stringBuilder.append(stringBuilder.length() == 0 ? "::" : ":").append(this.max.getConstraintString());
            }
            if (this.gapPush) {
                if (stringBuilder.length() > 0) {
                    stringBuilder.append(':');
                }
                stringBuilder.append("push");
            }
            return stringBuilder.toString();
        }
    }

    public boolean getGapPush() {
        return this.gapPush;
    }

    public final UnitValue getMax() {
        return this.max;
    }

    public final UnitValue getMin() {
        return this.min;
    }

    final int[] getPixelSizes(float f, ContainerWrapper containerWrapper, ComponentWrapper componentWrapper) {
        int i = 0;
        int[] iArr = new int[3];
        iArr[0] = this.min != null ? this.min.getPixels(f, containerWrapper, componentWrapper) : 0;
        if (this.pref != null) {
            i = this.pref.getPixels(f, containerWrapper, componentWrapper);
        }
        iArr[1] = i;
        iArr[2] = this.max != null ? this.max.getPixels(f, containerWrapper, componentWrapper) : 2097051;
        return iArr;
    }

    public final UnitValue getPreferred() {
        return this.pref;
    }

    final UnitValue getSize(int i) {
        switch (i) {
            case LogTarget.ERROR /*0*/:
                return this.min;
            case LogTarget.WARN /*1*/:
                return this.pref;
            case LogTarget.INFO /*2*/:
                return this.max;
            default:
                throw new IllegalArgumentException("Unknown size: " + i);
        }
    }

    public boolean isUnset() {
        return this == ZERO_PIXEL || (this.pref == null && this.min == null && this.max == null && !this.gapPush);
    }

    protected Object readResolve() throws ObjectStreamException {
        return LayoutUtil.getSerializedObject(this);
    }
}
