package org.jfree.data.xy;

public interface WindDataset extends XYDataset {
    Number getWindDirection(int i, int i2);

    Number getWindForce(int i, int i2);
}
