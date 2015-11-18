package net.miginfocom.layout;

public abstract class LayoutCallback {
    public void correctBounds(ComponentWrapper componentWrapper) {
    }

    public UnitValue[] getPosition(ComponentWrapper componentWrapper) {
        return null;
    }

    public BoundSize[] getSize(ComponentWrapper componentWrapper) {
        return null;
    }
}
