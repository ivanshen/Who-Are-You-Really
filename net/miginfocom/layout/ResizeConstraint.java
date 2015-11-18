package net.miginfocom.layout;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;

final class ResizeConstraint implements Externalizable {
    static final Float WEIGHT_100;
    Float grow;
    int growPrio;
    Float shrink;
    int shrinkPrio;

    static {
        WEIGHT_100 = new Float(100.0f);
    }

    public ResizeConstraint() {
        this.grow = null;
        this.growPrio = 100;
        this.shrink = WEIGHT_100;
        this.shrinkPrio = 100;
    }

    ResizeConstraint(int i, Float f, int i2, Float f2) {
        this.grow = null;
        this.growPrio = 100;
        this.shrink = WEIGHT_100;
        this.shrinkPrio = 100;
        this.shrinkPrio = i;
        this.shrink = f;
        this.growPrio = i2;
        this.grow = f2;
    }

    private Object readResolve() throws ObjectStreamException {
        return LayoutUtil.getSerializedObject(this);
    }

    public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
        LayoutUtil.setSerializedObject(this, LayoutUtil.readAsXML(objectInput));
    }

    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        if (getClass() == ResizeConstraint.class) {
            LayoutUtil.writeAsXML(objectOutput, this);
        }
    }
}
