package org.jfree.chart.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.Serializable;
import org.jfree.chart.HashUtilities;
import org.jfree.chart.JFreeChart;

public class DefaultShadowGenerator implements ShadowGenerator, Serializable {
    private static final long serialVersionUID = 2732993885591386064L;
    private double angle;
    private int distance;
    private Color shadowColor;
    private float shadowOpacity;
    private int shadowSize;

    public DefaultShadowGenerator() {
        this(5, Color.black, JFreeChart.DEFAULT_BACKGROUND_IMAGE_ALPHA, 5, -0.7853981633974483d);
    }

    public DefaultShadowGenerator(int size, Color color, float opacity, int distance, double angle) {
        ParamChecks.nullNotPermitted(color, "color");
        this.shadowSize = size;
        this.shadowColor = color;
        this.shadowOpacity = opacity;
        this.distance = distance;
        this.angle = angle;
    }

    public int getShadowSize() {
        return this.shadowSize;
    }

    public Color getShadowColor() {
        return this.shadowColor;
    }

    public float getShadowOpacity() {
        return this.shadowOpacity;
    }

    public int getDistance() {
        return this.distance;
    }

    public double getAngle() {
        return this.angle;
    }

    public int calculateOffsetX() {
        return ((int) (Math.cos(this.angle) * ((double) this.distance))) - this.shadowSize;
    }

    public int calculateOffsetY() {
        return (-((int) (Math.sin(this.angle) * ((double) this.distance)))) - this.shadowSize;
    }

    public BufferedImage createDropShadow(BufferedImage source) {
        BufferedImage subject = new BufferedImage(source.getWidth() + (this.shadowSize * 2), source.getHeight() + (this.shadowSize * 2), 2);
        Graphics2D g2 = subject.createGraphics();
        g2.drawImage(source, null, this.shadowSize, this.shadowSize);
        g2.dispose();
        applyShadow(subject);
        return subject;
    }

    protected void applyShadow(BufferedImage image) {
        int x;
        int dstWidth = image.getWidth();
        int dstHeight = image.getHeight();
        int left = (this.shadowSize - 1) >> 1;
        int right = this.shadowSize - left;
        int xStart = left;
        int xStop = dstWidth - right;
        int yStart = left;
        int yStop = dstHeight - right;
        int shadowRgb = this.shadowColor.getRGB() & 16777215;
        int[] aHistory = new int[this.shadowSize];
        int[] dataBuffer = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        int lastPixelOffset = right * dstWidth;
        float sumDivider = this.shadowOpacity / ((float) this.shadowSize);
        int y = 0;
        int bufferOffset = 0;
        while (y < dstHeight) {
            int aSum = 0;
            int historyIdx = 0;
            x = 0;
            while (true) {
                int i = this.shadowSize;
                if (x >= r0) {
                    break;
                }
                int a = dataBuffer[bufferOffset] >>> 24;
                aHistory[x] = a;
                aSum += a;
                x++;
                bufferOffset++;
            }
            bufferOffset -= right;
            x = xStart;
            while (x < xStop) {
                dataBuffer[bufferOffset] = (((int) (((float) aSum) * sumDivider)) << 24) | shadowRgb;
                aSum -= aHistory[historyIdx];
                a = dataBuffer[bufferOffset + right] >>> 24;
                aHistory[historyIdx] = a;
                aSum += a;
                historyIdx++;
                i = this.shadowSize;
                if (historyIdx >= r0) {
                    historyIdx -= this.shadowSize;
                }
                x++;
                bufferOffset++;
            }
            y++;
            bufferOffset = y * dstWidth;
        }
        x = 0;
        bufferOffset = 0;
        while (x < dstWidth) {
            aSum = 0;
            historyIdx = 0;
            y = 0;
            while (y < this.shadowSize) {
                a = dataBuffer[bufferOffset] >>> 24;
                aHistory[y] = a;
                aSum += a;
                y++;
                bufferOffset += dstWidth;
            }
            bufferOffset -= lastPixelOffset;
            y = yStart;
            while (y < yStop) {
                dataBuffer[bufferOffset] = (((int) (((float) aSum) * sumDivider)) << 24) | shadowRgb;
                aSum -= aHistory[historyIdx];
                a = dataBuffer[bufferOffset + lastPixelOffset] >>> 24;
                aHistory[historyIdx] = a;
                aSum += a;
                historyIdx++;
                i = this.shadowSize;
                if (historyIdx >= r0) {
                    historyIdx -= this.shadowSize;
                }
                y++;
                bufferOffset += dstWidth;
            }
            x++;
            bufferOffset = x;
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DefaultShadowGenerator)) {
            return false;
        }
        DefaultShadowGenerator that = (DefaultShadowGenerator) obj;
        if (this.shadowSize != that.shadowSize) {
            return false;
        }
        if (!this.shadowColor.equals(that.shadowColor)) {
            return false;
        }
        if (this.shadowOpacity != that.shadowOpacity) {
            return false;
        }
        if (this.distance != that.distance) {
            return false;
        }
        if (this.angle != that.angle) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return HashUtilities.hashCode(HashUtilities.hashCode(HashUtilities.hashCode(HashUtilities.hashCode(HashUtilities.hashCode(17, this.shadowSize), this.shadowColor), (double) this.shadowOpacity), this.distance), this.angle);
    }
}
