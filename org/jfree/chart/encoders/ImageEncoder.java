package org.jfree.chart.encoders;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

public interface ImageEncoder {
    void encode(BufferedImage bufferedImage, OutputStream outputStream) throws IOException;

    byte[] encode(BufferedImage bufferedImage) throws IOException;

    float getQuality();

    boolean isEncodingAlpha();

    void setEncodingAlpha(boolean z);

    void setQuality(float f);
}
