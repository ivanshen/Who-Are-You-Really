package org.jfree.chart.util;

import java.awt.image.BufferedImage;

public interface ShadowGenerator {
    int calculateOffsetX();

    int calculateOffsetY();

    BufferedImage createDropShadow(BufferedImage bufferedImage);
}
