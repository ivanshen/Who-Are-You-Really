package org.jfree.chart.util;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.util.Hashtable;

public class PaintAlpha {
    private static final double FACTOR = 0.7d;
    private static boolean legacyAlpha;

    static {
        legacyAlpha = false;
    }

    public static boolean setLegacyAlpha(boolean legacyAlpha) {
        boolean old = legacyAlpha;
        legacyAlpha = legacyAlpha;
        return old;
    }

    public static Paint darker(Paint paint) {
        if (paint instanceof Color) {
            return darker((Color) paint);
        }
        if (legacyAlpha) {
            return paint;
        }
        if (paint instanceof GradientPaint) {
            return darker((GradientPaint) paint);
        }
        if (paint instanceof LinearGradientPaint) {
            return darkerLinearGradientPaint((LinearGradientPaint) paint);
        }
        if (paint instanceof RadialGradientPaint) {
            return darkerRadialGradientPaint((RadialGradientPaint) paint);
        }
        if (!(paint instanceof TexturePaint)) {
            return paint;
        }
        try {
            return darkerTexturePaint((TexturePaint) paint);
        } catch (Exception e) {
            return paint;
        }
    }

    private static Color darker(Color paint) {
        return new Color((int) (((double) paint.getRed()) * FACTOR), (int) (((double) paint.getGreen()) * FACTOR), (int) (((double) paint.getBlue()) * FACTOR), paint.getAlpha());
    }

    private static GradientPaint darker(GradientPaint paint) {
        return new GradientPaint(paint.getPoint1(), darker(paint.getColor1()), paint.getPoint2(), darker(paint.getColor2()), paint.isCyclic());
    }

    private static Paint darkerLinearGradientPaint(LinearGradientPaint paint) {
        Color[] paintColors = paint.getColors();
        for (int i = 0; i < paintColors.length; i++) {
            paintColors[i] = darker(paintColors[i]);
        }
        return new LinearGradientPaint(paint.getStartPoint(), paint.getEndPoint(), paint.getFractions(), paintColors, paint.getCycleMethod(), paint.getColorSpace(), paint.getTransform());
    }

    private static Paint darkerRadialGradientPaint(RadialGradientPaint paint) {
        Color[] paintColors = paint.getColors();
        for (int i = 0; i < paintColors.length; i++) {
            paintColors[i] = darker(paintColors[i]);
        }
        return new RadialGradientPaint(paint.getCenterPoint(), paint.getRadius(), paint.getFocusPoint(), paint.getFractions(), paintColors, paint.getCycleMethod(), paint.getColorSpace(), paint.getTransform());
    }

    private static TexturePaint darkerTexturePaint(TexturePaint paint) {
        BufferedImage img;
        WritableRaster ras;
        int miX;
        int miY;
        int maY;
        int wid;
        int[] pix;
        if (paint.getImage().getColorModel().isAlphaPremultiplied()) {
            img = cloneImage(paint.getImage());
            ras = img.copyData(null);
            miX = ras.getMinX();
            miY = ras.getMinY();
            maY = ras.getMinY() + ras.getHeight();
            wid = ras.getWidth();
            pix = new int[(img.getSampleModel().getNumBands() * wid)];
        } else {
            img = cloneImage(paint.getImage());
            ras = img.copyData(null);
            miX = ras.getMinX();
            miY = ras.getMinY();
            maY = ras.getMinY() + ras.getHeight();
            wid = ras.getWidth();
            pix = new int[(img.getSampleModel().getNumBands() * wid)];
        }
        int y;
        int p;
        if (img.getColorModel() instanceof IndexColorModel) {
            int[] nco = new int[4];
            for (y = miY; y < maY; y++) {
                pix = ras.getPixels(miX, y, wid, 1, pix);
                for (p = 0; p < pix.length; p++) {
                    nco = img.getColorModel().getComponents(pix[p], nco, 0);
                    nco[0] = (int) (((double) nco[0]) * FACTOR);
                    nco[1] = (int) (((double) nco[1]) * FACTOR);
                    nco[2] = (int) (((double) nco[2]) * FACTOR);
                    pix[p] = img.getColorModel().getDataElement(nco, 0);
                }
                ras.setPixels(miX, y, wid, 1, pix);
            }
            img.setData(ras);
            return new TexturePaint(img, paint.getAnchorRect());
        } else if (img.getSampleModel().getNumBands() == 4) {
            for (y = miY; y < maY; y++) {
                pix = ras.getPixels(miX, y, wid, 1, pix);
                p = 0;
                while (p < pix.length) {
                    int i = p + 1;
                    pix[p] = (int) (((double) pix[p]) * FACTOR);
                    p = i + 1;
                    pix[i] = (int) (((double) pix[i]) * FACTOR);
                    i = p + 1;
                    pix[p] = (int) (((double) pix[p]) * FACTOR);
                    p = i + 1;
                }
                ras.setPixels(miX, y, wid, 1, pix);
            }
            img.setData(ras);
            return new TexturePaint(img, paint.getAnchorRect());
        } else {
            for (y = miY; y < maY; y++) {
                pix = ras.getPixels(miX, y, wid, 1, pix);
                for (p = 0; p < pix.length; p++) {
                    pix[p] = (int) (((double) pix[p]) * FACTOR);
                }
                ras.setPixels(miX, y, wid, 1, pix);
            }
            img.setData(ras);
            return new TexturePaint(img, paint.getAnchorRect());
        }
    }

    public static BufferedImage cloneImage(BufferedImage image) {
        WritableRaster rin = image.getRaster();
        WritableRaster ras = rin.createCompatibleWritableRaster();
        ras.setRect(rin);
        Hashtable props = null;
        String[] propNames = image.getPropertyNames();
        if (propNames != null) {
            props = new Hashtable();
            for (int i = 0; i < propNames.length; i++) {
                props.put(propNames[i], image.getProperty(propNames[i]));
            }
        }
        return new BufferedImage(image.getColorModel(), ras, image.isAlphaPremultiplied(), props);
    }
}
