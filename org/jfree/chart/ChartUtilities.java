package org.jfree.chart;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D.Double;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import org.jfree.chart.encoders.EncoderUtil;
import org.jfree.chart.encoders.ImageFormat;
import org.jfree.chart.imagemap.ImageMapUtilities;
import org.jfree.chart.imagemap.OverLIBToolTipTagFragmentGenerator;
import org.jfree.chart.imagemap.StandardToolTipTagFragmentGenerator;
import org.jfree.chart.imagemap.StandardURLTagFragmentGenerator;
import org.jfree.chart.imagemap.ToolTipTagFragmentGenerator;
import org.jfree.chart.imagemap.URLTagFragmentGenerator;
import org.jfree.chart.util.ParamChecks;

public abstract class ChartUtilities {
    public static void applyCurrentTheme(JFreeChart chart) {
        ChartFactory.getChartTheme().apply(chart);
    }

    public static void writeChartAsPNG(OutputStream out, JFreeChart chart, int width, int height) throws IOException {
        writeChartAsPNG(out, chart, width, height, null);
    }

    public static void writeChartAsPNG(OutputStream out, JFreeChart chart, int width, int height, boolean encodeAlpha, int compression) throws IOException {
        writeChartAsPNG(out, chart, width, height, null, encodeAlpha, compression);
    }

    public static void writeChartAsPNG(OutputStream out, JFreeChart chart, int width, int height, ChartRenderingInfo info) throws IOException {
        ParamChecks.nullNotPermitted(chart, "chart");
        EncoderUtil.writeBufferedImage(chart.createBufferedImage(width, height, info), ImageFormat.PNG, out);
    }

    public static void writeChartAsPNG(OutputStream out, JFreeChart chart, int width, int height, ChartRenderingInfo info, boolean encodeAlpha, int compression) throws IOException {
        ParamChecks.nullNotPermitted(out, "out");
        ParamChecks.nullNotPermitted(chart, "chart");
        writeBufferedImageAsPNG(out, chart.createBufferedImage(width, height, 2, info), encodeAlpha, compression);
    }

    public static void writeScaledChartAsPNG(OutputStream out, JFreeChart chart, int width, int height, int widthScaleFactor, int heightScaleFactor) throws IOException {
        ParamChecks.nullNotPermitted(out, "out");
        ParamChecks.nullNotPermitted(chart, "chart");
        double desiredWidth = (double) (width * widthScaleFactor);
        double desiredHeight = (double) (height * heightScaleFactor);
        double defaultWidth = (double) width;
        double defaultHeight = (double) height;
        boolean scale = false;
        if (!(widthScaleFactor == 1 && heightScaleFactor == 1)) {
            scale = true;
        }
        double scaleX = desiredWidth / defaultWidth;
        double scaleY = desiredHeight / defaultHeight;
        BufferedImage bufferedImage = new BufferedImage((int) desiredWidth, (int) desiredHeight, 2);
        Graphics2D g2 = bufferedImage.createGraphics();
        if (scale) {
            AffineTransform saved = g2.getTransform();
            g2.transform(AffineTransform.getScaleInstance(scaleX, scaleY));
            chart.draw(g2, new Double(0.0d, 0.0d, defaultWidth, defaultHeight), null, null);
            g2.setTransform(saved);
            g2.dispose();
        } else {
            chart.draw(g2, new Double(0.0d, 0.0d, defaultWidth, defaultHeight), null, null);
        }
        out.write(encodeAsPNG(bufferedImage));
    }

    public static void saveChartAsPNG(File file, JFreeChart chart, int width, int height) throws IOException {
        saveChartAsPNG(file, chart, width, height, null);
    }

    public static void saveChartAsPNG(File file, JFreeChart chart, int width, int height, ChartRenderingInfo info) throws IOException {
        ParamChecks.nullNotPermitted(file, "file");
        OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        try {
            writeChartAsPNG(out, chart, width, height, info);
        } finally {
            out.close();
        }
    }

    public static void saveChartAsPNG(File file, JFreeChart chart, int width, int height, ChartRenderingInfo info, boolean encodeAlpha, int compression) throws IOException {
        ParamChecks.nullNotPermitted(file, "file");
        ParamChecks.nullNotPermitted(chart, "chart");
        OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        try {
            writeChartAsPNG(out, chart, width, height, info, encodeAlpha, compression);
        } finally {
            out.close();
        }
    }

    public static void writeChartAsJPEG(OutputStream out, JFreeChart chart, int width, int height) throws IOException {
        writeChartAsJPEG(out, chart, width, height, null);
    }

    public static void writeChartAsJPEG(OutputStream out, float quality, JFreeChart chart, int width, int height) throws IOException {
        writeChartAsJPEG(out, quality, chart, width, height, null);
    }

    public static void writeChartAsJPEG(OutputStream out, JFreeChart chart, int width, int height, ChartRenderingInfo info) throws IOException {
        ParamChecks.nullNotPermitted(out, "out");
        ParamChecks.nullNotPermitted(chart, "chart");
        EncoderUtil.writeBufferedImage(chart.createBufferedImage(width, height, 1, info), ImageFormat.JPEG, out);
    }

    public static void writeChartAsJPEG(OutputStream out, float quality, JFreeChart chart, int width, int height, ChartRenderingInfo info) throws IOException {
        ParamChecks.nullNotPermitted(out, "out");
        ParamChecks.nullNotPermitted(chart, "chart");
        EncoderUtil.writeBufferedImage(chart.createBufferedImage(width, height, 1, info), ImageFormat.JPEG, out, quality);
    }

    public static void saveChartAsJPEG(File file, JFreeChart chart, int width, int height) throws IOException {
        saveChartAsJPEG(file, chart, width, height, null);
    }

    public static void saveChartAsJPEG(File file, float quality, JFreeChart chart, int width, int height) throws IOException {
        saveChartAsJPEG(file, quality, chart, width, height, null);
    }

    public static void saveChartAsJPEG(File file, JFreeChart chart, int width, int height, ChartRenderingInfo info) throws IOException {
        ParamChecks.nullNotPermitted(file, "file");
        ParamChecks.nullNotPermitted(chart, "chart");
        OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        try {
            writeChartAsJPEG(out, chart, width, height, info);
        } finally {
            out.close();
        }
    }

    public static void saveChartAsJPEG(File file, float quality, JFreeChart chart, int width, int height, ChartRenderingInfo info) throws IOException {
        ParamChecks.nullNotPermitted(file, "file");
        ParamChecks.nullNotPermitted(chart, "chart");
        OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        try {
            writeChartAsJPEG(out, quality, chart, width, height, info);
        } finally {
            out.close();
        }
    }

    public static void writeBufferedImageAsJPEG(OutputStream out, BufferedImage image) throws IOException {
        writeBufferedImageAsJPEG(out, 0.75f, image);
    }

    public static void writeBufferedImageAsJPEG(OutputStream out, float quality, BufferedImage image) throws IOException {
        EncoderUtil.writeBufferedImage(image, ImageFormat.JPEG, out, quality);
    }

    public static void writeBufferedImageAsPNG(OutputStream out, BufferedImage image) throws IOException {
        EncoderUtil.writeBufferedImage(image, ImageFormat.PNG, out);
    }

    public static void writeBufferedImageAsPNG(OutputStream out, BufferedImage image, boolean encodeAlpha, int compression) throws IOException {
        EncoderUtil.writeBufferedImage(image, ImageFormat.PNG, out, (float) compression, encodeAlpha);
    }

    public static byte[] encodeAsPNG(BufferedImage image) throws IOException {
        return EncoderUtil.encode(image, ImageFormat.PNG);
    }

    public static byte[] encodeAsPNG(BufferedImage image, boolean encodeAlpha, int compression) throws IOException {
        return EncoderUtil.encode(image, ImageFormat.PNG, (float) compression, encodeAlpha);
    }

    public static void writeImageMap(PrintWriter writer, String name, ChartRenderingInfo info, boolean useOverLibForToolTips) throws IOException {
        ToolTipTagFragmentGenerator toolTipTagFragmentGenerator;
        if (useOverLibForToolTips) {
            toolTipTagFragmentGenerator = new OverLIBToolTipTagFragmentGenerator();
        } else {
            toolTipTagFragmentGenerator = new StandardToolTipTagFragmentGenerator();
        }
        ImageMapUtilities.writeImageMap(writer, name, info, toolTipTagFragmentGenerator, new StandardURLTagFragmentGenerator());
    }

    public static void writeImageMap(PrintWriter writer, String name, ChartRenderingInfo info, ToolTipTagFragmentGenerator toolTipTagFragmentGenerator, URLTagFragmentGenerator urlTagFragmentGenerator) throws IOException {
        writer.println(ImageMapUtilities.getImageMap(name, info, toolTipTagFragmentGenerator, urlTagFragmentGenerator));
    }

    public static String getImageMap(String name, ChartRenderingInfo info) {
        return ImageMapUtilities.getImageMap(name, info, new StandardToolTipTagFragmentGenerator(), new StandardURLTagFragmentGenerator());
    }

    public static String getImageMap(String name, ChartRenderingInfo info, ToolTipTagFragmentGenerator toolTipTagFragmentGenerator, URLTagFragmentGenerator urlTagFragmentGenerator) {
        return ImageMapUtilities.getImageMap(name, info, toolTipTagFragmentGenerator, urlTagFragmentGenerator);
    }
}
