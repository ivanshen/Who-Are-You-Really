package org.jfree.date;

public abstract class AnnualDateRule implements Cloneable {
    public abstract SerialDate getDate(int i);

    protected AnnualDateRule() {
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
