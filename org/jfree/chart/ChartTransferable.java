package org.jfree.chart;

import java.awt.Graphics2D;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.awt.image.BufferedImage;
import java.io.IOException;
import org.jfree.data.xy.NormalizedMatrixSeries;

public class ChartTransferable implements Transferable {
    private JFreeChart chart;
    private int height;
    final DataFlavor imageFlavor;
    private int maxDrawHeight;
    private int maxDrawWidth;
    private int minDrawHeight;
    private int minDrawWidth;
    private int width;

    public ChartTransferable(JFreeChart chart, int width, int height) {
        this(chart, width, height, true);
    }

    public ChartTransferable(JFreeChart chart, int width, int height, boolean cloneData) {
        this(chart, width, height, 0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE, true);
    }

    public ChartTransferable(JFreeChart chart, int width, int height, int minDrawW, int minDrawH, int maxDrawW, int maxDrawH, boolean cloneData) {
        this.imageFlavor = new DataFlavor("image/x-java-image; class=java.awt.Image", "Image");
        try {
            this.chart = (JFreeChart) chart.clone();
        } catch (CloneNotSupportedException e) {
            this.chart = chart;
        }
        this.width = width;
        this.height = height;
        this.minDrawWidth = minDrawW;
        this.minDrawHeight = minDrawH;
        this.maxDrawWidth = maxDrawW;
        this.maxDrawHeight = maxDrawH;
    }

    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{this.imageFlavor};
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return this.imageFlavor.equals(flavor);
    }

    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (this.imageFlavor.equals(flavor)) {
            return createBufferedImage(this.chart, this.width, this.height, this.minDrawWidth, this.minDrawHeight, this.maxDrawWidth, this.maxDrawHeight);
        }
        throw new UnsupportedFlavorException(flavor);
    }

    private BufferedImage createBufferedImage(JFreeChart chart, int w, int h, int minDrawW, int minDrawH, int maxDrawW, int maxDrawH) {
        BufferedImage image = new BufferedImage(w, h, 2);
        Graphics2D g2 = image.createGraphics();
        boolean scale = false;
        double drawWidth = (double) w;
        double drawHeight = (double) h;
        double scaleX = NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR;
        double scaleY = NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR;
        if (drawWidth < ((double) minDrawW)) {
            scaleX = drawWidth / ((double) minDrawW);
            drawWidth = (double) minDrawW;
            scale = true;
        } else if (drawWidth > ((double) maxDrawW)) {
            scaleX = drawWidth / ((double) maxDrawW);
            drawWidth = (double) maxDrawW;
            scale = true;
        }
        if (drawHeight < ((double) minDrawH)) {
            scaleY = drawHeight / ((double) minDrawH);
            drawHeight = (double) minDrawH;
            scale = true;
        } else if (drawHeight > ((double) maxDrawH)) {
            scaleY = drawHeight / ((double) maxDrawH);
            drawHeight = (double) maxDrawH;
            scale = true;
        }
        Rectangle2D chartArea = new Double(0.0d, 0.0d, drawWidth, drawHeight);
        if (scale) {
            g2.transform(AffineTransform.getScaleInstance(scaleX, scaleY));
        }
        chart.draw(g2, chartArea, null, null);
        g2.dispose();
        return image;
    }
}
