package org.jfree.data.xy;

public interface OHLCDataset extends XYDataset {
    Number getClose(int i, int i2);

    double getCloseValue(int i, int i2);

    Number getHigh(int i, int i2);

    double getHighValue(int i, int i2);

    Number getLow(int i, int i2);

    double getLowValue(int i, int i2);

    Number getOpen(int i, int i2);

    double getOpenValue(int i, int i2);

    Number getVolume(int i, int i2);

    double getVolumeValue(int i, int i2);
}
