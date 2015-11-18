package org.jfree.chart.util;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.imageio.ImageIO;
import org.jfree.chart.encoders.ImageFormat;
import org.jfree.ui.Drawable;

public class ExportUtils {
    public static boolean isJFreeSVGAvailable() {
        Class<?> svgClass = null;
        try {
            svgClass = Class.forName("org.jfree.graphics2d.svg.SVGGraphics2D");
        } catch (ClassNotFoundException e) {
        }
        return svgClass != null;
    }

    public static boolean isOrsonPDFAvailable() {
        Class<?> pdfDocumentClass = null;
        try {
            pdfDocumentClass = Class.forName("com.orsonpdf.PDFDocument");
        } catch (ClassNotFoundException e) {
        }
        return pdfDocumentClass != null;
    }

    public static void writeAsSVG(Drawable drawable, int w, int h, File file) {
        if (isJFreeSVGAvailable()) {
            ParamChecks.nullNotPermitted(drawable, "drawable");
            ParamChecks.nullNotPermitted(file, "file");
            try {
                Class<?> svg2Class = Class.forName("org.jfree.graphics2d.svg.SVGGraphics2D");
                Graphics2D svg2 = (Graphics2D) svg2Class.getConstructor(new Class[]{Integer.TYPE, Integer.TYPE}).newInstance(new Object[]{Integer.valueOf(w), Integer.valueOf(h)});
                drawable.draw(svg2, new Double(0.0d, 0.0d, (double) w, (double) h));
                Class<?> svgUtilsClass = Class.forName("org.jfree.graphics2d.svg.SVGUtils");
                Graphics2D graphics2D = svg2;
                String element = (String) svg2Class.getMethod("getSVGElement", (Class[]) null).invoke(graphics2D, (Object[]) null);
                Class<?> cls = svgUtilsClass;
                cls.getMethod("writeToSVG", new Class[]{File.class, String.class}).invoke(svgUtilsClass, new Object[]{file, element});
                return;
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            } catch (InstantiationException ex2) {
                throw new RuntimeException(ex2);
            } catch (IllegalAccessException ex3) {
                throw new RuntimeException(ex3);
            } catch (NoSuchMethodException ex4) {
                throw new RuntimeException(ex4);
            } catch (SecurityException ex5) {
                throw new RuntimeException(ex5);
            } catch (IllegalArgumentException ex6) {
                throw new RuntimeException(ex6);
            } catch (InvocationTargetException ex7) {
                throw new RuntimeException(ex7);
            }
        }
        throw new IllegalStateException("JFreeSVG is not present on the classpath.");
    }

    public static final void writeAsPDF(Drawable drawable, int w, int h, File file) {
        if (isOrsonPDFAvailable()) {
            ParamChecks.nullNotPermitted(drawable, "drawable");
            ParamChecks.nullNotPermitted(file, "file");
            try {
                Class<?> pdfDocClass = Class.forName("com.orsonpdf.PDFDocument");
                Object pdfDoc = pdfDocClass.newInstance();
                Class<?> cls = pdfDocClass;
                Object page = cls.getMethod("createPage", new Class[]{Rectangle2D.class}).invoke(pdfDoc, new Object[]{new Rectangle(w, h)});
                Method method = page.getClass().getMethod("getGraphics2D", new Class[0]);
                Drawable drawable2 = drawable;
                drawable2.draw((Graphics2D) m2.invoke(page, new Object[0]), new Double(0.0d, 0.0d, (double) w, (double) h));
                cls = pdfDocClass;
                cls.getMethod("writeToFile", new Class[]{File.class}).invoke(pdfDoc, new Object[]{file});
                return;
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            } catch (InstantiationException ex2) {
                throw new RuntimeException(ex2);
            } catch (IllegalAccessException ex3) {
                throw new RuntimeException(ex3);
            } catch (NoSuchMethodException ex4) {
                throw new RuntimeException(ex4);
            } catch (SecurityException ex5) {
                throw new RuntimeException(ex5);
            } catch (IllegalArgumentException ex6) {
                throw new RuntimeException(ex6);
            } catch (InvocationTargetException ex7) {
                throw new RuntimeException(ex7);
            }
        }
        throw new IllegalStateException("OrsonPDF is not present on the classpath.");
    }

    public static void writeAsPNG(Drawable drawable, int w, int h, File file) throws FileNotFoundException, IOException {
        BufferedImage image = new BufferedImage(w, h, 2);
        drawable.draw(image.createGraphics(), new Rectangle(w, h));
        OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        try {
            ImageIO.write(image, ImageFormat.PNG, out);
        } finally {
            out.close();
        }
    }

    public static void writeAsJPEG(Drawable drawable, int w, int h, File file) throws FileNotFoundException, IOException {
        BufferedImage image = new BufferedImage(w, h, 1);
        drawable.draw(image.createGraphics(), new Rectangle(w, h));
        OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        try {
            ImageIO.write(image, "jpg", out);
        } finally {
            out.close();
        }
    }
}
